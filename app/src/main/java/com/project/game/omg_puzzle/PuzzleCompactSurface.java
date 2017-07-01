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

import java.util.Random;

public class PuzzleCompactSurface extends SurfaceView implements SurfaceHolder.Callback {
                                                       //SurfaceHolder.Callback是監聽surface改變的一個接口
    /** Surface Components **/
    private PuzzleThread gameThread;
    private volatile boolean running = false;
    private int found = -1;

    /** Puzzle and Canvas **/
    private int MAX_PUZZLE_PIECE_SIZE = 165;
    private int LOCK_ZONE_LEFT = 180;
    private int LOCK_ZONE_TOP = 65;

    private JigsawPuzzle puzzle;

    private BitmapDrawable backgroundImage;
    private BitmapDrawable[] scaledSurfacePuzzlePieces;
    private Rect[] scaledSurfaceTargetBounds;

    private Paint framePaint;
    private Context mcontext;

    private int outsize_y;

    private  int x_down ;
    private  int x_up ;
    private  int y_down ;
    private int y_up ;

    private static boolean[] ispieceGrab;

    public PuzzleCompactSurface(Context context) {
        super(context);
        mcontext = context;
        getHolder().addCallback(this); //利用getHolder()取得SurfaceHolder的引用對象

        gameThread = new PuzzleThread(getHolder(), context, this);



        setFocusable(true);
        Log.d("test", "PuzzleCompactSurface_constructor");
    }

