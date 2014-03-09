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
import com.amgems.uwschedule.provider.ScheduleContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.*;

/**
 * <p> Class containing a detailed overview of a particular course. </p>
 *
 * <p> This class is specified to be consistent with the various representations
 * of class schedules available from either the UW Registration or Time Schedule.</p>
  */
public class Course implements Parcelable {

    // TODO: Expose the fields
    // TODO: Customize GSON for Meetings

    @Expose
    @SerializedName("sln")
    private final String mSln;

    @Expose
    @SerializedName("department_code")
    private final String mDepartmentCode;

    @Expose
    @SerializedName("course_number")
    private final int mCourseNumber;

    @Expose
    @SerializedName("credits")
    private final int mCredits;

    @Expose
    @SerializedName("section_id")
    private final String mSectionId;

    @Expose
    @SerializedName("title")
    private final String mTitle;

    @Expose
    @SerializedName("type")
    private final Type mType;

    @Expose
    @SerializedName("meetings")
    private final List<Meeting> mMeetings;

    /** Section type of a given course */
    public static enum Type {
        CK("Clerkship"),
        CL("Clinic"),
        CO("Conference"),
        IS("Independent Study"),
        LB("Lab"),
        LC("Lecture"),
        PR("Practicum"),
        QZ("Quiz"),
        SM("Seminar"),
        ST("Studio");

        private final String mTypeText;

        private Type(String typeText) {
            mTypeText = typeText;
        }

        @Override
        public String toString() {
            return mTypeText;
        }
    }

    /** A {@link android.os.Parcelable.Creator} used to generate an instance of a {@code Course}*/
    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    Course(String sln, String departmentCode, int courseNumber, String sectionId, int credits,
           String title, Type type, List<Meeting> meetings) {
        mSln = sln;
        mDepartmentCode = departmentCode;
        mCourseNumber = courseNumber;
        mCredits = credits;
        mSectionId = sectionId;
        mTitle = title;
        mType = type;
        mMeetings = meetings;
    }

    private Course (Parcel in) {
        mSln = in.readString();
        mDepartmentCode = in.readString();
        mCourseNumber = in.readInt();
        mSectionId = in.readString();
        mCredits = in.readInt();
        mTitle = in.readString();
        mType = Type.valueOf(in.readString());
        mMeetings = new ArrayList<Meeting>();
        in.readTypedList(mMeetings, Meeting.CREATOR);
    }

    /**
     * Returns a new instance of the {@code Course} object.
     *
     * <p>This method also keeps track of each instance created in a local store,.
     * identified uniquely by the course's SLN. Note that if a course is added with
     * an SLN corresponding to a course already in the store, the previous reference
     * will be <i>removed</i>, and updated with the new instance.<p/>
     *
     * @param sln SLN of the course. Note that this value is used as a
     *            unique identifier for each course
     * @param departmentCode Departmental code for the course
     * @param courseNumber Three digit course number
     * @param sectionId Letters corresponding to the course's section
     * @param credits Credit value for the course
     * @param title Full text title of the course
     * @param type Section type of the course
     * @param meetings List of allocated meetings
     */
    public static Course newInstance(String sln, String departmentCode, int courseNumber, String sectionId, int credits,
                                     String title, Type type, List<Meeting> meetings) {
        Course instance = new Course(sln, departmentCode, courseNumber, sectionId, credits, title.toUpperCase(), type, meetings);
        return instance;
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
        out.writeString(mSln);
        out.writeString(mDepartmentCode);
        out.writeInt(mCourseNumber);
        out.writeString(mSectionId);
        out.writeInt(mCredits);
        out.writeString(mTitle);
        out.writeString(mType.name());
        out.writeTypedList(mMeetings);
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

    /**
     * Returns a representation of this {@link Course} tied to a given user
     * in the form of a {@link ContentValues}.
     *
     * This representation contains all logical information related to
     * a course. However, it excludes information relating to the meetings
     * tied to this course.
     *
     * @param username The username to associate this course to.
     */
    public ContentValues toContentValues(String username) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(ScheduleContract.Courses.SLN, getSln());
        contentValues.put(ScheduleContract.Courses.STUDENT_USERNAME, username);
        contentValues.put(ScheduleContract.Courses.DEPARTMENT_CODE, getDepartmentCode());
        contentValues.put(ScheduleContract.Courses.COURSE_NUMBER, getCourseNumber());
        contentValues.put(ScheduleContract.Courses.CREDITS, getCredits());
        contentValues.put(ScheduleContract.Courses.SECTION_ID, getSectionId());
        contentValues.put(ScheduleContract.Courses.TYPE, getType().toString());
        contentValues.put(ScheduleContract.Courses.TITLE, getTitle());

        return contentValues;
    }

    public String getSln() {
        return mSln;
    }

    public String getDepartmentCode() {
        return mDepartmentCode;
    }

    public int getCourseNumber() {
        return mCourseNumber;
    }

    public int getCredits() {
        return mCredits;
    }

    public String getSectionId() {
        return mSectionId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Type getType() {
        return mType;
    }

    public List<Meeting> getMeetings() {
        return mMeetings;
    }

    /**
     * Returns a brief description of this {@link Course}. The exact details
     * of this representation are unspecified and subject to change,
     * but the following can be regarded as typical:
     *
     * "{
     *     username : zac23
     *     fullname : Zachary Iqbal
     *     lastupdate : 2027
     * }"
     */
    @Override
    public String toString() {

        return "{\n" + " sln : " + getSln() + ",\n" +
                " deptcode : " + getDepartmentCode() + ",\n" +
                " coursenum : " + getCourseNumber() + "\n" +
                " sectid : " + getSectionId() + "\n" +
                " type: " + getType() + "\n" +
                " title : " + getTitle() + "\n" +
                " credits : " + getCredits() + "\n" +
                "}";

    }

}
