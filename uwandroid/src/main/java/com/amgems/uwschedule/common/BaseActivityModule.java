package com.amgems.uwschedule.common;

import android.content.Context;

import com.amgems.uwschedule.common.For.ContextOf;
import com.amgems.uwschedule.ui.HomeActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contributes to the object graph of a {@link com.amgems.uwschedule.common.BaseActivity}.
 */
@Module (
        injects = BaseActivity.class,
        library = true
)
public class BaseActivityModule {

    private final BaseActivity mActivity;

    public BaseActivityModule(BaseActivity activity) {
        mActivity = checkNotNull(activity);
    }

    @Provides
    @Singleton
    @For(ContextOf.ACTIVITY)
    public Context provideActivityContext() {
        return mActivity;
    }
}
