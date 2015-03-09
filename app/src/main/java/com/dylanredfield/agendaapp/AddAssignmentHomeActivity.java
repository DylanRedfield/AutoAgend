package com.dylanredfield.agendaapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAssignmentHomeActivity extends ActionBarActivity {
    private EditText mTitle;
    private EditText mDescription;
    private EditText mDateAssignedPicker;
    private EditText mDateDuePicker;
    private EditText mClassSelector;
    private Button mEnterButton;
    private Calendar mAssignedDate;
    private Calendar mDueDate;
    private Context mContext;
    private Activity a;
    private Bitmap mBitmap;
    public static final String ASSIGNED_TAG = "ASSIGNED_TAG";
    public static final String DUE_TAG = "DUE_TAG";
    // get value from index from parent class
    private int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment_home);

        // Class index
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        mBitmap = (Bitmap) getIntent().getParcelableExtra(MainActivity.BITMAP_STRING);
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
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), ASSIGNED_TAG);
            }
        });

        mDateDuePicker = (EditText) findViewById(R.id.date_due_picker);
        mDateDuePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), DUE_TAG);
            }
        });

        mClassSelector = (EditText) findViewById(R.id.class_picker);
        mClassSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ListView listView = new ListView(a);
                listView.setAdapter(new ArrayAdapter<String>(a,
                        android.R.layout.simple_list_item_1,
                        ClassList.getInstance(getApplicationContext()).getListString()));

                final Dialog dialog = new Dialog(a);
                dialog.setTitle("Choose Class");
                dialog.setContentView(listView);
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        index = position;
                        updtaeClassEditText();
                        dialog.hide();
                    }
                });
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setTitle(ClassList.getInstance(getApplicationContext()).getList()
                .get(index).getClassName());

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));

    }

    public void updtaeClassEditText() {
        mClassSelector.setText(ClassList.getInstance(getApplicationContext()).getListString()
                .get(index));
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.enter_actionbar:
                if (!mTitle.getText().toString().equals("")) {
                    if (mAssignedDate == null) {
                        mAssignedDate = Calendar.getInstance();
                    }
                    ClassList
                            .getInstance(getApplicationContext())
                            .getList()
                            .get(index)
                            .getAssignments()
                            .add(new Assignment(mTitle.getText().toString(),
                                    mDescription.getText().toString(),
                                    mAssignedDate, mDueDate, mBitmap));
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
                ClassList.getInstance(getApplicationContext()).getList());

    }

    public void updateEditText(String tag) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (mAssignedDate != null && tag.equals(ASSIGNED_TAG)) {
            mDateAssignedPicker.setText(sdf.format(mAssignedDate.getTime()));
        }
        if (mAssignedDate != null && tag.equals(DUE_TAG)) {
            mDateDuePicker.setText(sdf.format(mDueDate.getTime()));
        }
    }
}
