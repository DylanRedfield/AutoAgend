package org.spfk12.bestgroup.mancala;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dylan_000 on 3/18/2015.
 */
public class GridAdapter extends BaseAdapter{
    private Context mContext;

    public GridAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return 12;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(position + 1 + "");
        textView.setBackgroundColor(Color.WHITE);
        return textView;
    }

}
