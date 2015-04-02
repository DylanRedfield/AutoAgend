package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

public class EditAssignmentInfoActivity extends ActionBarActivity {
    private int classIndex;
    private int mAssignmentIndex;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mEndTime;
    private EditText mDateAssigned;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private android.support.v7.app.ActionBar mActionBar;

    private String myFormat = "MM/dd/yy";
    private Window mWindow;
    private ArrayList<SchoolClass> mList;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_assignment_info);

        classIndex = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        mAssignmentIndex = getIntent().getIntExtra(
                ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, 0);
        mList = ClassList.getInstance(getApplicationContext()).getList();
        mAssignedTime = Calendar.getInstance();
        mDueTime = Calendar.getInstance();

        mTitleEditText = (EditText) findViewById(R.id.edittext_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edittext_description);
        mDateAssigned = (EditText) findViewById(R.id.date_assigned_picker);
        mEndTime = (EditText) findViewById(R.id.date_due_picker);
        mAssignedTime = mList.get(classIndex).getAssignments().get(mAssignmentIndex)
                .getDateAssigned();
        mDueTime = mList.get(classIndex).getAssignments().get(mAssignmentIndex).getDateDue();

        setEditText();
        setListeners();
        setBars();
    }

    public void setEditText() {
        mTitleEditText.setText(mList.get(classIndex).getAssignments().get(mAssignmentIndex)
                .getTitle());
        mDescriptionEditText.setText(mList.get(classIndex).getAssignments().get(mAssignmentIndex)
                .getDescription());
        if (mList.get(classIndex).getAssignments()
                .get(mAssignmentIndex).getDateAssigned() != null) {
            mDateAssigned.setText(calanderToString(mList.get(classIndex).getAssignments()
                    .get(mAssignmentIndex).getDateAssigned()));
        }
        if (mList.get(classIndex).getAssignments()
                .get(mAssignmentIndex).getDateDue() != null) {
            mEndTime.setText(calanderToString(mList.get(classIndex).getAssignments()
                    .get(mAssignmentIndex).getDateDue()));
        }
    }

    public void setListeners() {
        mDateAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), ASSIGNED_TAG);
                mAssignedTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);
                DatePickerDialog dpd = new DatePickerDialog(EditAssignmentInfoActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mAssignedTime.set(year, monthOfYear, dayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateAssigned.setText(sdf.format(mAssignedTime.getTime()));
                            }
                        }, mAssignedTime.get(Calendar.YEAR), mAssignedTime.get(Calendar.MONTH),
                        mAssignedTime.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mAssignedTime = null;
                        mDateAssigned.setText("");
                    }
                });
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), DUE_TAG);

                mDueTime = Calendar.getInstance();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);
                DatePickerDialog dpd = new DatePickerDialog(EditAssignmentInfoActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDueTime.set(year, monthOfYear, dayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mEndTime.setText(sdf.format(mDueTime.getTime()));
                            }
                        }, mDueTime.get(Calendar.YEAR), mDueTime.get(Calendar.MONTH),
                        mDueTime.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDueTime = null;
                        mEndTime.setText("");
                    }
                });
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
        String temp = mList.get(classIndex).getAssignments().get(mAssignmentIndex).getFilePath();

        if (!mTitleEditText.getText().toString().equals("")) {
            mList.get(classIndex).getAssignments().set(mAssignmentIndex,
                    new Assignment(mTitleEditText.getText().toString(),
                            mDescriptionEditText.getText().toString(),
                            mAssignedTime,
                            mDueTime, temp));
            updateDatabase();
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    EditAssignmentInfoActivity.this);
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
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(c.getTime());
    }


}
