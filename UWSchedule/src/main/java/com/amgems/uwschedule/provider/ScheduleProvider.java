package com.amgems.uwschedule.provider;

import com.amgems.uwschedule.provider.ScheduleDatabaseHelper.Tables;
import com.amgems.uwschedule.provider.ScheduleContract.Accounts;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by zac on 12/1/13.
 */
public class ScheduleProvider extends ContentProvider {

    private ScheduleDatabaseHelper mDatabaseHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int ACCOUNTS = 100;
    private static final int ACCOUNTS_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, "accounts", ACCOUNTS);
        uriMatcher.addURI(authority, "accounts/*", ACCOUNTS_ID);

        return uriMatcher;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = ScheduleDatabaseHelper.getInstance(getContext());
        return (mDatabaseHelper != null) ? true : false;
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
                qb.appendWhere(Accounts._ID + " = " + uri.getLastPathSegment());
            }
        }

        return qb.query(db, projection, selection, selectionArgs, null, null, null);
    }

    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCOUNTS:
                return Accounts.CONTENT_TYPE;
            case ACCOUNTS_ID:
                return Accounts.CONTENT_ITEM_TYPE;
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
            case ACCOUNTS:
                db.insertOrThrow(Tables.ACCOUNTS, null, values);
                return Accounts.buildAccountsUri(values.getAsString(Accounts._ID));
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
