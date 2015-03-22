package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dylanredfield.agendaapp2.R;

import java.util.ArrayList;

public class AssignmentActivity extends ActionBarActivity {
    private ListView mAssignmentInfoList;
    private AssignmentInfoAdapter mAssignmentInfoAdapter;
    private int mIndexClass;
    private int mIndexAssignment;
    private ArrayList<SchoolClass> mClassList;
    private ActionBar mActionBar;
    private Window mWindow;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_assignment);


        mIndexAssignment = getIntent().getIntExtra(
                ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, 0);
        mIndexClass = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION,
                0);
        mClassList = ClassList.getInstance(getApplicationContext()).getList();

        makeListView();

        if (mClassList.get(mIndexClass)
                .getAssignments().get(mIndexAssignment).getFilePath() != null) {

        }
        setBars();

    }

    public void makeListView() {
        mAssignmentInfoList = (ListView) findViewById(R.id.assignments_list);
        mAssignmentInfoAdapter = new AssignmentInfoAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, mClassList
                .get(mIndexClass).getAssignments().get(mIndexAssignment).makeList2());
        mAssignmentInfoList.setAdapter(mAssignmentInfoAdapter);
    }

    public void makeListView(ListView listView, ArrayAdapter<String> adapter,
                             ArrayList<String> list) {

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);

        listView.setAdapter(adapter);
    }

    // Method with android:onClick attribute in XML
    public void onTextViewClick(View view) {
        Intent i = new Intent(getApplicationContext(), ImageFullScreenActivity.class);
        i.putExtra(MainActivity.EXTRA_INT_POSTITION, mIndexClass);
        i.putExtra(ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, mIndexAssignment);
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));
        mActionBar.setTitle(mClassList.get(mIndexClass).getClassName());

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Needs to re-instantiate list view since it is only created onCreate()
        mAssignmentInfoList = (ListView) findViewById(R.id.assignments_list);
        mAssignmentInfoAdapter = new AssignmentInfoAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, mClassList
                .get(mIndexClass).getAssignments().get(mIndexAssignment).makeList2());
        mAssignmentInfoList.setAdapter(mAssignmentInfoAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_assignment_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(),
                        EditAssignmentInfoActivity.class);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, mIndexClass);
                i.putExtra(ClassActivity.EXTRA_INT_ASSIGNMENT_POSTITION, mIndexAssignment);
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            substring = mList.get(position).substring(0, 2);

            // In the makeString method 2 chars are appended to the begining of the string
            // to check what kind of information it is.  If it equals a known info set the title
            // is set
            if (substring.equals("Ti")) {
                titleTextView.setText("Title");
            } else if (substring.equals("De")) {
                titleTextView.setText("Description");
            } else if (substring.equals("As")) {
                titleTextView.setText("Date Assigned");
            } else if (substring.equals("Du")) {
                titleTextView.setText("Date Due");
            } else if (substring.equals("Pi")) {
                titleTextView.setText("Picture");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTextViewClick(v);
                    }
                });
            }
            infoTextView = (TextView) convertView
                    .findViewById(R.id.assignment_info);
            infoTextView.setText(mList.get(position).substring(2));


            return convertView;

        }

    }
}
