package com.wisetv.physicslayout;

import android.support.annotation.IntDef;

import com.wisetv.physics.BodyDef;//import org.jbox2d.dynamics.BodyDef;
//import org.jbox2d.dynamics.BodyType;
import com.wisetv.physics.FixtureDef;//import org.jbox2d.dynamics.FixtureDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PhysicsConfig {

    public static final int SHAPE_TYPE_RECTANGLE = 0;
    public static final int SHAPE_TYPE_CIRCLE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHAPE_TYPE_RECTANGLE, SHAPE_TYPE_CIRCLE})
    public @interface ShapeType {}

    public static PhysicsConfig create() {
        PhysicsConfig config = new PhysicsConfig();
        config.shapeType = SHAPE_TYPE_RECTANGLE;
        config.fixtureDef = createDefaultFixtureDef();
        config.bodyDef = createDefaultBodyDef();
        config.radius = -1;
        return config;
    }

    public static FixtureDef createDefaultFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.density = 0.2f;
        return fixtureDef;
    }

    public static BodyDef createDefaultBodyDef() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = 2;
        return bodyDef;
    }

    public @ShapeType int shapeType;
    public FixtureDef fixtureDef;
    public BodyDef bodyDef;

    public float radius;

    private PhysicsConfig() {

    }
}
