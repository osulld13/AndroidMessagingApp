package com.example.ribbit2;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Indeterminate windows feature is a spinner progress indicator that runs while
        // a process is being waited upon. It should be used in scenarios where the time
        // a user could be waiting is potentially quite to variant.
        //
        // reqestWindowFeature must be called BEFORE set content view and onCreate
        //
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSignUpTextView = (TextView)findViewById(R.id.signUpText);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get String value from each field when login button is pressed
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                // remove whitespace from user input
                username = username.trim();
                password = password.trim();

                // Create an alert if any of the fields are empty
                if( username.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder emptyFieldAlertBuilder = new AlertDialog.Builder(LoginActivity.this);
                    emptyFieldAlertBuilder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                                    // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog emptyFieldAlertDialog = emptyFieldAlertBuilder.create();
                    emptyFieldAlertDialog.show();
                }
                //Login
                else{
                    // Make the indeterminate progress indicator visible as we try to login
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            //Turn off the indeterminate process indicator as log in transaction is complete
                            setProgressBarIndeterminateVisibility(false);
                            // Successful login
                            if ( e == null ){
                                Intent inboxIntent = new Intent(LoginActivity.this, MainActivity.class);
                                inboxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(inboxIntent);
                            }
                            // Error on login
                            else {
                                AlertDialog.Builder loginExceptionAlertBuilder = new AlertDialog.Builder(LoginActivity.this);
                                loginExceptionAlertBuilder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                                // Using genereric android string resource 'ok' and passing a null listener - as button has no action
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog loginExceptionAlertDialog = loginExceptionAlertBuilder.create();
                                loginExceptionAlertDialog.show();
                            }
                        }
                    });
                }
            }
        });

        // Add an onclick listener to the text view
        // This wil start the signUp activity when it is clicked
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

    }
}
