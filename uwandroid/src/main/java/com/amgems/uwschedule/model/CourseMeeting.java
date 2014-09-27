package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;

/**
* @author Sherman Pay.
* @version 0.1, 9/24/14.
*/
public class CourseMeeting implements Event {
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

    public EventGroup getEventGroup() {
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
    public int compareTo(@NonNull Event other) {
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
