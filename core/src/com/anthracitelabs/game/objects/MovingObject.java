package com.anthracitelabs.game.objects;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.map.TiledMapManager;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import com.anthracitelabs.game.world.GameWorld;

public class MovingObject extends GameObject {

    public ArrayList<CollisionData> mAllCollidingObjects = new ArrayList<CollisionData>();
    public boolean mIsKinematic = false;

    public boolean mPushesRight = false;
    public boolean mPushesLeft = false;
    public boolean mPushesBottom = false;
    public boolean mPushesTop = false;
    public boolean mPushedTop = false;
    public boolean mPushedBottom = false;
    public boolean mPushedRight = false;
    public boolean mPushedLeft = false;

    public boolean mPushesLeftObject = false;
    public boolean mPushesRightObject = false;
    public boolean mPushesBottomObject = false;
    public boolean mPushesTopObject = false;
    public boolean mPushedLeftObject = false;
    public boolean mPushedRightObject = false;
    public boolean mPushedBottomObject = false;
    public boolean mPushedTopObject = false;

    protected boolean mPushedRightWall;
    protected boolean mPushesRightWall;
    protected boolean mPushedLeftWall;
    protected boolean mPushesLeftWall;
    protected boolean mWasOnGround;
    protected boolean mOnGround;
    protected boolean mOnOneWayPlatform = false;
    protected boolean mWasAtCeiling;
    protected boolean mAtCeiling;

    public MovingObject mMountParent = null;
    public Vector2 mOffset;

    private float groundY = 0;
    private float ceilingY = 0;
    private float wallXLeft = 0;
    private float wallXRight = 0;

    private TiledMapManager mMapManager;

    // temporary Vector objects to save memory - ground check
    private Vector2 tempBottomLeft;
    private Vector2 tempBottomRight;
    private Vector2 tempOldBottomLeft;

    // temporary Vector objects to save memory - ceiling check
    private Vector2 tempTopLeft;
    private Vector2 tempTopRight;
    private Vector2 tempOldTopRight;

    // temporary Vector objects to save memory - left wall check
    Vector2 tempBottomLeftForSide;
    Vector2 tempOldBottomLeftForSide;

    // temporary Vector objects to save memory - right wall check
    Vector2 tempBottomRightForSide;
    Vector2 tempOldBottomRightForSide;

    private GameWorld mGameWorld;

    public MovingObject(TiledMapManager mapManager, GameWorld world) {
        super();
        mMapManager = mapManager;
        mGameWorld = world;

        tempBottomLeft = new Vector2();
        tempBottomRight = new Vector2();
        tempTopLeft = new Vector2();
        tempTopRight = new Vector2();
        tempOldBottomLeft = new Vector2();
        tempOldTopRight = new Vector2();
        tempBottomLeftForSide = new Vector2();
        tempOldBottomLeftForSide = new Vector2();
        tempBottomRightForSide = new Vector2();
        tempOldBottomRightForSide = new Vector2();

        mOffset = new Vector2();
    }

