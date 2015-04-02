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

public class EditClassInfoActivity extends ActionBarActivity {
    private int classIndex;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mEndTime;
    private EditText mPeriod;
    private EditText mDateAssigned;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private ActionBar mActionBar;
    private Window mWindow;
    private String myFormat = "h:mm a";
    private ArrayList<SchoolClass> mList;
    private boolean dateSet;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_class_info);

        classIndex = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        mList = ClassList.getInstance(getApplicationContext()).getList();

        mTitleEditText = (EditText) findViewById(R.id.edittext_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edittext_description);
        mDateAssigned = (EditText) findViewById(R.id.datepicker_assigned);
        mEndTime = (EditText) findViewById(R.id.datepicker_due);
        mPeriod = (EditText) findViewById(R.id.edittext_period);
        mAssignedTime = mList.get(classIndex).getStartTime();
        mDueTime = mList.get(classIndex).getEndTime();


        setEditText();
        setListeners();
        setBars();
    }

    public void setEditText() {
        mTitleEditText.setText(mList.get(classIndex).getClassName());
        mDescriptionEditText.setText(mList.get(classIndex).getDescription());
        if (mList.get(classIndex).getStartTime() != null) {
            mDateAssigned.setText(calanderToString(mList.get(classIndex).getStartTime()));
        }

        if (mList.get(classIndex).getEndTime() != null) {
            mEndTime.setText(calanderToString(mList.get(classIndex).getEndTime()));
        }
        mPeriod.setText("" + mList.get(classIndex).getPeriod());
    }

    public void setListeners() {
        mDateAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSet = false;
                mAssignedTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);
                TimePickerDialog dpd = new TimePickerDialog(EditClassInfoActivity.this,
                        R.style.StyledDialog,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                dateSet = true;
                                mAssignedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mAssignedTime.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateAssigned.setText(sdf.format(mAssignedTime.getTime()));

                            }
                        }, mAssignedTime.get(Calendar.HOUR_OF_DAY),
                        mAssignedTime.get(Calendar.MINUTE), false);
                dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Cancel code here

                        if (!dateSet) {
                            mAssignedTime = null;
                            mDateAssigned.setText("");
                        }
                    }

                });
                dpd.show();
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSet = false;

                mDueTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);
                TimePickerDialog dpd = new TimePickerDialog(EditClassInfoActivity.this,
                        R.style.StyledDialog,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                dateSet = true;
                                mDueTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mDueTime.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mEndTime.setText(sdf.format(mDueTime.getTime()));

                            }
                        }, mDueTime.get(Calendar.HOUR_OF_DAY),
                        mDueTime.get(Calendar.MINUTE), false);

                dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!dateSet) {
                            mDueTime = null;
                            mEndTime.setText("");
                        }
                    }
                });
                dpd.show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.primary_color)));
        mActionBar.setTitle(mList.get(classIndex).getClassName());


        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    public void updateList() {

        //Need to copy assignments while making new class
        ArrayList<Assignment> temp;
        temp = mList.get(classIndex).getAssignments();

        if (!mTitleEditText.getText().toString().equals("")) {

            if (String.valueOf(Integer.parseInt(mPeriod.getText().toString())).length() < 3) {
                mList.set(classIndex, new SchoolClass(mTitleEditText.getText().toString(),
                        mDescriptionEditText.getText().toString(),
                        Integer.parseInt(mPeriod.getText().toString()), mAssignedTime, mDueTime));
                mList.get(classIndex).setAssignments(temp);
                updateDatabase();
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        EditClassInfoActivity.this);
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
                    EditClassInfoActivity.this);
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

    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                mList);

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
                updateList();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String calanderToString(Calendar c) {
        String myFormat = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(c.getTime());
    }

}
