package org.spfk12.bestgroup.mancala;

import android.content.Context;
import android.graphics.Color;
import android.widget.GridView;
import android.widget.TextView;

public class Board {
    private int mBoard[][];
    private int mPlayerOneScore;
    private int mPlayerTwoScore;
    private int mNumStones;
    private Context mContext;

    public Board(int numStones, Context c) {
        mBoard = new int[2][6];
        mNumStones = numStones;
        mPlayerOneScore = 0;
        mPlayerTwoScore = 0;
        mContext = c;

        fillDefaultValues(mNumStones);

    }

    public void fillDefaultValues(int numStones) {
        for (int i = 0; i < mBoard.length; i++) {
            for (int j = 0; j < mBoard[i].length; j++) {
                mBoard[i][j] = numStones;
            }
        }
    }

    public void updateDisplay(GridView grid, TextView playerOneScore, TextView playerTwoScore,
                              int playerTurn) {
        grid.setAdapter(new GridAdapter(mContext, mBoard, playerTurn));
        grid.setBackgroundColor(Color.BLACK);
        grid.setVerticalSpacing(1);
        grid.setHorizontalSpacing(1);

        playerOneScore.setText("" + mPlayerOneScore);
        playerTwoScore.setText("" + mPlayerTwoScore);
    }

    public int[][] getBoard() {
        return mBoard;
    }

}
