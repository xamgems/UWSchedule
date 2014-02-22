package com.amgems.uwschedule.util;

/**
 * Defines a type-safe interface for publishing data to one or many
 * {@link Subscriber} instances.
 * <p>
 * @param <E> The type of data to publish
 * @author Zachary Iqbal
 */
public interface Publisher<E> {

    /**
     * Registers a subscriber to data that this publisher publishes.
     *
     * Once a new subscriber has been registered, this publisher should
     * ensure that the new subscriber has the most up to date data.
     *
     * @param dataSubscriber The subscriber to register for current and
     *                       future Publisher data.
     */
    public void register(Subscriber<? super E> dataSubscriber);

    /**
     * Publishes data to all currently registered subscribers.
     *
     * @param data The data to send.
     */
    public void publish(E data);
}