    public void UpdatePhysics(float deltaTime)
    {
        //assign the previous state of onGround, atCeiling, pushesRightWall, pushesLeftWall
        //before those get recalculated for this frame
        mWasOnGround = mOnGround;
        mPushedRightWall = mPushesRightWall;
        mPushedLeftWall = mPushesLeftWall;
        mWasAtCeiling = mAtCeiling;
        mPushedBottom = mPushesBottom;
        mPushedRight = mPushesRight;
        mPushedLeft = mPushesLeft;
        mPushedTop = mPushesTop;

        mPushesRightWall = false;
        mPushesLeftWall = false;
        mOnOneWayPlatform = false;

        mOldPosition.x = mPosition.x;
        mOldPosition.y = mPosition.y;
        mOldSpeed.x = mSpeed.x;
        mOldSpeed.y = mSpeed.y;

        mOffset.x = mSpeed.x * deltaTime;
        mOffset.y = mSpeed.y * deltaTime;

        if (mMountParent != null)
        {
            if (HasCollisionDataFor(mMountParent)) {
                mOffset.x += mMountParent.mPosition.x - mMountParent.mOldPosition.x;
                mOffset.y += mMountParent.mPosition.y - mMountParent.mOldPosition.y;
            }
            else
                mMountParent = null;
        }

        mPosition.x += mOffset.x;
        mPosition.y += mOffset.y;

        // left wall collision
        if (mSpeed.x <= 0.0f
                && CollidesWithLeftWall())
        {
            if (mOldPosition.x >= wallXLeft)
            {
                mPosition.x = wallXLeft;
                mPushesLeftWall = true;
            }
            mSpeed.x = Math.max(mSpeed.x, 0.0f);
        }
        else
            mPushesLeftWall = false;

        // right wall collision
        if (mSpeed.x >= 0.0f
                && CollidesWithRightWall())
        {
            if (mOldPosition.x + 2 * mAABB.mHalfSizeX <= wallXRight)
            {
                mPosition.x = wallXRight - 2 * mAABB.mHalfSizeX;
                mPushesRightWall = true;
            }

            mSpeed.x = Math.min(mSpeed.x, 0.0f);
        }
        else
            mPushesRightWall = false;

        // check ground
        groundY = 0;
        if (mSpeed.y <= 0.0f
                && HasGround())
        {
            mPosition.y = groundY;
            mSpeed.y = 0.0f;
            mOnGround = true;
        }
        else
            mOnGround = false;

        // ceiling collision
        if (mSpeed.y >= 0.0f
                && HasCeiling())
        {
            mPosition.y = ceilingY - 2 * mAABB.mHalfSizeY - 1.0f;
            mSpeed.y = 0.0f;
            mAtCeiling = true;
        }
        else
            mAtCeiling = false;

        //mPosition.x = Math.round(mPosition.x);
        mPosition.y = Math.round(mPosition.y);

        //update the aabb
        mAABB.mCenterX = mPosition.x + mAABB.mHalfSizeX;
        mAABB.mCenterY = mPosition.y + mAABB.mHalfSizeY;

    }

    public boolean HasGround()
    {
        mOnOneWayPlatform = false;

        float oldBottomLeftX = mOldPosition.x + 1.0f;
        float oldBottomLeftY = mOldPosition.y - 1.0f;
        float newBottomLeftX = mPosition.x + 1.0f;
        float newBottomLeftY = mPosition.y - 1.0f;

        int endY = mMapManager.GetMapTileYAtPoint(newBottomLeftY);
        int begY = Math.max(mMapManager.GetMapTileYAtPoint(oldBottomLeftY) - 1, endY);
        int dist = Math.max(Math.abs(endY - begY), 1);

        int tileIndexX;

        for (int tileIndexY = begY; tileIndexY >= endY; --tileIndexY)
        {
            tempBottomLeft.set(newBottomLeftX, newBottomLeftY);
            tempOldBottomLeft.set(oldBottomLeftX, oldBottomLeftY);
            tempBottomLeft.lerp(tempOldBottomLeft, (float)Math.abs(endY - tileIndexY) / dist);
            tempBottomRight.set(tempBottomLeft.x + mAABB.mHalfSizeX * 2.0f - 2.0f, tempBottomLeft.y);

            for (float checkedTileX = tempBottomLeft.x; ; checkedTileX += Constants.TILE_SIZE)
            {
                checkedTileX = Math.min(checkedTileX, tempBottomRight.x);

                tileIndexX = mMapManager.GetMapTileXAtPoint(checkedTileX);

                groundY = (float)tileIndexY * Constants.TILE_SIZE + Constants.TILE_SIZE;

                if (mMapManager.IsObstacle(tileIndexX, tileIndexY))
                {
                    mOnOneWayPlatform = false;
                    return true;
                }
                else if (mMapManager.IsOneWayPlatform(tileIndexX, tileIndexY)
                        && Math.abs(tempBottomLeft.y - groundY) <= Constants.ONE_WAY_PLATFORM_THRESHOLD + mOldPosition.y - mPosition.y)
                    mOnOneWayPlatform = true;

                if (checkedTileX >= tempBottomRight.x)
                {
                    if (mOnOneWayPlatform)
                        return true;
                    break;
                }
            }
        }

        return false;
    }

