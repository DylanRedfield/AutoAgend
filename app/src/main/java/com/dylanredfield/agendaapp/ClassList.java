package com.dylanredfield.agendaapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClassList {
    private static ClassList cInstance;
    private ArrayList<SchoolClass> mClassList;

    private ClassList(Context context) {
        mClassList = DatabaseHandler.getInstance(context).getAllClasses();
    }

    public static ClassList getInstance(Context context) {
        if (cInstance == null) {
            cInstance = new ClassList(context);
        }
        return cInstance;
    }

    public void sortByPeriod() {
        /*ArrayList<Assignment> holder = new ArrayList<Assignment>();

        for (int a = 0; a < mAssignments.size(); a++) {
            if (!mAssignments.get(a).isCompleted()) {
                holder.add(mAssignments.get(a));
            }
        }
        for (int b = 0; b < mAssignments.size(); b++) {
            if (mAssignments.get(b).isCompleted()) {
                holder.add(mAssignments.get(b));
            }
        }
        mAssignments = holder;*/

        Collections.sort(mClassList, new Comparator<SchoolClass>() {
            @Override
            public int compare(SchoolClass lhs, SchoolClass rhs) {
                if(lhs.getPeriod() < rhs.getPeriod()) {
                    return -1;
                }
                if(lhs.getPeriod() == rhs.getPeriod()) {
                    return 0;
                }
                return 1;
            }
        });
    }

    public ArrayList<SchoolClass> getList() {
        return mClassList;
    }

    public ArrayList<String> getListString() {
        ArrayList<String> holder = new ArrayList<String>();
        for (SchoolClass a : mClassList) {
            holder.add(a.getClassName());
        }
        return holder;
    }

    public void addSchoolClass(SchoolClass sc) {
        mClassList.add(sc);
    }


}
