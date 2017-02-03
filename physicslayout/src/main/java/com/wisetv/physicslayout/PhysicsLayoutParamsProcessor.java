package com.wisetv.physicslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.jawnnypoo.physicslayout.R;

public class PhysicsLayoutParamsProcessor {


    public static PhysicsConfig process(Context c, AttributeSet attrs) {
        PhysicsConfig config = PhysicsConfig.create();
        TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.Physics_Layout);
        processCustom(array, config);
        processBodyDef(array, config);
        processFixtureDef(array, config);
        array.recycle();
        return config;
    }

    private static void processCustom(TypedArray array, PhysicsConfig config) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_shape)) {
            int shape = array.getInt(R.styleable.Physics_Layout_layout_shape, PhysicsConfig.SHAPE_TYPE_RECTANGLE);
            config.shapeType = shape;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_circleRadius)) {
            int radius = array.getDimensionPixelSize(R.styleable.Physics_Layout_layout_circleRadius, -1);
            config.radius = radius;
        }
    }

    private static void processBodyDef(TypedArray array, PhysicsConfig config) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_bodyType)) {
            config.bodyDef.type = 2;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_fixedRotation)) {
            boolean fixedRotation = array.getBoolean(R.styleable.Physics_Layout_layout_fixedRotation, false);
            config.bodyDef.fixedRotation = fixedRotation;
        }
    }

    private static void processFixtureDef(TypedArray array, PhysicsConfig config) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_friction)) {
            float friction = array.getFloat(R.styleable.Physics_Layout_layout_friction, -1);
            config.fixtureDef.friction = friction;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_restitution)) {
            float restitution = array.getFloat(R.styleable.Physics_Layout_layout_restitution, -1);
            config.fixtureDef.restitution = restitution;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_density)) {
            float density = array.getFloat(R.styleable.Physics_Layout_layout_density, -1);
            config.fixtureDef.density = density;
        }
    }
}
