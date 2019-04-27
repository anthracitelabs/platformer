package com.anthracitelabs.game.objects;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.input.GameInput;
import com.anthracitelabs.game.map.TiledMapManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.anthracitelabs.game.world.GameWorld;

public class Player extends MovingObject {

    private Animation[] mAnimations;
    private float mAnimationTime;
    private TextureRegion mCurrentTextureRegion;
    private int mScale;

    private boolean[] mInputs;
    private boolean[] mPrevInputs;

    private int mPrevAnimationState, mAnimationState;

    private int mFramesFromJumpStart = 0;

    public Player(TextureAtlas textureAtlas, TiledMapManager mapManager, GameWorld world) {
        super(mapManager, world);

        mScale = 1;
        mState = Constants.CHARACTER_STATE_IDLE;
        mAnimationState = Constants.CHARACTER_STATE_IDLE;
        mPrevAnimationState = Constants.CHARACTER_STATE_IDLE;

        mAnimations = new Animation[7];

        mAnimations[Constants.CHARACTER_STATE_IDLE] = new Animation(0.2f, textureAtlas.findRegions("player_idle"), Animation.PlayMode.LOOP);
        mAnimations[Constants.CHARACTER_STATE_RUN] = new Animation(0.05f, textureAtlas.findRegions("player_run"), Animation.PlayMode.LOOP);
        mAnimations[Constants.CHARACTER_STATE_JUMP] = new Animation(0.1f, textureAtlas.findRegions("player_jump"), Animation.PlayMode.NORMAL);
        mAnimations[Constants.CHARACTER_STATE_FALL] = new Animation(0.1f, textureAtlas.findRegions("player_fall"), Animation.PlayMode.NORMAL);
        mAnimations[Constants.CHARACTER_STATE_SQUISH] = new Animation(0.1f, textureAtlas.findRegions("player_squish"), Animation.PlayMode.NORMAL);
        mAnimations[Constants.CHARACTER_STATE_ACT] = new Animation(0.1f, textureAtlas.findRegions("player_act"), Animation.PlayMode.NORMAL);
        mAnimations[Constants.CHARACTER_STATE_DIE] = new Animation(0.2f, textureAtlas.findRegions("player_die"), Animation.PlayMode.LOOP);
    }

