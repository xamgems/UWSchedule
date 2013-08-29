package com.amgems.uwschedule.api.uw;

import android.util.Log;
import com.amgems.uwschedule.services.LoginService;
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
 * Created by zac on 8/28/13.
 */
public class GetLoginAuthentication {

    private static final Pattern HIDDEN_PARAMS = Pattern.compile("<input type=\"hidden\" name=\"(.+)\" value=\"(.*)\">");

    private List<String> mCookies;
    private final String mUsername;
    private final String mPassword;

    public static enum LoginResponse {
        OK,
        AUTHENTICATION_ERROR,
        SERVER_ERROR,
        NETWORK_ERROR
    }

    GetLoginAuthentication(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public static GetLoginAuthentication newInstance(String username, String password) {
        return new GetLoginAuthentication(username, password);
    }

    public LoginResponse execute() {

        try {
            URL loginUrl = new URL(NetUtils.LOGIN_REQUEST_URL);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            HttpURLConnection connection = NetUtils.getInputConnection(loginUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            try {
                postParameters.add(new BasicNameValuePair("user", mUsername));
                postParameters.add(new BasicNameValuePair("pass", mPassword));
                captureHiddenParameters(reader, postParameters);
            } finally {
                connection.disconnect();
                reader.close();
            }

            connection = NetUtils.getOutputConnection(loginUrl);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            try {
                List<String> cookie = getAuthCookies(connection, bufferedWriter, postParameters);
                if (cookie != null) {
                    synchronized (cookie) {
                        mCookies = cookie;
                    }
                }
            } finally {
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            return LoginResponse.SERVER_ERROR;
        } catch (IOException e) {
            return LoginResponse.NETWORK_ERROR;
        }

        Log.d(LoginService.class.getSimpleName(), "SENDING SERVICE RESULT");

        if (mCookies != null) {
            return LoginResponse.OK;
        } else {
            return LoginResponse.AUTHENTICATION_ERROR;
        }
    }

    public List<String> getCookie() {
        synchronized (mCookies) {
            List<String> currCookie = mCookies;
            mCookies = null;
            return currCookie;
        }
    }

    private void captureHiddenParameters (BufferedReader reader,
                                                 List<? super NameValuePair> destHiddenParams) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher parameterMatcher = HIDDEN_PARAMS.matcher(line);
            if (parameterMatcher.matches()) {
                destHiddenParams.add(new BasicNameValuePair(parameterMatcher.group(1), parameterMatcher.group(2)));
            }
        }
    }

    private List<String> getAuthCookies (HttpURLConnection connection, BufferedWriter writer,
                                                List<? extends NameValuePair> postParams) throws IOException {
        writer.write(toQueryString(postParams));
        writer.flush();

        return connection.getHeaderFields().get("Set-Cookie");
    }

    private String toQueryString (List<? extends NameValuePair> postParameterPairs) {
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
