package com.dylanredfield.agendaapp;

import com.dylanredfield.agendaapp2.R;
import com.dylanredfield.agendaapp2.R.id;
import com.dylanredfield.agendaapp2.R.layout;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                		ActionBar ab = getSupportActionBar();

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));

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
                        period = ClassList.getInstance(getApplicationContext()).getList()
                                .size() + 1;

                    } else {
                        period = Integer.parseInt(mPeriod.getText().toString());
                    }
                    ClassList.getInstance(getApplicationContext()).addSchoolClass(
                            new SchoolClass(mTitle.getText().toString(),
                                    mDescription.getText().toString(), period));

                    updateDatabase();
                    finish();
                    ActionBar ab = getSupportActionBar();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Enter a class title",
                            Toast.LENGTH_SHORT).show();
                }

    }
	public void updateDatabase() {
		// Deletes all information in the database
		DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

		// Adds all classes from ArrayList back into database
		DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
				ClassList.getInstance(getApplicationContext()).getList());

	}
}
