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
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewClassActivity extends ActionBarActivity {
    private EditText mTitle;
    private EditText mDescription;
    private EditText mPeriod;
    private EditText mTimeAssignedSelector;
    private EditText mTimeDueSelector;
    private Calendar mAssignedTime;
    private Calendar mDueTime;
    private Button mEnter;
    private ActionBar mActionBar;
    private ArrayList<SchoolClass> mClassList;
    private Window mWindow;
    public static final String ASSIGNED_TIME_TAG = "ASSIGNED_TIME_TAG";
    public static final String DUE_TIME_TAG = "DUE_TIME_TAG";

    String mTitleString;
    int period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        mClassList = ClassList.getInstance(getApplicationContext()).getList();
        mTitle = (EditText) findViewById(R.id.edittext_title);
        mDescription = (EditText) findViewById(R.id.edittext_description);
        mPeriod = (EditText) findViewById(R.id.edittext_period);
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
            Toast.makeText(getApplicationContext(), "Enter a class title",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void setListeners() {
        mTimeAssignedSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), ASSIGNED_TIME_TAG);
            }
        });
        mTimeDueSelector.setOnClickListener(new View.OnClickListener() {
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
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.red_500)));

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.red_700));
        }
    }

    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                mClassList);

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
            mTimeAssignedSelector.setText(sdf.format(mAssignedTime.getTime()));
        }
        if (mDueTime != null && tag.equals(DUE_TIME_TAG)) {
            mTimeDueSelector.setText(sdf.format(mDueTime.getTime()));
        }
    }
}
