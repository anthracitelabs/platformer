package com.anthracitelabs.game.input;

import com.badlogic.gdx.InputProcessor;

public class VirtualController implements InputProcessor {

    protected int inputMethod;

    public class InputState {
        public boolean pressed = false;
        public boolean down = false;
        public boolean released = false;
    }

    public void EndFrameUpdate(){
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
	
	@Override
    public boolean touchCancelled(int a,int b,int c,int d) {
		return false;
	}

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
	
	@Override
    public boolean scrolled(float x, float y) {
        return false;
    }
}
