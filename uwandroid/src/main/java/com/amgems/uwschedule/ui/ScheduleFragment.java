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

package com.amgems.uwschedule.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import com.amgems.uwschedule.R;
import com.amgems.uwschedule.model.Meeting;
import com.amgems.uwschedule.provider.ScheduleContract;

/**
 * A fragment used to show a list of courses making up a student schedule.
 */
public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CoursesCursorTreeAdapter mCourseCursorAdapter;
    private ViewGroup mProgressGroup;
    private ExpandableListView mCoursesListView;

    /** Courses cursor loader ID */
    private static final int COURSE_CURSOR_LOADER = -1;

    /** Defines all incoming Course columns */
    private static final String[] FROM_COLUMNS = new String[] {
            ScheduleContract.Courses.DEPARTMENT_CODE, ScheduleContract.Courses.COURSE_NUMBER,
            ScheduleContract.Courses.SECTION_ID, ScheduleContract.Courses.TYPE,
            ScheduleContract.Courses.CREDITS,
            ScheduleContract.Courses.TITLE };

    /** Defines all mappings from Course to View IDs */
    private static final int[] TO_VIEWS = new int[] {  R.id.department_code, R.id.course_number,
           R.id.section_id, R.id.course_type, R.id.course_credits, R.id.course_title };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_list_fragment, container, false);
        mProgressGroup = (ViewGroup) rootView.findViewById(R.id.schedule_progress_group);
        mCoursesListView = (ExpandableListView) rootView.findViewById(R.id.courses_expandable_list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(COURSE_CURSOR_LOADER, null, this);

        mCourseCursorAdapter = new CoursesCursorTreeAdapter(
                getActivity(), R.layout.schedule_list_card, FROM_COLUMNS, TO_VIEWS, R.layout.schedule_list_meeting,
                new String[] {
                        ScheduleContract.Meetings.START_TIME,
                        ScheduleContract.Meetings.END_TIME
                },
                new int[] {
                        R.id.start_time,
                        R.id.end_time
                });
        mCoursesListView.setAdapter(mCourseCursorAdapter);
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Because each request for a set of the meetings data must know what position it's group (corresponding course)
        // is at, extra data must be stored within the Loader. Because extras can't be attached to a specific loader, a
        // a group id that can specify a unique group must be used as the loader id. This can then ae translated to a
        // group position using CourseCursorTreeAdapter#getGroupPosition(int loaderId).
        switch (id) {
            case COURSE_CURSOR_LOADER: {
                return new CursorLoader(getActivity(), ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
            }
            default: { // A request for meeting data was made.
                String groupSln = args.getString(CoursesCursorTreeAdapter.BUNDLE_SLN_KEY);
                return new CursorLoader(getActivity(), ScheduleContract.Meetings.CONTENT_URI, null,
                        ScheduleContract.Meetings.SLN + " = ?", new String[]{groupSln}, null);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Removes reference to old cursor adapter
        Log.d(ScheduleFragment.class.getSimpleName(), "Loader resetting");
        mCourseCursorAdapter.changeCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case COURSE_CURSOR_LOADER: {
                if (data.getCount() > 0) {
                    mCourseCursorAdapter.setGroupCursor(data);
                    mCoursesListView.setVisibility(View.VISIBLE);
                    mProgressGroup.setVisibility(View.GONE);
                }
                break;
            }
            default: {
                mCourseCursorAdapter.setChildrenCursor(mCourseCursorAdapter.getGroupPosition(loader.getId()), data);
                break;
            }
        }
    }

    /**
     * A {@link SimpleCursorTreeAdapter} implementation to managed the nested
     * courses, meeting UI structure.
     */
    public class CoursesCursorTreeAdapter extends SimpleCursorTreeAdapter {

        /** Defines Bundle keys for cursor tree group data */
        private static final String BUNDLE_SLN_KEY = "bundleSln";

        /** Mappings from the Loader id of a child request to the position of that child's group **/
        private final SparseIntArray mGroupIdPosMap;

        public CoursesCursorTreeAdapter(Context context, int groupLayout, String[] groupFrom,
                                        int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(context, null, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
            mGroupIdPosMap = new SparseIntArray();
        }

        @Override
        protected Cursor getChildrenCursor(Cursor group) {
            int groupId = group.getInt(group.getColumnIndex(ScheduleContract.Meetings._ID));
            mGroupIdPosMap.put(groupId, group.getPosition());

            Bundle childRequestBundle = new Bundle();
            String groupSln = group.getString(group.getColumnIndex(ScheduleContract.Meetings.SLN));
            childRequestBundle.putString(BUNDLE_SLN_KEY, groupSln);

            Loader<Object> loader = getLoaderManager().getLoader(groupId);
            // Loader has not yet been initialized or has a call to reset and
            // pending a state reset / being destroyed
            if (loader == null || loader.isReset()) {
                getLoaderManager().initLoader(groupId, childRequestBundle, ScheduleFragment.this);
            } else { // Loader is processing and needs to be restarted
                getLoaderManager().restartLoader(groupId, childRequestBundle, ScheduleFragment.this);
            }

            // Allows cursor loading for this group's child to be deferred to a non-ui thread
            return null;
        }

        public int getGroupPosition(int loaderId) {
            return mGroupIdPosMap.get(loaderId);
        }

        @Override
        protected void bindChildView(@NonNull View view, Context context, @NonNull Cursor cursor, boolean isLastChild) {
            for (Meeting.Day day : Meeting.Day.values()) {
                int currentDayIndex = cursor.getColumnIndex(day.getColumnName());
                boolean shouldShowCurrentDay = cursor.getInt(currentDayIndex) == ScheduleContract.Meetings.HAS_MEETING;
                int dayVisibiity = shouldShowCurrentDay ? View.VISIBLE : View.GONE;
                view.findViewById(day.getIconResId()).setVisibility(dayVisibiity);
            }
            super.bindChildView(view, context, cursor, isLastChild);
        }
    }
}
