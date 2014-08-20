/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface.
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
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

package com.amgems.uwschedule.api.uw;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A thread-safe store for cookie and session related data pertaining to the active user.
 * <p>
 * All session information at a given time is stored as a single cookie
 * string. Although this single cookie maps to a given user for the period of
 * time that the cookie is valid, the cookie stored is effectively user agnostic.
 * CookieStore keeps no notion of the user to which the current cookie belongs.
 */
public class CookieStore {

    private static CookieStore sInstance = null;

    private static final String PREFERENCE_NAME = "cookiePreference";
    private static final String PREFERENCE_KEY_COOKIE = "mActiveCookie";

    private SharedPreferences mCookiePrefrences;
    private volatile String mActiveCookie;

    private CookieStore(Context context) {
        mCookiePrefrences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns an instance of the {@code CookieStore} object.
     * <p>
     * Ensures that for a given process, only one instance of the CookieStore
     * object is returned. This means that changes made from one instance will
     * always be visible in the other.
     *
     * @param context Context from which the {@link SharedPreferences} for this CookieStore
     *                will be resolved;
     */
    public static synchronized CookieStore getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CookieStore(context);
        }
        return sInstance;
    }

    /**
     * Sets the active cookie for this {@link CookieStore}.
     *
     * @param cookie The string value of the cookie to store.
     */
    public synchronized void setActiveCookie(String cookie) {
        SharedPreferences.Editor editor = mCookiePrefrences.edit();
        editor.putString(PREFERENCE_KEY_COOKIE, cookie);
        editor.apply();
        mActiveCookie = cookie;
    }

    /**
     * Reads and returns the currently active cookie.
     *
     * @return The cookie String.
     */
    public synchronized String getActiveCookie() {
        if (mActiveCookie == null) {
            mActiveCookie = mCookiePrefrences.getString(PREFERENCE_KEY_COOKIE, null);
        }
        return mActiveCookie;
    }

    /**
     * Permanently removes the active cookie.
     */
    public synchronized void flushActiveCookie() {
        SharedPreferences.Editor editor = mCookiePrefrences.edit();
        editor.remove(PREFERENCE_KEY_COOKIE);
        editor.apply();
        mActiveCookie = null;
    }
}
