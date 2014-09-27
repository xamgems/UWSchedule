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
import android.net.Uri;
import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;
import com.amgems.uwschedule.model.Meeting;
import com.amgems.uwschedule.provider.ScheduleContract;

import java.util.List;
import java.util.Objects;

/**
 * @author Sherman Pay on 3/9/14.
 * This class is to facilitate asynchronous handling of data communication
 * to the local SQLite database
 */
public class AsyncDataHandler {
    private final String TAG = getClass().getSimpleName();

    /**
     * Tokens for V
     */
    public static enum OpToken {

        QUERY_ACCOUNT(1), QUERY_COURSE(2), QUERY_MEETING(3),
        INSERT_ACCOUNT(4), INSERT_COURSE(5), INSERT_MEETING(6),
        UPDATE_ACCOUNT(7), UPDATE_COURSE(8), UPDATE_MEETING(9),
        DELETE_ACCOUNT(10), DELETE_COURSE(11), DELETE_MEETING(12);

        int value;
        OpToken(int x) {
           this.value = x;
        }

        /**
         * Returns a token enum for a corresponding integer given
         * @param x
         * @return token enum or null if integer given does not map to a token
         */
        public static OpToken type(int x) {
            for (OpToken token : OpToken.values()) {
               if (token.value == x) {
                   return token;
               }
            }
            return null;
        }

        public int value() {
            return value;
        }
    }

    public static Uri ACCOUNTS_TABLE = ScheduleContract.Accounts.CONTENT_URI;
    public static Uri COURSES_TABLE = ScheduleContract.Courses.CONTENT_URI;
    public static Uri MEETINGS_TABLE = ScheduleContract.Meetings.CONTENT_URI;

    private AsyncQueryHandler mQueryHandler;

    /**
     * Instantiates a AsyncDataHandler provided an AsyncQueryHandler.
     * All operations on AsyncDataHandler will be forwarded to this AsyncQueryHandler.
     * Therefore it is expected that the AsyncQueryHandler implement the proprer On____Complete
     * methods.
     * Note: Custom AsyncQueryHandler should use the constant tokens provided by this class.
     * @param asyncQueryHandler custom AsyncQueryHandler for querying the database.
     */
    public AsyncDataHandler(AsyncQueryHandler asyncQueryHandler) {
        mQueryHandler = asyncQueryHandler;
    }

    /**
     * Query the local database
     * @param token Token used for the query operation. Use one of the enum constants.
     * @param cookie Object that will be passed into onQueryComplete of the AsyncQueryHandler
     * @param uri Uri of the table. Use the constants provided for each table.
     * @param projection A list of which columns to return. If null, returns all columns
     * @param selection A string formatted as a SQL WHERE clause (excluding the where)
     * @param selectionArgs Include ?s in the selection, and this will replace in the order they
     *                      appear.
     * @param orderBy Set the ordering of rows, formatted as an SQL ORDER BY clause.
     * @param queryHandler The AsyncQueryHandler object to use. Override onQueryComplete to
     *                     obtain the callback.
     */
    public static void query(OpToken token, Object cookie, Uri uri,
                             String[] projection, String selection,
                             String[] selectionArgs, String orderBy,
                             AsyncQueryHandler queryHandler) {
        queryHandler.startQuery(token.value(), cookie, uri, projection, selection,
                selectionArgs, orderBy);
    }

    /**
     * Inserts an Account Object into the local database.
     * Please use the {@link com.amgems.uwschedule.api.local.AsyncDataHandler
     * .OpToken#INSERT_ACCOUNT}.
     * And call the value() method to obtain the int token for onInsertComplete.
     * @param account The account object to insert.
     * @param cookie An Object that will be passed into onInsertComplete.
     */
    public void insertAccount(Account account, Objects cookie) {
        mQueryHandler.startInsert(OpToken.INSERT_ACCOUNT.value(), cookie,
                ScheduleContract.Accounts.CONTENT_URI, account.toContentValues());
    }

    /**
     * Inserts an Account Object into the local database.
     * Please use the {@link com.amgems.uwschedule.api.local.AsyncDataHandler
     * .OpToken#INSERT_ACCOUNT}.
     * And call the value() method to obtain the int token for onInsertComplete.
     * @param account The account object to insert.
     * @param cookie An Object that will be passed into onInsertComplete.
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onQueryComplete to obtain the callback.
     */
    public static void insertAccount(Account account, Objects cookie, AsyncQueryHandler queryHandler) {
        queryHandler.startInsert(OpToken.INSERT_ACCOUNT.value(), cookie,
                ScheduleContract.Accounts.CONTENT_URI, account.toContentValues());
    }

