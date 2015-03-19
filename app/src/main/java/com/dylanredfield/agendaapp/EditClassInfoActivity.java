package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.TimePicker;

import com.dylanredfield.agendaapp2.R;

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
    private ArrayList<SchoolClass> mList;
    public static final String ASSIGNED_TIME_TAG = "ASSIGNED_TIME_TAG";
    public static final String DUE_TIME_TAG = "DUE_TIME_TAG";

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
        mDateAssigned.setText(calanderToString(mList.get(classIndex).getStartTime()));
        mEndTime.setText(calanderToString(mList.get(classIndex).getEndTime()));
        mPeriod.setText("" + mList.get(classIndex).getPeriod());
    }

    public void setListeners() {
        mDateAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), ASSIGNED_TIME_TAG);
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), DUE_TIME_TAG);
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
        ArrayList<Assignment> temp = new ArrayList<Assignment>();
        temp = mList.get(classIndex).getAssignments();

        mList.set(classIndex, new SchoolClass(mTitleEditText.getText().toString(),
                mDescriptionEditText.getText().toString(),
                Integer.parseInt(mPeriod.getText().toString()), mAssignedTime, mDueTime));
        mList.get(classIndex).setAssignments(temp);
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
        String myFormat = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(c.getTime());
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        Calendar c;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

            // If assign was tagged set assign ET
            if (getTag().equals(ASSIGNED_TIME_TAG)) {
                mAssignedTime = c;
            }
            if (getTag().equals(DUE_TIME_TAG)) {
                mDueTime = c;
            }
            updateEditText(getTag());
        }
    }

    public void updateEditText(String tag) {
        String myFormat = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (mAssignedTime != null && tag.equals(ASSIGNED_TIME_TAG)) {
            mDateAssigned.setText(sdf.format(mAssignedTime.getTime()));
        }
        if (mDueTime != null && tag.equals(DUE_TIME_TAG)) {
            mEndTime.setText(sdf.format(mDueTime.getTime()));
        }
    }
}
