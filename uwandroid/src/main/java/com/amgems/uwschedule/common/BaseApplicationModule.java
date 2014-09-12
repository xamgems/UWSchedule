package com.amgems.uwschedule.common;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Module (
        injects = BaseApplication.class
)
public class BaseApplicationModule {

    private final Context mApplicationContext;

    public BaseApplicationModule(Context applicationContext) {
        mApplicationContext = checkNotNull(applicationContext);
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

}
