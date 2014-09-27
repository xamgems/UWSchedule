package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;

/**
 * @author Sherman Pay
 * @version 0.1, 9/6/14.
 * A special kind of TimetableEvent that represents a specific Meeting of a UW course.
 */
public class PaddedCourseMeeting implements TimetableEvent {
    private int beforePadding;
    private int afterPadding;
    private boolean firstEvent;
    private Event courseMeeting;

    public PaddedCourseMeeting(CourseMeeting courseMeeting) {
        this.courseMeeting = courseMeeting;
    }

    public PaddedCourseMeeting(CourseMeeting courseMeeting, int beforePadding) {
        this.courseMeeting = courseMeeting;
        this.beforePadding = beforePadding;
    }

    public int getBeforePadding() {
        return beforePadding;
    }

    public void setBeforePadding(int beforePadding) {
        this.beforePadding = beforePadding;
    }

    public int getAfterPadding() {
        return afterPadding;
    }

    public void setAfterPadding(int afterPadding) {
        this.afterPadding = afterPadding;
    }

    public void setFirstEvent(boolean firstEvent) {
        this.firstEvent = firstEvent;
    }

    public boolean isFirstEvent() {
        return firstEvent;
    }
    public Event getEvent() {
        return courseMeeting;
    }

    public void setCourseMeeting(CourseMeeting courseMeeting) {
        this.courseMeeting = courseMeeting;
    }

    public Meeting.Day getDay() {
        return this.courseMeeting.getDay();
    }

    @Override
    public int getStartTime() { return courseMeeting.getStartTime(); }

    @Override
    public int getEndTime() {
        return courseMeeting.getEndTime();
    }

    @Override
    public int compareTo(@NonNull TimetableEvent other) {
        return courseMeeting.compareTo(other.getEvent());
    }

    @Override
    public String toString() {
        return courseMeeting.toString();
    }

}
