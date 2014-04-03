package com.amgems.uwschedule.util;

/**
 * Defines a type-safe interface for subscribing to data from a {@link Publisher}
 * instance.
 * <p>
 * This subscriber should associate with a given Publisher when necessary by
 * calling {@link Publisher#register(Subscriber)}, with this Subscriber.
 *
 * @param <E> The type of data to be received.
 * @author Zachary Iqbal
 */
public interface Subscriber<E> {

    /**
     * Updates this subscriber with the new data.
     *
     * This is called by the Publisher registered for when new data is to
     * be published. There is no contract defining what this Subscriber
     * should do with previously updated data, if any.
     *
     * @param updateData New data to be updated
     */
    public void update(E updateData);

}
