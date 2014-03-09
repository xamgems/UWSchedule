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

package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.api.uw.GetStudentSlns;
import com.amgems.uwschedule.api.uw.LoginAuthenticator;

/**
 * A loader used to collect session cookies from a login instance.
 */
public class LoginAuthLoader extends AsyncTaskLoader<LoginAuthLoader.Result> {

    private LoginAuthenticator mAuthenticator;
    private String mUsername;

    public static class Result {
        private final LoginAuthenticator.Response mResponse;
        private final String mCookieValue;
        private final String mUsername;
        final Response mResponse;
        final String mCookieValue;
        final String mUsername;

        Result(Response response, String cookieValue, String username) {
            mResponse = response;
            mCookieValue = cookieValue;
            mUsername = username;
        }

        public String getCookieValue() {
            return mCookieValue;
        }

        public Response getResponse() { return mResponse; }

        public String getUsername() { return mUsername; }
    }

    public LoginAuthLoader(Context context, String username, String password) {
        super(context);
        mUsername = username;
        mAuthenticator = LoginAuthenticator.newInstance(username, password);
    }

    @Override
    public Result loadInBackground() {
        mAuthenticator.execute();
        GetStudentSlns getter = GetStudentSlns.newInstance(mAuthenticator.getCookie());
        getter.execute();
        return new Result(mAuthenticator.getResponse(), mAuthenticator.getCookie(), mUsername);
    }
}
