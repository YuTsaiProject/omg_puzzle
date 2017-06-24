package com.project.game.omg_puzzle;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public class PuzzleActivity extends FragmentActivity
        implements Puzzle_main.OnFragmentInteractionListener, Puzzle_Right.OnFragmentInteractionListener{

    private static PuzzleCompactSurface puzzleSurface;


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


        setContentView(R.layout.activity_puzzle);
        Log.d("test", "PuzzleActivity_onCreated");


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }



}
