package com.project.game.omg_puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettingShow extends AppCompatActivity  implements View.OnClickListener{

    private ImageButton img_BackHome, img_ControlMusic, img_ControlMusicEffect;
    static boolean isMusicOn = true;    //已經先在PuzzleActivity裡開啟音樂

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
        setContentView(R.layout.activity_setting_show);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.95));

        img_BackHome = (ImageButton)findViewById(R.id.img_back_home);
        img_ControlMusic = (ImageButton)findViewById(R.id.img_control_music);
        img_ControlMusicEffect = (ImageButton)findViewById(R.id.img_control_MusicEffect);

        img_BackHome.setOnClickListener(this);
        img_ControlMusic.setOnClickListener(this);
        img_ControlMusicEffect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch(v.getId()) {
            case R.id.img_back_home:
                Toast.makeText(SettingShow.this, "回到首頁按鈕", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_control_music:
                Toast.makeText(SettingShow.this, "開關音樂按鈕", Toast.LENGTH_SHORT).show();
                if(!isMusicOn){ //沒有開音樂的話
                    it = new Intent(SettingShow.this, Background_Music_Service.class);
                    it.setAction(Background_Music_Service.ACTION_PLAY);
                    startService(it);
                    isMusicOn = true;
                } else if(isMusicOn){
                    it = new Intent(SettingShow.this, Background_Music_Service.class);
                    it.setAction(Background_Music_Service.ACTION_PAUSE);
                    startService(it);
                    isMusicOn = false;
                }

                break;
            case R.id.img_control_MusicEffect:
                Toast.makeText(SettingShow.this, "開關音效按鈕", Toast.LENGTH_SHORT).show();
               break;

        }
    }
}
