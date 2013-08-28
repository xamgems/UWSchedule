package com.amgems.uwschedule.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.amgems.uwschedule.util.NetUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zac on 8/18/13.
 */
public class LoginService extends Service {

    public static final String ACTION_RESPONSE = "com.amgems.uwschedule.action.LOGIN_PROCESSED";

    public static final String PARAM_IN_USERNAME = "param.in.username";
    public static final String PARAM_IN_PASSWORD = "param.in.password";
    public static final String PARAM_OUT = "param.out";

    private static final Pattern HIDDEN_PARAMS = Pattern.compile("<input type=\"hidden\" name=\"(.+)\" value=\"(.*)\">");

    private List<String> mCookies;

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

    public synchronized boolean pollForCookie() {
        return mCookies != null;
    }

    public synchronized List<String> getCookie() {
        List<String> currCookie = mCookies;
        mCookies = null;
        return currCookie;
    }

    public synchronized void setCookie(List<String> cookie) {
        mCookies = cookie;
    }

    protected void onHandleIntent(Intent intent) {
        String response;
        try {
            String username = intent.getStringExtra(PARAM_IN_USERNAME);
            String password = intent.getStringExtra(PARAM_IN_PASSWORD);

            URL loginUrl = new URL(NetUtils.LOGIN_REQUEST_URL);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            HttpURLConnection connection = NetUtils.getInputConnection(loginUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            try {
                postParameters.add(new BasicNameValuePair("user", username));
                postParameters.add(new BasicNameValuePair("pass", password));
                captureHiddenParameters(reader, postParameters);
            } finally {
                connection.disconnect();
                reader.close();
            }

            connection = NetUtils.getOutputConnection(loginUrl);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            try {
                List<String> cookie = getAuthCookies(connection, bufferedWriter, postParameters);
                setCookie(cookie);
            } finally {
                connection.disconnect();
            }

            response = "OK";

        } catch (MalformedURLException e) {
            response = "BAD";
        } catch (IOException e) {
            response = "BAD";
        }

        Log.d(LoginService.class.getSimpleName(), "SENDING SERVICE RESULT");

        Intent broadcastIntent = new Intent();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastIntent.setAction(ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //broadcastIntent.putExtra(PARAM_OUT, mCookies != null ? mCookies.toString() : "Incorrect Username/Password");
        broadcastIntent.putExtra(PARAM_OUT, response);
        broadcastManager.sendBroadcast(broadcastIntent);
    }



    private static void captureHiddenParameters (BufferedReader reader,
                                                 List<? super NameValuePair> destHiddenParams) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher parameterMatcher = HIDDEN_PARAMS.matcher(line);
            if (parameterMatcher.matches()) {
                destHiddenParams.add(new BasicNameValuePair(parameterMatcher.group(1), parameterMatcher.group(2)));
            }
        }
    }

    private static List<String> getAuthCookies (HttpURLConnection connection, BufferedWriter writer,
                                       List<? extends NameValuePair> postParams) throws IOException {
        writer.write(toQueryString(postParams));
        writer.flush();

        return connection.getHeaderFields().get("Set-Cookie");
    }

    private static String toQueryString (List<? extends NameValuePair> postParameterPairs) {
        StringBuilder builder = new StringBuilder();
        boolean firstParameter = true;

        try {
            for (NameValuePair postParameterPair : postParameterPairs) {
                if (!firstParameter)
                    builder.append("&");
                firstParameter = false;

                builder.append(URLEncoder.encode(postParameterPair.getName(), NetUtils.CHARSET));
                builder.append("=");
                builder.append(URLEncoder.encode(postParameterPair.getValue(), NetUtils.CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LoginService.class.getSimpleName(), e.getMessage());
        }

        return builder.toString();
    }

}