package com.amgems.uwschedule.model;

/**
 * @author shermpay on 8/19/14.
 */
public class CourseMeeting implements Comparable<TimetableEvent> {
    private Course mCourse;
    private Meeting.Day mDay;
    private int mStartTime;
    private int mEndTime;

    public CourseMeeting(Course course, Meeting meeting, Meeting.Day day) {
        mCourse = course;
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
        int startDiff = Integer.compare(this.mStartTime, other.getStartTime());
        if (startDiff == 0) {
            return Integer.compare(this.mEndTime, other.getEndTime());
        } else {
            return startDiff;
        }
    }

    @Override
    public String toString() {
        return mCourse.getDepartmentCode() + "\n" +
                mCourse.getCourseNumber() + " " +
                mCourse.getSectionId();
    }
}
