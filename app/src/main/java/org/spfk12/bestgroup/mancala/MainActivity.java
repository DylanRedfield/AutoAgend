package org.spfk12.bestgroup.mancala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private TextView mPlayerOneScore;
    private TextView mPlayerTwoScore;
    private TextView mPlayerOne;
    private TextView mPlayerTwo;
    private Board mBoard;
    private int mPlayerTurn;
    private int mStoneCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateObjects();
        makeGrid();
        setListeners();
    }

    public void instantiateObjects() {
        mStoneCount = 4;
        mBoard = new Board(mStoneCount, this);
        mGridView = (GridView) findViewById(R.id.board);
        mPlayerTurn = 1;

        // Set default style to bold because player one always starts
        mPlayerOneScore = (TextView) findViewById(R.id.player_one_text);
        mPlayerOneScore.setTextColor(Color.RED);
        mPlayerOneScore.setTypeface(null, Typeface.BOLD);

        mPlayerTwoScore = (TextView) findViewById(R.id.player_two_text);
        mPlayerTwoScore.setTextColor(Color.BLUE);

        mPlayerOne = (TextView) findViewById(R.id.player_one_overview);
        mPlayerOne.setTextColor(Color.RED);
        mPlayerOne.setTypeface(null, Typeface.BOLD);

        mPlayerTwo = (TextView) findViewById(R.id.player_two_overview);
        mPlayerTwo.setTextColor(Color.BLUE);


        // TODO add user input for stonecount
    }

    public void makeGrid() {
        mGridAdapter = new GridAdapter(this, mBoard.getBoard(), mPlayerTurn);
        mGridView.setAdapter(mGridAdapter);

        //Creates grid pattern
        mGridView.setBackgroundColor(Color.BLACK);
        mGridView.setVerticalSpacing(1);
        mGridView.setHorizontalSpacing(1);
    }

    public void setListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mPlayerTurn == 1 && position <= 5) {
                    // mBoard.move();

                    //Swaps turn and bold
                    mPlayerTurn++;
                    mPlayerOne.setTypeface(null, Typeface.NORMAL);
                    mPlayerTwo.setTypeface(null, Typeface.BOLD);

                    mPlayerOneScore.setTypeface(null, Typeface.NORMAL);
                    mPlayerTwoScore.setTypeface(null, Typeface.BOLD);

                    // Needs to update grid to swap bold inside grid
                    makeGrid();
                } else if (mPlayerTurn == 2 && position > 5) {
                    mPlayerTurn--;
                    mPlayerTwo.setTypeface(null, Typeface.NORMAL);
                    mPlayerOne.setTypeface(null, Typeface.BOLD);

                    mPlayerTwoScore.setTypeface(null, Typeface.NORMAL);
                    mPlayerOneScore.setTypeface(null, Typeface.BOLD);
                    makeGrid();
                } else {
                    // TODO make into dialog so no toast spam
                    String player;
                    if(mPlayerTurn == 1) {
                        player = "one";
                    } else {
                        player = "two";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("It is player " + player + "'s turr!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.setTitle("Not Your Turn");
                    AlertDialog alert = builder.create();
                    if (alert != null)
                        alert.show();
                }
            }
        });
    }

}