    public PuzzleCompactSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PuzzleCompactSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) { //這個Activity得到或者失去焦點的時候就會call

        if (!hasWindowFocus) gameThread.pause();

        Log.d("PuzzleSurface", "onWindowsFocusChanged");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) { //在surface的大小發生改變時

        gameThread.setSurfaceSize(width, height);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //在創建時，一般在這裡調用畫圖的線程。
        gameThread.setRunning(true);
        gameThread.start();
        Log.d("test", "PuzzleCompactSurface_surfaceCreated");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//銷毀時，一般在這裡將畫圖的線程停止、釋放。
       boolean retry = true;
        gameThread.setRunning(false);
        while (retry) {
            try {
                gameThread.join();
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

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage = new BitmapDrawable(puzzle.getBackgroundTexture());
            backgroundImage.setBounds(0, 0, 800, 600);

        }
        Random r = new Random();

        outsize_y = outSize.y;

        framePaint = new Paint();
        framePaint.setColor(Color.BLACK);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setTextSize(20);

        /** Initialize drawables from puzzle pieces **/
        Bitmap[] originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();

        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];
        scaledSurfaceTargetBounds = new Rect[originalPieces.length];
        ispieceGrab = new boolean[originalPieces.length];

        for (int i = 0; i < originalPieces.length; i++) { //originalPieces.length = 12

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(getResources(),originalPieces[i]);
            // Top left is (0,0) in Android canvas
            int topLeftX = 1500;
            int topLeftY = r.nextInt(outsize_y - 2*MAX_PUZZLE_PIECE_SIZE);

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
                    topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);  //畫成100 100
        }

        for (int w = 0; w < dimensions[2]; w++) {
            for (int h = 0; h < dimensions[3]; h++) {
                int targetPiece = positions[w][h];
               // Log.d("targetPiece ", String.valueOf( positions[w][h]));
                ispieceGrab[targetPiece] = false;
                scaledSurfaceTargetBounds[targetPiece] = new Rect(
                        LOCK_ZONE_LEFT + w*MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_TOP + h*MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_LEFT + w*MAX_PUZZLE_PIECE_SIZE + MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_TOP + h*MAX_PUZZLE_PIECE_SIZE + MAX_PUZZLE_PIECE_SIZE);


            }
        }
        Log.d("test", "PuzzleCompactSurface_setPuzzle");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas == null){
           return;
            }
         canvas.drawARGB(255,255,255,144); //

           backgroundImage.setBounds(0,0,1950,outsize_y);
        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage.draw(canvas);    //畫背景
        }
            for (int h = 0; h < scaledSurfaceTargetBounds.length; h++) {

                canvas.drawRect(scaledSurfaceTargetBounds[h], framePaint);
            }

        //canvas.drawRect(20, 20, 420, 320, framePaint);

        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (puzzle.isPieceLocked(bmd)) {
                scaledSurfacePuzzlePieces[bmd].draw(canvas);
            }
        }

        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (!puzzle.isPieceLocked(bmd) && ispieceGrab[bmd] ) {
                Log.d("draww" , "piece = " + bmd + " isGrab = " + String.valueOf(ispieceGrab[bmd]));
                    scaledSurfacePuzzlePieces[bmd].draw(canvas);

            }

        }
        Log.d("test", "ondraw");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xPos =(int) event.getX();
        int yPos =(int) event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //手指按下去

                for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                    Rect place = scaledSurfacePuzzlePieces[i].copyBounds();

                    if (place.contains(xPos, yPos) && !puzzle.isPieceLocked(i) && ispieceGrab[i]) {
                        found = i;
                        //按下拼圖且未被鎖定，即觸發的事件 → 拼圖被撿起
                        puzzle.onJigsawEventPieceGrabbed(found, place.left, place.top);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: //手指放在屏幕上

                if (found >= 0 && found < scaledSurfacePuzzlePieces.length && !puzzle.isPieceLocked(found)) {
                    puzzle.onJigsawEventPieceMoved(found,
                            scaledSurfacePuzzlePieces[found].copyBounds().left,
                            scaledSurfacePuzzlePieces[found].copyBounds().top);
                    puzzle.onJigsawEventPieceDropped(found,
                            scaledSurfacePuzzlePieces[found].copyBounds().left,
                            scaledSurfacePuzzlePieces[found].copyBounds().top);

                    Rect rect = scaledSurfacePuzzlePieces[found].copyBounds();

                    if(xPos >=1620 || yPos >=980){ //防止使用者超出邊界
                        rect.left += 0;
                        rect.top += 0;
                        rect.right += 0;
                        rect.bottom += 0;
                    }else{
                        rect.left = xPos - MAX_PUZZLE_PIECE_SIZE/2;
                        rect.top = yPos - MAX_PUZZLE_PIECE_SIZE/2;
                        rect.right = xPos + MAX_PUZZLE_PIECE_SIZE/2;
                        rect.bottom = yPos + MAX_PUZZLE_PIECE_SIZE/2;
                    }

                    scaledSurfacePuzzlePieces[found].setBounds(rect);

                }
                break;

            case MotionEvent.ACTION_UP: //手指離開屏幕
                x_up = xPos;
                y_up = yPos;

                if (found >= 0 && found < scaledSurfacePuzzlePieces.length && !puzzle.isPieceLocked(found)) {
                    if (scaledSurfaceTargetBounds[found].contains(xPos, yPos)) {
                        scaledSurfacePuzzlePieces[found].setBounds(scaledSurfaceTargetBounds[found]);
                        puzzle.setPieceLocked(found, true);
                        PuzzleRightSurface.setPieceLocked(found, true);
                    }
                }


                if (found >= 0 && found < scaledSurfacePuzzlePieces.length) {
                    puzzle.onJigsawEventPieceDropped(found, xPos, yPos);
                    if(x_up > 1620){
                        ispieceGrab[found] = false;
                        PuzzleRightSurface.setGrab(found, false);
                        Random r = new Random();
                        int topLeftX = 1500;
                        int topLeftY = r.nextInt(outsize_y - 2*MAX_PUZZLE_PIECE_SIZE);

                        scaledSurfacePuzzlePieces[found].setBounds(topLeftX, topLeftY,
                                topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);

                    }
                    found = -1;
                }

                break;

        }

        return true;
    }

    public PuzzleThread getThread () {
        return gameThread;
    }
    public static void setGrab(int grab, boolean isgrab){
       ispieceGrab[grab] = isgrab;
        Log.d("touch", "grab = " + grab + " isgrab = " + isgrab );}
}