    public boolean HasCeiling() {
        ceilingY = 0.0f;

        float oldTopRightX = mOldPosition.x + 2 * mAABB.mHalfSizeX - 1.0f;
        float oldTopRightY = mOldPosition.y + 2 * mAABB.mHalfSizeY + 1.0f;

        float newTopRightX = mPosition.x + 2 * mAABB.mHalfSizeX - 1.0f;
        float newTopRightY = mPosition.y + 2 * mAABB.mHalfSizeY + 1.0f;

        int endY = mMapManager.GetMapTileYAtPoint(newTopRightY);
        int begY = Math.min(mMapManager.GetMapTileYAtPoint(oldTopRightY) + 1, endY);
        int dist = Math.max(Math.abs(endY - begY), 1);

        int tileIndexX;

        for (int tileIndexY = begY; tileIndexY <= endY; ++tileIndexY)
        {
            tempTopRight.set(newTopRightX, newTopRightY);
            tempOldTopRight.set(oldTopRightX, oldTopRightY);
            tempTopRight.lerp(tempOldTopRight, (float)Math.abs(endY - tileIndexY) / dist);
            tempTopLeft.set(tempTopRight.x - mAABB.mHalfSizeX * 2.0f + 2.0f, tempTopRight.y);

            for (float checked_x = tempTopLeft.x; ; checked_x += Constants.TILE_SIZE) {
                checked_x = Math.min(checked_x, tempTopRight.x);

                tileIndexX = mMapManager.GetMapTileXAtPoint(checked_x);

                if (mMapManager.IsObstacle(tileIndexX, tileIndexY))
                {
                    ceilingY = (float)tileIndexY * Constants.TILE_SIZE;
                    return true;
                }

                if (checked_x >= tempTopRight.x)
                    break;
            }
        }

        return false;
    }

    public boolean CollidesWithLeftWall() {
        wallXLeft = 0.0f;

        float oldBottomLeftX = mOldPosition.x - 1.0f;
        float oldBottomLeftY = mOldPosition.y;

        float newBottomLeftX = mPosition.x - 1.0f;
        float newBottomLeftY = mPosition.y;

        int tileIndexY;

        int endX = mMapManager.GetMapTileXAtPoint(newBottomLeftX);
        int begX = Math.max(mMapManager.GetMapTileXAtPoint(oldBottomLeftX) - 1, endX);
        int dist = Math.max(Math.abs(endX - begX), 1);

        for (int tileIndexX = begX; tileIndexX >= endX; --tileIndexX)
        {
            tempBottomLeftForSide.set(newBottomLeftX, newBottomLeftY);
            tempOldBottomLeftForSide.set(oldBottomLeftX, oldBottomLeftY);
            tempBottomLeftForSide.lerp(tempOldBottomLeftForSide, (float)Math.abs(endX - tileIndexX) / dist);

            float topLeft_y = tempBottomLeftForSide.y + mAABB.mHalfSizeY * 2.0f;

            for (float checkedTile_y = tempBottomLeftForSide.y; ; checkedTile_y += Constants.TILE_SIZE)
            {
                checkedTile_y = Math.min(checkedTile_y, topLeft_y);

                tileIndexY = mMapManager.GetMapTileYAtPoint(checkedTile_y);

                if (mMapManager.IsObstacle(tileIndexX, tileIndexY))
                {
                    wallXLeft = (float)tileIndexX * Constants.TILE_SIZE + Constants.TILE_SIZE;
                    return true;
                }

                if (checkedTile_y >= topLeft_y)
                    break;
            }
        }

        return false;
    }

    public boolean CollidesWithRightWall() {
        wallXRight = 0.0f;

        float oldBottomRightX = mOldPosition.x + 2 * mAABB.mHalfSizeX + 1.0f;
        float oldBottomRightY = mOldPosition.y;

        float newBottomRightX = mPosition.x + 2 * mAABB.mHalfSizeX + 1.0f;
        float newBottomRightY = mPosition.y;

        int endX = mMapManager.GetMapTileXAtPoint(newBottomRightX);
        int begX = Math.min(mMapManager.GetMapTileXAtPoint(oldBottomRightX) + 1, endX);
        int dist = Math.max(Math.abs(endX - begX), 1);

        int tileIndexY;

        for (int tileIndexX = begX; tileIndexX <= endX; ++tileIndexX)
        {
            tempBottomRightForSide.set(newBottomRightX, newBottomRightY);
            tempOldBottomRightForSide.set(oldBottomRightX, oldBottomRightY);
            tempBottomRightForSide.lerp(tempOldBottomRightForSide, (float)Math.abs(endX - tileIndexX) / dist);

            float topRight_y = tempBottomRightForSide.y + mAABB.mHalfSizeY * 2.0f;

            for (float checkedTile_y = tempBottomRightForSide.y; ; checkedTile_y += Constants.TILE_SIZE)
            {
                checkedTile_y = Math.min(checkedTile_y, topRight_y);

                tileIndexY = mMapManager.GetMapTileYAtPoint(checkedTile_y);

                if (mMapManager.IsObstacle(tileIndexX, tileIndexY))
                {
                    wallXRight = (float)tileIndexX * Constants.TILE_SIZE;
                    return true;
                }

                if (checkedTile_y >= topRight_y)
                    break;
            }
        }

        return false;
    }

