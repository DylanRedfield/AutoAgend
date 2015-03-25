package org.spfk12.bestgroup.mancala;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mArray;
    private final int mCount = 12;
    private int mPlayerTurn;

    public GridAdapter(Context c, int[][] array, int playerTurn) {
        mContext = c;
        mArray = new int[12];
        mPlayerTurn = playerTurn;

        matrixToArray(array);


    }

    public void matrixToArray(int[][] array) {
        int counter = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                mArray[counter] = array[i][j];
                counter++;
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 30, 8, 30);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText("" + mArray[position]);
        textView.setBackgroundColor(Color.WHITE);

        if(position <= 5) {
           textView.setTextColor(Color.RED);

            if(mPlayerTurn == 1) {
                textView.setTypeface(null, Typeface.BOLD);
            }
        } else {
            textView.setTextColor(Color.BLUE);

            if(mPlayerTurn == 2) {
                textView.setTypeface(null, Typeface.BOLD);
            }
        }

        return textView;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}
