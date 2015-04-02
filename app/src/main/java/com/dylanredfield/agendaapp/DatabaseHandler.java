package com.dylanredfield.agendaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler sInstance;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "classManager";

    // Contacts table name
    private static final String TABLE_SCHOOL_CLASSES = "schoolClass";

    public static DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Contacts Table Columns names
    private static final String KEY_PERIOD = "period";
    private static final String KEY_CLASS_NAME = "classname";
    private static final String KEY_ASSIGNMENTS = "assignments";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_START = "timeStart";
    private static final String KEY_END= "timeEnd";

    // private static final String KEY_ASSIGNMENTS = "assignments";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCHOOL_CLASS_TABLE = "CREATE TABLE "
                + TABLE_SCHOOL_CLASSES + "(" + KEY_PERIOD
                + " INTEGER," + KEY_START +  " INTEGER," + KEY_END
                + " INTEGER," + KEY_DESCRIPTION + " TEXT,"
                + KEY_CLASS_NAME + " TEXT," + KEY_ASSIGNMENTS + " TEXT" + ")";
        db.execSQL(CREATE_SCHOOL_CLASS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOL_CLASSES);

        // Create tables again
        onCreate(db);

    }



    public void deleteAllClasses() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCHOOL_CLASSES);
    }

    public ArrayList<SchoolClass> getAllClasses() {
        ArrayList<SchoolClass> classList = new ArrayList<SchoolClass>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCHOOL_CLASSES;
        Calendar tempStartTime = Calendar.getInstance();
        Calendar tempEndTime = Calendar.getInstance();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Gson gson = new Gson();

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SchoolClass sc = new SchoolClass();
                sc.setClassName(cursor.getString(4));
                sc.setDescription(cursor.getString(3));
                sc.setPeriod(Integer.parseInt(cursor.getString(0)));
                ArrayList<Assignment> obj = gson.fromJson(cursor.getString(5),
                        new TypeToken<ArrayList<Assignment>>() {
                        }.getType());
                sc.setAssignments(obj);
                if(cursor.getString(1) != null) {
                    tempStartTime.setTimeInMillis(Long.parseLong(cursor.getString(1)));
                } else {
                    tempStartTime = null;
                }
                sc.setStartTime(tempStartTime);

                if(cursor.getString(2) != null) {
                    tempEndTime.setTimeInMillis(Long.parseLong(cursor.getString(2)));
                } else {
                    tempEndTime = null;
                }
                sc.setEndTime(tempEndTime);
                tempStartTime = Calendar.getInstance();
                tempEndTime = Calendar.getInstance();

                // Adding contact to list
                classList.add(sc);

            } while (cursor.moveToNext());
        }

        // return contact list
        return classList;
    }

    public void addAllClasses(ArrayList<SchoolClass> classList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (int a = 0; a < classList.size(); a++) {
            values.put(KEY_CLASS_NAME, classList.get(a).getClassName());
            values.put(KEY_DESCRIPTION, classList.get(a).getDescription());
            values.put(KEY_PERIOD, classList.get(a).getPeriod());
            values.put(KEY_ASSIGNMENTS, arrayListToString(classList.get(a)
                    .getAssignments()));
            if(classList.get(a).getStartTime() != null) {
                values.put(KEY_START, classList.get(a).getStartTime().getTimeInMillis());
            }
            if(classList.get(a).getEndTime() != null) {
                values.put(KEY_END, classList.get(a).getEndTime().getTimeInMillis());
            }
            // Inserting Row
            db.insert(TABLE_SCHOOL_CLASSES, null, values);

        }
        db.close();

    }

    public String arrayListToString(ArrayList<Assignment> classList) {

        Gson gson = new Gson();
        return gson.toJson(classList);

    }
}
