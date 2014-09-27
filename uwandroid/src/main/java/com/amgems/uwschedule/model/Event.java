package com.amgems.uwschedule.model;

/**
 * @author Sherman Pay.
 * @version 0.1, 9/24/14.
 */
public interface Event extends Comparable<Event> {
    public EventGroup getEventGroup();
    public Meeting.Day getDay();
    public int getStartTime();
    public int getEndTime();
}
