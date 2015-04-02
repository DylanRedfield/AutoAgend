package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddAssignmentActivity extends ActionBarActivity {
    private EditText mTitle;
    private EditText mDescription;
    private EditText mDateAssignedPicker;
    private EditText mDateDuePicker;
    private Calendar mAssignedDate;
    private Calendar mDueDate;
    private String mFileLocation;
    private ArrayList<SchoolClass> mClassList;
    private SimpleDateFormat mSimpleDateFormat;
    // get value from mClassIndex from parent class
    private int mClassIndex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        // Class mClassIndex
        mClassIndex = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        mFileLocation = getIntent().getStringExtra(MainActivity.FILE_LOCATION_STRING);

        // Need to get ClassList so not constantly accessing
        mClassList = ClassList.getInstance(getApplicationContext()).getList();
        String myFormat = "MM/dd/yy";
        mSimpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);

        // Gets correct context  to create listview dialog
        Activity mActivityContext = this;
        while (mActivityContext.getParent() != null) {
            mActivityContext = mActivityContext.getParent();
        }

        findViewByIds();
        setListneres();
        setBars();
    }

    public void findViewByIds() {
        mTitle = (EditText) findViewById(R.id.edittext_title);
        mDescription = (EditText) findViewById(R.id.edittext_description);
        mDateAssignedPicker = (EditText) findViewById(R.id.date_assigned_picker);
        mDateDuePicker = (EditText) findViewById(R.id.date_due_picker);
    }

    public void setListneres() {

        // Used to create so Calender variable can be referenced to this

        mDateAssignedPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAssignedDate = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                mAssignedDate.set(year, monthOfYear, dayOfMonth);

                                // Formats Calender into viewable date
                                mDateAssignedPicker.setText(mSimpleDateFormat
                                        .format(mAssignedDate.getTime()));
                            }
                        }, mAssignedDate.get(Calendar.YEAR), mAssignedDate.get(Calendar.MONTH),
                        mAssignedDate.get(Calendar.DAY_OF_MONTH));
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mAssignedDate = null;
                        mDateAssignedPicker.setText("");
                    }
                });
                dpd.show();
            }
        });

        mDateDuePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDueDate = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDueDate.set(year, monthOfYear, dayOfMonth);

                                mDateDuePicker.setText(mSimpleDateFormat
                                        .format(mDueDate.getTime()));
                            }
                        }, mDueDate.get(Calendar.YEAR), mDueDate.get(Calendar.MONTH),
                        mDueDate.get(Calendar.DAY_OF_MONTH) + 1);
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDueDate = null;
                        mDateDuePicker.setText("");
                    }
                });
                //Adds 1 to day as most
                //assignments will be 1 day
                dpd.show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));
        mActionBar.setTitle(mClassList
                .get(mClassIndex).getClassName());

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            Window mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_enter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.enter_actionbar:
                // Cant make assignment without mActivityContext title
                if (!mTitle.getText().toString().equals("")) {
                    mClassList
                            .get(mClassIndex)
                            .getAssignments()
                            .add(new Assignment(mTitle.getText().toString(),
                                    mDescription.getText().toString(),
                                    mAssignedDate, mDueDate, mFileLocation));
                    updateDatabase();
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            AddAssignmentActivity.this);
                    builder.setMessage("Enter a Title")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.setTitle("Missing Information");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                mClassList);

    }
}
