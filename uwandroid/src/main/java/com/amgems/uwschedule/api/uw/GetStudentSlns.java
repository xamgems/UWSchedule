package com.amgems.uwschedule.api.uw;

import android.util.Log;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.util.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by JeremyTeoMBP on 9/2/14.
 */
public class GetStudentSlns {
    private String mCookie;
    private String mHtml;
    private Response mResponse;

    private GetStudentSlns(String cookie) {
        mCookie = cookie;
    }

    public static GetStudentSlns newInstance(String cookie) {
        return new GetStudentSlns(cookie);
    }

    public void execute() {
        try {
            URL registrationUrl = new URL(NetUtils.REGISTRATION_URL);
            HttpURLConnection connection = (HttpURLConnection) registrationUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Cookie", mCookie);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    Log.d("html", line);
                    count++;
                    if (count == 1000) break;
                }
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException e) {
            mResponse = Response.SERVER_ERROR;
        } catch (IOException e) {
            mResponse = Response.NETWORK_ERROR;
        }

    }

    public Response getResponse() {return mResponse;}
}
