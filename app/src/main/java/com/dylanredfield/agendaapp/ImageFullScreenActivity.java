package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageFullScreenActivity extends ActionBarActivity {
    private ImageView mImageView;
    private int indexClass;
    private int indexAssignment;
    private ActionBar mActionBar;
    private ArrayList<SchoolClass> mClassList;
    private Window mWindow;
    private PhotoViewAttacher mAttacher;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_image_full_screen);


        indexClass = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        indexAssignment = getIntent().getIntExtra(ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, 0);
        mClassList = ClassList.getInstance(getApplicationContext()).getList();

        mImageView = (ImageView) findViewById(R.id.full_screen_image);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(mClassList.get(indexClass)
                .getAssignments()
                .get(indexAssignment).getFilePath()));
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));
        mActionBar.setTitle(mClassList
                .get(indexClass).getAssignments().get(indexAssignment).getTitle());
        mAttacher = new PhotoViewAttacher(mImageView);


        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }
}
