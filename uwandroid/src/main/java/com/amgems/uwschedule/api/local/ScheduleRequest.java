package com.amgems.uwschedule.api.local;

import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by shermpay on 2/10/14.
 */
public interface ScheduleRequest {
    @GET("/find_user")
    void getAccount(@Query("user_name") String userName, Callback<Account> callback);

    @FormUrlEncoded
    @POST("/add_user")
    void addAccount(@Field("user_name") String userName, Callback<Account> callback);

    @GET("/user_courses")
    void getUserCourses(@Query("user_name") String userName, @Query("quarter") String quarter,
                    Callback<List<Course>> callback);

}
