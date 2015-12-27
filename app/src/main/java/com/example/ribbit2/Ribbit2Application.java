package com.example.ribbit2;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Donal on 27/12/2015.
 */

/*
Here an external class has been added as the entry point to the app
this is a good place to do extra processing before the app starts
*/
public class Ribbit2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
            This is code tests the parse.com backend.
         */

        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("foo", "bar");
        //testObject.saveInBackground();
    }

}
