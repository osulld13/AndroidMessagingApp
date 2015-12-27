package com.example.ribbit2;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // All activities are started using intents
        // To access another activity an intent to that activity has to be made
        // Intents are an abstract representation of an action to be performed
        // They can represent many types of action
        // In this case they represent an activity to start
        Intent loginIntent = new Intent(this, LoginActivity.class);
        // These flags are set to create a new task on the navigation stack and to clear the old task
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

        final ActionBar actionBar = getActionBar();



    }
}
