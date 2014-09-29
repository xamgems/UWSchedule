package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A special kind of TimetableEvent that represents a specific Meeting of a UW course.
 * @author Sherman Pay
 */
public class PaddedCourseMeeting implements TimetableEvent {
    private int beforePadding;
    private int afterPadding;
    private boolean firstEvent;
    private Event courseMeeting;

    public PaddedCourseMeeting(@NonNull CourseMeeting courseMeeting) {
        checkNotNull(courseMeeting);
        this.courseMeeting = courseMeeting;
    }

    public PaddedCourseMeeting(@NonNull CourseMeeting courseMeeting, int beforePadding) {
        checkNotNull(courseMeeting);
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

    public void setCourseMeeting(@NonNull CourseMeeting courseMeeting) {
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
