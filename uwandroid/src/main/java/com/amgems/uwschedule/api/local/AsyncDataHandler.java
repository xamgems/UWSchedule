/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by`
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.amgems.uwschedule.api.local;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;
import com.amgems.uwschedule.model.Meeting;
import com.amgems.uwschedule.provider.ScheduleContract;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Sherman Pay on 3/9/14.
 * This class is to facilitate ayschronous handling of data between the web server
 * and the client.
 *
 */
public class AsyncDataHandler {
    private final boolean DEBUG_MODE = false;
    private final String TAG = getClass().getSimpleName();

    /**
     * Tokens for V
     */
    public static enum AsyncDataHandlerTokens {
        INSERT_COURSES_TOKEN (1),
        INSERT_ACCOUNT_TOKEN (2),
        INSERT_MEETING_TOKEN (3);

        int value;
        AsyncDataHandlerTokens(int x) {
           this.value = x;
        }

        /**
         * Returns a token enum for a corresponding integer given
         * @param x
         * @return token enum or null if integer given does not map to a token
         */
        public static AsyncDataHandlerTokens getType(int x) {
            for (AsyncDataHandlerTokens token : AsyncDataHandlerTokens.values()) {
               if (token.value == x) {
                   return token;
               }
            }
            return null;
        }

        public int getValue() {
            return value;
        }
    }

    private WebService mWebService;
    private AsyncQueryHandler mQueryHandler;

    /**
     * Instantiates an AsyncDataHandler with the default AsyncQueryHandler.
     * Custom AsyncQueryHandlers can be passed in via a different constructor provided.
     * @param contentResolver ContentResolver obtained by context.getContentResolver()
     */
    public AsyncDataHandler(ContentResolver contentResolver) {
        mWebService = new WebService();
        mQueryHandler = new AsyncQueryHandler(contentResolver) {
            // TODO: REMOVE WHEN VERIFIED TO WORK
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                if (DEBUG_MODE) {
                    switch (AsyncDataHandlerTokens.getType(token)) {
                        case INSERT_ACCOUNT_TOKEN:
                            Log.d(TAG, "Account Inserted\n" + uri.toString());
                            this.startQuery(token, null,
                                    ScheduleContract.Accounts.CONTENT_URI, null, null, null, null);
                            break;
                        case INSERT_COURSES_TOKEN:
                            Log.d(TAG, "Course Inserted\n" + uri.toString());
                            this.startQuery(token, null,
                                    ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
                            break;
                        case INSERT_MEETING_TOKEN:
                            Log.d(TAG, "Meeting Inserted\n" + uri.toString());
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (DEBUG_MODE) {
                    switch (AsyncDataHandlerTokens.getType(token)) {
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
            }
        };

    }

    /**
     * Instantiates a AsyncDataHandler provided an AsyncQueryHandler.
     * Custom AsyncQueryHandler should use the constant tokens provided by this class.
     * @param asyncQueryHandler custom AsyncQueryHandler for querying the database.
     */
    public AsyncDataHandler(AsyncQueryHandler asyncQueryHandler) {
        mWebService = new WebService();
        mQueryHandler = asyncQueryHandler;
    }

    /**
     * Gets the account from server and stores a local copy
     * @param userName String representing the userName registered
     */
    public void getRemoteAccount(String userName) {
        mWebService.getAccount(userName, new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                localInsertAccount(account);
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                Log.d(TAG, response.getReason() + " URL: " + response.getUrl());
            }
        });
    }

    /**
     * Obtains the courses from the server and inserts the data locally
     * @param userName String representing the userName registered
     * @param quarter String representing the quarter for the courses registered.
     *                eg. "wi14" for winter 2014
     */
    public void getRemoteCourses(final String userName, String quarter) {
        mWebService.getCourses(userName, quarter, new Callback<List<Course>>() {
            @Override
            public void success(List<Course> courses, Response response) {
                localInsertCourses(userName, courses);
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                Log.d(TAG, response.getReason() + " URL: " + response.getUrl());
            }
        });
    }

    /**
     * Inserts an account into the remote server and locally, given the userName.
     * @param userName String representing the userName of the account.
     * @param studentName String representing the studentName of the account.
     */
    public void putAccount(String userName, String studentName) {
        mWebService.putAccount(userName, studentName, new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                localInsertAccount(account);
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                Log.d(TAG, response.getReason() + " URL: " + response.getUrl());
            }
        });
    }

    /**
     * Inserts into local storage an account.
     * @param userName String representing the userName of the account.
     * @param studentName String representing the studentName of the account.
     */
    public void putLocalAccount(String userName, String studentName) {
        Account account = new Account(userName, studentName, System.currentTimeMillis());
        localInsertAccount(account);
    }

    /**
     * Inserts an account into the remote server database.
     * @param userName String representing the userName of the account.
     * @param studentName String representing the studentName of the account.
     */
    public void putRemoteAccount(String userName, String studentName) {
        mWebService.putAccount(userName, studentName, new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                Log.d(TAG, "Status: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                Log.d(TAG, response.getReason() + " URL: " + response.getUrl());
            }
        });
    }

    /**
     * Inserts courses into the remote server and locally.
     * Given the userName, quarter and the SLNs of each individual course
     * @param userName String representing the userName of the account.
     * @param quarter String representing the quarter of registration. eg. "wi14" for winter 2014
     * @param slns String representing the toString() of a Java Collection containing the SLNs of
     *             the courses registered
     */
    public void putCourses(final String userName, String quarter, String slns) {
       mWebService.putCourses(userName, quarter, slns, new Callback<List<Course>>() {
           @Override
           public void success(List<Course> courses, Response response) {
               localInsertCourses(userName, courses);
               Log.d(TAG, "Status: " + response.getStatus());
           }

           @Override
           public void failure(RetrofitError error) {
               Response response = error.getResponse();
               Log.d(TAG, response.getReason() + " URL: " + response.getUrl());
           }
       });
    }

    private void localInsertAccount(Account account) {
        mQueryHandler.startInsert(AsyncDataHandlerTokens.INSERT_ACCOUNT_TOKEN.getValue(), null,
                ScheduleContract.Accounts.CONTENT_URI, account.toContentValues());
    }

    private void localInsertCourses(String userName, List<Course> courses) {
        for (Course course : courses) {
            mQueryHandler.startInsert(AsyncDataHandlerTokens.INSERT_COURSES_TOKEN.getValue(),
                    null, ScheduleContract.Courses.CONTENT_URI,
                    course.toContentValues(userName));
            Log.d(TAG, "Course: " + course + " Meetings: " + course.getMeetings());
            for (Meeting meeting : course.getMeetings()) {
               mQueryHandler.startInsert(AsyncDataHandlerTokens.INSERT_MEETING_TOKEN.getValue(),
                       null,  ScheduleContract.Meetings.CONTENT_URI,
                       meeting.toContentValues(course.getSln()));
                Log.d(TAG, "Meeting: " + meeting);
            }
        }
    }
}
