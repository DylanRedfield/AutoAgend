package com.dylanredfield.agendaapp;

import java.util.ArrayList;

import com.dylanredfield.agendaapp2.R;
import com.dylanredfield.agendaapp2.R.id;
import com.dylanredfield.agendaapp2.R.layout;
import com.dylanredfield.agendaapp2.R.menu;
import com.software.shell.fab.ActionButton;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// TODO add abilty to rename activities, assignments, and edit
// TODO change ArrayAdapter to hold custom view

// TODO list 1. Fix assignment sort 2. Add camera func 3. fix add ui
public class MainActivity extends ActionBarActivity {
	private ListView mListView;
	private ActionButton mButtonClass;
	private CustomAdapter adapter;
	private ArrayAdapter<String> noClassAdapter;
	private ArrayList<String> noClassList;
	public static final String EXTRA_INT_POSTITION = "com.dylanredfield.agendaapp.int_postition";
	public int tempInt;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		// Makes listview, sets up adapter, and applies adapter

		makeListView();

		// Add listener for each item in list
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(getApplicationContext(),
						ClassActivity.class);
				i.putExtra(EXTRA_INT_POSTITION, position);
				startActivity(i);

			}
		});

		// Add button listener that adds intent to make new class
		mButtonClass = (ActionButton) findViewById(id.action_button);
        mButtonClass.setButtonColor(getResources().getColor(R.color.red_500));
        mButtonClass.setImageDrawable(getResources().getDrawable(R.drawable.ic_note_add_white_36dp));
		mButtonClass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// intent to NewClassActivity
				Intent i = new Intent(getApplicationContext(),
						NewClassActivity.class);
				startActivity(i);

			}
		});
		// Creates contextMenu
		registerForContextMenu(mListView);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.red_700));
	}

	public void makeListView() {

		// Creates listview holding mClassList
		mListView = (ListView) findViewById(R.id.list);
		/*
		 * adapter = new ArrayAdapter<SchoolClass>(this,
		 * android.R.layout.simple_list_item_1, android.R.id.text1,
		 * ClassList.getInstance(getApplicationContext()).getList());
		 */
		adapter = new CustomAdapter(getApplicationContext(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				ClassList.getInstance(getApplicationContext()).getList());

		mListView.setAdapter(adapter);
		mListView.setEmptyView(findViewById(R.id.empty_list));

	}

	public void updateDatabase() {
		// Deletes all information in the database
		DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

		// Adds all classes from ArrayList back into database
		DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                ClassList.getInstance(getApplicationContext()).getList());

	}

	public void buttonClick(View v) {
		Intent i = new Intent(getApplicationContext(), NewClassActivity.class);
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// Creates contextMenu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.class_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {

		// Delete class from list
		case R.id.delete_class:
			// mClassList.getList().remove(info.position);
			ClassList.getInstance(getApplicationContext()).getList()
					.remove(info.position);
			makeListView();

			updateDatabase();
			return true;
			// Rename class
		case R.id.rename_class:
			// Makes int the value of index spot
			tempInt = info.position;
			userInputDialog("Rename class", "Enter new class name");
			adapter.notifyDataSetChanged();
			updateDatabase();
		default:
			return true;
		}
	}

	public void userInputDialog(String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(title);
		alert.setMessage(message);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);

		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Uses index spot to change name in array list
				ClassList.getInstance(getApplicationContext()).getList()
						.get(tempInt).setClassName(input.getText().toString());

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// as the app resumes from any activity update listview
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		} else {
			makeListView();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		updateDatabase();

	}

	public class CustomAdapter extends ArrayAdapter<SchoolClass> {

		private ArrayList<SchoolClass> mList;
        private TextView titleTextView;
        private TextView currentAssignment;
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, ArrayList<SchoolClass> objects) {
			super(context, resource, textViewResourceId, objects);
			mList = objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.class_row,
						null);
			}
			titleTextView = (TextView) convertView
					.findViewById(R.id.class_name_text);
			titleTextView.setText(mList.get(position).getClassName());

			currentAssignment = (TextView) convertView
					.findViewById(R.id.current_assignment);
			currentAssignment.setText(getAssignmentString(mList.get(position).getAssignments()
					.size()));
			return convertView;

		}

        private String getAssignmentString(int assignments) {
           if(assignments == 0 || assignments >1) {
               return "" + assignments + " assignments" ;
           } else {
               return "" + assignments + " assignment";
           }
        }

	}
}
