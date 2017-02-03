package com.wisetv.physicslayout;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.commit451.translationviewdraghelper.TranslationViewDragHelper;

//import org.jbox2d.callbacks.ContactImpulse;
//import org.jbox2d.callbacks.ContactListener;
//import org.jbox2d.collision.Manifold;
import com.jawnnypoo.physicslayout.R;
import com.wisetv.physics.CircleShape;//import org.jbox2d.collision.shapes.CircleShape;
import com.wisetv.physics.PolygonShape;//import org.jbox2d.collision.shapes.PolygonShape;
//import org.jbox2d.common.Vec2;
import com.wisetv.physics.Body;//import org.jbox2d.dynamics.Body;
import com.wisetv.physics.BodyDef;//import org.jbox2d.dynamics.BodyDef;
//import org.jbox2d.dynamics.BodyType;
import com.wisetv.physics.FixtureDef;//import org.jbox2d.dynamics.FixtureDef;
import com.wisetv.physics.World;//import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Random;

public class Physics {

    public static final boolean NATIVE = true;

    private static final String TAG = Physics.class.getSimpleName();

    public static final float EARTH_GRAVITY = 9.8f;

    private static final int BOUND_SIZE_DP = 20;
    private static final float FRAME_RATE = 1 / 60f;

    public static void setPhysicsConfig(@NonNull View view, @Nullable PhysicsConfig config) {
        view.setTag(R.id.physics_layout_config_tag, config);
    }

    private boolean debugDraw = false;
    private boolean debugLog = false;

    private int velocityIterations = 8;
    private int positionIterations = 3;
    private float pixelsPerMeter;
    private float boundsSize;

    private World world;
    private ArrayList<Body> bounds = new ArrayList<Body>();
    private float gravityX = 0.0f;
    private float gravityY = EARTH_GRAVITY;
    private boolean enablePhysics = true;
    private boolean hasBounds = true;
    private boolean allowFling = false;

    private ViewGroup viewGroup;
    private Paint debugPaint;
    private float density;
    private int width;
    private int height;
    private TranslationViewDragHelper viewDragHelper;
    private View viewBeingDragged;

    private OnFlingListener onFlingListener;
    private ArrayList<OnPhysicsProcessedListener> onPhysicsProcessedListeners;
    private OnBodyCreatedListener onBodyCreatedListener;

