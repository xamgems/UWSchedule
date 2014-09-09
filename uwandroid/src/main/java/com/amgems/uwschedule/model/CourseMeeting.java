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
        mMeeting = meeting;
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
        int startDiff = this.mStartTime - other.getStartTime();
        if (startDiff == 0) {
            return this.mEndTime - other.getEndTime() ;
        } else {
            return startDiff;
        }
    }

    @Override
    public String toString() {
        return mCourse.getDepartmentCode() + "\n" +
                mCourse.getCourseNumber() + "\n" +
                mCourse.getSectionId() + "\n" +
                mStartTime  + " - " + mEndTime;
    }
}
