package com.amgems.uwschedule.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.amgems.uwschedule.LoginActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zac on 8/18/13.
 */
public class LoginService extends IntentService {

    public static final String PARAM_IN_USERNAME = "param.in.username";
    public static final String PARAM_IN_PASSWORD = "param.in.password";
    public static final String PARAM_OUT = "param.out";

    public static final String LOGIN_REQUEST_URL = "https://weblogin.washington.edu/";

    public LoginService () {
        super(LoginService.class.getSimpleName());
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        String username = intent.getStringExtra(PARAM_IN_USERNAME);
        String password = intent.getStringExtra(PARAM_IN_PASSWORD);

        HttpURLConnection connection = null;

        SystemClock.sleep(4500);

        try {
            URL loginUrl = new URL(LOGIN_REQUEST_URL);
            connection = (HttpURLConnection) loginUrl.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            while ((line = bufferedReader.readLine()) != null) {

            }


            connection.disconnect();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


        Intent broadcastIntent = new Intent();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastIntent.setAction(LoginActivity.LoginResponseReceiver.ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT, username);
        broadcastManager.sendBroadcast(broadcastIntent);
    }
}
