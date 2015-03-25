package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditAssignmentInfoActivity extends ActionBarActivity {
    private int mClassIndex;
    private int mAssignmentIndex;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mEndTime;
    private EditText mDateAssigned;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private Calendar mDialogCalander;

    private String mFormat = "MM/dd/yy";
    private ArrayList<SchoolClass> mList;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_assignment_info);

        mClassIndex = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        mAssignmentIndex = getIntent().getIntExtra(
                ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, 0);
        mList = ClassList.getInstance(getApplicationContext()).getList();
        mDialogCalander = Calendar.getInstance();


        mAssignedTime = mList.get(mClassIndex).getAssignments().get(mAssignmentIndex)
                .getDateAssigned();
        mDueTime = mList.get(mClassIndex).getAssignments().get(mAssignmentIndex).getDateDue();

        findViewsByIds();
        setEditText();
        setListeners();
        setBars();
    }

    public void findViewsByIds() {
        mTitleEditText = (EditText) findViewById(R.id.edittext_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edittext_description);
        mDateAssigned = (EditText) findViewById(R.id.date_assigned_picker);
        mEndTime = (EditText) findViewById(R.id.date_due_picker);
    }
    public void setEditText() {
        mTitleEditText.setText(mList.get(mClassIndex).getAssignments().get(mAssignmentIndex)
                .getTitle());
        mDescriptionEditText.setText(mList.get(mClassIndex).getAssignments().get(mAssignmentIndex)
                .getDescription());
        if (mList.get(mClassIndex).getAssignments()
                .get(mAssignmentIndex).getDateAssigned() != null) {
            mDateAssigned.setText(calanderToString(mList.get(mClassIndex).getAssignments()
                    .get(mAssignmentIndex).getDateAssigned()));
        }
        if (mList.get(mClassIndex).getAssignments()
                .get(mAssignmentIndex).getDateDue() != null) {
            mEndTime.setText(calanderToString(mList.get(mClassIndex).getAssignments()
                    .get(mAssignmentIndex).getDateDue()));
        }
    }

    public void setListeners() {
        mDateAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), ASSIGNED_TAG);
                DatePickerDialog dpd = new DatePickerDialog(EditAssignmentInfoActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDialogCalander.set(year, monthOfYear, dayOfMonth);


                                mAssignedTime = mDialogCalander;

                                SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
                                mDateAssigned.setText(sdf.format(mAssignedTime.getTime()));
                            }
                        }, mDialogCalander.get(Calendar.YEAR), mDialogCalander.get(Calendar.MONTH),
                        mDialogCalander.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), DUE_TAG);
                DatePickerDialog dpd = new DatePickerDialog(EditAssignmentInfoActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDialogCalander.set(year, monthOfYear, dayOfMonth);


                                mDueTime = mDialogCalander;

                                SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
                                mEndTime.setText(sdf.format(mDueTime.getTime()));
                            }
                        }, mDialogCalander.get(Calendar.YEAR), mDialogCalander.get(Calendar.MONTH),
                        mDialogCalander.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.primary_color)));
        actionBar.setTitle(mList.get(mClassIndex).getClassName());


        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    public void updateList() {

        //Need to copy assignments while making new class
        String temp = mList.get(mClassIndex).getAssignments().get(mAssignmentIndex).getFilePath();

        if (!mTitleEditText.getText().toString().equals("")) {

            mList.get(mClassIndex).getAssignments().set(mAssignmentIndex,
                    new Assignment(mTitleEditText.getText().toString(),
                            mDescriptionEditText.getText().toString(),
                            mAssignedTime,
                            mDueTime, temp));
            updateDatabase();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Enter a Title", Toast.LENGTH_SHORT).show();
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
