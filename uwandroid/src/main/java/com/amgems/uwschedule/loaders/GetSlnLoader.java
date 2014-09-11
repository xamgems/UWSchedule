package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.api.uw.GetStudentSlns;
import com.amgems.uwschedule.util.HttpClient;

import java.util.List;

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

    public GetSlnLoader(Context context, HttpClient httpClient, String cookie) {
        super(context);
        mCookie = cookie;
        mGetter = GetStudentSlns.newInstance(mCookie, httpClient);
    }

    @Override
    public Slns loadInBackground() {
        mGetter.execute();
        return new Slns(mGetter.getResponse(), mGetter.getSlns(), mGetter.getHtml());
    }

    public static class Slns {
        final Response mResponse;
        final List<String> mSlns;
        //      mHtml for debugging purposes
        final String mHtml;

        Slns(Response response, List<String> slns, String html) {
            mResponse = response;
            mSlns = slns;
            mHtml = html;
        }

        public String getHtml() {
            return mHtml;
        }

        public List<String> getSlns() {
            return mSlns;
        }
    }
}
