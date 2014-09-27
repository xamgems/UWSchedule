package com.amgems.uwschedule.common;

import android.content.Context;

import com.amgems.uwschedule.common.For.ContextOf;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contributes to the object graph of a {@link com.amgems.uwschedule.common.BaseApplication}.
 */
@Module (
        injects = BaseApplication.class,
        library = true
)
public class BaseApplicationModule {

    private final BaseApplication mApplication;

    public BaseApplicationModule(BaseApplication application) {
        mApplication = checkNotNull(application);
    }

    @Provides
    @Singleton
    @For(ContextOf.APPLICATION)
    public Context provideApplicationContext() {
        return mApplication;
    }

}
