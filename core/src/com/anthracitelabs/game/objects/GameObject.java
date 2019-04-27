package com.anthracitelabs.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class GameObject {

    public boolean alive;

    public AABB mAABB;

    public Vector2 mPosition;
    public Vector2 mOldPosition;
    public Vector2 mSpeed;
    public Vector2 mOldSpeed;

    protected int mState, mPreviousState;

    public GameObject() {
        alive = false;
        mAABB = new AABB(0f, 0f, 0f, 0f);

        mPosition = new Vector2(0, 0);
        mOldPosition = new Vector2(0, 0);
        mSpeed = new Vector2(0, 0);
        mOldSpeed = new Vector2(0, 0);
    }

    public void init(Vector2 pos, Vector2 halfSize) {
        mPosition = new Vector2(pos);
        mAABB.mHalfSizeX = halfSize.x;
        mAABB.mHalfSizeY = halfSize.y;
        mAABB.mCenterX = mPosition.x + mAABB.mHalfSizeX;
        mAABB.mCenterY = mPosition.y + mAABB.mHalfSizeY;

        alive = true;
    }

    public void update(float delta) {
        // alive is assigned false when object is dead
    }

    public void render(SpriteBatch batch, float delta) {

    }
}
