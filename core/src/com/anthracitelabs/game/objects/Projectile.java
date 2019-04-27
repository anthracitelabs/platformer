package com.anthracitelabs.game.objects;

import com.badlogic.gdx.utils.Pool;

public class Projectile extends GameObject implements Pool.Poolable {

    public Projectile() {
        super();
    }

    /**
     * Callback method when the object is freed. It is automatically called by Pool.free()
     * Must reset every meaningful field of this bullet.
     */
    @Override
    public void reset() {
        mAABB.set(0f, 0f, 0f, 0f);
        mPosition.set(0, 0);
        mOldPosition.set(0, 0);
        mSpeed.set(0, 0);
        mOldSpeed.set(0, 0);
        alive = false;
    }
}
