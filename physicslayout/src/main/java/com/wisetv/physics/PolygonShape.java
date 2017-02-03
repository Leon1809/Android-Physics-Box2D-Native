package com.wisetv.physics;

import android.util.Log;

/**
 * Created by Administrator on 2016/9/7.
 */
public class PolygonShape {
    public static String TAG = "PolygonShape";

    long mNativePointer;

    public PolygonShape() {
        mNativePointer = b2genPolygonShape();
        if(mNativePointer == 0) {
            Log.e(TAG, "PolygonShape native create failed");
        }
    }

    public long getNative() {
        return mNativePointer;
    }

    public void setAsBox(float w, float h) {
        if(mNativePointer != 0) {
            b2setAsBox(mNativePointer, w, h);
        } else {
            Log.e(TAG, "native null in setAsBox!");
        }
    }

    public native long b2genPolygonShape();

    public native void b2setAsBox(long pointer, float w, float h);
}
