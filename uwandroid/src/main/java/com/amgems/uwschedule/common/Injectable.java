package com.amgems.uwschedule.common;

/**
 * Represents contract binding components that can inject objects with the component-specific
 * dependency sub-graph.
 */
public interface Injectable {

    /**
     * Injects this component's sub-graph into a specified target.
     *
     * @param target The object whose {@link javax.inject.Inject} parameters will be injected.
     */
    public void inject(Object target);

}