    private final TranslationViewDragHelper.Callback viewDragHelperCallback = new TranslationViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            viewBeingDragged = capturedChild;
            Body body = (Body) viewBeingDragged.getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                body.setAngularVelocity(0);
                body.setLinearVelocity(0, 0);
            }

            if (onFlingListener != null) {
                onFlingListener.onGrabbed(capturedChild);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            viewBeingDragged = null;
            Body body = (Body) releasedChild.getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                translateBodyToView(body, releasedChild);
                body.setLinearVelocity(pixelsToMeters(xvel), pixelsToMeters(yvel));
                body.setAwake(true);
            }
            if (onFlingListener != null) {
                onFlingListener.onReleased(releasedChild);
            }
        }
    };

    public float metersToPixels(float meters) {
        return meters * pixelsPerMeter;
    }

    public float pixelsToMeters(float pixels) {
        return pixels / pixelsPerMeter;
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

    public Physics(ViewGroup viewGroup) {
        this(viewGroup, null);
    }

    public Physics(ViewGroup viewGroup, AttributeSet attrs) {
        this.viewGroup = viewGroup;
        viewDragHelper = TranslationViewDragHelper.create(viewGroup, 1.0f, viewDragHelperCallback);
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setStyle(Paint.Style.STROKE);
        density = viewGroup.getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray a = viewGroup.getContext()
                    .obtainStyledAttributes(attrs, R.styleable.Physics);
            enablePhysics = a.getBoolean(R.styleable.Physics_physics, enablePhysics);
            gravityX = a.getFloat(R.styleable.Physics_gravityX, gravityX);
            gravityY = a.getFloat(R.styleable.Physics_gravityY, gravityY);
            hasBounds = a.getBoolean(R.styleable.Physics_bounds, hasBounds);
            boundsSize = a.getDimension(R.styleable.Physics_boundsSize, BOUND_SIZE_DP * density);
            allowFling = a.getBoolean(R.styleable.Physics_fling, allowFling);
            velocityIterations = a
                    .getInt(R.styleable.Physics_velocityIterations, velocityIterations);
            positionIterations = a
                    .getInt(R.styleable.Physics_positionIterations, positionIterations);
            pixelsPerMeter = a.getFloat(R.styleable.Physics_pixelsPerMeter, viewGroup.getResources().getDimensionPixelSize(R.dimen.physics_layout_dp_per_meter));
            Log.e(TAG, "JAVA gX : " + gravityX + ", gY : " + gravityY + ", hasBounds : " + hasBounds + ", " + boundsSize
                    + ", velocityIter : " + velocityIterations + ", posIter : " + positionIterations + ", pixPMeter : " + pixelsPerMeter);
            a.recycle();
        }
    }

    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void onLayout(boolean changed) {
        if (debugLog) {
            Log.d(TAG, "onLayout");
        }
        createWorld();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!allowFling) {
            return false;
        }
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!allowFling) {
            return false;
        }
        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    public void onDraw(Canvas canvas) {
        if (!enablePhysics) {
            return;
        }
        world.step(FRAME_RATE, velocityIterations, positionIterations);
        View view;
        Body body;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);
            body = (Body) view.getTag(R.id.physics_layout_body_tag);

            if (view == viewBeingDragged) {
                if (body != null) {
                    translateBodyToView(body, view);
                    view.setRotation(radiansToDegrees(body.getAngle()) % 360);
                }
                continue;
            }

            if (body != null) {
                view.setX(metersToPixels(body.getPosX()) - view.getWidth() / 2);
                view.setY(metersToPixels(body.getPosY()) - view.getHeight() / 2);
                view.setRotation(radiansToDegrees(body.getAngle()) % 360);

                if (debugDraw) {
                    PhysicsConfig config = (PhysicsConfig) view.getTag(R.id.physics_layout_config_tag);
                    if (config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE) {
                        canvas.drawRect(metersToPixels(body.getPosX()) - view.getWidth() / 2,
                                metersToPixels(body.getPosY()) - view.getHeight() / 2,
                                metersToPixels(body.getPosX()) + view.getWidth() / 2,
                                metersToPixels(body.getPosY()) + view.getHeight() / 2, debugPaint);
                    } else if (config.shapeType == PhysicsConfig.SHAPE_TYPE_CIRCLE) {
                        canvas.drawCircle(
                                metersToPixels(body.getPosX()),
                                metersToPixels(body.getPosY()),
                                config.radius,
                                debugPaint);
                    }
                }
            }
        }
        if (onPhysicsProcessedListeners != null) {
            for (int i = 0; i < onPhysicsProcessedListeners.size(); i++) {
                onPhysicsProcessedListeners.get(i).onPhysicsProcessed(this, world);
            }
        }
        viewGroup.invalidate();
    }

    public void createWorld() {
        ArrayList<Body> oldBodiesArray = new ArrayList<Body>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            Body body = (Body) viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                oldBodiesArray.add(body);
            } else {
                oldBodiesArray.add(null);
            }
            viewGroup.getChildAt(i).setTag(R.id.physics_layout_body_tag, null);
        }
        bounds.clear();
        if (debugLog) {
            Log.d(TAG, "createWorld");
        }
        world = new World(gravityX, gravityY);
        Log.i("Leon", "hasBounds : " + hasBounds);
        if (hasBounds) {
            enableBounds();
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            Body body = createBody(viewGroup.getChildAt(i), oldBodiesArray.get(i));
            if (onBodyCreatedListener != null) {
                onBodyCreatedListener.onBodyCreated(viewGroup.getChildAt(i), body);
            }
        }
    }

    private void enableBounds() {
        hasBounds = true;
        createTopAndBottomBounds();
        createLeftAndRightBounds();
    }

    private void disableBounds() {
        hasBounds = false;
        for (Body body : bounds) {
            world.destroyBody(body);
        }
        bounds.clear();
    }

    private void createTopAndBottomBounds() {
        int boundSize = Math.round(boundsSize);
        int boxWidth = (int) pixelsToMeters(width);
        int boxHeight = (int) pixelsToMeters(boundSize);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = 0;

        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth, boxHeight);
        Log.e(TAG, "JAVA tb box w : " + boxWidth + ", h : " + boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box.getNative();
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.userData = R.id.physics_layout_bound_top;
        bodyDef.posX = 0; bodyDef.posY = -boxHeight;
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        //////////
        float fric = topBody.printfInfo();
        Log.e(TAG, "fric from C : " + fric);
        //////////
        bounds.add(topBody);

        fixtureDef.userData = R.id.physics_layout_body_bottom;
        bodyDef.posX = 0; bodyDef.posY = pixelsToMeters(height) + boxHeight;
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bounds.add(bottomBody);
    }

    private void createLeftAndRightBounds() {
        int boundSize = Math.round(boundsSize);
        int boxWidth = (int) pixelsToMeters(boundSize);
        int boxHeight = (int) pixelsToMeters(height);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = 0;

        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth, boxHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box.getNative();
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.userData = R.id.physics_layout_body_left;
        bodyDef.posX = -boxWidth; bodyDef.posY = 0;
        Body leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);
        bounds.add(leftBody);

        fixtureDef.userData = R.id.physics_layout_body_right;
        bodyDef.posX = pixelsToMeters(width) + boxWidth; bodyDef.posY = 0;
        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
        bounds.add(rightBody);
    }

    private Body createBody(View view, Body oldBody) {
        PhysicsConfig config = (PhysicsConfig) view.getTag(R.id.physics_layout_config_tag);
        if (config == null) {
            if (view.getLayoutParams() instanceof PhysicsLayoutParams) {
                config = ((PhysicsLayoutParams) view.getLayoutParams()).getConfig();
            }
            if (config == null) {
                config = PhysicsConfig.create();
            }
            view.setTag(R.id.physics_layout_config_tag, config);
        }
        BodyDef bodyDef = config.bodyDef;
        bodyDef.posX = pixelsToMeters(view.getX() + view.getWidth() / 2);
        bodyDef.posY = pixelsToMeters(view.getY() + view.getHeight() / 2);

        if (oldBody != null) {
            bodyDef.angle = oldBody.getAngle();
            bodyDef.angularVelocity = oldBody.getAngularVelocity();
            bodyDef.linearVelocityX = oldBody.getLinearVelocityX();
            bodyDef.linearVelocityY = oldBody.getLinearVelocityY();
            bodyDef.angularDamping = oldBody.getAngularDamping();
            bodyDef.linearDamping = oldBody.getLinearDamping();
        } else {
            bodyDef.angularVelocity = degreesToRadians(view.getRotation());
        }

        FixtureDef fixtureDef = config.fixtureDef;
        fixtureDef.shape = config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE
                ? createBoxShape(view).getNative() : createCircleShape(view, config).getNative();
        fixtureDef.userData = view.getId();

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        view.setTag(R.id.physics_layout_body_tag, body);
        return body;
    }

    private PolygonShape createBoxShape(View view) {
        PolygonShape box = new PolygonShape();
        float boxWidth = pixelsToMeters(view.getWidth() / 2);
        float boxHeight = pixelsToMeters(view.getHeight() / 2);
        box.setAsBox(boxWidth, boxHeight);
        return box;
    }

    private CircleShape createCircleShape(View view, PhysicsConfig config) {
        CircleShape circle = new CircleShape();
        if (config.radius == -1) {
            config.radius = Math.max(view.getWidth() / 2, view.getHeight() / 2);
        }
        circle.setRadius(pixelsToMeters(config.radius));
        return circle;
    }

    @Nullable
    public Body findBodyById(int id) {
        View view = viewGroup.findViewById(id);
        if (view != null) {
            return (Body) view.getTag(R.id.physics_layout_body_tag);
        }
        return null;
    }

    public World getWorld() {
        return world;
    }

    public void enablePhysics() {
        enablePhysics = true;
        viewGroup.invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
    }

    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }

    public void giveRandomImpulse() {
        Body body;
        Random random = new Random();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            body = (Body) viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag);
            body.applyLinearImpulse(random.nextInt(1000) - 1000, random.nextInt(1000) - 1000, body.getPosX(), body.getPosY());
        }
    }

    private void translateBodyToView(@NonNull Body body, @NonNull View view) {
        body.setTransform(pixelsToMeters(view.getX() + view.getWidth() / 2),
                        pixelsToMeters(view.getY() + view.getHeight() / 2),
                body.getAngle());
    }

    public void enableFling() {
        allowFling = true;
    }

    public void disableFling() {
        allowFling = false;
    }

    public boolean isFlingEnabled() {
        return allowFling;
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.onFlingListener = onFlingListener;
    }

    public void setBoundsSize(float size) {
        boundsSize = size * density;

        if (hasBounds()) {
            disableBounds();
        }
        enableBounds();
    }

    public void setHasBounds(boolean hasBounds) {
        this.hasBounds = hasBounds;
    }

    public boolean hasBounds() {
        return hasBounds;
    }

    public void setGravityX(float newGravityX) {
        setGravity(newGravityX, gravityY);
    }

    public float getGravityX() {
        return gravityX;
    }

    public void setGravityY(float newGravityY) {
        setGravity(gravityX, newGravityY);
    }

    public float getGravityY() {
        return gravityY;
    }

    public void setGravity(float gravityX, float gravityY) {
        this.gravityX = gravityX;
        this.gravityY = gravityY;
        world.setGravity(gravityX, gravityY);
    }

    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public int getVelocityIterations() {
        return velocityIterations;
    }

    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }

    public int getPositionIterations() {
        return positionIterations;
    }

    public void setPixelsPerMeter(float pixelsPerMeter) {
        this.pixelsPerMeter = pixelsPerMeter;
    }

    public float getPixelsPerMeter() {
        return pixelsPerMeter;
    }

    public void addOnPhysicsProcessedListener(OnPhysicsProcessedListener listener) {
        if (onPhysicsProcessedListeners == null) {
            onPhysicsProcessedListeners = new ArrayList<OnPhysicsProcessedListener>();
        }
        onPhysicsProcessedListeners.add(listener);
    }

    public void removeOnPhysicsProcessedListener(OnPhysicsProcessedListener listener) {
        if (onPhysicsProcessedListeners != null) {
            onPhysicsProcessedListeners.remove(listener);
        }
    }

    public void setOnBodyCreatedListener(OnBodyCreatedListener listener) {
        this.onBodyCreatedListener = listener;
    }

    public interface OnPhysicsProcessedListener {

        void onPhysicsProcessed(Physics physics, World world);
    }

    public interface OnFlingListener {
        void onGrabbed(View grabbedView);

        void onReleased(View releasedView);
    }

    public interface OnBodyCreatedListener {

        void onBodyCreated(View view, Body body);
    }
}
