/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by`
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

package com.amgems.uwschedule.provider;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * A Contract class used for interacting with a {@link ScheduleProvider},
 * defining the schema for its underlying {@link ScheduleDatabaseHelper}.
 */
public final class ScheduleContract {

    public static final String CONTENT_AUTHORITY = "com.amgems.uwschedule.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Suppress default constructor for noninstantiabiltiy */
    private ScheduleContract() { }

    interface AccountsColumns {
        public static final String STUDENT_NAME = "student_name";
        public static final String STUDENT_USERNAME = "student_username";
        public static final String USER_LAST_UPDATE = "user_last_update";
    }

    private static final String PATH_ACCOUNTS = "accounts";

    public static abstract class Accounts implements BaseColumns, AccountsColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.uwschedule.account";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.uwschedule.account";

        public static Uri buildAccountUri(long accountId) {
            String idString = String.valueOf(accountId);
            return CONTENT_URI.buildUpon().appendPath(idString).build();
        }
    }

    interface CoursesColumns {
        public static final String SLN = "course_sln";
        public static final String STUDENT_USERNAME = "student_username";
        public static final String DEPARTMENT_CODE = "course_dept_code";
        public static final String COURSE_NUMBER = "course_number";
        public static final String CREDITS = "course_credits";
        public static final String SECTION_ID = "course_section";
        public static final String TYPE = "course_type";
        public static final String TITLE = "course_title";
    }

    private static final String PATH_COURSES = "courses";

    public static abstract class Courses implements BaseColumns, CoursesColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.uwschedule.course";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.uwschedule.course";

        public static Uri buildCoursesUri(long courseId) {
            String idString = String.valueOf(courseId);
            return CONTENT_URI.buildUpon().appendPath(idString).build();
        }
    }

}
