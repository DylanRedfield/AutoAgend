package com.dylanredfield.agendaapp;

import java.util.ArrayList;

import com.dylanredfield.agendaapp2.R;
import com.dylanredfield.agendaapp2.R.id;
import com.dylanredfield.agendaapp2.R.layout;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AssignmentActivity extends ActionBarActivity {
	private ListView mAssignmentInfoList;
	private AssignmentInfoAdapter mAssignmentInfoAdapter;
	private int indexClass;
	private int indexAssignment;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_assignment);


		indexAssignment = getIntent().getIntExtra(
				ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, 0);
		indexClass = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION,
				0);
        		ActionBar ab = getSupportActionBar();
		ab.setTitle(ClassList.getInstance(getApplicationContext()).getList()
                .get(indexClass).getClassName());

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));
		/*
		 * mAssignmentInfoList = (ListView) findViewById(R.id.assignments_list);
		 * makeListView( mAssignmentInfoList, mAssignmentInfoAdapter,
		 * ClassList.getInstance(getApplicationContext()).getList()
		 * .get(indexClass).getAssignments() .get(indexAssignment).makeList());
		 */
		mAssignmentInfoList = (ListView) findViewById(R.id.assignments_list);
		mAssignmentInfoAdapter = new AssignmentInfoAdapter(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, ClassList
						.getInstance(getApplicationContext()).getList()
						.get(indexClass).getAssignments().get(indexAssignment).makeList2());
		mAssignmentInfoList.setAdapter(mAssignmentInfoAdapter);
	}

	public void makeListView(ListView listView, ArrayAdapter<String> adapter,
			ArrayList<String> list) {

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, list);

		listView.setAdapter(adapter);
	}

	public class AssignmentInfoAdapter extends ArrayAdapter<String> {

		private ArrayList<String> mList;
		private TextView titleTextView;
		private TextView infoTextView;
		private String substring;

		public AssignmentInfoAdapter(Context context, int resource,
				int textViewResourceId, ArrayList<String> objects) {
			super(context, resource, textViewResourceId, objects);
			mList = objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.assignment_detail_row, null);
			}
			titleTextView = (TextView) convertView
					.findViewById(R.id.assignment_info_label);
			substring= mList.get(position).substring(0,2);
			if(substring.equals("Ti")) {
				titleTextView.setText("Title");
			} else if(substring.equals("De")) {
				titleTextView.setText("Description");
			} else if(substring.equals("As")) {
				titleTextView.setText("Date Assigned");
			} else if(substring.equals("Du")) {
				titleTextView.setText("Date Due");
			}
			infoTextView = (TextView) convertView
					.findViewById(R.id.assignment_info);
			infoTextView.setText(mList.get(position).substring(2));

			return convertView;

		}

	}
}
