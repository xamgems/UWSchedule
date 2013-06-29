package com.amgems.uwschedule;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import java.lang.Override;

public class LoginActivity extends FragmentActivity {

    private Button mSyncButton;
    private CheckBox mSyncCheckbox;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        mSyncCheckbox = (CheckBox) findViewById(R.id.sync_checkbox);
        mPassword = (EditText) findViewById(R.id.password);

        mSyncCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int visibility = b ? View.VISIBLE : View.GONE;
                mPassword.setVisibility(visibility);
                mPassword.setText("");
            }
        });

        mSyncButton = (Button) findViewById(R.id.sync_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
}
