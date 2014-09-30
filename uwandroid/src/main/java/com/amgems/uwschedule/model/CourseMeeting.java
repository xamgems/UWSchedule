package com.amgems.uwschedule.model;

import android.support.annotation.NonNull;

import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a concrete occurrence of a Meeting for a particular Course at a particular time span.
 *
 * @author Sherman Pay.
 */
public class CourseMeeting implements Event {
    private Course mCourse;
    private Meeting mMeeting;
    private Meeting.Day mDay;
    private int mStartTime;
    private int mEndTime;

    public CourseMeeting(@NonNull Course course, @NonNull Meeting meeting,
                         @NonNull Meeting.Day day) {
        checkNotNull(course);
        checkNotNull(meeting);
        checkNotNull(day);
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
        int result = 23;
        result = result * 31 + mStartTime;
        result = result * 31 + mEndTime;
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CourseMeeting) {
            return this.mStartTime == ((CourseMeeting) other).mStartTime &&
                    this.mEndTime == ((CourseMeeting) other).mEndTime;
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Event other) {
        int startDiff = Ints.compare(this.mStartTime, other.getStartTime());
        if (startDiff == 0) {
            return Ints.compare(this.mEndTime, other.getEndTime());
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
