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

package com.amgems.uwschedule.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.provider.ScheduleContract.AccountsColumns;
import com.amgems.uwschedule.provider.ScheduleContract.CoursesColumns;
import com.amgems.uwschedule.provider.ScheduleContract.MeetingsColumns;

/**
 * A helper class managing {@link android.database.sqlite.SQLiteDatabase} used as
 * an interface to the Schedule related data stored in the client.
 *<p>
 * The data stored in the database is a reflection of the most recent
 * {@link com.amgems.uwschedule.model.Course} and {@link com.amgems.uwschedule.model.Meeting}
 * related data for a given user.
 */
public class ScheduleDatabaseHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scheduleDatabase";

    private static ScheduleDatabaseHelper sInstance;

    static class Tables {
        public static final String ACCOUNTS = "accounts";
        public static final String COURSES = "courses";
        public static final String MEETINGS = "meetings";
    }

    static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE " + Tables.ACCOUNTS + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AccountsColumns.STUDENT_NAME + " TEXT NOT NULL, " +
            AccountsColumns.STUDENT_USERNAME + " TEXT NOT NULL, " +
            AccountsColumns.USER_LAST_UPDATE + " INT DEFAULT 0, " +
            "UNIQUE (" + AccountsColumns.STUDENT_USERNAME + ") ON CONFLICT REPLACE)";

    static final String CREATE_TABLE_COURSES = "CREATE TABLE " + Tables.COURSES + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CoursesColumns.SLN + " TEXT NOT NULL, " +
            CoursesColumns.STUDENT_USERNAME + " TEXT NOT NULL, " +
            CoursesColumns.DEPARTMENT_CODE + " TEXT NOT NULL, " +
            CoursesColumns.COURSE_NUMBER + " TEXT, " +
            CoursesColumns.CREDITS + " TEXT, " +
            CoursesColumns.SECTION_ID + " TEXT NOT NULL, " +
            CoursesColumns.TYPE + " TEXT NOT NULL, " +
            CoursesColumns.TITLE + " TEXT NOT NULL, " +
            "FOREIGN KEY(" + CoursesColumns.STUDENT_USERNAME + ") REFERENCES " +
            Tables.ACCOUNTS + "(" + AccountsColumns.STUDENT_USERNAME + "), " +
            "UNIQUE (" + CoursesColumns.SLN + ") ON CONFLICT REPLACE)";

    static final String CREATE_TABLE_MEETINGS = "CREATE TABLE " + Tables.MEETINGS + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MeetingsColumns.SLN + " TEXT NOT NULL, " +
            MeetingsColumns.START_TIME + " INT NOT NULL, " +
            MeetingsColumns.END_TIME + " INT NOT NULL, " +
            MeetingsColumns.LOCATION + " TEXT NOT NULL, " +
            MeetingsColumns.INSTRUCTOR + " TEXT NOT NULL, " +
            MeetingsColumns.MONDAY_MEET + " INT DEFAULT 0, " +
            MeetingsColumns.TUESDAY_MEET + " INT DEFAULT 0, " +
            MeetingsColumns.WEDNESDAY_MEET + " INT DEFAULT 0, " +
            MeetingsColumns.THURSDAY_MEET + " INT DEFAULT 0, " +
            MeetingsColumns.FRIDAY_MEET + " INT DEFAULT 0, " +
            MeetingsColumns.SATURDAY_MEET + " INT DEFAULT 0, " +
            "FOREIGN KEY(" + MeetingsColumns.SLN + ") REFERENCES " +
            Tables.COURSES + "(" + CoursesColumns.SLN + "))";


    private ScheduleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns an instance of the {@code ScheduleDatabaseHelper} object.
     * <p>
     * Ensures that for a given process, only one instance of the ScheduleDatabaseHelper
     * object is returned.
     * @param context Context from which the database will be resolved;
     */
    public static synchronized ScheduleDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScheduleDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ACCOUNTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_COURSES);
        sqLiteDatabase.execSQL(CREATE_TABLE_MEETINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        throw new AssertionError(DATABASE_VERSION + "is the only supported version at this time.");
    }

    /**
     * Returns an {@link Account} corresponding to a given username,
     * or {@code null} if none is found.
     *
     * @param accountUsername The username to query for.
     * @return The Account associated with the username.
     */
    public Account getAccount(String accountUsername) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                AccountsColumns.STUDENT_NAME,
                AccountsColumns.USER_LAST_UPDATE};
        String selection = AccountsColumns.STUDENT_USERNAME + " = ?";
        String[] selectionArgs = { accountUsername };

        Cursor result = db.query(Tables.ACCOUNTS, projection, selection, selectionArgs, null, null, null);

        Account account = null;
        if (result.moveToFirst()) {
            int accountId = result.getInt(0);
            String accountName = result.getString(1);
            Long accountLastUpdate = result.getLong(2);
            account = new Account(accountUsername, accountName, accountLastUpdate);
        }
        return account;
    }

    /**
     * Inserts the {@link Account} to the database.
     *
     * The username, full name and last time of update for the
     * given Account will be stored as an entry in the database.
     *
     * @param account The Account to add
     */
    public void insertAccount(Account account) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccountsColumns.STUDENT_USERNAME, account.getUsername());
        values.put(AccountsColumns.STUDENT_NAME, account.getStudentName());
        values.put(AccountsColumns.USER_LAST_UPDATE, account.getLastUpdateTime());

        db.insert(Tables.ACCOUNTS, null, values);
    }

}
