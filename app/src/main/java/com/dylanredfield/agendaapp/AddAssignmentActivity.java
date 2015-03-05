package com.dylanredfield.agendaapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.dylanredfield.agendaapp2.R;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddAssignmentActivity extends ActionBarActivity {
	private EditText mTitle;
	private EditText mDescription;
	private EditText mDateAssignedPicker;
	private EditText mDateDuePicker;
	private Button mEnterButton;
	private Calendar mAssignedDate;
	private Calendar mDueDate;
	public static final String ASSIGNED_TAG = "ASSIGNED_TAG";
	public static final String DUE_TAG = "DUE_TAG";
	// get value from index from parent class
	private int index;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);

		// Class index
		index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);

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

		mEnterButton = (Button) findViewById(R.id.button_add_assignment);
		mEnterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ClassList
						.getInstance(getApplicationContext())
						.getList()
						.get(index)
						.getAssignments()
						.add(new Assignment(mTitle.getText().toString(),
								mDescription.getText().toString(),
								mAssignedDate, mDueDate));
				finish();

			}
		});

		ActionBar ab = getActionBar();
		ab.setTitle(ClassList.getInstance(getApplicationContext()).getList()
				.get(index).getClassName());
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
