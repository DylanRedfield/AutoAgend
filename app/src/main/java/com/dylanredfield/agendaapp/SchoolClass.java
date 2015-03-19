package com.dylanredfield.agendaapp;

import java.util.ArrayList;
import java.util.Calendar;

public class SchoolClass {

    private int mPeriod;
    private String mClassName;
    private String mDescription;
    private ArrayList<Assignment> mAssignments;
    private Calendar mStartTime;
    private Calendar mEndTime;

    public SchoolClass() {
        mAssignments = new ArrayList<Assignment>();
    }

    public SchoolClass(String className, String description, int period,
                       Calendar assignedTime, Calendar dueTime) {
        mPeriod = period;
        mClassName = className;
        mDescription = description;
        mAssignments = new ArrayList<Assignment>();
        mStartTime = assignedTime;
        mEndTime = dueTime;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public int getPeriod() {
        return mPeriod;
    }

    public void setPeriod(int period) {
        mPeriod = period;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void addAssignment(Assignment assignment) {
        mAssignments.add(assignment);
    }

    public ArrayList<Assignment> getAssignments() {
        return mAssignments;
    }
    public void setStartTime(Calendar startTime) {
        mStartTime = startTime;
    }
    public void setEndTime(Calendar endTime) {
        mEndTime = endTime;
    }
    public void setAssignments(ArrayList<Assignment> assignments) {
        mAssignments = assignments;
    }
    public Calendar getStartTime() {
        return mStartTime;
    }
    public Calendar getEndTime() {
        return mEndTime;
    }

    public ArrayList<String> makeList() {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("Class: " + mClassName);
        temp.add("Description: " + mDescription);
        temp.add("Period: " + ((Integer) mPeriod).toString());

        return temp;
    }

    public void sortAssignmentsByCompleted() {
        ArrayList<Assignment> holder = new ArrayList<Assignment>();

        for(int a = 0; a < mAssignments.size(); a++) {
            if(!mAssignments.get(a).isCompleted()) {
                holder.add(mAssignments.get(a));
            }
        }
        for(int b = 0; b < mAssignments.size(); b++) {
            if(mAssignments.get(b).isCompleted()) {
                holder.add(mAssignments.get(b));
            }
        }
        mAssignments = holder;
    }

    public String toString() {
        String rep = "" + mClassName;
        return rep;
    }

}
