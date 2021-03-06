package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClassActivity extends ActionBarActivity {
    public static int REQUEST_IMAGE_CAPTURE_CLASS = 2;
    public static String EXTRA_INT_ASSIGNMENT_POSTITION =
            "com.dylanredfield.agendaapp.int_assignment_position";
    private ListView mAssignmentsListView;
    private AssignmentAdapter mAssignmentsAdapter;
    private ActionButton mButtonClass;
    private ActionButton mButtonPicture;
    private ActionButton mButtonText;
    private String mCurrentPhotoPath;
    private boolean showFlag = false;
    private int index;
    private ArrayList<SchoolClass> mClassList;
    private ArrayList<Assignment> mHiddenList;
    private ArrayList<Assignment> mVisibleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO wire edit info button. Should bring to activity similar to new
        // class
        // Will make a new schoolclass with new info, and replace current class
        // in list
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Gets index extra of class
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        createLists();
        // Creates AssignmentAdapter, ect
        instaniateAssignmentAdapter();


        // Instaniates ActionButtons and sets properties
        declareActionButtons();


        // Adds all listeners
        setListeners();

        // Sets statusbar and actionbar
        setBars();

        // Registers for context menu for Assignments
        // TODO make/rename to add context menu for InfoList
        registerForContextMenu(mAssignmentsListView);
    }

    public void createLists() {
        ClassList.getInstance(getApplicationContext()).sortByPeriod();
        mClassList = ClassList.getInstance(getApplicationContext()).getList();
        mClassList.get(index).sortAssignmentsByCompleted();
        mHiddenList = new ArrayList<>();
        mVisibleList = new ArrayList<>();
        for (Assignment a : mClassList.get(index).getAssignments()) {
            if (a.isHidden()) {
                mHiddenList.add(a);
            } else {
                mVisibleList.add(a);
            }
        }

    }

    public void instaniateAssignmentAdapter() {
        mAssignmentsListView = (ListView) findViewById(R.id.assignments_list);

        mAssignmentsAdapter = new AssignmentAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mVisibleList);
        mAssignmentsListView.setAdapter(mAssignmentsAdapter);
        mAssignmentsListView.setEmptyView(findViewById(R.id.empty_list));
    }

    public void declareActionButtons() {
        mButtonClass = (ActionButton) findViewById(R.id.action_button);

        // Call to second helper method that sets properties
        makeActionButton(mButtonClass, R.drawable.ic_file_document_white_36dp);

        mButtonPicture = (ActionButton) findViewById(R.id.action_button_picture);
        makeActionButton(mButtonPicture, R.drawable.ic_camera_white_36dp);

        mButtonText = (ActionButton) findViewById(R.id.action_button_assignment);
        makeActionButton(mButtonText, R.drawable.ic_file_document_white_36dp);
    }

    public ActionButton makeActionButton(ActionButton ab, int drawable) {
        // Creates, and sets ActionButtons
        ab.setButtonColor(getResources().getColor(R.color.primary_color));
        ab.setButtonColorPressed(getResources().getColor(R.color.dark_primary));
        ab.setImageDrawable(getResources().getDrawable(drawable));

        return ab;
    }

    public void setListeners() {
        mButtonClass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!showFlag) {
                    mButtonPicture.setVisibility(View.VISIBLE);
                    mButtonText.setVisibility(View.VISIBLE);

                    YoYo.with(Techniques.ZoomIn).duration(200).playOn(mButtonPicture);
                    YoYo.with(Techniques.ZoomIn).duration(200).playOn(mButtonText);

                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_close_white_48dp));
                    showFlag = true;
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    YoYo.with(Techniques.ZoomOut).duration(200).playOn(mButtonPicture);
                    YoYo.with(Techniques.ZoomOut).duration(200).playOn(mButtonText);
                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_file_document_white_36dp));
                    showFlag = false;
                }


            }
        });
        mButtonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mButtonText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // intent to NewClassActivity
                Intent i = new Intent(getApplicationContext(),
                        AddAssignmentActivity.class);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(i);
            }
        });

        // Set onItemClickListener for assignmentsList
        mAssignmentsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int tempPosition = 0;
                tempPosition = mClassList.get(index).getAssignments()
                        .lastIndexOf(mVisibleList.get(position));

                Intent i = new Intent(getApplicationContext(),
                        AssignmentActivity.class);
                i.putExtra(EXTRA_INT_ASSIGNMENT_POSTITION, tempPosition);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(i);

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));
        mActionBar.setTitle(mClassList
                .get(index).getClassName());

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            Window mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void dispatchTakePictureIntent() {

        // Makes intent to take pic
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                //eror
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CLASS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_CLASS && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();

            Intent i = new Intent(getApplicationContext(), AddAssignmentActivity.class);

            i.putExtra(MainActivity.FILE_LOCATION_STRING, mCurrentPhotoPath);
            i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
            startActivity(i);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "1mind_" + timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);
        mCurrentPhotoPath = photo.getAbsolutePath();
        return photo;
    }


    @Override
    protected void onResume() {
        super.onResume();
        showFlag = false;
        // recreates adapters to update them
        // TODO check to see if bundle is better for this
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        createLists();
        if (mAssignmentsAdapter == null) {
            instaniateAssignmentAdapter();
        } else {
            instaniateAssignmentAdapter();
            mAssignmentsAdapter.notifyDataSetChanged();
            Log.d("resumeTest", "resume");
        }
        mButtonPicture.setVisibility(View.INVISIBLE);
        mButtonText.setVisibility(View.INVISIBLE);
        mButtonClass.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_file_document_white_36dp));
        declareActionButtons();

        setBars();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Creates context
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assignment_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        switch (item.getItemId()) {
            // delete assignment
            case R.id.delete_assignment:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog


                        mVisibleList.remove(mClassList.get(index)
                                .getAssignments().get(info.position));
                        mClassList.get(index)
                                .getAssignments().remove(info.position);
                        // Reinstaniate the list
                        mAssignmentsAdapter.notifyDataSetChanged();
                        updateDatabase();
                        dialog.dismiss();
                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            case R.id.edit_assignment:
                int temp;
                temp = mClassList.get(index).getAssignments()
                        .lastIndexOf(mVisibleList.get(info.position));
                Intent i = new Intent(getApplicationContext(), EditAssignmentInfoActivity.class);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                i.putExtra(EXTRA_INT_ASSIGNMENT_POSTITION, temp);
                startActivity(i);
                return true;
            case R.id.hide_assignment:
                int tempHide;

                tempHide = mClassList.get(index).getAssignments()
                        .lastIndexOf(mVisibleList.get(info.position));
                mClassList.get(index).getAssignments().get(tempHide).setHidden(true);
                mVisibleList.remove(info.position);
                mAssignmentsAdapter.notifyDataSetChanged();
                updateDatabase();
                return true;
            case R.id.set_complete:
                int tempComplete;

                tempComplete = mClassList.get(index).getAssignments()
                        .lastIndexOf(mVisibleList.get(info.position));
                mClassList.get(index).getAssignments().get(tempComplete).setCompleted(
                        !mClassList.get(index).getAssignments().get(tempComplete).isCompleted());
                mAssignmentsAdapter.notifyDataSetChanged();
                updateDatabase();
                return true;
            default:
                return true;
        }

    }

    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                mClassList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void emptyPress(View v) {
        Intent i = new Intent(getApplicationContext(),
                AddAssignmentActivity.class);
        i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);

        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(),
                        EditClassInfoActivity.class);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(i);

                return true;
            case R.id.add_assignment:
                Intent intent = new Intent(getApplicationContext(),
                        AddAssignmentActivity.class);
                intent.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(intent);

                return true;
            case R.id.show_hidden:
                Intent hiddenIntent = new Intent(getApplicationContext(),
                        ClassHiddenActivity.class);
                hiddenIntent.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(hiddenIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class AssignmentAdapter extends ArrayAdapter<Assignment> {

        private ArrayList<Assignment> mList;
        private TextView titleTextView;
        private TextView backUpDueTextView;
        private TextView assignedDate;
        private TextView dueDate;
        private TextView divider;
        private ImageView imageView;
        private Calendar calendarAssigned;
        private Calendar calendarDue;

        public AssignmentAdapter(Context context, int resource,
                                 int textViewResourceId, ArrayList<Assignment> objects) {
            super(context, resource, textViewResourceId, objects);
            mList = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Save db on assignemnt add

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.assignment_row, null);
            }
            calendarAssigned = mList.get(position).getDateAssigned();
            calendarDue = mList.get(position).getDateDue();
            divider = (TextView) convertView.findViewById(R.id.divider);
            titleTextView = (TextView) convertView
                    .findViewById(R.id.assignment_text);
            if (mList.get(position).getTitle().length() < 50) {
                titleTextView.setText(mList.get(position).getTitle());
            } else {
                titleTextView.setText(mList.get(position).getTitle().substring(0, 50) + "...");
            }
            assignedDate = (TextView) convertView.findViewById(R.id.assigned_text);
            dueDate = (TextView) convertView.findViewById(R.id.due_text);
            imageView = (ImageView) convertView.findViewById(R.id.icon_image);
            backUpDueTextView = (TextView) convertView.findViewById(R.id.backup_due);

            if (calendarAssigned != null && calendarDue != null) {
                assignedDate.setVisibility(View.VISIBLE);
                assignedDate.setText("Assigned: " + calendarToString(calendarAssigned));
                backUpDueTextView.setVisibility(View.VISIBLE);
                backUpDueTextView.setText("Due: " + calendarToString(calendarDue));

            } else if (calendarAssigned != null && calendarDue == null) {
                assignedDate.setVisibility(View.VISIBLE);
                assignedDate.setText("Assigned: " + calendarToString(calendarAssigned));
                divider.setVisibility(View.GONE);
                dueDate.setVisibility(View.GONE);
                backUpDueTextView.setVisibility(View.GONE);
            } else if (calendarAssigned == null && calendarDue != null) {
                assignedDate.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                dueDate.setVisibility(View.VISIBLE);
                dueDate.setText("Due: " + calendarToString(calendarDue));
                backUpDueTextView.setVisibility(View.GONE);
            } else {
                assignedDate.setVisibility(View.GONE);
                divider.setVisibility(View.VISIBLE);
                divider.setText("No Date Assigned");
                dueDate.setVisibility(View.GONE);
                backUpDueTextView.setVisibility(View.GONE);
            }


            if (mList.get(position).getFilePath() != null) {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_file_image_box_grey600_36dp));
            } else {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_file_document_grey600_36dp));
            }

            if (mList.get(position).isCompleted()) {
                titleTextView.setPaintFlags
                        (titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                assignedDate.setPaintFlags
                        (assignedDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                divider.setPaintFlags
                        (divider.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dueDate.setPaintFlags
                        (dueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                backUpDueTextView.setPaintFlags
                        (backUpDueTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleTextView.setPaintFlags
                        (titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                assignedDate.setPaintFlags
                        (assignedDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                divider.setPaintFlags
                        (divider.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                dueDate.setPaintFlags
                        (dueDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                backUpDueTextView.setPaintFlags
                        (backUpDueTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }


            return convertView;

        }


        public String calendarToString(Calendar c) {
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(c.getTime());
        }

    }

}
