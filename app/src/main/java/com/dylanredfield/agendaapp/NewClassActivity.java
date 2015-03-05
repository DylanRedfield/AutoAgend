package com.dylanredfield.agendaapp;

import com.dylanredfield.agendaapp2.R;
import com.dylanredfield.agendaapp2.R.id;
import com.dylanredfield.agendaapp2.R.layout;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewClassActivity extends ActionBarActivity {
	private EditText mTitle;
	private EditText mDescription;
	private EditText mPeriod;
	private Button mEnter;

	String mTitleString;
	int period;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_class);

		mTitle = (EditText) findViewById(R.id.edittext_title);
		mDescription = (EditText) findViewById(R.id.edittext_description);
		mPeriod = (EditText) findViewById(R.id.edittext_period);

		mEnter = (Button) findViewById(R.id.button_sumbit_class);
		mEnter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// adds new SchoolClass with data from edittexts
				// DatabaseHandler.getInstance(getApplicationContext()).addClass(
				// new SchoolClass(mTitle.getText().toString()));

				// TODO accept all input types (period cant be null
				if (mPeriod.getText().toString().equals("")) {
					period = 0;

				} else {
					period = Integer.parseInt(mPeriod.getText().toString());
				}
				ClassList.getInstance(getApplicationContext()).addSchoolClass(
						new SchoolClass(mTitle.getText().toString(),
								mDescription.getText().toString(), period));

				updateDatabase();
				finish();

			}
		});
	}

	public void updateDatabase() {
		// Deletes all information in the database
		DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

		// Adds all classes from ArrayList back into database
		DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
				ClassList.getInstance(getApplicationContext()).getList());

	}
}
