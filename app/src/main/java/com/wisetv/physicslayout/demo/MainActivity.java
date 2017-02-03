package com.wisetv.physicslayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jawnnypoo.physicslayout.sample.R;
import com.wisetv.physicslayout.Physics;
import com.wisetv.physicslayout.PhysicsFrameLayout;
import com.squareup.picasso.Picasso;

import com.wisetv.physics.Body;
import com.wisetv.physics.World;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("wisephysics");
    }

    private static final String TAG = "TESTING";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout physicsLayout;
    @Bind(R.id.physics_switch)
    SwitchCompat physicsSwitch;
    @Bind(R.id.fling_switch)
    SwitchCompat flingSwitch;
    @Bind(R.id.impulse_button)
    View impulseButton;
    @Bind(R.id.add_view_button)
    View addViewButton;
    @Bind(R.id.collision)
    TextView collisionView;


    int catIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Body body = new Body(0);
        long testPointer = body.test(250, 108.6f);
        Log.e("Leon", "testPointer : " + testPointer);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu_main);

        physicsSwitch.setChecked(physicsLayout.getPhysics().isPhysicsEnabled());
        physicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsLayout.getPhysics().enablePhysics();
                } else {
                    physicsLayout.getPhysics().disablePhysics();
                    for (int i=0; i<physicsLayout.getChildCount(); i++) {
                        physicsLayout.getChildAt(i)
                                .animate().translationY(0).translationX(0).rotation(0);
                    }
                }
            }
        });
        flingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsLayout.getPhysics().enableFling();
                } else {
                    physicsLayout.getPhysics().disableFling();
                }
            }
        });
        impulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                physicsLayout.getPhysics().giveRandomImpulse();
            }
        });
        final View circleView = findViewById(R.id.circle);
        addViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(R.drawable.ic_logo);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.square_size),
                        getResources().getDimensionPixelSize(R.dimen.square_size));
                imageView.setLayoutParams(llp);
                imageView.setId(catIndex);
                physicsLayout.addView(imageView);
                Picasso.with(MainActivity.this)
                        .load("http://lorempixel.com/200/200/cats/" + ((catIndex % 10) + 1))
                        .placeholder(R.drawable.ic_logo)
                        .into(imageView);
                catIndex++;
            }
        });


        for (int i=0; i<physicsLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) physicsLayout.getChildAt(i);
            imageView.setId(i);
            Picasso.with(this)
                    .load("http://lorempixel.com/200/200/cats/" + (i + 1))
                    .placeholder(R.drawable.ic_logo)
                    .into(imageView);
        }
        catIndex = physicsLayout.getChildCount();

        physicsLayout.getPhysics().addOnPhysicsProcessedListener(new Physics.OnPhysicsProcessedListener() {
            @Override
            public void onPhysicsProcessed(Physics physics, World world) {
                Body body = physics.findBodyById(circleView.getId());
                if (body != null) {
                    body.applyAngularImpulse(0.001f);
                } else {
                    Log.e(TAG, "Cannot rotate, body was null");
                }
            }
        });

        physicsLayout.getPhysics().setOnBodyCreatedListener(new Physics.OnBodyCreatedListener() {
            @Override
            public void onBodyCreated(View view, Body body) {
                Log.d(TAG, "Body created for view " + view.getId());
            }
        });
    }
}
