package com.project.game.omg_puzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.SurfaceHolder;

/**
 * Created by 16JUNE29 on 2017/6/14.
 */

public class PuzzleRightThead extends Thread {

    private SurfaceHolder surfaceHolder;
    private PuzzleRightSurface rsurface;
    private boolean running = false;
    private Context puzzleContext;

    private int gameState;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_RUNNING = 2;

    private int puzzleSurfaceWidth = 1;
    private int puzzleSurfaceHeight = 1;


    public PuzzleRightThead(SurfaceHolder holder, Context context, PuzzleRightSurface puzzleSurface) {

        surfaceHolder = holder;
        rsurface = puzzleSurface;
        puzzleContext = context;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    public void startPuzzle() {
        synchronized (surfaceHolder) {
            setState(STATE_RUNNING);
        }
    }

    public void pause() {
        synchronized (surfaceHolder) {
            if (gameState == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    public synchronized void restoreState(Bundle savedState) {
        synchronized (surfaceHolder) {
            setState(STATE_PAUSE);
        }
    }

    public void setState(int stateToSet) {
        synchronized (surfaceHolder) {
            // TODO Message Handling
        }
    }

    public Bundle saveState(Bundle map) {
        synchronized (surfaceHolder) {
            if (map != null) {


            }
        }
        return map;
    }

    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (surfaceHolder) {
            puzzleSurfaceWidth = width;
            puzzleSurfaceHeight = height;
        }
    }

    public void unpause() {
        setState(STATE_RUNNING);
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas=null;
            try {
                canvas = surfaceHolder.lockCanvas(null);   //鎖定畫布

                synchronized (surfaceHolder) {
                    rsurface.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);   //結束鎖定畫布

                }
            }
        }
    }
}
