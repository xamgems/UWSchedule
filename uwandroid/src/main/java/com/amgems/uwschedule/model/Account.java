package com.amgems.uwschedule.model;

import android.content.ContentValues;
import com.amgems.uwschedule.provider.ScheduleContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * An immutable class containing data pertaining to the account of a
 * specific user.
 * <p>
 *
 * The class specifies basic information such as username and full name
 * of the student and also provides information to determine the last
 * time this account was updated on the uwschedule database.
 */
public class Account {

    @Expose
    @SerializedName("user_name")
    private final String mUserName;

    private final String mStudentName;

    private final long mLastUpdateTime;

    public static final long NOT_UPDATED = 0l;

    /**
     * Constructs an account instance with a specified timestamp of
     * last update.
     * <p>
     * The last update time specified corresponds to the the number of
     * seconds in unix time since the user was last updated on the
     * database.
     *
     * @param username The username for this Account
     * @param studentName The full name of the student
     * @param lastUpdateTime A long representing the last time of update
     *                       on the database or {@link Account#NOT_UPDATED}
     *                       if the user has yet to enter the database.
     */
    public Account (String username, String studentName, long lastUpdateTime) {
        mUserName = username;
        mStudentName = studentName;
        mLastUpdateTime = lastUpdateTime;
    }

    /**
     * Returns the account username.
     *
     * @return Username String for an account.
     */
    public String getUsername() {
        return mUserName;
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
     * Determines if a given {@link Account} is marked as added
     * into the UWSchedule database.
     *
     * @return {@code true} if this Account is considered to have
     *         entered the database and {@code false} if otherwise.
     */
    public boolean hasEnteredDatabase() {
        return mLastUpdateTime != NOT_UPDATED;
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
        return "{\n" + " username : " + mUserName + ",\n" +
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
