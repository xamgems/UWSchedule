package com.amgems.uwschedule.model;

import android.content.ContentValues;
import com.amgems.uwschedule.provider.ScheduleContract;

/**
 * Class containing data pertaining to the account of a specific user.
 */
public class Account {

    private final String mUserame;
    private final String mStudentName;

    private final long mLastUpdateTime;

    public Account (String username, String studentName) {
        mUserame = username;
        mStudentName = studentName;
        mLastUpdateTime = 0;
    }

    public Account (String username, String studentName, long lastUpdateTime) {
        mUserame = username;
        mStudentName = studentName;
        mLastUpdateTime = lastUpdateTime;
    }

    /**
     * Returns the account username.
     *
     * @return Username String for an account.
     */
    public String getUsername() {
        return mUserame;
    }

    /**
     * Returns the full name of the user for this
     * {@link Account}.
     *
     * @return Full name of user of an account.
     */
    public String getStudentName() {
        return mStudentName;
    }

    /**
     * Returns the last update time for this {@link Account}.
     *
     * Last time of update for a given user is stored numerically
     * as unix time in milliseconds.
     *
     * @return Time last updated.
     */
    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    /**
     * Determines if a given {@link Account} account has its primary
     * information updated.
     * <p>
     * If primary information relating to an account is available, information
     * about a user's full name and accurate last update information is
     * available.
     * </p>
     * @return {@code true} if information is available and {@code false}
     *         if otherwise.
     */
    public boolean hasPrimaryDataUpdated() {
        return mLastUpdateTime != 0;
    }

    /**
     * Returns a brief description of this account. The exact details
     * of this representation are unspecified and subject to change,
     * but the following can be regarded as typical:
     *
     * "{
     *     username : zac23
     *     fullname : Zachary Iqbal
     *     lastupdate : 2027
     * }"
     */
    @Override
    public String toString() {
        return "{\n" + " username : " + mUserame + ",\n" +
                   " fullname : " + mStudentName + ",\n" +
                   " lastupdate : " + mLastUpdateTime + "\n" +
                "}";
    }

    /**
     * Returns a representation of this {@link Account} in the
     * form of a {@link ContentValues}.
     */
    public ContentValues toContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleContract.Accounts.STUDENT_USERNAME, getUsername());
        contentValues.put(ScheduleContract.Accounts.STUDENT_NAME, getStudentName());
        contentValues.put(ScheduleContract.Accounts.USER_LAST_UPDATE, getLastUpdateTime());
        return contentValues;
    }

}
