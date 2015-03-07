package com.dylanredfield.agendaapp;

import android.content.Context;

import java.util.ArrayList;

public class ClassList {
    private ArrayList<SchoolClass> mClassList;
    private static ClassList cInstance;

    public static ClassList getInstance(Context context) {
        if (cInstance == null) {
            cInstance = new ClassList(context);
        }
        return cInstance;
    }

    private ClassList(Context context) {
        mClassList = DatabaseHandler.getInstance(context).getAllClasses();
    }

    public ArrayList<SchoolClass> getList() {
        return mClassList;
    }
    public ArrayList<String> getListString() {
        ArrayList<String> holder = new ArrayList<String>();
        for(SchoolClass a: mClassList) {
           holder.add(a.getClassName());
        }
        return holder;
    }

    public void addSchoolClass(SchoolClass sc) {
        mClassList.add(sc);
    }


}
