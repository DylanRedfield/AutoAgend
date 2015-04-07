package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dylanredfield.agendaapp.R.id;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


// TODO Add due date to assignment list
public class MainActivity extends ActionBarActivity {
    public static final String EXTRA_INT_POSTITION = "com.dylanredfield.agendaapp.int_postition";
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String FILE_LOCATION_STRING = "FILE_LOCATION";
    public static int NOTIFCATION_CODE = 20;
    private ListView mListView;
    private ActionButton mButtonClass;
    private ActionButton mButtonPicture;
    private ActionButton mButtonText;
    private ActionBar mActionBar;
    private Window mWindow;
    private CustomAdapter adapter;
    private boolean showFlag;
    private String mCurrentPhotoPath;
    private ArrayList<SchoolClass> mClassList;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Makes listview that holds classes, sets up adapter, and applies adapter
        makeListView();

        // Creates ActionButton for 3 buttons and sets properties
        declareActionButtons();


        // Adds all listeners for buttons, and items in list
        setListeners();

        // Sets up ActionBar and StatusBar
        setBars();

        // Creates contextMenu
        registerForContextMenu(mListView);
    }

    public void makeListView() {

        ClassList.getInstance(getApplicationContext()).sortByPeriod();
        mClassList = ClassList.getInstance(getApplicationContext()).getList();
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
        makeActionButton(mButtonClass, R.drawable.ic_file_document_white_36dp);

        mButtonPicture = (ActionButton) findViewById(id.action_button_picture);
        makeActionButton(mButtonPicture, R.drawable.ic_camera_white_36dp);

        mButtonText = (ActionButton) findViewById(id.action_button_assignment);
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
                            .getDrawable(R.drawable.ic_file_document_white_36dp));
                    showFlag = false;
                }


            }
        });
        mButtonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Creates intent to take picture
                if (ClassList.getInstance(getApplicationContext()).getList().size() > 0) {
                    dispatchTakePictureIntent();
                } else {
                    // If there was no class forces to add one
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);
                    builder.setMessage("Create a class by clicking the \"+\" above!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.setTitle("No Class Created");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);
                    builder.setMessage("Create a class by clicking the \"+\" above!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    builder.setTitle("No Class Created");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.primary_color)));

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.dark_primary));
        }
    }


    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(mClassList);

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
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            // Delete class from list
            case R.id.delete_class:
                // mClassList.getList().remove(info.position);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        ClassList.getInstance(getApplicationContext()).getList()
                                .remove(info.position);
                        adapter.notifyDataSetChanged();

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
            case R.id.edit_class:
                Intent i = new Intent(getApplicationContext(), EditClassInfoActivity.class);
                i.putExtra(EXTRA_INT_POSTITION, info.position);
                startActivity(i);
            default:
                return true;
        }
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
            i.putExtra(FILE_LOCATION_STRING, mCurrentPhotoPath);
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
        if (adapter == null) {
            makeListView();
        } else {
            adapter.notifyDataSetChanged();
        }
        mButtonPicture.setVisibility(View.INVISIBLE);
        mButtonText.setVisibility(View.INVISIBLE);
        mButtonClass.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_file_document_white_36dp));
        showFlag = false;
        updateDatabase();

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

    // CustomAdapter for ListView
    public class CustomAdapter extends ArrayAdapter<SchoolClass> {

        private ArrayList<SchoolClass> mList;
        private TextView titleTextView;
        private TextView currentAssignment;
        private TextView period;
        private String titleString;
        private TextDrawable textDrawable;
        private ImageView imageView;
        private RelativeLayout rel;
        private ColorGenerator generator;
        private int color;
        private int assignmentCount;

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
            generator = ColorGenerator.DEFAULT;


            rel = (RelativeLayout) convertView.findViewById(R.id.relative);
            titleTextView = (TextView) convertView
                    .findViewById(R.id.class_name_text);


            // Set to class name
            titleString = mList.get(position).getClassName();
            if (titleString.length() > 20) {
                titleString = titleString.substring(0, 20) + "...";
            }
            titleTextView.setText(titleString);
            period = (TextView) convertView.findViewById(R.id.period_text);
            period.setText("Period " + mList.get(position).getPeriod());

            color = generator.getColor(titleString);

            currentAssignment = (TextView) convertView
                    .findViewById(R.id.current_assignment);

            // Sets to ammount of current assignments.
            // Get assignment String makes sure correct plural is used
            for (Assignment a : mList.get(position).getAssignments()) {
                if (!a.isHidden() && !a.isCompleted()) {
                    assignmentCount++;
                }
            }
            currentAssignment.setText(getAssignmentString(assignmentCount));
            assignmentCount = 0;

            imageView = (ImageView) convertView.findViewById(R.id.test);
            rel.getViewTreeObserver().addOnPreDrawListener(new RelOnPreDrawListener(imageView,
                    titleString.substring(0, 1), color));
            return convertView;

        }

        private String getAssignmentString(int assignments) {
            if (assignments == 0 || assignments > 1) {
                return "" + assignments + " assignments";
            } else {
                return "" + assignments + " assignment";
            }
        }

        private class RelOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
            private ImageView imageView;
            private String character;
            private int colorSet;

            public RelOnPreDrawListener(ImageView imageView, String s, int c) {
                this.imageView = imageView;
                this.character = s;
                this.colorSet = c;
            }

            @Override
            public boolean onPreDraw() {
                ViewTreeObserver observer = imageView.getViewTreeObserver();
                if (observer.isAlive()) {
                    textDrawable = TextDrawable.builder()
                            .beginConfig()
                            .height(rel.getHeight() - 5)
                            .width(rel.getHeight() - 5)
                            .endConfig()
                            .buildRoundRect(character, colorSet, 100);
                    imageView.setImageDrawable(textDrawable);
                }
                observer.removeOnPreDrawListener(this);
                return true;
            }
        }
    }


}
