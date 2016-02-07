package com.example.ribbit2;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB

    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogueListener =
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    switch(which){
                        case 0: // Take picture
                            takePicture();
                            break;
                        case 1: // Take video
                            takeVideo();
                            break;
                        case 2: // Choose picture
                            choosePhoto();
                            break;
                        case 3: // Choose video
                            chooseVideo();
                            break;
                    }
                }

                private void takePicture() {
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri == null){
                        //display an error
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                }

                private void takeVideo(){
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri == null){
                        //display an error
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Add some properties to vieo intent such as max length, quality etc.
                        // Reduce video quality to lowest to send over network
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                    }
                }

                private void choosePhoto(){
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                }

                private void chooseVideo(){
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                }

                private Uri getOutputMediaFileUri(int mediaType){
                    // To be safe, you should check that the External Storage is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    if(isExternalStorageAvailable()){
                        // get Uri

                        // 1. Get external storage directory
                        String appName = MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName
                                );


                        // 2. Create our own sub directory
                        if (! mediaStorageDir.exists()){
                            if (! mediaStorageDir.mkdirs()){
                                Log.e(TAG, "Failed to create directory.");
                                return null;
                            }
                        }

                        // 3. Create a file name
                        // 4. Create the file
                        File mediaFile;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(now);

                        String path = mediaStorageDir.getPath() + File.separator; // file separator gets the default seperator on system
                        if(mediaType == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                        }
                        else if(mediaType == MEDIA_TYPE_VIDEO){
                            mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                        }
                        else {
                            return null;
                        }

                        Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                        // 5. Return the files URI
                        return Uri.fromFile(mediaFile);

                    }
                    else {
                        return null;
                    }
                }

                private boolean isExternalStorageAvailable() {
                    String state = Environment.getExternalStorageState();

                    if (state.equals(Environment.MEDIA_MOUNTED)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The indeterminate progress indicator can now be called in the fragments
        // contained in the activity
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        //if user is not logged in send them to the signin screen
        if(currentUser == null) {
            // All activities are started using intents
            // To access another activity an intent to that activity has to be made
            // Intents are an abstract representation of an action to be performed
            // They can represent many types of action
            // In this case they represent an activity to start
            navigateToLogin();
        }
        else {
            // User is logged in
            // Log their data
            Log.i(TAG, currentUser.getUsername());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(MainActivity.this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            // add it to the gallery

            // If the result has come from the get photo or video options
            // get item URI from its intent
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                if(data == null){
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri = data.getData();
                }

                Log.i(TAG, "Media URI: " + mMediaUri);
                if (requestCode == PICK_VIDEO_REQUEST){
                    // make sure the file is less than 10 mb
                    int fileSize = 0;

                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch(FileNotFoundException e){
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch(IOException e){
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    if (fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            // In the case where we took a new photo or video
            else {
                // Notify that new files are available to be included
                // The file has already been saved at this point
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            //Start the select recipients activity attach the uri of the item to be sent
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            startActivity(recipientsIntent);
        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }

    // Method to send user to the login activity
    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        // These flags are set to create a new task on the navigation stack and to clear the old task
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //onOptionsItemSelected is called when an item in the on options menu us selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_logout:
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent editFriendsIntent = new Intent(this, EditFriendsActivity.class);
                startActivity(editFriendsIntent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogueListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
