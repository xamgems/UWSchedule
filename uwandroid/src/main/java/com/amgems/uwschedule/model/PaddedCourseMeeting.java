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
        return courseMeeting.toString();
    }

    /**
     * @author shermpay on 8/19/14.
     */
    public static class CourseMeeting implements Comparable<TimetableEvent> {
        private Course mCourse;
        private Meeting mMeeting;
        private Meeting.Day mDay;
        private int mStartTime;
        private int mEndTime;

        public CourseMeeting(Course course, Meeting meeting, Meeting.Day day) {
            mCourse = course;
            mMeeting = meeting;
            this.mDay = day;
            mStartTime = meeting.getStartTime();
            mEndTime = meeting.getEndTime();
        }

        public Course getCourse() {
            return mCourse;
        }

        public Meeting.Day getDay() {
            return mDay;
        }

        public int getStartTime() {
            return mStartTime;
        }

        public int getEndTime() {
            return mEndTime;
        }

        @Override
        public int hashCode() {
           return ((Integer) mStartTime).hashCode() + ((Integer) mEndTime).hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CourseMeeting &&
                    this.mStartTime == ((CourseMeeting)other).mStartTime &&
                    this.mEndTime == ((CourseMeeting) other).mEndTime;
        }

        @Override
        public int compareTo(TimetableEvent other) {
            int startDiff = Integer.valueOf(this.mStartTime).compareTo(other.getStartTime());
            if (startDiff == 0) {
                return Integer.valueOf(this.mEndTime).compareTo(other.getEndTime());
            } else {
                return startDiff;
            }
        }

        @Override
        public String toString() {
            return mCourse.getDepartmentCode() + " " + mCourse.getCourseNumber() + " " +
                    mCourse.getSectionId() + "\n" + mMeeting.getLocation();
        }
    }
}