    public boolean OverlapsSigned(AABB other, Vector2 overlap)
    {
        overlap.x = 0;
        overlap.y = 0;

        if (mAABB.mHalfSizeX == 0.0f || mAABB.mHalfSizeY == 0.0f || other.mHalfSizeX == 0.0f || other.mHalfSizeY == 0.0f
                || Math.abs(mAABB.mCenterX - other.mCenterX) > mAABB.mHalfSizeX + other.mHalfSizeX
                || Math.abs(mAABB.mCenterY - other.mCenterY) > mAABB.mHalfSizeY + other.mHalfSizeY)
            return false;

        float signX = Math.signum(mAABB.mCenterX - other.mCenterX);
        float signY = Math.signum(mAABB.mCenterY - other.mCenterY);
        overlap.x = (signX == 0? 1.0f : signX) * ((other.mHalfSizeX + mAABB.mHalfSizeX) - Math.abs(mAABB.mCenterX - other.mCenterX));
        overlap.y = (signY == 0? 1.0f : signY) * ((other.mHalfSizeY + mAABB.mHalfSizeY) - Math.abs(mAABB.mCenterY - other.mCenterY));

        if (overlap.x == 0 && overlap.y == 0)
            return false;

        return true;
    }

    public boolean HasCollisionDataFor(MovingObject other)
    {
        for (int i = 0; i < mAllCollidingObjects.size(); ++i)
        {
            if (mAllCollidingObjects.get(i).other == other)
                return true;
        }

        return false;
    }

    public void UpdatePhysicsP2(float deltaTime)
    {
        mPosition.x -= mOffset.x;
        mPosition.y -= mOffset.y;

        //mPosition.x = Math.round(mPosition.x);
        mPosition.y = Math.round(mPosition.y);

        //update the aabb
        mAABB.mCenterX = mPosition.x + mAABB.mHalfSizeX;
        mAABB.mCenterY = mPosition.y + mAABB.mHalfSizeY;

        UpdatePhysicsResponse();

        mPosition.x += mOffset.x;
        mPosition.y += mOffset.y;

        //mPosition.x = Math.round(mPosition.x);
        mPosition.y = Math.round(mPosition.y);

        mPushesBottom = mOnGround || mPushesBottomObject;
        mPushesRight = mPushesRightWall || mPushesRightObject;
        mPushesLeft = mPushesLeftWall || mPushesLeftObject;
        mPushesTop = mAtCeiling || mPushesTopObject;

        //update the aabb
        mAABB.mCenterX = mPosition.x + mAABB.mHalfSizeX;
        mAABB.mCenterY = mPosition.y + mAABB.mHalfSizeY;
    }

