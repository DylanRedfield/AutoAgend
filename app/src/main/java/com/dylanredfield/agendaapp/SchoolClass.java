package com.dylanredfield.agendaapp;

import java.util.ArrayList;

import android.util.Log;

public class SchoolClass {

	private int mPeriod;
	private String mClassName;
	private String mDescription;
	private ArrayList<Assignment> mAssignments;

	public SchoolClass() {
		mAssignments = new ArrayList<Assignment>();
	}

	public SchoolClass(String className, String description, int period) {
		mPeriod = period;
		mClassName = className;
		mDescription = description;
		mAssignments = new ArrayList<Assignment>();
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

	public void setAssignments(ArrayList<Assignment> assignments) {
		mAssignments = assignments;
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
		
		for(Assignment a : mAssignments) {
			if(a.isCompleted()) {
				holder.add(a);
			}
			if(!a.isCompleted()) {
				holder.add(0, a);
			}
		}
		mAssignments = holder;
	}

	public String toString() {
		String rep = "" + mClassName;
		return rep;
	}

}