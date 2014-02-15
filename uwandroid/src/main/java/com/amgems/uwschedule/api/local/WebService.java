package com.amgems.uwschedule.api.local;

import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by shermpay on 2/10/14.
 */
public class WebService {
    private static final String TAG = "WEB SERVICE";
    private static ScheduleRequest request;
    public static final String WEB_SERVICE_URL = "http://shermanpay.com/uw_schedule/";

    public WebService() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(WEB_SERVICE_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        request = restAdapter.create(ScheduleRequest.class);
    }

    public void getAccount(String userName, Callback<Account> callback) {
        request.getAccount(userName, callback);
    }

    public void getUserCourses(String userName, String quarter, Callback<List<Course>> callback) {
        request.getUserCourses(userName, quarter, callback);
    }
}
