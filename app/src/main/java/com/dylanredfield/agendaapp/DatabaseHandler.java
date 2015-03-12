package com.dylanredfield.agendaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context mAppContext;
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
        mAppContext = context.getApplicationContext();
    }

    // Contacts Table Columns names
    private static final String KEY_PERIOD = "period";
    private static final String KEY_CLASS_NAME = "classname";
    private static final String KEY_ASSIGNMENTS = "assignments";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCHOOL_CLASS_TABLE = "CREATE TABLE "
                + TABLE_SCHOOL_CLASSES + "(" + KEY_PERIOD
                + " INTEGER PRIMARY KEY," + KEY_DESCRIPTION + " TEXT,"
                + KEY_CLASS_NAME + " TEXT," + KEY_ASSIGNMENTS + " TEXT," + KEY_START_TIME
                + " INTEGER PRIMARY KEY," + KEY_END_TIME + " INTEGER PRIMARY KEY" + ")";
        db.execSQL(CREATE_SCHOOL_CLASS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOL_CLASSES);

        // Create tables again
        onCreate(db);

    }

    public void deleteClass(int row) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "_id" + "=?";
        String[] whereArgs = new String[]{String.valueOf(row)};
        db.delete(TABLE_SCHOOL_CLASSES, whereClause, whereArgs);
    }

    public void deleteAllClasses() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCHOOL_CLASSES);
    }

    public void addClass(SchoolClass sc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(KEY_PERIOD, sc.getPeriod());
        values.put(KEY_CLASS_NAME, sc.getClassName());

        // Inserting Row
        db.insert(TABLE_SCHOOL_CLASSES, null, values);
        db.close();
    }

    public ArrayList<SchoolClass> getAllClasses() {
        ArrayList<SchoolClass> classList = new ArrayList<SchoolClass>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCHOOL_CLASSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Gson gson = new Gson();

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SchoolClass sc = new SchoolClass();
                sc.setPeriod(Integer.parseInt(cursor.getString(0)));
                sc.setDescription(cursor.getString(1));
                sc.setClassName(cursor.getString(2));
                ArrayList<Assignment> obj = gson.fromJson(cursor.getString(3),
                        new TypeToken<ArrayList<Assignment>>() {
                        }.getType());
                sc.setAssignments(obj);
                sc.setStartTime(getDate(Integer.parseInt(cursor.getString(4))));
                sc.setStartTime(getDate(Integer.parseInt(cursor.getString(5))));

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
            values.put(KEY_START_TIME,  (int) (classList.get(a).getStartTime().getTimeInMillis()));
            values.put(KEY_END_TIME, (int)(classList.get(a).getEndTime().getTimeInMillis()));
            // Inserting Row
            db.insert(TABLE_SCHOOL_CLASSES, null, values);

        }
        db.close();

    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }
    public Calendar getDate(long milliSeconds)
    {

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return calendar;
    }

    public String arrayListToString(ArrayList<Assignment> classList) {
        // shit dont work, arrayList is comoing in as null?
        /*
         * JSONObject json = new JSONObject(); String arrayList = ""; try {
		 * Toast.makeText(mAppContext, new JSONArray(classList).toString(),
		 * Toast.LENGTH_SHORT).show(); JSONArray jsonArray = new JSONArray();
		 * for (Assignment a : classList) jsonArray.put(a); json.put(KEY_JSON,
		 * jsonArray);
		 * 
		 * Toast.makeText(mAppContext, classList.get(0).toString(),
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * arrayList = json.toString();
		 * 
		 * } catch (JSONException e) { // e.printStackTrace(); } return
		 * arrayList;
		 */

        Gson gson = new Gson();
        return gson.toJson(classList);

    }
}
