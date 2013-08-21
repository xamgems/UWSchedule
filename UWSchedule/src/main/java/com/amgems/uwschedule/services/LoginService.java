package com.amgems.uwschedule.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.amgems.uwschedule.LoginActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zac on 8/18/13.
 */
public class LoginService extends IntentService {

    public static final String PARAM_IN_USERNAME = "param.in.username";
    public static final String PARAM_IN_PASSWORD = "param.in.password";
    public static final String PARAM_OUT = "param.out";

    public static final String LOGIN_REQUEST_URL = "https://weblogin.washington.edu";
    public static final String CHARSET = "UTF-8";

    private static final Pattern HIDDEN_PARAMS = Pattern.compile("<input type=\"hidden\" name=\"(.+)\" value=\"(.*)\">");

    public LoginService () {
        super(LoginService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String username = intent.getStringExtra(PARAM_IN_USERNAME);
        String password = intent.getStringExtra(PARAM_IN_PASSWORD);

        HttpURLConnection connection = null;

        List<NameValuePair> postParameters = null;

        try {
            URL loginUrl = new URL(LOGIN_REQUEST_URL);
            connection = (HttpURLConnection) loginUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("user", username));
            postParameters.add(new BasicNameValuePair("pass", password));

            while ((line = bufferedReader.readLine()) != null) {
                Matcher parameterMatcher = HIDDEN_PARAMS.matcher(line);
                if (parameterMatcher.matches()) {
                    postParameters.add(new BasicNameValuePair(parameterMatcher.group(1), parameterMatcher.group(2)));
                }
            }

            connection.disconnect();

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.toString());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }

        List<String> cookies = null;
        StringBuilder postCompletionBuilder = new StringBuilder();

        try {
            URL loginUrl = new URL(LOGIN_REQUEST_URL);
            connection = (HttpURLConnection) loginUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(toRequestBodyString(postParameters));
            bufferedWriter.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                postCompletionBuilder.append(nextLine);
            }

            cookies = connection.getHeaderFields().get("Set-Cookie");

        }  catch (Exception e) {
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
        broadcastIntent.putExtra(PARAM_OUT, cookies.toString());
        broadcastManager.sendBroadcast(broadcastIntent);
    }

    private static String toRequestBodyString(List<NameValuePair> postParameterPairs) {
        StringBuilder builder = new StringBuilder();

        boolean firstParameter = true;

        try {
            for (NameValuePair postParameterPair : postParameterPairs) {
                if (!firstParameter) {
                    builder.append("&");
                }
                firstParameter = false;

                builder.append(URLEncoder.encode(postParameterPair.getName(), CHARSET));
                builder.append("=");
                builder.append(URLEncoder.encode(postParameterPair.getValue(), CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LoginService.class.getSimpleName(), e.getMessage());
        }

        return builder.toString();
    }


}