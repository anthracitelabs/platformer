package com.anthracitelabs.game.screen;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.MyGdxGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends UIScreen {

    public MainMenuScreen(MyGdxGame game, Skin skin) {
        super(game, Constants.MAIN_MENU_BACKGROUND_FILE, skin);

        createMenuScreenLayout();
        createButtons();
    }

    protected void createButtons() {
        TextButton newGameButton = new TextButton("NEW GAME", mSkin, "font_18");
        newGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mGame.setScreen(Constants.LEVEL_SCREEN);
            }
        });

        TextButton settingsButton = new TextButton("SETTINGS", mSkin, "font_18");
        TextButton removeAdsButton = new TextButton("REMOVE ADS", mSkin, "font_18");

        TextButton[] buttons = {newGameButton, settingsButton, removeAdsButton};

        addButtonsCenteredSimple(buttons);
    }

    @Override
    public void resize(int width, int height) {
        mStage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mStage);
    }
}
