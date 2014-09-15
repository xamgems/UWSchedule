package com.amgems.uwschedule.common;

import android.app.Application;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

/**
 * The default {@link android.app.Application} used, providing access to a root
 * {@link dagger.ObjectGraph}.
 */
public class BaseApplication extends Application implements Injectable {

    private ObjectGraph mApplicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationGraph = ObjectGraph.create(getModules());
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new BaseApplicationModule(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inject(Object target) {
        mApplicationGraph.inject(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectGraph getObjectGraph() {
        return mApplicationGraph;
    }

}
