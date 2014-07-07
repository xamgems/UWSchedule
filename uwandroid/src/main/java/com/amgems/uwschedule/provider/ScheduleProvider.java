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

import com.amgems.uwschedule.provider.ScheduleDatabaseHelper.Tables;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Content provider for accessing all UWSchedule data.
 *
 * Clients are notified as the structure backing this provider is changed. This means that
 * sequential inserts should be staged as a transaction and then commited to avoid overnotifying any
 * listeners.
 */
public class ScheduleProvider extends ContentProvider {

    private ScheduleDatabaseHelper mDatabaseHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int ACCOUNTS = 100;
    private static final int ACCOUNTS_ID = 101;
    private static final int COURSES = 200;
    private static final int COURSES_ID = 201;
    private static final int MEETINGS = 300;
    private static final int MEETINGS_ID = 301;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, "accounts", ACCOUNTS);
        uriMatcher.addURI(authority, "accounts/#", ACCOUNTS_ID);
        uriMatcher.addURI(authority, "courses", COURSES);
        uriMatcher.addURI(authority, "courses/#", COURSES_ID);
        uriMatcher.addURI(authority, "meetings", MEETINGS);
        uriMatcher.addURI(authority, "meetings/#", MEETINGS_ID);

        return uriMatcher;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = ScheduleDatabaseHelper.getInstance(getContext());
        return mDatabaseHelper != null;
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        final int match = sUriMatcher.match(uri);

        // Build corresponding query based on Uri
        switch (match) {
            case ACCOUNTS: {
                qb.setTables(Tables.ACCOUNTS);
                break;
            }
            case ACCOUNTS_ID: {
                qb.setTables(Tables.ACCOUNTS);
                qb.appendWhere(ScheduleContract.Accounts._ID + " = " + uri.getLastPathSegment());
                break;
            }
            case COURSES: {
                qb.setTables(Tables.COURSES);
                break;
            }
            case COURSES_ID: {
                qb.setTables(Tables.COURSES);
                qb.appendWhere(ScheduleContract.Courses._ID + " = " + uri.getLastPathSegment());
                break;
            }
            case MEETINGS: {
                qb.setTables(Tables.MEETINGS);
                break;
            }
            case MEETINGS_ID: {
                qb.setTables(Tables.MEETINGS);
                qb.appendWhere(ScheduleContract.Meetings._ID + " = " + uri.getLastPathSegment());
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Cursor result = qb.query(db, projection, selection, selectionArgs, null, null, null);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCOUNTS:
                return ScheduleContract.Accounts.CONTENT_TYPE;
            case ACCOUNTS_ID:
                return ScheduleContract.Accounts.CONTENT_ITEM_TYPE;
            case COURSES:
                return ScheduleContract.Courses.CONTENT_TYPE;
            case COURSES_ID:
                return ScheduleContract.Courses.CONTENT_ITEM_TYPE;
            case MEETINGS:
                return ScheduleContract.Meetings.CONTENT_TYPE;
            case MEETINGS_ID:
                return ScheduleContract.Meetings.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCOUNTS: {
                final long rowId = db.insertOrThrow(Tables.ACCOUNTS, null, values);
                return ScheduleContract.Accounts.buildUriFromId(rowId);
            }
            case COURSES: {
                final long rowId = db.insertOrThrow(Tables.COURSES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ScheduleContract.Courses.buildUriFromId(rowId);
            }
            case MEETINGS: {
                final long rowId = db.insertOrThrow(Tables.MEETINGS, null, values);
                return ScheduleContract.Meetings.buildUriFromId(rowId);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
