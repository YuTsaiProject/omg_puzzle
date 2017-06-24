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

    private Bundle config;

    private Bitmap puzzleResult;
    private Bitmap[] puzzlePiecesArray;
    private int[][] puzzlePieceTargetPositions;
    private boolean[] pieceLocked;

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
    public JigsawPuzzle(Context context, Bundle configuration) {
        config = configuration;
        mContext = context;

        loadPuzzleResources(mContext.getResources(),
                config.getBundle("img").getInt("img_local"), 400, 300); //索取圖片，設定大小，長300，寬400

        loadPuzzleConfiguration();

    }

    public void loadPuzzleResources (Resources res, int resourceId, long targetWidth, long targetHeight) {
         count++; //第一次背景呼叫decodePuzzleBitmapFromResource

        backgroundTexture = decodePuzzleBitmapFromResource(res, R.drawable.background, 800, 800);
        Log.d("loadPuzzleResource", "getwidth/getheight_" + String.valueOf(count) + " : " + backgroundTexture.getWidth() + " , "+  backgroundTexture.getHeight());
        count++; //第二次照片呼叫decodePuzzleBitmapFromResource

        Bitmap decodedPuzzleResource = decodePuzzleBitmapFromResource(
                res, resourceId, targetWidth, targetHeight);
       //400,300



        puzzleResult = decodedPuzzleResource;
        puzzleXDimension = decodedPuzzleResource.getWidth(); // 800,600
        puzzleYDimension = decodedPuzzleResource.getHeight();
        Log.d("loadPuzzleResource", "getwidth/getheight_" + String.valueOf(count) + " : " + decodedPuzzleResource.getWidth() + " , "+  decodedPuzzleResource.getHeight());
    //   Toast.makeText(this.mContext, puzzleXDimension + " , " + puzzleYDimension, Toast.LENGTH_LONG).show();


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
    public static Bitmap decodePuzzleBitmapFromResource (   //利用decodeFile()將手機裡的圖檔轉換成Bitmap物件
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
        Log.d("calculateScaled", "targetwidth/targetheight_" + String.valueOf(count) + " : " + targetWidth + " , "+ targetHeight);

        if (height > targetHeight || width > targetWidth) {

            final int heightRatio = Math.round((float) height / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);
            Log.d("calculateScaled", "widthRatio/heightRatio_" + String.valueOf(count) + " : " + widthRatio + " , "+ heightRatio);

            imageScaleRatio = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d("calculateScaled", "imageScaleRatio_" + String.valueOf(count) + " : " +imageScaleRatio);

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



        // Generate array of bitmaps
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

        // Puzzle Grid
        puzzleGridX = grid.getInt("ncols"); //4

        puzzleGridY = grid.getInt("nrows"); //3

        puzzlePieceHeight = puzzleYDimension / puzzleGridY; //200

        puzzlePieceWidth = puzzleXDimension / puzzleGridX; //200
     //   Log.d("puzzleYDimension ", String.valueOf(puzzleYDimension));

        // Fill Puzzle
        puzzlePieceTargetPositions = new int[puzzleGridX][puzzleGridY];
        puzzlePiecesArray = new Bitmap[puzzleGridX * puzzleGridY];
        pieceLocked = new boolean[puzzleGridX * puzzleGridY];



        int counter = 0;
        for (int w = 0; w < puzzleGridX; w++) {
            for (int h = 0; h < puzzleGridY; h++) {
                puzzlePiecesArray[counter] = Bitmap.createBitmap(puzzleResult, w*puzzlePieceWidth, h*puzzlePieceHeight,
                        puzzlePieceWidth, puzzlePieceHeight);
                Log.d("loadPuzzleconfig","createBitmap: " + w*puzzlePieceWidth+" , " +h*puzzlePieceHeight +" , " + puzzlePieceWidth+" , "+ puzzlePieceHeight);

                pieceLocked[counter] = false;


                puzzlePieceTargetPositions[w][h] = counter;

                counter++;
            }
        }

    }

    /** Getters and Setters **/

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

    public void setPieceLocked (int piece, boolean locked) {
        if (piece >= 0 && piece < pieceLocked.length) {
            pieceLocked[piece] = locked;
        }
    }

    public boolean isPieceLocked(int piece) {
        if (piece >= 0 && piece < pieceLocked.length) {
            return pieceLocked[piece];
        } else {
            return false;

        }
    }

    /** Jigsaw Puzzle Message Handlers - Probably Override **/
    public void onJigsawEventPieceGrabbed (int index, int topLeftX, int topLeftY) {
       // Toast.makeText(this.mContext, "grab" + " , found: " + index, Toast.LENGTH_SHORT).show();
    }

    public void onJigsawEventPieceMoved (int index, int topLeftX, int topLeftY) {
       // Toast.makeText(this.mContext, "move" + " , found: " + index + ", topLeftX: " + topLeftX + " ,topLeftY: " + topLeftY , Toast.LENGTH_SHORT).show();
    }

    public void onJigsawEventPieceDropped (int index, int topLeftX, int topLeftY) {
     //   Toast.makeText(this.mContext, "DROP" + " , found: " + index + ", topLeftX: " + topLeftX + " ,topLeftY: " + topLeftY , Toast.LENGTH_SHORT).show();
    }
}
