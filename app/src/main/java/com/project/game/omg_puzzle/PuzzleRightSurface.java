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
    private PuzzleRightThead rightThread;
    private volatile boolean running = false;
    private int grab = -1;

    /** Puzzle and Canvas **/
    private int MAX_PUZZLE_PIECE_SIZE = 120;
    private int LOCK_ZONE_LEFT = 60;
    private int LOCK_ZONE_TOP = 20;

    private int outsize_y;

    private JigsawPuzzle puzzle;

    private BitmapDrawable backgroundImage;
    private Bitmap[] originalPieces;
    private BitmapDrawable[] scaledSurfacePuzzlePieces;

    private Paint framePaint;
    private Context mcontext;

    private static boolean[] ispieceLocked;       //與PuzzleCompactSurface共用
    private static boolean[] isgrabed;
    private static boolean[] isPieceSelected;

    private List<Integer> al;     //填入亂數

    private  int x_down ;
    private  int x_up ;
    private  int y_down ;
    private int y_up ;



    public PuzzleRightSurface(Context context) {
        super(context);
        mcontext = context;

        getHolder().addCallback(this); //利用getHolder()取得SurfaceHolder的引用對象

        rightThread = new PuzzleRightThead(getHolder(), context, this);


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

        if (!hasWindowFocus) rightThread.pause();
        Log.d("PuzzleSurface", "onWindowsFocusChanged");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) { //在surface的大小發生改變時
        rightThread.setSurfaceSize(width, height);  // 1184,720
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //在創建時，一般在這裡調用畫圖的線程。
        rightThread.setRunning(true);
        rightThread.startPuzzle();
        rightThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//銷毀時，一般在這裡將畫圖的線程停止、釋放。
        boolean retry = true;
        rightThread.setRunning(false);
        while (retry) {
            try {
                rightThread.join();
                retry = false;
                Log.d("Puzzle_Right", "surfaceDestroyed()");
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
        outsize_y = outSize.y;

        framePaint = new Paint();
        framePaint.setColor(Color.RED);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(10);
        framePaint.setTextSize(20);

        /** Initialize drawables from puzzle pieces **/
        originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();
        ispieceLocked = new boolean[originalPieces.length];
        isgrabed = new boolean[originalPieces.length];
        isPieceSelected = new boolean[originalPieces.length];

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage = new BitmapDrawable(puzzle.getBackgroundTexture());
            backgroundImage.setBounds(-53, -70, 400, outsize_y+80);
        }

        Random_puzzle();

        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];

        for (int i = 0; i < originalPieces.length; i++) { //originalPieces.length = 12

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(getResources(),originalPieces[i]);

            ispieceLocked[i] = false;
            isgrabed[i] = false;
            isPieceSelected[i] = false;
            // Top left is (0,0) in Android canvas

            int topLeftX = LOCK_ZONE_LEFT;
            int topLeftY = 180* al.get(i)+ LOCK_ZONE_TOP;

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
            topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);
            //scaledSurfacePuzzlePieces[i].setBounds(0, 0,0,0);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(canvas == null){
            return;
        }
        canvas.drawColor(Color.WHITE); //

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage.draw(canvas);    //畫背景
        }

        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (!ispieceLocked[bmd] && !isgrabed[bmd] && isPieceSelected[bmd]) {
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

                    if (place.contains(xPos, yPos) && ! ispieceLocked[i]) {
                        grab = i;
                        //按下拼圖且未被鎖定，即觸發的事件 → 拼圖被撿起
                        puzzle.onJigsawEventPieceGrabbed(grab, place.left, place.top);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: //手指放在屏幕上，在這裡不讓使用者拖曳

                break;
            case MotionEvent.ACTION_UP:
                x_up = xPos;
                y_up = yPos;
                Log.d("touch", "found = "+ grab);

                if(x_down!=0 && y_down!=0){
                    if(y_down - y_up > 100 && Math.abs(x_down - x_up) < 200 ) {
                        Log.d("touch", "upup"); //往上滑
                        for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                            Rect rect = scaledSurfacePuzzlePieces[i].copyBounds();
                            if((rect.top <= LOCK_ZONE_TOP+ 180*0) &&
                                    rect.bottom <= LOCK_ZONE_TOP+ 180*0+MAX_PUZZLE_PIECE_SIZE){
                                Log.d("touch" , "up over");
                                rect.top = 180*(scaledSurfacePuzzlePieces.length-1)+ LOCK_ZONE_TOP;
                                rect.bottom = 180*(scaledSurfacePuzzlePieces.length-1)+ LOCK_ZONE_TOP+MAX_PUZZLE_PIECE_SIZE;
                            }else{
                                rect.top -= 180;
                                rect.bottom -= 180;
                            }

                            scaledSurfacePuzzlePieces[i].setBounds(rect);
                        }
                    }
                    else if(y_down - y_up < -120 && Math.abs(x_down - x_up) < 200 ) {
                        Log.d("touch", "down"); //往下滑
                        for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                            Rect rect = scaledSurfacePuzzlePieces[i].copyBounds();
                            if((rect.top >= LOCK_ZONE_TOP+ 180* (scaledSurfacePuzzlePieces.length-1)) &&
                                    rect.bottom >= LOCK_ZONE_TOP+ 180* (scaledSurfacePuzzlePieces.length-1)+MAX_PUZZLE_PIECE_SIZE){
                               Log.d("touch" , "over");
                                rect.top = 180* 0+ LOCK_ZONE_TOP;
                                rect.bottom = 180* 0+ LOCK_ZONE_TOP+MAX_PUZZLE_PIECE_SIZE;
                            }else{
                                rect.top += 180;
                                rect.bottom += 180;
                            }

                            scaledSurfacePuzzlePieces[i].setBounds(rect);
                        }
                    }else if(Math.abs(y_down - y_up) < 8){
                        if(grab > -1){
                            isgrabed[grab] = true;
                        }
                    }
                }
                break;
        }

        return true;
    }

    public PuzzleRightThead getThread () {
        return rightThread;
    }

    public static void setPieceLocked(int locked, boolean isLocked){
        if (locked >= 0 && locked < ispieceLocked.length) {
            ispieceLocked[locked] = isLocked;
        }
    }

    public static boolean isPieceLocked(int piece) {
        if (piece >= 0 && piece < ispieceLocked.length) {
            return ispieceLocked[piece];
        } else {
            return false;
        }
    }

    public static void setGrab(int grab, boolean isGrabed){
        if(grab >= 0 && grab < isgrabed.length)
        isgrabed[grab] = isGrabed;
    }

    public static boolean isPieceGrab(int piece) {
        if (piece >= 0 && piece < isgrabed.length) {
            return isgrabed[piece];
        } else {
            return false;

        }
    }

    public void Random_puzzle(){
        Random r = new Random();
        al=new ArrayList<Integer>();
        while(al.size()<originalPieces.length){ //總共originalPieces.length

            int n=r.nextInt(originalPieces.length);
            Log.d("random" , n+"");
            Log.d("random1" , String.valueOf(al));
            if(al.contains(n))
                continue;     //重複的不加入
            else {
                al.add(n);
                Log.d("random2" , String.valueOf(al));
            }
            Log.d("random3" , String.valueOf(al));
        }
        Log.d("random" , "bye random_puzzle");

    }

    public void setFolderPieceShow(int[] folderPieceShow){

        for(int i=0; i<isPieceSelected.length;i++){
            isPieceSelected[i]=false;
            for(int k=0; k<folderPieceShow.length;k++){
                if(i==folderPieceShow[k]){
                    isPieceSelected[i]=true;
                }
            }
        }

        int count = 0;
        for(int j=0;j<isPieceSelected.length;j++){
            if(isPieceSelected[j]) {
                int topLeftX = LOCK_ZONE_LEFT;
                int topLeftY = 180 * count + LOCK_ZONE_TOP;

                scaledSurfacePuzzlePieces[j].setBounds(topLeftX, topLeftY,
                        topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);
                count++;
            }else if(!isPieceSelected[j]){
                scaledSurfacePuzzlePieces[j].setBounds(0,0,0,0);
            }

        }
        Log.d("byebye", "setFolderPieceShow");
    }


}