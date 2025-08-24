package com.anthracitelabs.game;

public class Constants {

    public static final String SKIN_FILE = "skin\\myskin.json";
    public static final String MAIN_MENU_BACKGROUND_FILE = "backgrounds/menu_background.png";
    public static final String LEVELS_FILE = "levels\\levels.txt";

    public static final int MAIN_MENU_SCREEN = 0;
    public static final int GAME_PLAY_SCREEN = 1;
    public static final int LEVEL_SCREEN = 2;

    public static final int KEYBOARD_CONTROLLER = 0;
    public static final int TOUCH_CONTROLLER = 1;
    public static final int GAME_PAD_CONTROLLER = 2;

    public static final float REFERENCE_WORLD_WIDTH = 300;

    public static final int TOUCH_BUTTON_LEFT = 0;
    public static final int TOUCH_BUTTON_RIGHT = 1;
    public static final int TOUCH_BUTTON_UP = 2;
    public static final int TOUCH_BUTTON_ACTION = 3;
    public static final int TOUCH_BUTTON_PAUSE = 4;
    public static final int TOUCH_BUTTONS_COUNT = 5;

    public static final int GAME_STATE_INIT = 0;
    public static final int GAME_STATE_PLAY = 1;
    public static final int GAME_STATE_PAUSE = 2;
    public static final int GAME_STATE_GAME_OVER = 3;
    public static final int GAME_STATE_LEVEL_COMPLETE = 4;
    public static final int GAME_STATE_GAME_COMPLETE = 5;

    public static final int TILE_SIZE = 16;

    public static final int CHARACTER_STATE_IDLE = 0;
    public static final int CHARACTER_STATE_RUN = 1;
    public static final int CHARACTER_STATE_JUMP = 2;
    public static final int CHARACTER_STATE_FALL = 3;
    public static final int CHARACTER_STATE_SQUISH = 4;
    public static final int CHARACTER_STATE_ACT = 5;
    public static final int CHARACTER_STATE_DIE = 6;

    public static final float PLAYER_JUMP_SPEED = 200f;
    public static final float PLAYER_RUN_SPEED = 160f;
    public static final float PLAYER_HORIZONTAL_FLY_SPEED = 150f;
    public static final float PLAYER_MAX_FALLING_SPEED = -200f;
    public static final float GRAVITY = 500f;
    public static final float ONE_WAY_PLATFORM_THRESHOLD = 2.0f;
    public static final int JUMP_FRAMES_THRESHOLD = 4;
}
