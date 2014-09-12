package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;

/**
 * @author Sherman Pay
 * @version 0.1, 9/6/14.
 * This class will hold "paddings" before and after a CourseMeeting.
 * Those "paddings" are meant to represent the empty time between CourseMeetings
 */
public class PaddedCourseMeeting implements TimetableEvent {
    private int beforePadding;
    private int afterPadding;
    private boolean firstEvent;
    private CourseMeeting courseMeeting;

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

    public boolean firstEvent() {
        return firstEvent;
    }
    public CourseMeeting getCourseMeeting() {
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
        return courseMeeting.compareTo(other);
    }

    @Override
    public String toString() {
        return courseMeeting.toString() +
                "\nAfter: " + afterPadding + "\n";
    }

}