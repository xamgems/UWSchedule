package com.amgems.uwschedule;

import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;import com.amgems.uwschedule.R;import java.lang.Override;

public class LoginActivity extends FragmentActivity {

    Button mLoginButton;
    Button mSyncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mSyncButton = (Button) findViewById(R.id.sync_button);
        mSyncButton.setText("HELLO WORLD");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
}
