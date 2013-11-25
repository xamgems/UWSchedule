package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.amgems.uwschedule.api.uw.LoginAuthenticator;

/**
 * Created by zac on 8/31/13.
 */
public class LoginAuthLoader extends AsyncTaskLoader<LoginAuthLoader.Result>{

    LoginAuthenticator mAuthenticator;
    String mUsername;

    public static class Result {
        final LoginAuthenticator.Response mResponse;
        final String mCookieValue;
        final String mUsername;

        Result(LoginAuthenticator.Response response, String cookieValue, String username) {
            mResponse = response;
            mCookieValue = cookieValue;
            mUsername = username;
        }

        public String getCookieValue() {
            return mCookieValue;
        }

        public LoginAuthenticator.Response getResponse() {
            return mResponse;
        }

        public String getUsername() { return mUsername; }
    }

    public LoginAuthLoader(Context context, String username, String password) {
        super(context);
        mAuthenticator = LoginAuthenticator.newInstance(username, password);
    }

    @Override
    public Result loadInBackground() {
        mAuthenticator.execute();
        return new Result(mAuthenticator.getResponse(), mAuthenticator.getCookie(), mUsername);
    }
}