    /**
     * Insert a List of courses for a specific Account into local database.
     * Please use the {@link com.amgems.uwschedule.api.local.AsyncDataHandler
     * .OpToken#INSERT_COURSE}.
     * And call the value() method to obtain the int token for onInsertComplete.
     * @param userName Username of the Account
     * @param courses List of courses to insert
     * @param cookie Object that will be passed into onInsertComplete of the AsyncQueryHandler
     */
    public void insertUserCourses(String userName, List<Course> courses, Object cookie) {
        for (Course course : courses) {
            mQueryHandler.startInsert(OpToken.INSERT_COURSE.value(), cookie,
                    COURSES_TABLE, course.toContentValues(userName));
            for (Meeting meeting : course.getMeetings()) {
                mQueryHandler.startInsert(OpToken.INSERT_MEETING.value(),
                        cookie, MEETINGS_TABLE,
                        meeting.toContentValues(course.getSln()));
            }
        }
    }

    /**
     * Insert a List of courses for a specific Account into local database.
     * Please use the {@link com.amgems.uwschedule.api.local.AsyncDataHandler
     * .OpToken#INSERT_COURSE}.
     * And call the value() method to obtain the int token for onInsertComplete.
     * @param userName Username of the Account
     * @param courses List of courses to insert
     * @param cookie Object that will be passed into onInsertComplete of the AsyncQueryHandler
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onInsertComplete to obtain the callback.
     */
    public static void insertUserCourses(String userName, List<Course> courses, Object cookie,
                                     AsyncQueryHandler queryHandler) {
        for (Course course : courses) {
            queryHandler.startInsert(OpToken.INSERT_COURSE.value(), cookie,
                    COURSES_TABLE, course.toContentValues(userName));
            for (Meeting meeting : course.getMeetings()) {
               queryHandler.startInsert(OpToken.INSERT_MEETING.value(),
                       cookie, MEETINGS_TABLE,
                       meeting.toContentValues(course.getSln()));
            }
        }
    }

    /**
     * Updates an Account object in the databaase.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#UPDATE_ACCOUNT}.
     * And call the value() method on the enum to obtain the int token for onUpdateComplete.
     * @param account Account object to update
     * @param cookie Object that will be passed into onUpdateComplete of the AsyncQueryHandler
     */
    public void updateAccount(Account account, Object cookie) {
        mQueryHandler.startUpdate(OpToken.UPDATE_ACCOUNT.value(), cookie,
                ACCOUNTS_TABLE, account.toContentValues(), null, null);
    }

    /**
     * Updates an Account object in the databaase.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#UPDATE_ACCOUNT}.
     * And call the value() method on the enum to obtain the int token for onUpdateComplete.
     * @param account Account object to update
     * @param cookie Object that will be passed into onUpdateComplete of the AsyncQueryHandler
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onUpdateComplete to obtain the callback.
     */
    public static void updateAccount(Account account, Object cookie,
                                     AsyncQueryHandler queryHandler) {
        queryHandler.startUpdate(OpToken.UPDATE_ACCOUNT.value(), cookie,
                ACCOUNTS_TABLE, account.toContentValues(), null, null);
    }

    /**
     * Updates the Courses an Account is linked to.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#UPDATE_COURSE}.
     * And call the value() method on the enum to obtain the int token for onUpdateComplete.
     * @param userName Username of the Account
     * @param courses List of courses to update
     * @param cookie Object that will be passed into onUpdateComplete of the AsyncQueryHandler
     */
    public void updateUserCourses(String userName, List<Course> courses, Object cookie)  {
        for (Course course : courses) {
            mQueryHandler.startUpdate(OpToken.UPDATE_COURSE.value(), cookie,
                    COURSES_TABLE, course.toContentValues(userName), null, null);
            for (Meeting meeting : course.getMeetings()) {
                mQueryHandler.startUpdate(OpToken.UPDATE_MEETING.value(),
                        cookie, MEETINGS_TABLE, meeting.toContentValues(course.getSln()), null,
                        null);
            }
        }
    }

