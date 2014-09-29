package com.amgems.uwschedule.model;

/**
 * An Event is a something that can occur multiple times during a week for a specific time span.
 * @author Sherman Pay.
 */
public interface Event extends Comparable<Event> {
    public EventGroup getEventGroup();
    public Meeting.Day getDay();
    public int getStartTime();
    public int getEndTime();
}
