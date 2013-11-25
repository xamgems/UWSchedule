package com.amgems.uwschedule.model;

/**
 * Class containing data pertaining to the account of a specific user.
 */
public class Account {

    private final int mId;
    private final String mUserame;
    private final String mStudentName;

    private final long mLastUpdateTime;

    public Account (int id, String username, String studentName, long lastUpdateTime) {
        mId = id;
        mUserame = username;
        mStudentName = studentName;
        mLastUpdateTime = lastUpdateTime;
    }

    /**
     * Returns the database ID for this {@link Account}.
     *
     * @return ID for this account.
     */
    public int getId() {
        return mId;
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
    public String getmStudentName() {
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

}