    /**
     * Updates the Courses an Account is linked to.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#UPDATE_COURSE}.
     * And call the value() method on the enum to obtain the int token for onUpdateComplete.
     * @param userName Username of the Account
     * @param courses List of courses to update
     * @param cookie Object that will be passed into onUpdateComplete of the AsyncQueryHandler
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onUpdateComplete to obtain the callback.
     */
    public static void updateUserCourses(String userName, List<Course> courses, Object cookie,
                                     AsyncQueryHandler queryHandler)  {
        for (Course course : courses) {
            queryHandler.startUpdate(OpToken.UPDATE_COURSE.value(), cookie,
                    COURSES_TABLE, course.toContentValues(userName), null, null);
            for (Meeting meeting : course.getMeetings()) {
                queryHandler.startUpdate(OpToken.UPDATE_MEETING.value(),
                        cookie, MEETINGS_TABLE, meeting.toContentValues(course.getSln()), null,
                        null);
            }
        }
    }

    /**
     * Deletes an Account entry in the database. This cannot be reverted!
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#DELETE_ACCOUNT}.
     * And call the value() method on the enum to obtain the int token for onDeleteComplete.
     * @param account Account object to delete.
     * @param cookie Object that will be passed into onDeleteComplete of the AsyncQueryHandler
     */
    public void deleteAccount(Account account, Object cookie) {
        mQueryHandler.startDelete(OpToken.DELETE_ACCOUNT.value(), cookie, ACCOUNTS_TABLE, null,
                null);
    }

    /**
     * Deletes an Account entry in the database. This operation cannot be reverted!
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#DELETE_ACCOUNT}.
     * And call the value() method on the enum to obtain the int token for onDeleteComplete.
     * @param account Account object to delete.
     * @param cookie Object that will be passed into onDeleteComplete of the AsyncQueryHandler
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onDeleteComplete to obtain the callback.
     */
    public static void deleteAccount(Account account, Object cookie, AsyncQueryHandler queryHandler) {
        queryHandler.startDelete(OpToken.DELETE_ACCOUNT.value(), cookie, ACCOUNTS_TABLE, null,
                null);
    }

    /**
     * Deletes the courses that are linked to a specifc Account. This operation cannot be reverted.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#DELETE_COURSE}.
     * And call the value() method on the enum to obtain the int token for onDeleteComplete.
     * @param userName Username of the Account.
     * @param courses List of courses to delete.
     * @param cookie Object that will be passed into onDeleteComplete of the AsyncQueryHandler.
     */
    public void deleteUserCourses(String userName, List<Course> courses, Object cookie) {
        for (Course course : courses) {
            mQueryHandler.startDelete(OpToken.DELETE_COURSE.value(), cookie,
                    COURSES_TABLE, ScheduleContract.Courses.STUDENT_USERNAME, new String[]{userName});
            for (Meeting meeting : course.getMeetings()) {
                mQueryHandler.startDelete(OpToken.DELETE_MEETING.value(),
                        cookie, MEETINGS_TABLE, ScheduleContract.Meetings.SLN,
                        new String[]{course.getSln()});
            }
        }
    }

    /**
     * Deletes the courses that are linked to a specifc Account. This operation cannot be reverted.
     * Use {@link com.amgems.uwschedule.api.local.AsyncDataHandler.OpToken#DELETE_COURSE}.
     * And call the value() method on the enum to obtain the int token for onDeleteComplete.
     * @param userName Username of the Account.
     * @param courses List of courses to delete.
     * @param cookie Object that will be passed into onDeleteComplete of the AsyncQueryHandler.
     * @param queryHandler The AsyncQueryHandler object to use.
     *                     Override onDeleteComplete to obtain the callback.
     */
    public static void deleteUserCourses(String userName, List<Course> courses, Object cookie,
                                         AsyncQueryHandler queryHandler) {
        for (Course course : courses) {
            queryHandler.startDelete(OpToken.DELETE_COURSE.value(), cookie,
                    COURSES_TABLE, ScheduleContract.Courses.STUDENT_USERNAME, new String[]{userName});
            for (Meeting meeting : course.getMeetings()) {
                queryHandler.startDelete(OpToken.DELETE_MEETING.value(),
                        cookie, MEETINGS_TABLE, ScheduleContract.Meetings.SLN,
                        new String[]{course.getSln()});
            }
        }
    }

    /**
     * Sets the AsyncQueryHandler of this class.
     * Used if you want to change the behaviour of on___Complete callback methods.
     * @param queryHandler AsyncQueryHandler object.
     */
    public void setAsyncQueryHandler(AsyncQueryHandler queryHandler) {
        this.mQueryHandler = queryHandler;
    }
}
