package com.wisetv.physics;

/**
 * Created by Administrator on 2016/9/7.
 */
public class BodyDef {
    public int type;
    public float posX;
    public float posY;

    public Object userData;
    public float angle;

    public float linearVelocityX;
    public float linearVelocityY;
    public float angularVelocity;

    public float linearDamping;
    public float angularDamping;

    public boolean allowSleep;

    public boolean awake;

    public boolean fixedRotation;

    public boolean bullet;

    public boolean active;

    public float gravityScale;
}
