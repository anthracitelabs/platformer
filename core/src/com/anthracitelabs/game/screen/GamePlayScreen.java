package com.anthracitelabs.game.screen;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.MyGdxGame;
import com.anthracitelabs.game.data.GameData;
import com.anthracitelabs.game.data.Level;
import com.anthracitelabs.game.input.GameInput;
import com.anthracitelabs.game.input.InputManager;
import com.anthracitelabs.game.map.TiledMapManager;
import com.anthracitelabs.game.objects.GameObject;
import com.anthracitelabs.game.objects.Projectile;
import com.anthracitelabs.game.stage.TouchControllerStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import com.anthracitelabs.game.render.GameRenderer;
import com.anthracitelabs.game.world.GameWorld;

public class GamePlayScreen extends UIScreen {

    private GameData mGameData;
    private InputManager mInputManager;
    private TouchControllerStage mTouchControllerStage;
    private boolean[] mInputs;
    private boolean[] mPrevInputs;
    private GameWorld mWorld;
    private GameRenderer mRenderer;

    private final Array<GameObject> activeGameObjects = new Array<GameObject>();
    private final Array<Projectile> activeProjectiles = new Array<Projectile>();
    private final Pool<Projectile> projectilePool = Pools.get(Projectile.class);

    private TiledMapManager mTiledManager;

    private final float fixedDeltaTime = 1.0f / 60.0f;

    public GamePlayScreen(MyGdxGame game, Skin skin) {
        super(game,null, skin);

        mGameData = mGame.getGameData();
        mInputs = new boolean[GameInput.Count];
        mPrevInputs = new boolean[GameInput.Count];
        mTouchControllerStage = new TouchControllerStage(viewport, mBatch, mSkin, mInputs, mPrevInputs);
        mInputManager = new InputManager(mInputs, mPrevInputs, mTouchControllerStage);


        mWorld = new GameWorld(activeGameObjects, activeProjectiles, mInputs, mPrevInputs);
        mTiledManager = new TiledMapManager(activeGameObjects, activeProjectiles, mGame.mTextureAtlas, mWorld);
        mRenderer = new GameRenderer(mBatch, activeGameObjects, activeProjectiles);

        createMenuScreenLayout();
        createPauseMenuButtons();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(27f/255f, 30f/255f, 40f/255f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch(mGameData.getCurrentGameState()) {
            case Constants.GAME_STATE_INIT:
                updateGameInit(delta);
                break;
            case Constants.GAME_STATE_PLAY:
                updateGamePlay(delta);
                break;
            case Constants.GAME_STATE_PAUSE:
                updatePause(delta);
                break;
            case Constants.GAME_STATE_GAME_OVER:
                updateGameOver(delta);
                break;
            case Constants.GAME_STATE_LEVEL_COMPLETE:
                updateLevelComplete(delta);
                break;
            case Constants.GAME_STATE_GAME_COMPLETE:
                updateGameComplete(delta);
                break;
            default:
                System.out.println("UNKNOWN STATE..Going back to Main Screen..");
                mGame.setScreen(Constants.MAIN_MENU_SCREEN);
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        mTouchControllerStage.getViewport().update(width, height);
        mStage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        switch(mGameData.getCurrentGameState()) {
            case Constants.GAME_STATE_INIT:
                System.out.println("Game State : INIT");

                // TODO : initialize game here
                Level currentLevel = mGameData.getLevel(mGameData.getCurrentLevelIndex());

                mTiledManager.init(currentLevel.getMapFileName());
                mRenderer.init(mTiledManager.getCurrentTiledMap());
                mWorld.init();

                mGameData.setCurrentGameState(Constants.GAME_STATE_PLAY);
                mGame.setScreen(Constants.GAME_PLAY_SCREEN);
                break;
            case Constants.GAME_STATE_PLAY:
                System.out.println("Game State : PLAY");
                mInputManager.setInputMethod(mGameData.getControllerSelection());

                break;
            case Constants.GAME_STATE_PAUSE:
                System.out.println("Game State : PAUSE");
                Gdx.input.setInputProcessor(mStage);
                break;
            case Constants.GAME_STATE_GAME_OVER:
                System.out.println("Game State : GAME OVER");
                break;
            case Constants.GAME_STATE_LEVEL_COMPLETE:
                System.out.println("Game State : LEVEL COMPLETE");
                break;
            case Constants.GAME_STATE_GAME_COMPLETE:
                System.out.println("Game State : GAME COMPLETE");
                break;
            default:
                System.out.println("UNKNOWN STATE..Going back to Main Screen..");
                mGame.setScreen(Constants.MAIN_MENU_SCREEN);
                break;
        }
    }

    private void createPauseMenuButtons() {
        TextButton resumeButton = new TextButton("RESUME", mSkin, "font_18");
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mGameData.setCurrentGameState(Constants.GAME_STATE_PLAY);
                mGame.setScreen(Constants.GAME_PLAY_SCREEN);
            }
        });

        TextButton restartButton = new TextButton("RESTART", mSkin, "font_18");
        restartButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mGameData.setCurrentGameState(Constants.GAME_STATE_INIT);
                mGame.setScreen(Constants.GAME_PLAY_SCREEN);
            }
        });

        TextButton exitButton = new TextButton("EXIT", mSkin, "font_18");
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mGameData.setCurrentGameState(Constants.GAME_STATE_INIT);
                mGame.setScreen(Constants.MAIN_MENU_SCREEN);
            }
        });

        TextButton[] buttons = {resumeButton, restartButton, exitButton};

        addButtonsCenteredSimple(buttons);
    }

    private void updateGameInit(float delta) {

    }

    private void updateGamePlay(float delta) {
        mInputManager.StartFrameUpdate();

        if (mInputs[GameInput.Pause]) {

            mGameData.setCurrentGameState(Constants.GAME_STATE_PAUSE);
            mGame.setScreen(Constants.GAME_PLAY_SCREEN);
        }

        // GameWorld is updated
        // use fixedDeltaTime instead of the variable delta time
        // Fixed delta time solution - https://gafferongames.com/post/fix_your_timestep/
        mWorld.updatePlay(fixedDeltaTime);

        // Rendering is done
        mRenderer.render(delta);

        mInputManager.EndFrameUpdate();
    }

    private void updatePause(float delta) {
        mStage.getViewport().apply();
        mStage.act(delta);
        mStage.draw();
    }

    private void updateGameOver(float delta) {

    }

    private void updateLevelComplete(float delta) {

    }

    private void updateGameComplete(float delta) {

    }
}
