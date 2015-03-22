package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddAssignmentActivity extends ActionBarActivity implements
        DatePickerDialogFragment.DatePickerDialogHandler, CalendarDatePickerDialog.OnDateSetListener {
    public static final String ASSIGNED_TAG = "ASSIGNED_TAG";
    public static final String DUE_TAG = "DUE_TAG";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private EditText mTitle;
    private EditText mDescription;
    private EditText mDateAssignedPicker;
    private EditText mDateDuePicker;
    private EditText mClassSelector;
    private Button mEnterButton;
    private Calendar mAssignedDate;
    private Calendar mDueDate;
    private Context mContext;
    private ActionBar mActionBar;
    private Calendar c;
    private Window mWindow;
    private Activity a;
    private String myFormat = "MM/dd/yy";
    private String mFileLocation;
    private ArrayList<SchoolClass> mClassList;
    // get value from index from parent class
    private int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        // Class index
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);

        mClassList = ClassList.getInstance(getApplicationContext()).getList();

        c = Calendar.getInstance();

        mFileLocation = getIntent().getStringExtra("TEST");
        a = this;
        while (a.getParent() != null) {
            a = a.getParent();
        }

        mTitle = (EditText) findViewById(R.id.edittext_title);
        mDescription = (EditText) findViewById(R.id.edittext_description);

        mDateAssignedPicker = (EditText) findViewById(R.id.date_assigned_picker);

        mDateAssignedPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), ASSIGNED_TAG);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                c.set(year, monthOfYear, dayOfMonth);


                                mAssignedDate = c;

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateAssignedPicker.setText(sdf.format(mAssignedDate.getTime()));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        mDateDuePicker = (EditText) findViewById(R.id.date_due_picker);
        mDateDuePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), DUE_TAG);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                c.set(year, monthOfYear, dayOfMonth);


                                mDueDate = c;

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateDuePicker.setText(sdf.format(mDueDate.getTime()));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH) + 1);
                dpd.show();
            }
        });

        setBars();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*public class ClassDialog extends DialogFragment {
        Calendar c;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

        }
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_enter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onDialogDateSet(int year, int monthOfYear, int dayOfMonth) {
        // Do something with your date!
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.enter_actionbar:
                if (!mTitle.getText().toString().equals("")) {
                    if (mAssignedDate == null) {
                        mAssignedDate = Calendar.getInstance();
                    }
                    mClassList
                            .get(index)
                            .getAssignments()
                            .add(new Assignment(mTitle.getText().toString(),
                                    mDescription.getText().toString(),
                                    mAssignedDate, mDueDate, mFileLocation));
                    updateDatabase();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter a title",
                            Toast.LENGTH_SHORT).show();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));
        mActionBar.setTitle(mClassList
                .get(index).getClassName());

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    public void updateEditText(String tag) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (mAssignedDate != null && tag.equals(ASSIGNED_TAG)) {
            mDateAssignedPicker.setText(sdf.format(mAssignedDate.getTime()));
        }
        if (mDueDate != null && tag.equals(DUE_TAG)) {
            mDateDuePicker.setText(sdf.format(mDueDate.getTime()));
        }
    }

    @Override
    public void onDialogDateSet(int i, int i2, int i3, int i4) {

    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {

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
                mAssignedDate = c;
            }
            if (getTag().equals(DUE_TAG)) {
                mDueDate = c;
            }
            updateEditText(getTag());

        }
    }
}
