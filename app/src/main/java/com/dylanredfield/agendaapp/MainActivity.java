package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;
import com.dylanredfield.agendaapp2.R.id;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// TODO add abilty to rename activities, assignments, and edit

// TODO Add due date to assignment list
// TODO Add new class dialog instead of activity
// TODO list 1. Fix assignment sort
public class MainActivity extends ActionBarActivity {
    private ListView mListView;
    private ActionButton mButtonClass;
    private ActionButton mButtonCancel;
    private ActionButton mButtonPicture;
    private ActionButton mButtonText;
    private ActionBar mActionBar;
    private Window mWindow;
    private RelativeLayout mLinearLayout;
    private CustomAdapter adapter;
    private boolean showFlag;
    private String mCurrentPhotoPath;
    private ArrayList<SchoolClass> mClassList;
    public static final String EXTRA_INT_POSTITION = "com.dylanredfield.agendaapp.int_postition";
    public int tempInt;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String BITMAP_STRING = "BIT_MAP_IMAGE";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClassList = ClassList.getInstance(getApplicationContext()).getList();
        // Makes listview that holds classes, sets up adapter, and applies adapter
        makeListView();

        // Creates ActionButton for 3 buttons and sets properties
        declareActionButtons();

        // Adds all listeners for buttons, and items in list
        addListeners();

        // Sets up ActionBar and StatusBar
        setBars();

        // Creates contextMenu
        registerForContextMenu(mListView);


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
                android.R.layout.simple_list_item_1, android.R.id.text1, mClassList);

        mListView.setAdapter(adapter);
        mListView.setEmptyView(findViewById(R.id.empty_list));

    }

    public void declareActionButtons() {
        mButtonClass = (ActionButton) findViewById(id.action_button);

        // Call to second helper method that sets properties
        makeActionButton(mButtonClass, R.drawable.ic_note_add_white_36dp);

        mButtonPicture = (ActionButton) findViewById(id.action_button_picture);
        makeActionButton(mButtonPicture, R.drawable.ic_file_image_box_white_48dp);

        mButtonText = (ActionButton) findViewById(id.action_button_assignment);
        makeActionButton(mButtonText, R.drawable.ic_note_add_white_36dp);
    }

    public ActionButton makeActionButton(ActionButton ab, int drawable) {
        // Creates, and sets ActionButtons
        ab.setButtonColor(getResources().getColor(R.color.red_500));
        ab.setButtonColorPressed(getResources().getColor(R.color.red_900));
        ab.setImageDrawable(getResources().getDrawable(drawable));
        ab.setButtonColorPressed(getResources().getColor(R.color.red_900));

        return ab;
    }

    public void addListeners() {
        // Add listener for each item in list
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // When clicked ClassActivty is called to show assignments
                Intent i = new Intent(getApplicationContext(),
                        ClassActivity.class);
                // Position represents the index spot in the ClassList
                i.putExtra(EXTRA_INT_POSTITION, position);
                startActivity(i);

            }
        });

        // Add listener for base ActionBarMenu
        mButtonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Opens and closes ActionButtonMenu

                // Show flags defaults value
                if (!showFlag) {

                    // Sets buttons visible (relative layout)
                    mButtonPicture.setVisibility(View.VISIBLE);
                    mButtonText.setVisibility(View.VISIBLE);

                    // Changes base button to drawable close
                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_close_white_48dp));
                    showFlag = true;
                } else {

                    // Opposite as above
                    mButtonPicture.setVisibility(View.INVISIBLE);
                    mButtonText.setVisibility(View.INVISIBLE);
                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_note_add_white_36dp));
                    showFlag = false;
                }


            }
        });
        mButtonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Creates intent to take picture
                dispatchTakePictureIntent();

            }
        });

        // Add listener for text only assignment
        mButtonText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // intent to NewClassActivity, makes sure a class is made because
                // will need to choose class

                if (ClassList.getInstance(getApplicationContext()).getList().size() > 0) {
                    Intent i = new Intent(getApplicationContext(),
                            AddAssignmentHomeActivity.class);
                    startActivity(i);
                } else {
                    // If there was no class forces to add one
                    Toast.makeText(getApplicationContext(), "Create a class first by clicking " +
                                    "plus above",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.red_700));
        }
    }


    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(mClassList);

    }

    // Is used for xml android:onClick. Used when listView is empty
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
                mClassList.get(tempInt).setClassName(input.getText().toString());

            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.show();
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

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();

            Intent i = new Intent(getApplicationContext(), AddAssignmentHomeActivity.class);
            i.putExtra("TEST", mCurrentPhotoPath);
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

        // as the app resumes from any activity update listview
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            makeListView();
        }
        mButtonPicture.setVisibility(View.INVISIBLE);
        mButtonText.setVisibility(View.INVISIBLE);
        mButtonClass.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_note_add_white_36dp));
        showFlag = false;

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

    // CustomAdapter for ListView
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

            // Set to class name
            titleTextView.setText(mList.get(position).getClassName());

            currentAssignment = (TextView) convertView
                    .findViewById(R.id.current_assignment);

            // Sets to ammount of current assignments.
            // Get assignment String makes sure correct plural is used
            currentAssignment.setText(getAssignmentString(mList.get(position).getAssignments()
                    .size()));
            return convertView;

        }

        private String getAssignmentString(int assignments) {
            if (assignments == 0 || assignments > 1) {
                return "" + assignments + " assignments";
            } else {
                return "" + assignments + " assignment";
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.enter_actionbar:
                Intent i = new Intent(getApplicationContext(),
                        NewClassActivity.class);
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
