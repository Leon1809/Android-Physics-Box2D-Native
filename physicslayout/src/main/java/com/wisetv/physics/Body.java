package com.wisetv.physics;

import android.util.Log;

import com.wisetv.physicslayout.Physics;

/**
 * Created by Administrator on 2016/9/7.
 */
public class Body {
    public static String TAG = "Body";
    private long mNativePointer = 0;

    public Body(long nativePointer) {
        if(Physics.NATIVE) {
            mNativePointer = nativePointer;
        }
    }

    public long getNative() {
        return mNativePointer;
    }

    public void setAngularVelocity(float av) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2setAngularVelocity(mNativePointer, av);
            } else {
                Log.e(TAG, "native null in setAngularVelocity!");
            }
        } else {
        }
    }

    public float getAngularVelocity() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getAngularVelocity(mNativePointer);
            } else {
                Log.e(TAG, "native null in getAngularVelocity!");
            }
            return 0.0f;
        } else {
            return 0;
        }
    }

    public void setLinearVelocity(float lvx, float lvy) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2setLinearVelocity(mNativePointer, lvx, lvy);
            } else {
                Log.e(TAG, "native null in setLinearVelocity!");
            }
        } else {
        }
    }

    public float getLinearVelocityX() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getLinearVelocityX(mNativePointer);
            } else {
                Log.e(TAG, "native null in getLinearVelocityX!");
            }
            return 0.0f;
        } else {
            return 0;
         }
    }

    public float getLinearVelocityY() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getLinearVelocityY(mNativePointer);
            } else {
                Log.e(TAG, "native null in getLinearVelocityY!");
            }
            return 0.0f;
        } else {
            return 0;
        }
    }

    public void setAwake(boolean b) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2setAwake(mNativePointer, b);
            } else {
                Log.e(TAG, "native null in setAwake!");
            }
        } else {
         }
    }

    public float getAngle() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getAngle(mNativePointer);
            } else {
                Log.e(TAG, "native null in getAngle!");
                return 0.0f;
            }
        } else {
            return 0;
        }
    }

    public float getPosX() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getPosX(mNativePointer);
            } else {
                Log.e(TAG, "native null in getPosX!");
                return 0.0f;
            }
        } else {
            return 0;
        }
    }

    public float getPosY() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getPosY(mNativePointer);
            } else {
                Log.e(TAG, "native null in getPosY!");
                return 0.0f;
            }
        } else {
            return 0;
         }
    }

    public float getAngularDamping() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getAngularDamping(mNativePointer);
            } else {
                Log.e(TAG, "native null in getAngularDamping!");
            }
            return 0.0f;
        } else {
            return 0;
        }
    }

    public float getLinearDamping() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2getLinearDamping(mNativePointer);
            } else {
                Log.e(TAG, "native null in getLinearDamping!");
            }
            return 0.0f;
        } else {
            return 0;
        }
    }

    public void applyLinearImpulse(float impulseX, float impulseY, float posX, float posY) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2applyLinearImpulse(mNativePointer, impulseX, impulseY, posX, posY);
            } else {
                Log.e(TAG, "native null in applyLinearImpulse!");
            }
        } else {
        }
    }

    public void applyAngularImpulse(float impulse) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2applyAngularImpulse(mNativePointer, impulse);
            } else {
                Log.e(TAG, "native null in applyAngularImpulse!");
            }
        } else {
        }
    }

    public void setTransform(float posX, float posY, float angle) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                b2setTransform(mNativePointer, posX, posY, angle);
            } else {
                Log.e(TAG, "native null in setTransform!");
            }
        } else {
        }
    }

    public void createFixture(FixtureDef fixture) {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                if (fixture != null && fixture.shape != 0) {
                    b2createFixture(mNativePointer,
                            fixture.shape,
                            fixture.userData,
                            fixture.friction,
                            fixture.restitution,
                            fixture.density,
                            fixture.isSensor);//Filter absence
                }
            } else {
                Log.e(TAG, "native null in createFixture!");
            }
        } else {
//            org.jbox2d.dynamics.FixtureDef fix = new org.jbox2d.dynamics.FixtureDef();
//            fix.density = fixture.density;
//            fix.shape
//            internal.createFixture(fix);
        }
    }

    public float printfInfo() {
        if(Physics.NATIVE) {
            if (mNativePointer != 0) {
                return b2printfInfo(mNativePointer);
            }
            return 0.0f;
        } else {
            return 0;
        }
    }

    public native float b2printfInfo(long pointer);

    public native long test(long l, float f);

    public native void b2createFixture(long pointer,
                                       long shapePtr,
                                       int userData,
                                       float friction,
                                       float restitution,
                                       float density,
                                       boolean isSensor);//Filter absence

    public native void b2setTransform(long pointer, float posX, float posY, float angle);

    public native void b2applyLinearImpulse(long pointer, float impulseX, float impulseY, float posX, float posY);

    public native void b2applyAngularImpulse(long pointer, float impulse);

    public native void b2setAngularVelocity(long pointer, float av);

    public native float b2getAngularVelocity(long pointer);

    public native void b2setLinearVelocity(long pointer, float lvx, float lvy);

    public native float b2getLinearVelocityX(long pointer);

    public native float b2getLinearVelocityY(long pointer);

    public native void b2setAwake(long pointer, boolean b);

    public native float b2getAngle(long pointer);

    public native float b2getPosX(long pointer);

    public native float b2getPosY(long pointer);

    public native float b2getAngularDamping(long pointer);

    public native float b2getLinearDamping(long pointer);
}
