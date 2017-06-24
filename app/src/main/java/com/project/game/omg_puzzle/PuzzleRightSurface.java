package com.project.game.omg_puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 16JUNE29 on 2017/6/14.
 */

public class PuzzleRightSurface extends SurfaceView implements SurfaceHolder.Callback {
    /** Surface Components **/
    private PuzzleRightThead rightThead;
    private volatile boolean running = false;
    private int grab = -1;

    /** Puzzle and Canvas **/
    private int MAX_PUZZLE_PIECE_SIZE = 120;
    private int LOCK_ZONE_LEFT = 20;
    private int LOCK_ZONE_TOP = 20;

    private JigsawPuzzle puzzle;

    private  Bitmap[] originalPieces;
    private BitmapDrawable[] scaledSurfacePuzzlePieces;
    private Rect[] scaledSurfaceTargetBounds;

    private Paint framePaint;
    private Context mcontext;

    private static boolean[] ispieceLocked;
    private static boolean[] isgrabed;


    private List<Integer> al;

    private  int x_down ;
    private  int x_up ;
    private  int y_down ;
    private int y_up ;

    public PuzzleRightSurface(Context context) {
        super(context);
        mcontext = context;

        getHolder().addCallback(this); //利用getHolder()取得SurfaceHolder的引用對象

       rightThead = new PuzzleRightThead(getHolder(), context, this);


        setFocusable(true);


    }

    public PuzzleRightSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PuzzleRightSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) { //這個Activity得到或者失去焦點的時候就會call
        if (!hasWindowFocus) rightThead.pause();

        Log.d("PuzzleSurface", "onWindowsFocusChanged");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) { //在surface的大小發生改變時

        rightThead.setSurfaceSize(width, height);  // 1184,720

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //在創建時，一般在這裡調用畫圖的線程。
        rightThead.setRunning(true);
        rightThead.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//銷毀時，一般在這裡將畫圖的線程停止、釋放。
        boolean retry = true;
        rightThead.setRunning(false);
        while (retry) {
            try {
                rightThead.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    public void setPuzzle(JigsawPuzzle jigsawPuzzle) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE); //獲得窗口管理對象
        Display display = wm.getDefaultDisplay(); //獲得螢幕資訊
        Point outSize = new Point(); //負責接收size
        display.getSize(outSize);

        puzzle = jigsawPuzzle;

        framePaint = new Paint();
        framePaint.setColor(Color.RED);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(10);
        framePaint.setTextSize(20);
        // Log.d("outsizeX: ",  String.valueOf( outSize.x));
        // Log.d("outsizeY: ",  String.valueOf( outSize.y));
        /** Initialize drawables from puzzle pieces **/
        originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();
        ispieceLocked = new boolean[originalPieces.length];
        isgrabed = new boolean[originalPieces.length];


        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];
        scaledSurfaceTargetBounds = new Rect[originalPieces.length];

        Random r = new Random();
        al=new ArrayList<Integer>();
        while(al.size()<=originalPieces.length){ //總共originalPieces.length

            int n=r.nextInt(originalPieces.length);
            if(al.contains(n))
                continue;     //重複的不加入
            else
                al.add(n);
            Log.d("random" , String.valueOf(al));
        }
        for (int i = 0; i < originalPieces.length; i++) { //originalPieces.length = 12


            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(getResources(),originalPieces[i]);
            ispieceLocked[i] = false;
            isgrabed[i] = false;
            // Top left is (0,0) in Android canvas

            int topLeftX = 20;
            int topLeftY = 180* i+ LOCK_ZONE_TOP;

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
                    topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);  //畫成100 100
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(255,45,215,99); //


        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (!ispieceLocked[bmd] && !isgrabed[bmd]) {
                scaledSurfacePuzzlePieces[bmd].draw(canvas);
            }


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xPos =(int) event.getX();
        int yPos =(int) event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x_down = xPos;
                y_down = yPos;//手指按下去
                for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                    Rect place = scaledSurfacePuzzlePieces[i].copyBounds();

                    if (place.contains(xPos, yPos) && !puzzle.isPieceLocked(i)) {
                        grab = i;
                        //按下拼圖且未被鎖定，即觸發的事件 → 拼圖被撿起
                        puzzle.onJigsawEventPieceGrabbed(grab, place.left, place.top);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: //手指放在屏幕上

                break;
            case MotionEvent.ACTION_UP:
                x_up = xPos;
                y_up = yPos;
                Log.d("touch", "found = "+ grab);
                if(grab > -1){
                    PuzzleCompactSurface.setGrab(grab, true);
                    isgrabed[grab] = true;
                }
                if(x_down!=0 && y_down!=0){

                    if(y_down - y_up > 100 && Math.abs(x_down - x_up) < 200 ) {
                        Log.d("touch", "upup"); //往上滑
                        for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                            Rect rect = scaledSurfacePuzzlePieces[i].copyBounds();
                            rect.top -= 180;
                            rect.bottom -= 180;
                            scaledSurfacePuzzlePieces[i].setBounds(rect);
                        }

                    }
                    else if(y_down - y_up < -120 && Math.abs(x_down - x_up) < 200 ) {
                        Log.d("touch", "down"); //往下滑
                        for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                            Rect rect = scaledSurfacePuzzlePieces[i].copyBounds();
                            rect.top += 180;
                            rect.bottom += 180;
                            scaledSurfacePuzzlePieces[i].setBounds(rect);
                        }


                    }
                }
                break;

        }


        return true;
    }

    public PuzzleRightThead getThread () {
        return rightThead;
    }
    public static void setPieceLocked(int locked, boolean isLocked){
        ispieceLocked[locked] = isLocked;
        Log.d("touch", "lock = " + locked + " islocked = " + isLocked );}
    public static void setGrab(int grab, boolean isGrabed){
        isgrabed[grab] = isGrabed;
    }

    public void Random_puzzle(){

    }

}