package com.dylanredfield.agendaapp;

import android.graphics.Bitmap;

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
    private String mFilePath;
    private boolean mCompleted;
    private boolean isHidden;
    private Bitmap thumbnail;

    public Assignment(String title, String description, Calendar dateAssigned,
                      Calendar dateDue, String filePath) {
        mTitle = title;
        mDescription = description;
        mDateAssigned = dateAssigned;
        mDateDue = dateDue;
        mFilePath = filePath;
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
    public boolean isHidden() {
        return isHidden;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }
    public void setHidden(boolean hidden) {
        isHidden = hidden;
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
        if (mFilePath != null){
            temp.add("Pi" + "Click Here to View Photo");
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
