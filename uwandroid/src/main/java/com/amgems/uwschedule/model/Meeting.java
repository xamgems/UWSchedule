/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface.
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.amgems.uwschedule.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.amgems.uwschedule.R;
import com.amgems.uwschedule.provider.ScheduleContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a scheduled meeting on a Course.
 *
 * Meetings expose information about the time, location and instructor
 * for any given course.
 */
public class Meeting implements Parcelable {

    @Expose
    @SerializedName("days")
    private final Set<Day> mMeetingDays;

    @Expose
    @SerializedName("start_time")
    private final int mStartTime;

    @Expose
    @SerializedName("end_time")
    private final int mEndTime;

    @Expose
    @SerializedName("location")
    private final String mLocation;

    @Expose
    @SerializedName("instructor")
    private final String mInstructor;

    /**
     * The day of the week for which a class meets.
     */
    public static enum Day {
        M("Monday", R.id.monday_icon, ScheduleContract.Meetings.MONDAY_MEET),
        T("Tuesday", R.id.tuesday_icon, ScheduleContract.Meetings.TUESDAY_MEET),
        W("Wednesday", R.id.wednesday_icon, ScheduleContract.Meetings.WEDNESDAY_MEET),
        TH("Thursday", R.id.thursday_icon, ScheduleContract.Meetings.THURSDAY_MEET),
        F("Friday", R.id.friday_icon, ScheduleContract.Meetings.FRIDAY_MEET),
        S("Saturday", R.id.saturday_icon, ScheduleContract.Meetings.SATURDAY_MEET);

        private final String mDayText;

        /**
         * The resource id of a TextView that can specify a given day.
         */
        private final int mIconResId;

        /** The name of the boolean day column this day corresponds to in the
         *  database's meetings table.
         *  See {@link com.amgems.uwschedule.provider.ScheduleContract.Meetings}
         *  for more.*/
        private final String mColumnName;

        private Day(String dayText, int iconResId, String columnName) {
            mDayText = dayText;
            mIconResId = iconResId;
            mColumnName = columnName;
        }

        public String getColumnName() {
            return mColumnName;
        }

        public int getIconResId() {
            return mIconResId;
        }

        @Override
        public String toString() {
            return mColumnName + ":" + mDayText;
        }

        /**
         * Returns the Day enum representation of a given daycode.
         *
         * <p>This method does not enforce case sensitivity on dayCode
         * and thus should always be used in favor of {@link Enum#valueOf(Class, String)}</p>
         *
         * @param dayCode Case insensitive day code corresponding to
         *                a given day
         */
        public static Day valueOfDayCode(String dayCode) {
            return Day.valueOf(dayCode.toUpperCase());
        }
    }

