package com.wisetv.physics;

//import org.jbox2d.collision.shapes.Shape;
//import org.jbox2d.dynamics.Filter;

/**
 * Created by Administrator on 2016/9/7.
 */
public class FixtureDef {
    public long shape = 0;//pointer to Shape in C++, public Shape shape = null;

    public int userData;

    public float friction;

    public float restitution;

    public float density;

    public boolean isSensor;

    //public Filter filter;

}