    private void UpdatePhysicsResponse()
    {
        if (mIsKinematic)
            return;

        mPushedBottomObject = mPushesBottomObject;
        mPushedRightObject = mPushesRightObject;
        mPushedLeftObject = mPushesLeftObject;
        mPushedTopObject = mPushesTopObject;

        mPushesBottomObject = false;
        mPushesRightObject = false;
        mPushesLeftObject = false;
        mPushesTopObject = false;

        Vector2 offsetSum = new Vector2(0, 0);

        for (int i = 0; i < mAllCollidingObjects.size(); ++i)
        {
            MovingObject other = mAllCollidingObjects.get(i).other;
            CollisionData data = mAllCollidingObjects.get(i);
            Vector2 overlap = new Vector2(data.overlap);
            overlap.x = overlap.x - offsetSum.x;
            overlap.y = overlap.y - offsetSum.y;

            if (overlap.x == 0.0f)
            {
                if (other.mAABB.mCenterX > mAABB.mCenterX)
                {
                    mPushesRightObject = true;
                    mSpeed.x = Math.min(mSpeed.x, 0.0f);
                }
                else
                {
                    mPushesLeftObject = true;
                    mSpeed.x = Math.max(mSpeed.x, 0.0f);
                }
                continue;
            }
            else if (overlap.y == 0.0f)
            {
                if (other.mAABB.mCenterY > mAABB.mCenterY)
                {
                    mPushesTopObject = true;
                    mSpeed.y = Math.min(mSpeed.y, 0.0f);
                }
                else
                {
                    TryAutoMount(other);
                    mPushesBottomObject = true;
                    mSpeed.y = Math.max(mSpeed.y, 0.0f);
                }
                continue;
            }

            float pos1X = data.pos1.x + mAABB.mHalfSizeX;
            float pos1Y = data.pos1.y + mAABB.mHalfSizeY;
            float oldPos1X = data.oldPos1.x + mAABB.mHalfSizeX;
            float oldPos1Y = data.oldPos1.y + mAABB.mHalfSizeY;

            float pos2X = data.pos2.x + data.other.mAABB.mHalfSizeX;
            float pos2Y = data.pos2.y + data.other.mAABB.mHalfSizeY;
            float oldPos2X = data.oldPos2.x + data.other.mAABB.mHalfSizeX;
            float oldPos2Y = data.oldPos2.y + data.other.mAABB.mHalfSizeY;

            Vector2 absSpeed1 = new Vector2(Math.abs(pos1X - oldPos1X), Math.abs(pos1Y - oldPos1Y));
            Vector2 absSpeed2 = new Vector2(Math.abs(pos2X - oldPos2X), Math.abs(pos2Y - oldPos2Y));
            Vector2 speedSum = new Vector2(absSpeed1);
            speedSum.x += absSpeed2.x;
            speedSum.y += absSpeed2.y;

            float speedRatioX, speedRatioY;

            if (other.mIsKinematic)
                speedRatioX = speedRatioY = 1.0f;
            else
            {
                if (speedSum.x == 0.0f && speedSum.y == 0.0f)
                {
                    speedRatioX = speedRatioY = 0.5f;
                }
                else if (speedSum.x == 0.0f)
                {
                    speedRatioX = 0.5f;
                    speedRatioY = absSpeed1.y / speedSum.y;
                }
                else if (speedSum.y == 0.0f)
                {
                    speedRatioX = absSpeed1.x / speedSum.x;
                    speedRatioY = 0.5f;
                }
                else
                {
                    speedRatioX = absSpeed1.x / speedSum.x;
                    speedRatioY = absSpeed1.y / speedSum.y;
                }
            }

            float offsetX = overlap.x * speedRatioX;
            float offsetY = overlap.y * speedRatioY;

            boolean overlappedLastFrameX = Math.abs(oldPos1X - oldPos2X) < mAABB.mHalfSizeX + other.mAABB.mHalfSizeX;
            boolean overlappedLastFrameY = Math.abs(oldPos1Y - oldPos2Y) < mAABB.mHalfSizeY + other.mAABB.mHalfSizeY;

            if ((!overlappedLastFrameX && overlappedLastFrameY)
                    || (!overlappedLastFrameX && !overlappedLastFrameY && Math.abs(overlap.x) <= Math.abs(overlap.y)))
            {
                mOffset.x += offsetX;
                offsetSum.x += offsetX;

                if (overlap.x < 0.0f)
                {
                    mPushesRightObject = true;
                    mSpeed.x = Math.min(mSpeed.x, 0.0f);
                }
                else
                {
                    mPushesLeftObject = true;
                    mSpeed.x = Math.max(mSpeed.x, 0.0f);
                }
            }
            else //if (!overlappedLastFrameY)//if (Mathf.Abs(data.oldPos1.x - data.oldPos2.x) < mAABB.HalfSizeX + other.mAABB.HalfSizeX)
            {
                mOffset.y += offsetY;
                offsetSum.y += offsetY;

                if (overlap.y < 0.0f)
                {
                    mPushesTopObject = true;
                    mSpeed.y = Math.min(mSpeed.y, 0.0f);
                }
                else
                {
                    TryAutoMount(other);
                    mPushesBottomObject = true;
                    mSpeed.y = Math.max(mSpeed.y, 0.0f);
                }
            }
        }
    }

    public void TryAutoMount(MovingObject platform)
    {
        if (mMountParent == null)
        {
            mMountParent = platform;
            mGameWorld.swapObjects(this, platform);
        }
    }
}
