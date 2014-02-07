package com.amgems.uwschedule.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import com.amgems.uwschedule.api.uw.LoginAuthenticator;

/**
 * Created by zac on 8/18/13.
 */
public class LoginService extends Service {

    public static final String ACTION_RESPONSE = "com.amgems.uwschedule.action.LOGIN_PROCESSED";

    public static final String PARAM_IN_USERNAME = "param.in.username";
    public static final String PARAM_IN_PASSWORD = "param.in.password";
    public static final String PARAM_OUT_RESPONSE = "param.out.response";
    public static final String PARAM_OUT_COOKIE = "param.out.cookie";

    private Looper mServiceLooper;
    private Handler mHandler;

    public class LoginHandler extends Handler {

        public LoginHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    onHandleIntent((Intent) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private final IBinder mBinder = new LocalLoginBinder();

    public class LocalLoginBinder extends Binder {
        public LoginService getService() {
            // Return instance of LoginService for clients to make calls
            return LoginService.this;
        }
    }

    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("HANDLERTHREAD", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        mServiceLooper = handlerThread.getLooper();
        mHandler = new LoginHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = mHandler.obtainMessage();
        msg.obj = intent;
        msg.what = 0;
        mHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected void onHandleIntent(Intent intent) {

        LoginAuthenticator command = LoginAuthenticator.newInstance(intent.getStringExtra(PARAM_IN_USERNAME),
                intent.getStringExtra(PARAM_IN_PASSWORD));
        command.execute();
        LoginAuthenticator.Response response = command.getResponse();

        Intent broadcastIntent = new Intent();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastIntent.setAction(ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        broadcastIntent.putExtra(PARAM_OUT_RESPONSE, response);
        if (response == LoginAuthenticator.Response.OK) {
            broadcastIntent.putExtra(PARAM_OUT_COOKIE, command.getCookie().toString());
        }

        broadcastManager.sendBroadcast(broadcastIntent);
    }

}