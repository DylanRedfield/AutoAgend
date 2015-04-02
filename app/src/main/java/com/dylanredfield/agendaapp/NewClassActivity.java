package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.TimePicker;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewClassActivity extends ActionBarActivity {
    int period;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mPeriod;
    private EditText mTimeAssignedSelector;
    private EditText mTimeDueSelector;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private ArrayList<SchoolClass> mClassList;
    private String myFormat = "h:mm a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        mClassList = ClassList.getInstance(getApplicationContext()).getList();
        mTitle = (EditText) findViewById(R.id.edittext_title);
        mDescription = (EditText) findViewById(R.id.edittext_description);
        mPeriod = (EditText) findViewById(R.id.edittext_period);

        if (mClassList.size() > 0) {
            mPeriod.setText("" + (mClassList.get(mClassList.size() - 1).getPeriod() + 1));
        } else {
            mPeriod.setText("" + 1);
        }
        mTimeAssignedSelector = (EditText) findViewById(R.id.timepicker_assigned);
        mTimeDueSelector = (EditText) findViewById(R.id.timepicker_due);


        setBars();
        setListeners();
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
                onButtonClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onButtonClick() {
        // TODO accept all input types (period cant be null
        if (!mTitle.getText().toString().equals("")) {
            if (String.valueOf(Integer.parseInt(mPeriod.getText().toString())).length() < 3) {

                if (mPeriod.getText().toString().equals("")) {
                    period = mClassList.size() + 1;

                } else {
                    period = Integer.parseInt(mPeriod.getText().toString());
                }
                ClassList.getInstance(getApplicationContext()).addSchoolClass(
                        new SchoolClass(mTitle.getText().toString(),
                                mDescription.getText().toString(), period, mAssignedTime, mDueTime));

                updateDatabase();
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        NewClassActivity.this);
                builder.setMessage("Enter a smaller period")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                builder.setTitle("Period Too Large");
                AlertDialog alert = builder.create();
                alert.show();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    NewClassActivity.this);
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

    }

    public void setListeners() {
        mTimeAssignedSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAssignedTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);

                TimePickerDialog assignedTimePicker = new TimePickerDialog(NewClassActivity.this,
                        R.style.StyledDialog,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mAssignedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mAssignedTime.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mTimeAssignedSelector.setText(sdf.format(mAssignedTime.getTime()));

                            }
                        }, mAssignedTime.get(Calendar.HOUR_OF_DAY),
                        mAssignedTime.get(Calendar.MINUTE), false);

                assignedTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mAssignedTime = null;
                        mTimeAssignedSelector.setText("");
                    }
                });
                assignedTimePicker.show();
            }
        });
        mTimeDueSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDueTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);

                TimePickerDialog dpd = new TimePickerDialog(NewClassActivity.this,
                        R.style.StyledDialog,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mDueTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mDueTime.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mTimeDueSelector.setText(sdf.format(mDueTime.getTime()));

                            }
                        }, mDueTime.get(Calendar.HOUR_OF_DAY),
                        mDueTime.get(Calendar.MINUTE), false);
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDueTime = null;
                        mTimeDueSelector.setText("");
                    }
                });
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

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            Window mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
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
