package com.anthracitelabs.game.data;

import com.anthracitelabs.game.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameData {
    private Level[] mLevels;
    private Preferences mSettings;

    // game play data
    private int mCurrentLevelIndex;
    private int mCurrentGameState;

    // saved settings
    private int mLastUnlockedLevel;
    private int mControllerSelection;

    public GameData() {

        mCurrentLevelIndex = -1;
        mCurrentGameState = Constants.GAME_STATE_INIT;

        mSettings = Gdx.app.getPreferences("settings");
        loadAllSettings();

        String[] lines;
        FileHandle handle = Gdx.files.internal(Constants.LEVELS_FILE);
        String text = handle.readString();
        lines = text.split("\\r?\\n");

        mLevels = new Level[lines.length];

        for (int i = 0; i < mLevels.length; i++) {
            String[] lineWords = lines[i].split("\\t");
            if (i <= mLastUnlockedLevel)
                mLevels[i] = new Level(lineWords[0], lineWords[1], false);
            else
                mLevels[i] = new Level(lineWords[0], lineWords[1], true);
        }
    }

    public void loadAllSettings() {
        mLastUnlockedLevel = mSettings.getInteger("UNLOCKED_LEVEL_INDEX", 0);
        mControllerSelection = mSettings.getInteger("CONTROLLER_SELECTION", Constants.KEYBOARD_CONTROLLER);
    }

    public void saveAllSettings() {
        mSettings.putInteger("UNLOCKED_LEVEL_INDEX", mLastUnlockedLevel);
        mSettings.putInteger("CONTROLLER_SELECTION", mControllerSelection);
        mSettings.flush();
    }

    public void updateUnlockedLevel(int index) {
        mLastUnlockedLevel = index;
        mSettings.putInteger("UNLOCKED_LEVEL_INDEX", mLastUnlockedLevel);
        mSettings.flush();
    }

    public Level getLevel(int index) {
        return mLevels[index];
    }

    public int getNumberOfLevels() {
        return mLevels.length;
    }

    public int getCurrentLevelIndex() {
        return mCurrentLevelIndex;
    }

    public void setCurrentLevelIndex(int val) {
        mCurrentLevelIndex = val;
    }

    public int getCurrentGameState() {
        return mCurrentGameState;
    }

    public void setCurrentGameState(int val) {
        mCurrentGameState = val;
    }

    public int getControllerSelection() {
        return mControllerSelection;
    }

    public void updateControllerSelection(int index) {
        mControllerSelection = index;
        mSettings.putInteger("CONTROLLER_SELECTION", mControllerSelection);
        mSettings.flush();
    }
}
