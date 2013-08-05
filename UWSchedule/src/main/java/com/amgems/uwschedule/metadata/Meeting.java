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

package com.amgems.uwschedule.metadata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Meeting implements Parcelable {

    private final Set<Day> mMeetingDays;
    private final int mStartTime;
    private final int mEndTime;
    private final String mLocation;
    private final String mInstructor;

    /**
     * The day of the week for which a class meets.
     */
    public static enum Day {
        M("Monday"),
        T("Tuesday"),
        W("Wednesday"),
        TH("Thursday"),
        F("Friday"),
        S("Saturday");

        // Upper-case
        private final String mDayText;

        private Day(String dayText) {
            mDayText = dayText;
        }

        @Override
        public String toString() {
            return mDayText;
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

    /** A {@link Parcelable.Creator} used to generate an instance of a {@code Course}*/
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
        mMeetingDays = new HashSet<>();
        List<String> dayNameList = new ArrayList<>();
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
        List<String> meetingDayList = new ArrayList<>();
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

    public String getLocation() {
        return mLocation;
    }

    public String getInstructor() {
        return mInstructor;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    @Override
    public String toString() {
        return  getStartTime() + " - " + getEndTime() + " | mLocation: " + getLocation();
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
