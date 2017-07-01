package com.project.game.omg_puzzle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleActivity extends FragmentActivity
        implements Puzzle_main.OnFragmentInteractionListener, Puzzle_Right.OnFragmentInteractionListener{

    private static PuzzleCompactSurface puzzleSurface;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    FloatingActionButton fab5;
    FloatingActionButton fab6;
    FloatingActionButton fab7;
    FloatingActionButton fab8;

    TextView userId, scores;

    CoordinatorLayout rootLayout;

    Animation show_fab_1;
    Animation hide_fab_1;

    private boolean FAB_Status = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        Intent it;
        it = new Intent(PuzzleActivity.this, Background_Music_Service.class);
        it.setAction(Background_Music_Service.ACTION_PLAY);
        startService(it);

        setContentView(R.layout.activity_puzzle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab_4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab_5);
        fab6 = (FloatingActionButton) findViewById(R.id.fab_6);
        fab7 = (FloatingActionButton) findViewById(R.id.fab_7);
        fab8 = (FloatingActionButton) findViewById(R.id.fab_8);

        userId = (TextView)findViewById(R.id.user_id);
        scores = (TextView)findViewById(R.id.scores);

        userId.setText("珍紅茶妮妮");
        scores.setText("0/100");

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FAB_Status == false) {
                    //Display FAB menu
                    Log.d("fab", "down");
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    Log.d("fab", "up");
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "ImageShow.class", Toast.LENGTH_SHORT).show();
                Intent it = new Intent();
                it.setClass(PuzzleActivity.this, ImageShow.class);
                startActivity(it);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "SettingShow.class", Toast.LENGTH_SHORT).show();
                Intent it = new Intent();
                it.setClass(PuzzleActivity.this, SettingShow.class);
                startActivity(it);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 3", Toast.LENGTH_SHORT).show();
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 4", Toast.LENGTH_SHORT).show();
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 5", Toast.LENGTH_SHORT).show();
            }
        });

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 6", Toast.LENGTH_SHORT).show();
            }
        });
        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 7", Toast.LENGTH_SHORT).show();
            }
        });

        fab8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Floating Action Button 8", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("test", "PuzzleActivity_onCreated");



    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.leftMargin += 200;
        layoutParams.bottomMargin += 10;
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.leftMargin += 350;
        layoutParams2.bottomMargin += 10;
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_1);
        fab2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.leftMargin += 500;
        layoutParams3.bottomMargin += 10;
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_1);
        fab3.setClickable(true);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.leftMargin += 650;
        layoutParams4.bottomMargin += 10;
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(show_fab_1);
        fab4.setClickable(true);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.leftMargin += 800;
        layoutParams5.bottomMargin += 10;
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(show_fab_1);
        fab5.setClickable(true);

        FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) fab6.getLayoutParams();
        layoutParams6.leftMargin += 950;
        layoutParams6.bottomMargin += 10;
        fab6.setLayoutParams(layoutParams6);
        fab6.startAnimation(show_fab_1);
        fab6.setClickable(true);

        FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) fab7.getLayoutParams();
        layoutParams7.leftMargin += 1100;
        layoutParams7.bottomMargin += 10;
        fab7.setLayoutParams(layoutParams7);
        fab7.startAnimation(show_fab_1);
        fab7.setClickable(true);

        FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) fab8.getLayoutParams();
        layoutParams8.leftMargin += 1250;
        layoutParams8.bottomMargin += 10;
        fab8.setLayoutParams(layoutParams8);
        fab8.startAnimation(show_fab_1);
        fab8.setClickable(true);


    }


    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.leftMargin -= 200;
        layoutParams.bottomMargin -= 10;
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.leftMargin -= 350;
        layoutParams2.bottomMargin -= 10;
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_1);
        fab2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.leftMargin -= 500;
        layoutParams3.bottomMargin -= 10;
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_1);
        fab3.setClickable(false);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.leftMargin -= 650;
        layoutParams4.bottomMargin -= 10;
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(hide_fab_1);
        fab4.setClickable(true);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.leftMargin -= 800;
        layoutParams5.bottomMargin -= 10;
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(hide_fab_1);
        fab5.setClickable(true);

        FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) fab6.getLayoutParams();
        layoutParams6.leftMargin -= 950;
        layoutParams6.bottomMargin -= 10;
        fab6.setLayoutParams(layoutParams6);
        fab6.startAnimation(hide_fab_1);
        fab6.setClickable(true);

        FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) fab7.getLayoutParams();
        layoutParams7.leftMargin -= 1100;
        layoutParams7.bottomMargin -= 10;
        fab7.setLayoutParams(layoutParams7);
        fab7.startAnimation(hide_fab_1);
        fab7.setClickable(true);

        FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) fab8.getLayoutParams();
        layoutParams8.leftMargin -= 1250;
        layoutParams8.bottomMargin -= 10;
        fab8.setLayoutParams(layoutParams8);
        fab8.startAnimation(hide_fab_1);
        fab8.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent it;
        it = new Intent(PuzzleActivity.this, Background_Music_Service.class);
        stopService(it);


    }
}
