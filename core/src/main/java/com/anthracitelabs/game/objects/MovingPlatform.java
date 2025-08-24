package com.anthracitelabs.game.objects;

import com.anthracitelabs.game.map.TiledMapManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import com.anthracitelabs.game.world.GameWorld;

public class MovingPlatform extends MovingObject {

    private float mMovingSpeed;
    private TextureRegion mTextureRegion;

    public MovingPlatform(TextureAtlas textureAtlas, TiledMapManager mapManager, GameWorld world) {
        super(mapManager, world);

        mTextureRegion = textureAtlas.findRegion("red_block");
    }

    @Override
    public void init(Vector2 pos, Vector2 halfSize)
    {
        super.init(pos, halfSize);

        mMovingSpeed = 50.0f;
        mIsKinematic = true;
        mSpeed.x = mMovingSpeed;
    }

    @Override
    public void update(float delta)
    {
        if (mPushesRightWall && !mOnGround)
            mSpeed.y = -mMovingSpeed;
        else if (mOnGround && !mPushesLeftWall)
            mSpeed.x = -mMovingSpeed;
        else if (mPushesLeftWall && !mAtCeiling)
            mSpeed.y = mMovingSpeed;
        else if (mAtCeiling && !mPushesRightWall)
            mSpeed.x = mMovingSpeed;

        UpdatePhysics(delta);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(mTextureRegion,
                mPosition.x / 16,
                mPosition.y / 16,
                mAABB.mHalfSizeX * 2 * 1/16,
                mAABB.mHalfSizeY * 2 * 1/16);
    }
}
