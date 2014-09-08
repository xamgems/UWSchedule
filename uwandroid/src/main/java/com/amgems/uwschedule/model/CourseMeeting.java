package com.amgems.uwschedule.model;

/**
 * @author shermpay on 8/19/14.
 */
public class CourseMeeting implements TimetableEvent {
    private Course mCourse;
    private Meeting mMeeting;
    private int mStartTime;
    private int mEndTime;

    public CourseMeeting(Course course, Meeting meeting) {
        mCourse = course;
        mMeeting = mMeeting;
        mStartTime = mMeeting.getStartTime();
        mEndTime = mMeeting.getEndTime();
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
        int startDiff = other.getStartTime() - this.mStartTime;
        if (startDiff == 0) {
            return other.getEndTime() - this.mEndTime;
        } else {
            return startDiff;
        }
    }
}
