package com.anthracitelabs.game.screen;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.MyGdxGame;
import com.anthracitelabs.game.data.GameData;
import com.anthracitelabs.game.data.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LevelScreen extends UIScreen {

    private TextButton[] buttons;
    private int currentPage;
    private TextButton[] mLevelButtons;
    private float mLevelButtonsSpacing;

    public LevelScreen(MyGdxGame game, Skin skin) {
        super(game, Constants.MAIN_MENU_BACKGROUND_FILE, skin);

        createMenuScreenLayout();
        createButtons();
    }

    protected void createButtons() {
        final GameData data = mGame.getGameData();
        buttons = new TextButton[data.getNumberOfLevels()];

        for (int i = 0; i < data.getNumberOfLevels(); i++) {
            Level level = data.getLevel(i);
            TextButton levelButton = new TextButton(level.getName(), mSkin, "font_18");
            final int levelIndex = i;
            levelButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    data.setCurrentGameState(Constants.GAME_STATE_INIT);
                    data.setCurrentLevelIndex(levelIndex);
                    mGame.setScreen(Constants.GAME_PLAY_SCREEN);
                }
            });

            buttons[i] = levelButton;
        }

        addButtonsCenteredGrid(buttons);

        Button backButton = new Button(mSkin, "back");
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mGame.setScreen(Constants.MAIN_MENU_SCREEN);
            }
        });
        addBottomTable(backButton);
    }

    public void updateLevelButtons() {
        GameData data = mGame.getGameData();

        for (int i = 0; i < data.getNumberOfLevels(); i++) {
            Level level = data.getLevel(i);
            if (level.getLocked()) {
                buttons[i].setTouchable(Touchable.disabled);
                buttons[i].setDisabled(true);
            }
        }
    }

    @Override
    public void show() {
        updateLevelButtons();
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void resize(int width, int height) {
        mStage.getViewport().update(width, height);
    }

    private void showPage(int page) {
        if (page >= 1 && mLevelButtons.length > (page - 1) * 25) {
            buttonsTable.clearChildren();

            int startIndex = (page - 1) * 25;
            int endIndex = (page - 1) * 25 + 25;
            if (endIndex > mLevelButtons.length)
                endIndex = mLevelButtons.length;

            for (int i = startIndex; i < endIndex; i++) {
                buttonsTable.add(mLevelButtons[i]).width(viewport.getWorldHeight() / 8).height(viewport.getWorldHeight() / 8).padRight(mLevelButtonsSpacing).padBottom(mLevelButtonsSpacing);
                if ((i + 1) % 5 == 0)
                    buttonsTable.row();
            }

            currentPage = page;
        }
    }

    protected void addButtonsCenteredGrid(TextButton[] buttons) {

        currentPage = 1;
        mLevelButtons = buttons;

        // any number of buttons will be placed on seperate tables, each table will be 5x5 buttons
        mLevelButtonsSpacing = (viewport.getWorldHeight() / 8) / 5;

        TextButton next = new TextButton(">", mSkin, "font_18");
        next.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                showPage(currentPage + 1);
            }
        });

        TextButton previous = new TextButton("<", mSkin, "font_18");
        previous.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                showPage(currentPage - 1);
            }
        });

        centerTable.add(previous).width(viewport.getWorldHeight() / 8).height(viewport.getWorldHeight() / 8).padRight(mLevelButtonsSpacing);
        centerTable.add(buttonsTable);
        centerTable.add(next).width(viewport.getWorldHeight() / 8).height(viewport.getWorldHeight() / 8);

        int endIndex = 25;
        if (endIndex > mLevelButtons.length)
            endIndex = mLevelButtons.length;
        for (int i = 0; i < endIndex; i++) {
            buttonsTable.add(buttons[i]).width(viewport.getWorldHeight() / 8).height(viewport.getWorldHeight() / 8).padRight(mLevelButtonsSpacing).padBottom(mLevelButtonsSpacing);
            if ((i + 1) % 5 == 0)
                buttonsTable.row();
        }
    }
}
