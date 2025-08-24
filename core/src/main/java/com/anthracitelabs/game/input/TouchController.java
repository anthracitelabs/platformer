package com.anthracitelabs.game.input;

import com.anthracitelabs.game.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TouchController extends VirtualController {

    public Array<TouchState> touchStates = new Array<TouchState>();

    public TouchController() {
        inputMethod = Constants.TOUCH_CONTROLLER;

        //this may not make much sense right now, but I need to create
        //atleast one TouchState object due to Desktop users who utilize
        //a mouse rather than touch.
        touchStates.add(new TouchState(0, 0, 0, 0));
    }

    public class TouchState extends InputState{
        //keep track of which finger this object belongs to
        public int pointer;
        //coordinates of this finger/mouse
        public Vector2 coordinates;
        //mouse button
        public int button;
        //track the displacement of this finger/mouse
        private Vector2 lastPosition;
        public Vector2 displacement;

        public TouchState(int coord_x, int coord_y, int pointer, int button){
            this.pointer = pointer;
            coordinates = new Vector2(coord_x, coord_y);
            this.button = button;

            lastPosition = new Vector2(0, 0);
            displacement = new Vector2(lastPosition.x,lastPosition.y);
        }
    }

    @Override
    public void EndFrameUpdate(){
        for (int i = 0; i < touchStates.size; i++) {
            TouchState t = touchStates.get(i);

            t.pressed = false;
            t.released = false;

            t.displacement.x = 0;
            t.displacement.y = 0;
        }
    }

    public void Reset() {
        touchStates.clear();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //There is always at least one touch event initialized (mouse).
        //However, Android can handle multiple touch events (multiple fingers touching the screen at once).

        //Due to this difference, the input manager will add touch events on the fly if more than one
        //finger is touching the screen.

        //check for existing pointer (touch)
        boolean pointerFound = false;

        //get altered coordinates
        int coord_x = coordinateX(screenX);
        int coord_y = coordinateY(screenY);

        //set the state of all touch state events
        for (int i = 0; i < touchStates.size; i++) {
            TouchState t = touchStates.get(i);
            if (t.pointer == pointer) {
                t.down = true;
                t.pressed = true;

                //store the coordinates of this touch event
                t.coordinates.x = coord_x;
                t.coordinates.y = coord_y;
                t.button = button;

                //recording last position for displacement values
                t.lastPosition.x = coord_x;
                t.lastPosition.y = coord_y;

                //this pointer exists, don't add a new one.
                pointerFound = true;
            }
        }

        //this pointer doesn't exist yet, add it to touchStates and initialize it.
        if (!pointerFound) {
            touchStates.add(new TouchState(coord_x, coord_y, pointer, button));
            TouchState t = touchStates.get(pointer);

            t.down = true;
            t.pressed = true;

            t.lastPosition.x = coord_x;
            t.lastPosition.y = coord_y;
        }

        return false;
    }

    public static int coordinateX (int screenX) {
        return screenX;// - Gdx.graphics.getWidth()/2;
    }
    public static int coordinateY (int screenY) {
        return Math.abs(screenY - Gdx.graphics.getHeight());
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        TouchState t = touchStates.get(pointer);
        t.down = false;
        t.released = true;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //get altered coordinates
        int coord_x = coordinateX(screenX);
        int coord_y = coordinateY(screenY);

        TouchState t = touchStates.get(pointer);
        //set coordinates of this touchstate
        t.coordinates.x = coord_x;
        t.coordinates.y = coord_y;
        //calculate the displacement of this touchstate based on
        //the information from the last frame's position
        t.displacement.x = coord_x - t.lastPosition.x;
        t.displacement.y = coord_y - t.lastPosition.y;
        //store the current position into last position for next frame.
        t.lastPosition.x = coord_x;
        t.lastPosition.y = coord_y;

        return false;
    }

    //check states of supplied touch
    public boolean isTouchPressed(int pointer){
        return touchStates.get(pointer).pressed;
    }
    public boolean isTouchDown(int pointer){
        return touchStates.get(pointer).down;
    }
    public boolean isTouchReleased(int pointer){
        return touchStates.get(pointer).released;
    }
    public boolean isTouchSwipeDown(int pointer) {
        return (touchStates.get(pointer).displacement.y < 0);
    }

    public Vector2 touchCoordinates(int pointer){
        return touchStates.get(pointer).coordinates;
    }
    public Vector2 touchDisplacement(int pointer){
        return touchStates.get(pointer).displacement;
    }

    public TouchState getTouchState(int pointer){
        if (touchStates.size > pointer) {
            return touchStates.get(pointer);
        } else {
            return null;
        }
    }
}
