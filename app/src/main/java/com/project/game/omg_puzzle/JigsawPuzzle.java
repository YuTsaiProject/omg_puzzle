package com.project.game.omg_puzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class JigsawPuzzle {

    protected static int puzzleXDimension;
    protected static int puzzleYDimension;

    protected static int puzzlePieceHeight;
    protected static int puzzlePieceWidth;

    protected static int puzzleGridX;
    protected static int puzzleGridY;

    private boolean backgroundTextureOn = true;
    private Bitmap backgroundTexture;
    private Bitmap backgroundTexture2;
    private int backgroundselect;

    private Bundle config;

    private Bitmap puzzleResult;
    private Bitmap[] puzzlePiecesArray;
    private int[][] puzzlePieceTargetPositions;

    private boolean[] pieceLocked;  //拼塊是否已完成(鎖定)

    private Context mContext;

    private static int count=0;
    /**
     * JigsawPuzzle constructor: Dynamic Configuration
     * @param res
     * @param resourceId
     *
     * This jigsaw puzzle will be configured dynamically by partitioning
     * the provided source image.
     */
    public JigsawPuzzle(Resources res, Integer resourceId) {
        loadPuzzleResources(res, resourceId, 450, 300);
        buildDynamicPuzzleGrid();
    }

    /**
     * JigsawPuzzle constructor: Bundle configuration
     * @param context
     * @param configuration
     */
    public JigsawPuzzle(Context context, Bundle configuration, int background) {
        config = configuration;
        mContext = context;
        backgroundselect = background;

        loadPuzzleResources(mContext.getResources(),
                config.getBundle("img").getInt("img_local"), 800, 600);
        //索取圖片，設定大小，根據拼圖大小*欄數(列數)

        loadPuzzleConfiguration();

    }

    public void loadPuzzleResources (Resources res, int resourceId, long targetWidth, long targetHeight) {

         count++; //第一次背景呼叫decodePuzzleBitmapFromResource
        if(backgroundselect == 1){
            Bitmap decodedbackgroundTexture = decodePuzzleBitmapFromResource(res, R.drawable.surfaceview_main, 1000, 1000);
            backgroundTexture = decodedbackgroundTexture;
            Bitmap decodedbackgroundTexture2 = decodePuzzleBitmapFromResource(res, R.drawable.surfaceview_main2, 1000, 1000);
            backgroundTexture2 = decodedbackgroundTexture2;
        }else if(backgroundselect == 2){
            Bitmap decodedbackgroundTexture = decodePuzzleBitmapFromResource(res, R.drawable.surfaceview_right, 1000, 1000);
            backgroundTexture = decodedbackgroundTexture;
        }else if(backgroundselect == 0){

        }



        count++; //第二次照片呼叫decodePuzzleBitmapFromResource
        Bitmap decodedPuzzleResource = decodePuzzleBitmapFromResource(
                res, resourceId, targetWidth, targetHeight);

        puzzleResult = decodedPuzzleResource;      //圖片縮小後的尺寸
        puzzleXDimension = decodedPuzzleResource.getWidth();
        puzzleYDimension = decodedPuzzleResource.getHeight();


        Log.d("loadPuzzleResource", "getwidth/getheight_" + String.valueOf(count) + " : "
                + decodedPuzzleResource.getWidth() + " , "+  decodedPuzzleResource.getHeight());

    }

    /**
     * decodePuzzleBitmapFromResource
     * @param res
     * @param resId
     * @param targetWidth
     * @param targetHeight
     * @return Bitmap
     *
     *  Bitmap Loading Code from Android Developer lesson: "Loading Large Bitmaps Efficiently"
     */
    public static Bitmap decodePuzzleBitmapFromResource (
            Resources res, int resId, long targetWidth, long targetHeight) {

        // Load only the dimensions of the puzzle image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只取lk得圖片的長與寬
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate ratio to scale puzzle bitmap
        options.inSampleSize = calculateScaledPuzzleSize(options, targetWidth, targetHeight);

        // Decode puzzle resource image to bitmap from computed ratio
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * calculateScaledPuzzleSize
     *
     * Adapted from Android Developer lesson: "Loading Large Bitmaps Efficiently"
     */
    public static int calculateScaledPuzzleSize (
            BitmapFactory.Options options, long targetWidth, long targetHeight) {

        // Source Image Dimensions
        final int height = options.outHeight;
        final int width = options.outWidth;
        int imageScaleRatio = 1;

        if (height > targetHeight || width > targetWidth) {

            final int heightRatio = Math.round((float) height / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            imageScaleRatio = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return imageScaleRatio;
    }

    /**
     * buildDynamicPuzzleGrid
     *
     * If not already set, computes optimal puzzle piece size using Greatest Common Divisor.
     * Computes Bitmaps for each piece and sets their target positions.
     *
     * TODO: Handle case where GCD is 1, or if piece should be a rectangle?
     */
    public void buildDynamicPuzzleGrid() {
        // Compute optimal piece size:
        int optimalPieceSize = greatestCommonDivisor(puzzleXDimension, puzzleYDimension);

        // Update puzzle dimension variables
        puzzlePieceHeight = optimalPieceSize;
        puzzlePieceWidth = optimalPieceSize;
        puzzleGridX = puzzleXDimension / puzzlePieceWidth;
        puzzleGridY = puzzleYDimension / puzzlePieceHeight;

        // Initialize and fill puzzle
        puzzlePieceTargetPositions = new int[puzzleGridX][puzzleGridY];
        puzzlePiecesArray = new Bitmap[puzzleGridX * puzzleGridY];
        pieceLocked = new boolean[puzzleGridX * puzzleGridY];

        // 在此切割圖片成拼塊，產生Bitmap的陣列
        int counter = 0;
        for (int w = 0; w < puzzleGridX; w++) {
            for (int h = 0; h < puzzleGridY; h++) {
                puzzlePiecesArray[counter] = Bitmap.createBitmap(puzzleResult,
                        w*puzzlePieceWidth, h*puzzlePieceHeight, puzzlePieceWidth, puzzlePieceHeight);
                pieceLocked[counter] = false;

                puzzlePieceTargetPositions[w][h] = counter;

                counter++;
            }
        }
    }

    /**
     * greatestCommonDivisor
     * @param n1
     * @param n2
     * @return gcd of n1, n2
     *
     * Utility class for computing optimal puzzle bitmap scaling.
     */
    public int greatestCommonDivisor (int n1, int n2) {
        if (n2 == 0) return n1;
        return greatestCommonDivisor(n2, n1 % n2);
    }

    public void loadPuzzleConfiguration() {
        Bundle grid = config.getBundle("grid");
        Bundle image = config.getBundle("img");
        Bundle pieces = config.getBundle("pieces");

        puzzleGridX = grid.getInt("ncols");
        puzzleGridY = grid.getInt("nrows");

        puzzlePieceHeight = puzzleYDimension / puzzleGridY; //200
        puzzlePieceWidth = puzzleXDimension / puzzleGridX; //200

        // Fill Puzzle
        puzzlePieceTargetPositions = new int[puzzleGridX][puzzleGridY];
        puzzlePiecesArray = new Bitmap[puzzleGridX * puzzleGridY];
        pieceLocked = new boolean[puzzleGridX * puzzleGridY];

        // 在此切割圖片成拼塊，產生Bitmap的陣列
        int counter = 0;
        for (int w = 0; w < puzzleGridX; w++) {
            for (int h = 0; h < puzzleGridY; h++) {
                puzzlePiecesArray[counter] = Bitmap.createBitmap(puzzleResult, w*puzzlePieceWidth, h*puzzlePieceHeight,
                        puzzlePieceWidth, puzzlePieceHeight);
                Log.d("loadPuzzleconfig","createBitmap: " + w*puzzlePieceWidth+" , "
                        +h*puzzlePieceHeight +" , " + puzzlePieceWidth+" , "+ puzzlePieceHeight);

                pieceLocked[counter] = false;

                puzzlePieceTargetPositions[w][h] = counter;

                counter++;
            }
        }
        releaseBitmap();

    }

    /** Getters and Setters **/
    public Bitmap getPuzzleResult(){return puzzleResult;}

    public Bitmap[] getPuzzlePiecesArray () {
        return puzzlePiecesArray;
    }

    public int[] getPuzzleDimensions () {
        return new int[] { puzzleXDimension, puzzleYDimension, puzzleGridX, puzzleGridY };
    }

    public int[][] getPuzzlePieceTargetPositions () {
        return puzzlePieceTargetPositions;
    }

    public Bundle getConfig() {
        return config;
    }

    public boolean isBackgroundTextureOn() {
        return backgroundTextureOn;
    }

    public void setBackgroundTextureOn(boolean texture) {
        backgroundTextureOn = texture;
    }

    public Bitmap getBackgroundTexture() {
        return backgroundTexture;
    }
    public Bitmap getBackgroundTexture2() {
        return backgroundTexture2;
    }

   /* public void setPieceLocked (int piece, boolean locked) {
        if (piece >= 0 && piece < pieceLocked.length) {
            pieceLocked[piece] = locked;
        }
    }*/

   /*public boolean isPieceLocked(int piece) {
       if (piece >= 0 && piece < pieceLocked.length) {
           return pieceLocked[piece];
       } else {
            return false;

        }
     }*/

    /** Jigsaw Puzzle Message Handlers - Probably Override **/
    public void onJigsawEventPieceGrabbed (int index, int topLeftX, int topLeftY) {
       // Toast.makeText(this.mContext, "grab" + " , found: " + index, Toast.LENGTH_SHORT).show();
        //可以在此寫入播放音效程式
    }

    public void onJigsawEventPieceMoved (int index, int topLeftX, int topLeftY) {
       // Toast.makeText(this.mContext, "move" + " , found: " + index + ", topLeftX: " + topLeftX + " ,topLeftY: " + topLeftY , Toast.LENGTH_SHORT).show();
    }

    public void onJigsawEventPieceDropped (int index, int topLeftX, int topLeftY) {
     //   Toast.makeText(this.mContext, "DROP" + " , found: " + index + ", topLeftX: " + topLeftX + " ,topLeftY: " + topLeftY , Toast.LENGTH_SHORT).show();
        //可以在此寫入播放音效程式
    }

    public void releaseBitmap() {
        if (puzzleResult != null) {
            puzzleResult.recycle();
            puzzleResult = null;
        }
    }
}
