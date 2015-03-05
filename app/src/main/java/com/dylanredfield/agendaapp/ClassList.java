package com.dylanredfield.agendaapp;

import java.util.ArrayList;

import android.content.Context;

public class ClassList {
	private ArrayList<SchoolClass> mClassList;
	private static ClassList cInstance;

	public static ClassList getInstance(Context context)
	{
		if(cInstance == null)
		{
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
	public void addSchoolClass(SchoolClass sc)
	{
		mClassList.add(sc);
	}
		

}
