package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddAssignmentHomeActivity extends ActionBarActivity {
    private EditText mTitle;
    private EditText mDescription;
    private EditText mDateAssignedPicker;
    private EditText mDateDuePicker;
    private EditText mClassSelector;
    private Calendar mAssignedDate;
    private Calendar mDueDate;
    private Calendar mDialogCalendar;
    private Activity mActivityContext;
    private ArrayList<SchoolClass> mClassList;
    private String myFormat = "MM/dd/yy";
    private String mFileLocation;
    // get value from index from parent class
    private int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment_home);

        // Class index
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        // Needs location of picture in order to make class
        mFileLocation = getIntent().getStringExtra(MainActivity.FILE_LOCATION_STRING);

        mDialogCalendar = Calendar.getInstance();

        mClassList = ClassList.getInstance(getApplicationContext()).getList();

        // Used to make ListView in class selector
        mActivityContext = this;
        while (mActivityContext.getParent() != null) {
            mActivityContext = mActivityContext.getParent();
        }

        findViewsByIds();
        setListeners();
        checkTimeFrame();
        setBars();
    }

    public void findViewsByIds() {
        mTitle = (EditText) findViewById(R.id.edittext_title);
        mDescription = (EditText) findViewById(R.id.edittext_description);
        mDateAssignedPicker = (EditText) findViewById(R.id.date_assigned_picker);
        mDateDuePicker = (EditText) findViewById(R.id.date_due_picker);
        mClassSelector = (EditText) findViewById(R.id.class_picker);
    }

    public void setListeners() {
        mDateAssignedPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), ASSIGNED_TAG);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentHomeActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDialogCalendar.set(year, monthOfYear, dayOfMonth);


                                mAssignedDate = mDialogCalendar;

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateAssignedPicker.setText(sdf.format(mAssignedDate.getTime()));
                            }
                        },
                        mDialogCalendar.get(Calendar.YEAR),
                        mDialogCalendar.get(Calendar.MONTH),
                        mDialogCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        mDateDuePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getFragmentManager(), DUE_TAG);
                DatePickerDialog dpd = new DatePickerDialog(AddAssignmentHomeActivity.this,
                        R.style.StyledDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDialogCalendar.set(year, monthOfYear, dayOfMonth);


                                mDueDate = mDialogCalendar;

                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                mDateDuePicker.setText(sdf.format(mDueDate.getTime()));
                            }
                        },
                        mDialogCalendar.get(Calendar.YEAR),
                        mDialogCalendar.get(Calendar.MONTH),
                        // Add 1 because most assignments are due the day after
                        mDialogCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                dpd.show();
            }
        });
        mClassSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Creates listview that is added into a dialog
                ListView listView = new ListView(mActivityContext);

                // The list view holds strings of the class titles
                listView.setAdapter(new ArrayAdapter<>(mActivityContext,
                        android.R.layout.simple_list_item_1,
                        ClassList.getInstance(getApplicationContext()).getListString()));

                final Dialog dialog = new Dialog(mActivityContext);
                dialog.setTitle("Choose Class");
                dialog.setContentView(listView);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // Changes index to be the position the user selected
                        index = position;
                        updateClassEditText();
                        dialog.hide();
                    }
                });
            }
        });
    }

    // Currently doesn't work over 2 days
    public void checkTimeFrame() {
        Calendar mTimeFrameCalendar = Calendar.getInstance();
        int currentMinutes = mTimeFrameCalendar.get(Calendar.HOUR_OF_DAY) * 60 +
                mTimeFrameCalendar.get(Calendar.MINUTE);
        int startMinutes;
        int endMinutes;

        ArrayList<SchoolClass> mList = mClassList;

        // Changes index if assignment falls under mTimeFrameCalendar frame of class
        for (int a = 0; a < mList.size(); a++) {

            if (mList.get(a).getStartTime() != null && mList.get(a).getEndTime() != null) {
                startMinutes = mList.get(a).getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                        + mList.get(a).getStartTime().get(Calendar.MINUTE);
                endMinutes = mList.get(a).getEndTime().get(Calendar.HOUR_OF_DAY) * 60
                        + mList.get(a).getEndTime().get(Calendar.MINUTE);
                if (currentMinutes >= startMinutes && currentMinutes <= endMinutes) {
                    index = a;
                    updateClassEditText();
                } else {
                    Log.d("test1", "Current mTimeFrameCalendar: " + currentMinutes
                            + "\n Start Time: " + startMinutes
                            + "\n End Time: " + endMinutes);
                }
            }
        }
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
                ClassList.getInstance(getApplicationContext()).getList());

    }

    public void updateClassEditText() {
        mClassSelector.setText(ClassList.getInstance(getApplicationContext()).getListString()
                .get(index));
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
                if (!mTitle.getText().toString().equals("")) {
                    if (!mClassSelector.getText().toString().equals("")) {
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
                        Toast.makeText(getApplicationContext(), "Select class",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter title",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
