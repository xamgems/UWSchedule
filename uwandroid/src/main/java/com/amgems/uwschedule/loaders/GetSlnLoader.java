package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import android.os.Handler;
import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.api.uw.GetStudentSlns;

/**
 * Created by JeremyTeoMBP on 9/2/14.
 */
public class GetSlnLoader extends AsyncTaskLoader<GetSlnLoader.Slns> {
    String mCookie;
    GetStudentSlns mGetter;

    public static class Slns {
        final Response mResponse;
        final String[] mSlns;
        final String mHtml;

        Slns(Response response, String[] slns, String html) {
            mResponse = response;
            mSlns = slns;
            mHtml = html;
        }

        public String getHtml() {
            return mHtml;
        }
    }
    public GetSlnLoader(Context context, Handler activiyHandler, String cookie) {
        super(context);
        mCookie = cookie;
        mGetter = GetStudentSlns.newInstance(context, activiyHandler, mCookie);
    }

    @Override
    public Slns loadInBackground() {
        mGetter.execute();
        return new Slns(mGetter.getResponse(), null, mGetter.getHtml());
    }
}
