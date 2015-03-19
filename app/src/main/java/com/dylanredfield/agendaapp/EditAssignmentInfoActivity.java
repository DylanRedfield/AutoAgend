package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by dylan_000 on 3/19/2015.
 */
public class EditAssignmentInfoActivity extends ActionBarActivity {
    public static final String ASSIGNED_TAG = "ASSIGNED_TAG";
    public static final String DUE_TAG = "DUE_TAG";
    private int classIndex;
    private int mAssignmentIndex;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mEndTime;
    private EditText mPeriod;
    private EditText mDateAssigned;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private android.support.v7.app.ActionBar mActionBar;
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
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), ASSIGNED_TAG);
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), DUE_TAG);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.red_500)));
        mActionBar.setTitle(mList.get(classIndex).getClassName());


        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.red_700));
        }
    }

    public void updateList() {

        //Need to copy assignments while making new class
        String temp = mList.get(classIndex).getAssignments().get(mAssignmentIndex).getFilePath();

        mList.get(classIndex).getAssignments().set(mAssignmentIndex,
                new Assignment(mTitleEditText.getText().toString(),
                        mDescriptionEditText.getText().toString(),
                        mAssignedTime,
                        mDueTime, temp));
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
                updateDatabase();
                finish();

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

    public void updateEditText(String tag) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (mAssignedTime != null && tag.equals(ASSIGNED_TAG)) {
            mDateAssigned.setText(sdf.format(mAssignedTime.getTime()));
        }
        if (mDueTime != null && tag.equals(DUE_TAG)) {
            mEndTime.setText(sdf.format(mDueTime.getTime()));
        }
    }

    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        Calendar c;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // If date due set 1 day behind
            if (getTag().equals(DUE_TAG)) {
                day++;
            }
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            c.set(year, month, day);

            // If assign was tagged set assign ET
            if (getTag().equals(ASSIGNED_TAG)) {
                mAssignedTime = c;
            }
            if (getTag().equals(DUE_TAG)) {
                mDueTime = c;
            }
            updateEditText(getTag());

        }
    }
}
