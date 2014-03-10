package com.amgems.uwschedule.api.local;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;
import com.amgems.uwschedule.provider.ScheduleContract;

import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author shermpay on 3/9/14.
 */
public class WebServiceManager {
    private final String TAG = "WEB_SERVICE_MANAGER";

    private final int INSERT_ACCOUNT_TOKEN = 1;
    private final int INSERT_COURSES_TOKEN = 2;

    private WebService mWebService;
    private ContentResolver mContentResolver;
    private AsyncQueryHandler mQueryHandler;

    public WebServiceManager(Context context) {
        mWebService = new WebService();
        mContentResolver = context.getContentResolver();
        mQueryHandler = new AsyncQueryHandler(mContentResolver) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                switch (token) {
                    case INSERT_ACCOUNT_TOKEN:
                        Log.d(TAG, "Account Inserted\n" + uri.toString());
                        this.startQuery(INSERT_ACCOUNT_TOKEN, null,
                                ScheduleContract.Accounts.CONTENT_URI, null, null, null, null);
                        break;
                    case INSERT_COURSES_TOKEN:
                        Log.d(TAG, "Account Inserted\n" + uri.toString());
                        this.startQuery(INSERT_COURSES_TOKEN, null,
                                ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                switch (token) {
                    case INSERT_ACCOUNT_TOKEN:
                        Log.d(TAG, Arrays.toString(cursor.getColumnNames()));
                        Log.d(TAG, cursor.getColumnCount() + "");
                        cursor.moveToFirst();
                        String res = "";
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            res += cursor.getString(i) + " ";
                        }
                        Log.d(TAG, res);
                        break;
                    case INSERT_COURSES_TOKEN:
                        Log.d(TAG, Arrays.toString(cursor.getColumnNames()));
                        cursor.moveToFirst();
                        int columnCount = cursor.getColumnCount();
                        while (!cursor.isAfterLast()) {
                            String str = "";
                            for (int i = 0; i < columnCount; i++) {
                                str += cursor.getString(i) + " ";
                            }
                            Log.d(TAG, str);
                            cursor.moveToNext();
                        }
                        break;
                    default:
                        break;
                }
                cursor.close();

            }
        };
    }

    /**
     * Gets the account from server and stores a local copy
     * @param username String representing the username registered
     */
    public void getAccount(String username) {
        mWebService.getAccount(username, new Callback<Account>() {

            @Override
            public void success(Account account, Response response) {
                mQueryHandler.startInsert(INSERT_ACCOUNT_TOKEN, null, ScheduleContract.Accounts.CONTENT_URI, account.toContentValues());
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    public void getCourses(final String username, String quarter) {
        mWebService.getCourses(username, quarter, new Callback<List<Course>>() {
            @Override
            public void success(List<Course> courses, Response response) {
                for (Course course : courses) {
                    mQueryHandler.startInsert(INSERT_COURSES_TOKEN, null,
                            ScheduleContract.Courses.CONTENT_URI, course.toContentValues(username));
                }
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
            }
        });
    }
}