    private void updateAnimation() {
        if ((!mPushedBottom && mPushesBottom)
                || (!mPushedTop && mPushesTop)) { // TODO: can play sound here!
            mAnimationState = Constants.CHARACTER_STATE_SQUISH;
            return;
        }
        else if (mSpeed.y < 0) {
            mAnimationState = Constants.CHARACTER_STATE_FALL;
            return;
        }

        if (mState == Constants.CHARACTER_STATE_IDLE && mAnimationState == Constants.CHARACTER_STATE_SQUISH) {
            if (mAnimations[mAnimationState].isAnimationFinished(mAnimationTime)) {
                mAnimationState = Constants.CHARACTER_STATE_IDLE;
            }
        }
        else {
            mAnimationState = mState;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (mAnimationState != mPrevAnimationState) {
            mPrevAnimationState = mAnimationState;
            mAnimationTime = 0f;
        }

        mCurrentTextureRegion = (TextureRegion) mAnimations[mAnimationState].getKeyFrame(mAnimationTime);
        batch.draw(mCurrentTextureRegion,
                (mScale == -1) ? (mAABB.mCenterX + mCurrentTextureRegion.getRegionWidth() * 0.5f) / 16 : (mAABB.mCenterX - mCurrentTextureRegion.getRegionWidth() * 0.5f) / 16,
                (mAABB.mCenterY - mAABB.mHalfSizeY)*1/16,
                mScale * mCurrentTextureRegion.getRegionWidth()*1/16,
                mCurrentTextureRegion.getRegionHeight()*1/16);

        mAnimationTime += delta;
    }

    @Override
    public void update(float delta) {
        mPreviousState = mState;

        switch(mState) {
            case Constants.CHARACTER_STATE_IDLE:
                mSpeed.x = 0f;
                mSpeed.y = 0f;

                if (!mPushesBottom) {
                    mState = Constants.CHARACTER_STATE_JUMP;
                    break;
                }

                if (mInputs[GameInput.GoLeft] != mInputs[GameInput.GoRight]) {
                    mState = Constants.CHARACTER_STATE_RUN;
                    break;
                }
                else if (mInputs[GameInput.GoUp]) {
                    mSpeed.y = Constants.PLAYER_JUMP_SPEED;
                    mState = Constants.CHARACTER_STATE_JUMP;
                    break;
                }

                if (mInputs[GameInput.GoDown])
                {
                    if (mOnOneWayPlatform)
                        mPosition.y -= Constants.ONE_WAY_PLATFORM_THRESHOLD;
                }

                break;
            case Constants.CHARACTER_STATE_RUN:
                if (mInputs[GameInput.GoLeft] == mInputs[GameInput.GoRight]) {
                    mState = Constants.CHARACTER_STATE_IDLE;
                    mSpeed.x = 0f;
                    mSpeed.y = 0f;
                    break;
                }
                else if (mInputs[GameInput.GoLeft]) {
                    if (mPushesLeft)
                        mSpeed.x = 0.0f;
                    else
                        mSpeed.x = -Constants.PLAYER_RUN_SPEED;

                    mScale = -1;
                }
                else if (mInputs[GameInput.GoRight]) {
                    if (mPushesRight)
                        mSpeed.x = 0.0f;
                    else
                        mSpeed.x = Constants.PLAYER_RUN_SPEED;

                    mScale = 1;
                }

                if (mInputs[GameInput.GoUp])
                {
                    mSpeed.y = Constants.PLAYER_JUMP_SPEED;
                    mState = Constants.CHARACTER_STATE_JUMP;
                    //updateAnimation(Constants.CHARACTER_STATE_JUMP);
                    break;
                }
                else if (!mPushesBottom)
                {
                    mState = Constants.CHARACTER_STATE_JUMP;
                    break;
                }

                if (mInputs[GameInput.GoDown])
                {
                    if (mOnOneWayPlatform)
                        mPosition.y -= Constants.ONE_WAY_PLATFORM_THRESHOLD;
                }

                break;
            case Constants.CHARACTER_STATE_JUMP:
                ++mFramesFromJumpStart;

                if (mFramesFromJumpStart <= Constants.JUMP_FRAMES_THRESHOLD)
                {
                    if (mPushesTop || mSpeed.y > 0.0f)
                        mFramesFromJumpStart = Constants.JUMP_FRAMES_THRESHOLD + 1;
                    else if (mInputs[GameInput.GoUp])
                        mSpeed.y = Constants.PLAYER_JUMP_SPEED;
                }

                mSpeed.y -= Constants.GRAVITY * delta;

                mSpeed.y = Math.max(mSpeed.y, Constants.PLAYER_MAX_FALLING_SPEED);

                if (!mInputs[GameInput.GoUp] && mSpeed.y > 0.0f)
                {
                    mSpeed.y = Math.min(mSpeed.y, 200.0f);
                }

                if (mInputs[GameInput.GoRight] == mInputs[GameInput.GoLeft])
                {
                    mSpeed.x = 0.0f;
                }
                else if (mInputs[GameInput.GoRight])
                {
                    if (mPushesRight)
                        mSpeed.x = 0.0f;
                    else
                        mSpeed.x = Constants.PLAYER_HORIZONTAL_FLY_SPEED;
                    mScale = 1;
                }
                else if (mInputs[GameInput.GoLeft])
                {
                    if (mPushesLeft)
                        mSpeed.x = 0.0f;
                    else
                        mSpeed.x = -Constants.PLAYER_HORIZONTAL_FLY_SPEED;
                    mScale = -1;
                }

                //if we hit the ground
                if (mPushesBottom)
                {
                    //if there's no movement change state to standing
                    if (mInputs[GameInput.GoRight] == mInputs[GameInput.GoLeft])
                    {
                        mState = Constants.CHARACTER_STATE_IDLE;
                        mSpeed.x = 0f;
                        mSpeed.y = 0f;
                    }
                    else    //either go right or go left are pressed so we change the state to walk
                    {
                        mState = Constants.CHARACTER_STATE_RUN;
                        mSpeed.y = 0.0f;
                    }
                }

                break;
            case Constants.CHARACTER_STATE_DIE:

                break;
        }

        updateAnimation();

        UpdatePhysics(delta);

        if (mPushedBottom && !mPushesBottom)
            mFramesFromJumpStart = 0;
    }

    public void setInputs(boolean[] inputs, boolean[] prevInputs) {
        this.mInputs = inputs;
        this.mPrevInputs = prevInputs;
    }
}
