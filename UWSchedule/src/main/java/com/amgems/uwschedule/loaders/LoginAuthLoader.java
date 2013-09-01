package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.amgems.uwschedule.api.uw.LoginAuthenticator;

/**
 * Created by zac on 8/31/13.
 */
public class LoginAuthLoader extends AsyncTaskLoader<LoginAuthLoader.Result>{

    public static final String PARAM_IN_USERNAME = "param.in.username";
    public static final String PARAM_IN_PASSWORD = "param.in.password";

    LoginAuthenticator mAuthenticator;

    public static class Result {
        final LoginAuthenticator.Response mResponse;
        final String mCookieValue;

        Result(LoginAuthenticator.Response response, String cookieValue) {
            mResponse = response;
            mCookieValue = cookieValue;
        }

        public String getCookieValue() {
            return mCookieValue;
        }

        public LoginAuthenticator.Response getResponse() {
            return mResponse;
        }
    }

    public LoginAuthLoader(Context context, String username, String password) {
        super(context);
        mAuthenticator = LoginAuthenticator.newInstance(username, password);
    }

    @Override
    public Result loadInBackground() {
        mAuthenticator.execute();
        return new Result(mAuthenticator.getResponse(), mAuthenticator.getCookie());
    }
}
