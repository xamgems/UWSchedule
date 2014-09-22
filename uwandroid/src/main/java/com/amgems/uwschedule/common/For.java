package com.amgems.uwschedule.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Qualifies the subclass type that is returned for a given object graph.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface For {

    ContextOf value();

    /**
     * Provides for a subclass of {@link android.content.Context}.
     */
    public enum ContextOf {
        /**
         * Provides for the Context of an {@link android.app.Application}
         */
        APPLICATION,

        /**
         * Provides for the Context of an {@link android.app.Activity}
         */
        ACTIVITY
    }

}
