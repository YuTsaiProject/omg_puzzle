package com.project.game.omg_puzzle;

import android.os.Bundle;
import android.util.Log;

public final class ExampleJigsawConfigurations {

    /**
     * For documentation supporting this example, see:
     * https://github.com/gentimouton/rcat/wiki/Jigsaw-Puzzle
     * @return Bundle configuration
     */
    public static Bundle getRcatKittenExample() { //
        Bundle config = new Bundle();

        Bundle board = new Bundle();
        Bundle grid = new Bundle();
        Bundle frus = new Bundle();
        Bundle pieces = new Bundle();
        Bundle img = new Bundle();
        Bundle scores = new Bundle();

        board.putInt("w", 800);
        board.putInt("h", 600);
        board.putInt("minScale", 1);
        board.putInt("maxScale", 10);

        grid.putInt("x", 200);
        grid.putInt("y", 150);
        grid.putInt("ncols", 8);     //在此更改拼圖切割欄數
        grid.putInt("nrows", 6);    //在此更改拼圖切割列數
        grid.putInt("cellw", 100);
        grid.putInt("cellh", 100);

        frus.putInt("x", 0);
        frus.putInt("y", 0);
        frus.putInt("scale", 1);
        frus.putString("w", null);
        frus.putString("h", null);

        scores.putStringArray("top", new String[]{"Top1", "Top2", "Top3"});
        scores.putInt("numTop", 20);
        Bundle[] connected = new Bundle[2];
        connected[0] = new Bundle();
        connected[1] = new Bundle();
        connected[0].putString("user","Arthur");
        connected[0].putString("uid", "player5678-uid");
        connected[0].putInt("score", 47);
        connected[1].putString("user", "Thomas");
        connected[1].putString("uid", "player9012-uid");
        connected[1].putInt("score", 39);
        scores.putParcelableArray("connected", connected);

        img.putString("img_url", "" +
                "");
        img.putInt("img_local", R.drawable.test);
        img.putInt("img_w", 267);
        img.putInt("img_h", 189);

        // Pieces
        Bundle p;
        String key;
        for (int h = 0; h < grid.getInt("nrows"); h++) {
            for (int w = 0; w < grid.getInt("ncols"); w++) {
                key = "piece_" + String.valueOf(w) + String.valueOf(h);
                p = new Bundle();
                p.putString("l", "-1");
                p.putString("pid", key);
                p.putBoolean("b", false);
                p.putInt("x", w*(img.getInt("img_w")/grid.getInt("ncols")));
                p.putInt("y", h*(img.getInt("img_h")/grid.getInt("nrows")));
                p.putInt("r", h);
                p.putInt("c", w);
                pieces.putBundle(key, p);
            }
        }

        config.putBundle("board", board);
        config.putBundle("grid", grid);
        config.putBundle("frus", frus);
        config.putBundle("pieces", pieces);
        config.putString("myId", "player1234-uid");
        config.putBundle("img", img);
        config.putBundle("scores", scores);
        Log.d("config", "ExampleJigsaw");
        return config;

    }

    private ExampleJigsawConfigurations () {
        throw new AssertionError();
    }
}
