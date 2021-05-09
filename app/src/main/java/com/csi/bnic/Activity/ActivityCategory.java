package com.csi.bnic.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.csi.bnic.Insurance.Car.ActivityCarEntry;
import com.csi.bnic.Insurance.Motorcycle.ActivityMotorcycleEntry;
import com.csi.bnic.R;

public class ActivityCategory extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout linearLayoutBikeInsurance,linearLayoutCarInsurance;
    Animation animationLeftEnter,animationRightEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initToolBar();
        initUI();
        animationLeftEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_enter);
        animationRightEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_enter);
        linearLayoutBikeInsurance.setAnimation(animationLeftEnter);
        linearLayoutCarInsurance.setAnimation(animationRightEnter);

        linearLayoutBikeInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCategory.this,ActivityMotorcycleEntry.class));
            }
        });
        linearLayoutCarInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCategory.this,ActivityCarEntry.class));
            }
        });
    }

    private void initUI() {
        linearLayoutBikeInsurance = (LinearLayout) findViewById(R.id.linearBikeInsurance);
        linearLayoutCarInsurance = (LinearLayout) findViewById(R.id.linearCarInsurance);
    }

    private void initToolBar() {
        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.onlineInsurance);
        toolbar.setBackgroundColor(getResources().getColor(R.color.md_yellow_800));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
