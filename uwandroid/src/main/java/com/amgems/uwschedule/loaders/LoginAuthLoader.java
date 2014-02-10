package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.api.uw.GetStudentSlns;
import com.amgems.uwschedule.api.uw.LoginAuthenticator;

/**
 * Created by zac on 8/31/13.
 */
public class LoginAuthLoader extends AsyncTaskLoader<LoginAuthLoader.Result> {

    LoginAuthenticator mAuthenticator;
    String mUsername;

    public static class Result {
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
