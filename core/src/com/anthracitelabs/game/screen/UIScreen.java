package com.anthracitelabs.game.screen;

import com.anthracitelabs.game.MyGdxGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UIScreen implements Screen {

    protected MyGdxGame mGame;

    protected Stage mStage;
    protected SpriteBatch mBatch;
    protected OrthographicCamera mCamera;
    protected Viewport viewport;
    protected Group mGroup;
    protected Skin mSkin;

    protected Table buttonsTable, centerTable, bottomTable;

    private String mBackgroundImageFile;

    public UIScreen(MyGdxGame game, String backgroundImageFile, Skin skin) {
        mGame = game;
        mCamera = new OrthographicCamera();
        mBatch = new SpriteBatch();
        mSkin = skin;
        mBackgroundImageFile = backgroundImageFile;

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.setCamera(mCamera);
    }

    protected void createMenuScreenLayout() {
        mStage = new Stage(viewport, mBatch);
        mGroup = new Group();
        mGroup.setWidth(Gdx.graphics.getWidth());
        mGroup.setHeight(Gdx.graphics.getHeight());
        mStage.addActor(mGroup);

        if (mBackgroundImageFile != null) {
            Texture texture = new Texture(Gdx.files.internal(mBackgroundImageFile));
            Image image = new Image(texture);
            image.setX(0);
            image.setFillParent(true);
            mGroup.addActor(image);
        }

        centerTable = new Table();
        bottomTable = new Table();
        centerTable.center();
        bottomTable.bottom().right();

        centerTable.setFillParent(true);
        bottomTable.setFillParent(true);

        buttonsTable = new Table();

        mGroup.addActor(centerTable);
        mGroup.addActor(bottomTable);
    }

    protected void addButtonsCenteredSimple(TextButton[] buttons) {

        float verticalSpacing = (viewport.getWorldHeight() / 8) / buttons.length;

        for (int i = 0; i < buttons.length; i++) {
            buttonsTable.add(buttons[i]).width(viewport.getWorldWidth() / 4).height(viewport.getWorldHeight() / 8).padBottom(verticalSpacing);
            buttonsTable.row();
        }

        centerTable.add(buttonsTable);
    }

    protected void addBottomTable(Button backButton) {
        float spacing = viewport.getWorldHeight() / 24;
        bottomTable.add(backButton).width(viewport.getWorldHeight() / 8).height(viewport.getWorldHeight() / 8).padBottom(spacing).padRight(spacing);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mStage.getViewport().apply();
        mStage.act(delta);
        mStage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
