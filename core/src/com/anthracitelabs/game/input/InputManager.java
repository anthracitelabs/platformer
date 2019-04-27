package com.anthracitelabs.game.input;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.stage.TouchControllerStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputManager {

    private int mCurrentInputMethod;

    KeyboardController mKeyboard;
    TouchController mTouch;
    GamePadController mGamePad;

    private boolean[] mInputs;
    private boolean[] mPrevInputs;
    private TouchControllerStage mTouchControllerStage; // the only visible controller

    public InputManager(boolean[] inputs, boolean[] prevInputs, TouchControllerStage touchControllerStage) {
        mInputs = inputs;
        mPrevInputs = prevInputs;
        mTouchControllerStage = touchControllerStage;

        mKeyboard = new KeyboardController();
        mTouch = new TouchController();
        mGamePad = new GamePadController();
    }

    public void setInputMethod(int method) {

        switch(method) {
            case Constants.KEYBOARD_CONTROLLER:
                Gdx.input.setInputProcessor(mKeyboard);
                break;
            case Constants.TOUCH_CONTROLLER:
                Gdx.input.setInputProcessor(mTouch);
                break;
            case Constants.GAME_PAD_CONTROLLER:
                Gdx.input.setInputProcessor(mGamePad);
                break;
            default:
                Gdx.input.setInputProcessor(mKeyboard);
                break;
        }

        mCurrentInputMethod = method;
    }

    public void StartFrameUpdate() {
        mInputs[GameInput.GoRight] = false;
        mInputs[GameInput.GoLeft] = false;
        mInputs[GameInput.GoDown] = false;
        mInputs[GameInput.GoUp] = false;
        mInputs[GameInput.Action1] = false;
        mInputs[GameInput.Pause] = false;

        switch(mCurrentInputMethod) {
            case Constants.KEYBOARD_CONTROLLER:
                mInputs[GameInput.GoRight] = mKeyboard.isKeyDown(Input.Keys.RIGHT);
                mInputs[GameInput.GoLeft] = mKeyboard.isKeyDown(Input.Keys.LEFT);
                mInputs[GameInput.GoDown] = mKeyboard.isKeyDown(Input.Keys.DOWN);
                mInputs[GameInput.GoUp] = mKeyboard.isKeyDown(Input.Keys.UP);
                mInputs[GameInput.Action1] = mKeyboard.isKeyDown(Input.Keys.X);
                mInputs[GameInput.Pause] = mKeyboard.isKeyPressed(Input.Keys.P);

                // TODO: TESTING
                mTouchControllerStage.toggleButton();
                break;
            case Constants.TOUCH_CONTROLLER:
                for (int i = 0; i < mTouch.touchStates.size; i++) {
                    TouchController.TouchState t = mTouch.getTouchState(i);

                    if (mTouch.isTouchDown(t.pointer)) {
                        mTouchControllerStage.touchDown(t.coordinates);

                        if (mTouch.isTouchSwipeDown(t.pointer)) {
                            mInputs[GameInput.GoDown] = true;
                        }
                    }
                }

                mTouchControllerStage.toggleButton();

                break;
            case Constants.GAME_PAD_CONTROLLER:

                break;
            default:

                break;
        }
    }

    public void EndFrameUpdate() {
        switch(mCurrentInputMethod) {
            case Constants.KEYBOARD_CONTROLLER:
                // TODO: controller buttons will be seperated from pause button
                mTouchControllerStage.getViewport().apply();
                mTouchControllerStage.draw();
                //////////////////

                mKeyboard.EndFrameUpdate();
                break;
            case Constants.TOUCH_CONTROLLER:
                mTouchControllerStage.getViewport().apply();
                mTouchControllerStage.draw();
                mTouch.EndFrameUpdate();
                break;
            case Constants.GAME_PAD_CONTROLLER:
                // TODO: TESTING
                //mTouchControllerStage.getViewport().apply();
                //mTouchControllerStage.draw();
                //////////////////

                mGamePad.EndFrameUpdate();
                break;
            default:
                mKeyboard.EndFrameUpdate();
                break;
        }

        // update previous inputs
        for (byte i = 0; i < GameInput.Count; ++i)
            mPrevInputs[i] = mInputs[i];
    }
}
