package com.amgems.uwschedule.common;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * The default {@link android.app.Application} used, providing access to a root
 * {@link dagger.ObjectGraph}.
 */
public class BaseApplication extends Application implements Injectable {

    private ObjectGraph mApplicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationGraph = ObjectGraph.create(getModules().toArray());
        mApplicationGraph.inject(this);
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

    /**
     * Provides modules to use for inject this Application's object graph.
     *
     * Subclasses should override this method to include required modules given that they
     * <em>always</em> include modules provided by calling {@code super.getModules()}.
     *
     * @return A List of modules for this Application's object graph to use.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new BaseApplicationModule(this));
    }

}
