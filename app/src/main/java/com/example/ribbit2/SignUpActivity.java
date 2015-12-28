package com.example.ribbit2;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passswordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get string values from text fields when signup button is pressed
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                //Trim whitespace from each string
                username = username.trim();
                password = password.trim();
                email = email.trim();

                // Create an alert if any of the fields are empty
                if( username.isEmpty() || password.isEmpty() || email.isEmpty() ){
                    AlertDialog.Builder emptyFieldAlertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                    emptyFieldAlertBuilder.setMessage(R.string.sign_up_error_message)
                        .setTitle(R.string.sign_up_error_title)
                        // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog emptyFieldAlertDialog = emptyFieldAlertBuilder.create();
                    emptyFieldAlertDialog.show();
                }

                //Create a new user using field data
                else{
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    // Show the intermediate progress bar
                    setProgressBarIndeterminateVisibility(true);
                    // Use the sign up in background method to assign the sign up process to a background thread
                    // This will prevent the signup from locking up user input
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            // Hide the intermediate progress bar
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null){
                                // Sign up successful
                                Intent inboxIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                inboxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(inboxIntent);
                            }
                            else {
                                // Error on signup
                                AlertDialog.Builder signUpExceptionAlertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                                signUpExceptionAlertBuilder.setMessage(e.getMessage())
                                        .setTitle(R.string.sign_up_error_title)
                                        // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog  signUpExceptionAlertDialog = signUpExceptionAlertBuilder.create();
                                signUpExceptionAlertDialog.show();
                            }
                        }
                    });
                }
            }
        });
    }
}