    /** A {@link android.os.Parcelable.Creator} used to generate an instance of a {@code Course}*/
    public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {

        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    private Meeting(Builder builder) {
        mMeetingDays = builder.mMeetingDays;
        mStartTime = builder.mStartTime;
        mEndTime = builder.mEndTime;
        mLocation = builder.mLocation;
        mInstructor = builder.mInstructor;
    }

    private Meeting (Parcel in) {
        mMeetingDays = EnumSet.noneOf(Day.class);
        List<String> dayNameList = new ArrayList<String>();
        in.readStringList(dayNameList);
        for (String dayName : dayNameList) {
            mMeetingDays.add(Day.valueOfDayCode(dayName));
        }
        mStartTime = in.readInt();
        mEndTime = in.readInt();
        mLocation = in.readString();
        mInstructor = in.readString();
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * <p>Note that this method should <i><not</i> be called by a client code.<p/>
     *
     * @param out The Parcel in which the object should be written.
     * @param i Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int i) {
        List<String> meetingDayList = new ArrayList<String>();
        for (Day day : mMeetingDays) {
            meetingDayList.add(day.name());
        }
        out.writeStringList(meetingDayList);
        out.writeInt(mStartTime);
        out.writeInt(mEndTime);
        out.writeString(mLocation);
        out.writeString(mInstructor);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * <p>Note that this method should <i><not</i> be called by a client code.<p/>
     *
     * @return A bitmask indicating the set of special object types marshalled by
     *         the Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public Set<Day> getMeetingDays() { return mMeetingDays; }

    public String getLocation() { return mLocation; }

    public String getInstructor() {
        return mInstructor;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    /**
     * Returns a representation of this {@link Meeting}, given a SLN.
     *
     * This representation contains all logical information related to
     * a meeting. The inclusion of an SLN tied to this Meeting is solely
     * for forming course to meeting relationships in the client database.
     *
     * @param sln The SLN to represent this meeting being tied to.
     */
    public ContentValues toContentValues(String sln) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(ScheduleContract.Meetings.SLN, sln);
        contentValues.put(ScheduleContract.Meetings.START_TIME, getStartTime());
        contentValues.put(ScheduleContract.Meetings.END_TIME, getEndTime());
        contentValues.put(ScheduleContract.Meetings.LOCATION, getLocation());
        contentValues.put(ScheduleContract.Meetings.INSTRUCTOR, getInstructor());


        Log.d(getClass().getSimpleName(), "sln: " + sln + "meetings: " + getMeetingDays());
        for (Day day : getMeetingDays()) {
            contentValues.put(day.getColumnName(), ScheduleContract.Meetings.HAS_MEETING);
        }

        return contentValues;
    }

    @Override
    public String toString() {
        return  "{" + "days: " + getMeetingDays() + "\n" +
                getStartTime() + " - " + getEndTime() + "\n" +
                "Location: " + getLocation() + "\n" +
                "Instructor: " + getInstructor() + "}";
    }

    /**
     * A helper class used for constructing {@code Meeting} objects.
     *
     * <p>For example, a meeting object can be constructed as follows:</p>
     *
     *
     * <pre> {@code Meeting.Builder builder = new Meeting.Builder(meetingDays, "340-430")
     *                                   .location("SAV 231")
     *                                   .instructor("SOLOMON K.");
     *
     * Meeting meet = builder.build(); }</pre>
     *
     */
    public static class Builder {
        // Required parameters
        private Set<Day> mMeetingDays;
        private int mStartTime;
        private int mEndTime;

        // Optional parameters
        private String mLocation = "";
        private String mInstructor = "";

        private static final Pattern TIME_SPAN_PATTERN = Pattern.compile("^([0-9]{1,2}:?[0-9]{2})- ?([0-9]{1,2}:?[0-9]{2}) ?(?:P|PM)?$");

        /**
         * Constructs a builder for the {@code Meeting} class.
         *
         * @param meetingDays set of days for which the course meets
         * @param timeSpan a string representation of the time span
         *                 during which the course has meetings
         */
        public Builder(Set<Day> meetingDays, String timeSpan) {
            Matcher timeMatcher = TIME_SPAN_PATTERN.matcher(timeSpan);

            if (timeMatcher.matches()) {

                boolean isEveningMeeting = (timeMatcher.group(3) != null);
                setStartTime(timeMatcher.group(1), isEveningMeeting);
                setEndTime(timeMatcher.group(2), isEveningMeeting);
            } else {

                throw new IllegalArgumentException(timeSpan + " is not a valid time span format");
            }

            mMeetingDays = meetingDays;
        }

        /**
         * Sets an optional location for the meeting.
         *
         * @param location String representing meeting's location
         */
        public Builder location (String location) {
            mLocation = location;
            return this;
        }

        /**
         * Sets an optional location for the meeting.
         *
         * @param instructor String representing meeting's instructor
         */
        public Builder instructor (String instructor) {
            mInstructor = instructor;
            return this;
        }

        private void setStartTime(String timeStart, boolean isEveningMeeting) {
            int start = Integer.parseInt(timeStart);
            if (isEveningMeeting)
                mStartTime = 1200 + start;
            else {
                if (start <= 500)
                    mStartTime = 1200 + start;
                else
                    mStartTime = start;
            }
        }

        private void setEndTime(String timeEnd, boolean isEveningMeeting) {
            int end = Integer.parseInt(timeEnd);
            if (isEveningMeeting) {
                mEndTime = 1200 + end;
            } else {
                if (end <= 500)
                    mEndTime = 1200 + end;
                else
                    mEndTime = end;
           }
        }

        /**
         * Returns an instance of meeting specified by this builder.
         */
        public Meeting build() {
            return new Meeting(this);
        }
    }

}
