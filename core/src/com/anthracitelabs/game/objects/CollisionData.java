package com.anthracitelabs.game.objects;

import com.badlogic.gdx.math.Vector2;

public class CollisionData {

    public CollisionData(MovingObject other, Vector2 overlap, Vector2 speed1, Vector2 speed2, Vector2 oldPos1, Vector2 oldPos2, Vector2 pos1, Vector2 pos2)
    {
        this.other = other;
        this.overlap = overlap;
        this.speed1 = speed1;
        this.speed2 = speed2;
        this.oldPos1 = oldPos1;
        this.oldPos2 = oldPos2;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public MovingObject other;
    public Vector2 overlap;
    public Vector2 speed1, speed2;
    public Vector2 oldPos1, oldPos2, pos1, pos2;
}
