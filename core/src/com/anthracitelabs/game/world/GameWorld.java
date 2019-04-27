package com.anthracitelabs.game.world;

import com.anthracitelabs.game.objects.CollisionData;
import com.anthracitelabs.game.objects.GameObject;
import com.anthracitelabs.game.objects.MovingObject;
import com.anthracitelabs.game.objects.Player;
import com.anthracitelabs.game.objects.Projectile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameWorld {

    private Array<GameObject> mActiveGameObjects;
    private Array<Projectile> mActiveProjectiles;
    private Player mPlayer;
    private boolean[] mInputs;
    private boolean[] mPrevInputs;

    public GameWorld(Array<GameObject> activeGameObjects, Array<Projectile> activeProjectiles, boolean[] inputs, boolean[] prevInputs) {
        mActiveGameObjects = activeGameObjects;
        mActiveProjectiles = activeProjectiles;
        mInputs = inputs;
        mPrevInputs = prevInputs;
    }

    public void init() {
        for (GameObject o : mActiveGameObjects) {
            if (o instanceof Player) {
                mPlayer = (Player)o;
                mPlayer.setInputs(mInputs, mPrevInputs);
                break;
            }
        }
    }

    public void updatePlay(float delta) {
        for (GameObject o : mActiveGameObjects) {
            o.update(delta);
            if (o instanceof MovingObject)
                ((MovingObject)o).mAllCollidingObjects.clear();
        }

        CheckCollisions();

        for (GameObject o : mActiveGameObjects) {
            if (o instanceof MovingObject)
                ((MovingObject)o).UpdatePhysicsP2(delta);
        }
    }

    public void pause() {

    }

    public void gameOver() {

    }

    public void levelComplete() {

    }

    public void gameComplete() {

    }

    public void CheckCollisions()
    {
        for (int i = 0; i < mActiveGameObjects.size - 1; ++i)
        {
            GameObject obj1 = mActiveGameObjects.get(i);
            if (obj1 instanceof MovingObject == false)
                continue;

            for (int j = i + 1; j < mActiveGameObjects.size; ++j)
            {
                GameObject obj2 = mActiveGameObjects.get(j);
                if (obj2 instanceof MovingObject == false)
                    continue;

                Vector2 overlap = new Vector2();
                Vector2 overlap2 = new Vector2();

                if (((MovingObject)obj1).OverlapsSigned(obj2.mAABB, overlap) && !((MovingObject)obj1).HasCollisionDataFor((MovingObject) obj2))
                {
                    //System.out.println(obj1 + " collided with " + obj2);

                    ((MovingObject)obj1).mAllCollidingObjects.add(new CollisionData((MovingObject)obj2, overlap, obj1.mSpeed, obj2.mSpeed, obj1.mOldPosition, obj2.mOldPosition, obj1.mPosition, obj2.mPosition));
                    overlap2.x = -overlap.x;
                    overlap2.y = -overlap.y;
                    ((MovingObject)obj2).mAllCollidingObjects.add(new CollisionData((MovingObject)obj1, overlap2, obj2.mSpeed, obj1.mSpeed, obj2.mOldPosition, obj1.mOldPosition, obj2.mPosition, obj1.mPosition));
                }
            }
        }
    }

    public void swapObjects(GameObject obj, GameObject platform) {
        int objIndex = mActiveGameObjects.indexOf(obj, false);
        int platformIndex = mActiveGameObjects.indexOf(platform, false);

        if (platformIndex > objIndex)
            mActiveGameObjects.swap(objIndex, platformIndex);
    }
}
