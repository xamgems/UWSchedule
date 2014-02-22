package com.amgems.uwschedule.api.local;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;
import com.amgems.uwschedule.provider.ScheduleContract;
import com.amgems.uwschedule.ui.HomeActivity;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by shermpay on 2/15/14.
 */
public class SyncStudent {
    private static final String TAG = "SYNC_STUDENT";
    private static final int SYNC_INSERT_TOKEN = 1;
    private Account mAccount;
    private String mQuarter;
    private List<Integer> mSlns;
    private final Context mContext;

    public SyncStudent(Account account, String quarter, List<Integer> slns, Context context) {
        mAccount = account;
        mQuarter = quarter;
        mSlns = slns;
        mContext = context;

        WebService service = new WebService();
        service.syncCourses(account.getUsername(), quarter, slns.toString(), new Callback<List<Course>>() {
            @Override
            public void success(List<Course> courses, Response response) {
                Log.d(TAG, "HTTP status: " + response.getStatus());
                AsyncQueryHandler queryHandler = new AsyncQueryHandler(new ContentResolver(mContext) {}) {};
                for (Course course : courses)
                    queryHandler.startInsert(SYNC_INSERT_TOKEN, null, ScheduleContract.Courses.CONTENT_URI,
                            course.toContentValues(mAccount.getUsername()));
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
