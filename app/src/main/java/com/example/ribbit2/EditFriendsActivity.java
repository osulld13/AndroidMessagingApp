package com.example.ribbit2;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
        setupActionBar();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    /*
    Run query in onResume method so it runs before the activity begins
    */
    @Override
    protected void onResume() {
        super.onResume();

        //Get relation containing a users friends
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.editFriendsProgressBar);

        mProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    // Success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);

                    //Add checkmarks to friends in list
                    addFriendCheckmarks();
                } else {
                    Log.e(TAG, e.getMessage());
                    // Error on signup
                    AlertDialog.Builder signUpExceptionAlertBuilder = new AlertDialog.Builder(EditFriendsActivity.this);
                    signUpExceptionAlertBuilder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                                    // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog signUpExceptionAlertDialog = signUpExceptionAlertBuilder.create();
                    signUpExceptionAlertDialog.show();
                }
            }
        });
    }

    private void setupActionBar(){
        if (getActionBar() != null)
        {
            getActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(getListView().isItemChecked(position)) {
            //add friend
            mFriendsRelation.add(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                  @Override
                  public void done(ParseException e) {
                      if (e != null) {
                          Log.e(TAG, e.getMessage());
                      }
                  }
              }
            );
        }
        else {
            //remove friend
        }
    }

    private void addFriendCheckmarks(){
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < mUsers.size(); i++){
                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends ){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

}
