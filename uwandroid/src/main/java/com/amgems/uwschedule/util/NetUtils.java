package com.amgems.uwschedule.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zac on 8/27/13.
 */
public class NetUtils {

    public static final String LOGIN_REQUEST_URL = "https://weblogin.washington.edu";
    public static final String USER_AGENT_STRING = "Mozilla/5.0";
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CHARSET = "UTF-8";

    // Suppress default constructor to ensure noninstantiability
    private NetUtils() { }

    public static HttpURLConnection getInputConnection (URL targetUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT_STRING);
        return connection;
    }

    public static HttpURLConnection getOutputConnection (URL targetUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT_STRING);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setDoOutput(true);
        return connection;
    }

}
