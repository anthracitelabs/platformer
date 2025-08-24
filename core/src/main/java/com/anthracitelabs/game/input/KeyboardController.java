package com.anthracitelabs.game.input;

import com.anthracitelabs.game.Constants;
import com.badlogic.gdx.utils.Array;

public class KeyboardController extends VirtualController {

    private Array<KeyState> keyStates = new Array<KeyState>();

    public KeyboardController() {
        inputMethod = Constants.KEYBOARD_CONTROLLER;

        //create the initial state of every key on the keyboard.
        //There are 256 keys available which are all represented as integers.
        for (int i = 0; i < 256; i++) {
            keyStates.add(new KeyState(i));
        }
    }

    public class KeyState extends InputState{
        //the keyboard key of this object represented as an integer.
        public int key;

        public KeyState(int key){
            this.key = key;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        //this function only gets called once when an event is fired. (even if this key is being held down)

        //I need to store the state of the key being held down as well as pressed
        keyStates.get(keycode).pressed = true;
        keyStates.get(keycode).down = true;

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        //the key was released, I need to set it's down state to false and released state to true
        keyStates.get(keycode).down = false;
        keyStates.get(keycode).released = true;

        return false;
    }

    //check states of supplied key
    public boolean isKeyPressed(int key){
        return keyStates.get(key).pressed;
    }
    public boolean isKeyDown(int key){
        return keyStates.get(key).down;
    }
    public boolean isKeyReleased(int key){
        return keyStates.get(key).released;
    }

    @Override
    public void EndFrameUpdate(){
        Reset();
    }

    public void Reset() {
        //for every keystate, set pressed and released to false.
        for (int i = 0; i < 256; i++) {
            keyStates.get(i).pressed = false;
            keyStates.get(i).released = false;
        }
    }
}
