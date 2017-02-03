package com.wisetv.physics;

import android.util.Log;

/**
 * Created by Administrator on 2016/9/7.
 */
public class World {
    private long mNativePointer = 0;

    public static String TAG = "World";
    public World(float gravityX, float gravityY) {
        mNativePointer = b2genWorld(gravityX, gravityY);
        if(mNativePointer == 0) {
            throw new NullPointerException("native world create failed!");
        }
        Log.e(TAG, "World's mNativePointer : " + mNativePointer);
    }

    public void step(float f, int v, int p) {
        if(mNativePointer != 0) {
            b2step(mNativePointer, f, v, p, 1);
        } else {
            Log.e(TAG, "native null in step!");
        }
    }

    public Body createBody(BodyDef bd) {
        if(mNativePointer != 0) {
            long bodyPointer = b2createBody(mNativePointer,
                    bd.type,
                    bd.posX,
                    bd.posY,
                    bd.angle,
                    bd.linearVelocityX,
                    bd.linearVelocityY,
                    bd.angularVelocity,
                    bd.linearDamping,
                    bd.angularDamping,
                    bd.fixedRotation);
            if(bodyPointer == 0) {
                Log.e(TAG, "createBody native failed!");
                return null;
            }
            return new Body(bodyPointer);
        } else {
            Log.e(TAG, "world native NULL!");
            return null;
        }
    }

    public void setGravity(float gx, float gy) {
        if(mNativePointer != 0) {
            b2setGravity(mNativePointer, gx, gy);
        } else {
            Log.e(TAG, "native null in setGravity!");
        }
    }

    public void destroyBody(Body body) {
        if(mNativePointer != 0) {
            if (body.getNative() != 0) {
                b2destroyBody(mNativePointer, body.getNative());
            } else {
                Log.e(TAG, "body native null!");
            }
        } else {
            Log.e(TAG, "world native NULL");
        }
    }

    public native void b2setGravity(long pointer, float gx, float gy);

    public native long b2genWorld(float gravityX, float gravityY);

    public native void b2step(long pointer, float f, int v, int p, int pc);

    public native long b2createBody(long pointer,
                                    int type,
                                    float posX,
                                    float posY,
                                    float angle,
                                    float linearVelocityX,
                                    float linearVelocityY,
                                    float angularVelocity,
                                    float linearDamping,
                                    float angularDamping,
                                    boolean fixedRotation);

    public native void b2destroyBody(long worldPointer, long bodyPointer);
}
