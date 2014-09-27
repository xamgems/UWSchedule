package com.amgems.uwschedule.model;

/**
 * @author Sherman Pay.
 * @version 0.1, 9/6/14.
 *
 * TimetableEvent to be held within Timetables. An TimetableEvent is a timespan with a start
 * time, end time and paddings before and after a TimetableEvent to  specify empty timespans
 * between TimetableEvents. Each TimetableEvent also holds a Day it is on.
 * @see com.amgems.uwschedule.model.Timetable
 * @see com.amgems.uwschedule.model.Meeting.Day
 */
public interface TimetableEvent extends Comparable<TimetableEvent> {
    public int getStartTime();
    public int getEndTime();
    public Meeting.Day getDay();
    public int getBeforePadding();
    public void setBeforePadding(int padding);
    public int getAfterPadding();
    public void setAfterPadding(int padding);
    public void setFirstEvent(boolean firstEvent);
    public Event getEvent();
    public boolean isFirstEvent();
}
