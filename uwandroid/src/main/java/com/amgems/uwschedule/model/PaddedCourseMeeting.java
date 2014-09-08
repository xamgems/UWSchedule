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
    private CourseMeeting courseMeeting;
    private int afterPadding;

    public PaddedCourseMeeting(CourseMeeting courseMeeting) {
        this.courseMeeting = courseMeeting;
    }

    public PaddedCourseMeeting(CourseMeeting courseMeeting, int beforePadding, int afterPadding) {
        this.courseMeeting = courseMeeting;
        this.beforePadding = beforePadding;
        this.afterPadding = afterPadding;
    }

    public int getBeforePadding() {
        return beforePadding;
    }

    public void setBeforePadding(int beforePadding) {
        this.beforePadding = beforePadding;
    }

    public CourseMeeting getCourseMeeting() {
        return courseMeeting;
    }

    public void setCourseMeeting(CourseMeeting courseMeeting) {
        this.courseMeeting = courseMeeting;
    }

    public int getAfterPadding() {
        return afterPadding;
    }

    public void setAfterPadding(int afterPadding) {
        this.afterPadding = afterPadding;
    }

    public int getStartTime() {
        return courseMeeting.getStartTime();
    }

    @Override
    public int compareTo(@NonNull TimetableEvent other) {
        return courseMeeting.compareTo(other);
    }

    public int getEndTime() {
        return courseMeeting.getEndTime();
    }
}
