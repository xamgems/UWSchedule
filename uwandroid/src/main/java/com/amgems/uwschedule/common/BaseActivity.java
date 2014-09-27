package com.amgems.uwschedule.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * The default {@link android.app.Activity} used, providing access to a root activity specific
 * {@link dagger.ObjectGraph}. All Activity classes should inherit this class.
 */
public abstract class BaseActivity extends FragmentActivity {

    ObjectGraph mActivityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector application = (Injector) getApplication();
        mActivityGraph = application.getObjectGraph().plus(getModules().toArray());
        mActivityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mActivityGraph = null;
    }

    /**
     * Provides modules to use for inject this Activity's object graph.
     *
     * Subclasses should override this method to include required modules given that they
     * <em>always</em> include modules provided by calling {@code super.getModules()}.
     *
     * @return A List of modules for this Activity's object graph to use.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new BaseActivityModule(this));
    }
}
