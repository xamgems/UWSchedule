package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.api.uw.GetStudentSlns;

/**
 * Collects registration data from the UW registration website for a given user.
 *
 * Preforms the
 *
 * @author Jeremy Teo
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
    public GetSlnLoader(Context context, String cookie) {
        super(context);
        mCookie = cookie;
        mGetter = GetStudentSlns.newInstance(mCookie);
    }

    @Override
    public Slns loadInBackground() {
        mGetter.execute();
        return new Slns(mGetter.getResponse(), null, mGetter.getHtml());
    }
}
