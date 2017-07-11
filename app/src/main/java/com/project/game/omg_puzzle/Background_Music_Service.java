package com.project.game.omg_puzzle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

public class Background_Music_Service extends Service implements MediaPlayer.OnPreparedListener,
                                                                MediaPlayer.OnErrorListener,
                                                                MediaPlayer.OnCompletionListener,
                                                                AudioManager.OnAudioFocusChangeListener{

    public static final String
            ACTION_PLAY = "tw.android.mediaplayer.action.PLAY",
            ACTION_PAUSE = "tw.android.mediaplayer.action.PAUSE",
            ACTION_SET_REPEAT = "tw.android.mediaplayer.action.SET_REPEAT",
            ACTION_CANCEL_REPEAT = "tw.android.mediaplayer.action.CANCEL_REPEAT",
            ACTION_GOTO = "tw.android.mediaplayer.action.GOTO";

    private MediaPlayer mMediaPlayer = null;

    private boolean mbIsInitialised = true;

    public Background_Music_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.song);

        try {
            mMediaPlayer.setDataSource(this, uri);

        } catch (Exception e) {
            Toast.makeText(this, "指定的音樂檔錯誤！", Toast.LENGTH_LONG)
                    .show();
        }

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY))

            if (mbIsInitialised) {
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setLooping(true);
                mbIsInitialised = false;//update
            }
            else
                mMediaPlayer.start();

        else if (intent.getAction().equals(ACTION_PAUSE))
            mMediaPlayer.pause();
        else if (intent.getAction().equals(ACTION_SET_REPEAT))
            mMediaPlayer.setLooping(true);
        else if (intent.getAction().equals(ACTION_CANCEL_REPEAT))
            mMediaPlayer.setLooping(false);
        else if (intent.getAction().equals(ACTION_GOTO)) {
            int seconds = intent.getIntExtra("GOTO_POSITION_SECONDS", 0);
            mMediaPlayer.seekTo(seconds * 1000); // 以毫秒（千分之一秒）為單位
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        if (mMediaPlayer == null)
            return;

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // 程式取得聲音播放權
                mMediaPlayer.setVolume(0.8f, 0.8f);
                mMediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // 程式尚失聲音播放權，而且時間可能很久
                stopSelf();		// 結束這個Service
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // 程式尚失聲音播放權，但預期很快就會再取得
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 程式尚失聲音播放權，但是可以用很小的音量繼續播放
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
       // mMediaPlayer.release();
      //  mMediaPlayer = null;

        //mbIsInitialised = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.release();
        mMediaPlayer = null;

        Toast.makeText(this, "發生錯誤，停止播放", Toast.LENGTH_LONG)
                .show();

        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        AudioManager audioMgr =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int r = audioMgr.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (r != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            mMediaPlayer.setVolume(0.1f, 0.1f);	// 降低音量

        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
