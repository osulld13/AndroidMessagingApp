package com.example.ribbit2;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toolbar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
        setupActionBar();
    }

    /*
    Run query in onResume method so it runs before the activity begins
    */
    @Override
    protected void onResume() {
        super.onResume();

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null){
                    // Success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for(ParseUser user : mUsers){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);
                }
                else {
                    Log.e(TAG, e.getMessage());
                    // Error on signup
                    AlertDialog.Builder signUpExceptionAlertBuilder = new AlertDialog.Builder(EditFriendsActivity.this);
                    signUpExceptionAlertBuilder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog  signUpExceptionAlertDialog = signUpExceptionAlertBuilder.create();
                    signUpExceptionAlertDialog.show();
                }
            }
        });
    }

    private void setupActionBar(){
        //((ActionBarActivity)getActivity()).getActionBar();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar()

        if (getActionBar() != null)
        {
            getActionBar().setDisplayShowHomeEnabled(true);
        }

    }

}
