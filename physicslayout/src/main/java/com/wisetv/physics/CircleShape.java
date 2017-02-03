package com.wisetv.physics;

import android.util.Log;

/**
 * Created by Administrator on 2016/9/7.
 */
public class CircleShape {
    public static String TAG = "CircleShape";

    long mNativePointer;

    public CircleShape() {
        mNativePointer = b2genCircleShape();
        if(mNativePointer == 0) {
            Log.e(TAG, "CircleShape native create failed");
        }
    }

    public long getNative() {
        return mNativePointer;
    }

    public void setRadius(float r) {
        if(mNativePointer != 0) {
            b2setRadius(mNativePointer, r);
        } else {
            Log.e(TAG, "native null in setRadius!");
        }
    }

    public native long b2genCircleShape();

    public native void b2setRadius(long pointer, float r);
}
