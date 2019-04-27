package com.anthracitelabs.game.objects;

import com.badlogic.gdx.math.Vector2;

public class AABB {
    public float mCenterX, mCenterY;
    public float mHalfSizeX, mHalfSizeY;

    public AABB(float centerX, float centerY, float halfSizeX, float halfSizeY) {
        this.mCenterX = centerX;
        this.mCenterY = centerY;

        this.mHalfSizeX = halfSizeX;
        this.mHalfSizeY = halfSizeY;
    }

    public void set(float centerX, float centerY, float halfSizeX, float halfSizeY) {
        this.mCenterX = centerX;
        this.mCenterY = centerY;

        this.mHalfSizeX = halfSizeX;
        this.mHalfSizeY = halfSizeY;
    }
}