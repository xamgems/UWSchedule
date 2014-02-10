package com.amgems.uwschedule.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

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

        Slns(Response response, String[] slns) {
            mResponse = response;
            mSlns = slns;
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
        return new Slns(mGetter.getResponse(), null);
    }
}
