package com.amgems.uwschedule.model;

/**
 * @author Sherman Pay.
 * @version 0.1, 9/6/14.
 */
public interface TimetableEvent extends Comparable<TimetableEvent> {
    public int getStartTime();
    public int getEndTime();
}
