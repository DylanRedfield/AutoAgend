package com.dylanredfield.agendaapp;

import android.graphics.Bitmap;
import android.graphics.Picture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Assignment {
    private String mTitle;
    private String mDescription;
    private Calendar mDateAssigned;
    private Calendar mDateDue;
    private Bitmap mThumbnail;
    private Picture mPicture;
    private boolean mCompleted;
    private Bitmap thumbnail;

    public Assignment(String title, String description, Calendar dateAssigned,
                      Calendar dateDue, Bitmap thumbnail) {
        mTitle = title;
        mDescription = description;
        mDateAssigned = dateAssigned;
        mDateDue = dateDue;
        mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Calendar getDateAssigned() {
        return mDateAssigned;
    }

    public void setDateAssigned(Calendar dateAssigned) {
        mDateAssigned = dateAssigned;
    }

    public Calendar getDateDue() {
        return mDateDue;
    }

    public void setDateDue(Calendar dateDue) {
        mDateDue = dateDue;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }
    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public ArrayList<String> makeList() {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("Title: " + mTitle);
        temp.add("Description: " + mDescription);
        return temp;
    }

    public ArrayList<String> makeList2() {
        ArrayList<String> temp = new ArrayList<String>();
        if (!mTitle.equals("")) {
            temp.add("Ti" + mTitle);
        }
        if (!mDescription.equals("")) {
            temp.add("De" + mDescription);
        }
        if (mDateAssigned != null) {
            temp.add("As" + calendarToString(mDateAssigned));
        }
        if (mDateDue != null) {
            temp.add("Du" + calendarToString(mDateDue));
        }
        return temp;
    }

    public String calendarToString(Calendar c) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(c.getTime());
    }

    public String toString() {
        return "" + mTitle;
    }

}
