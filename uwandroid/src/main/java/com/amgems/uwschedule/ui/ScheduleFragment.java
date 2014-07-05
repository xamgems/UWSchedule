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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;
import com.amgems.uwschedule.R;
import com.amgems.uwschedule.provider.ScheduleContract;

/**
 * A fragment used to show a list of courses making up a student schedule.
 */
public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CursorTreeAdapter mCourseCursorAdapter;
    private ViewGroup mProgressGroup;
    private ExpandableListView mCoursesListView;

    /** Courses cursor loader ID */
    private static final int COURSE_CURSOR_LOADER = 0;
    private static final int MEETINGS_CURSOR_LOADER = 1;

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

        mCourseCursorAdapter = new CoursesCursorTreeAdapter(R.layout.schedule_list_card,
                                                            FROM_COLUMNS, TO_VIEWS, R.layout.drawer_group_item,
                                                            new String[] {ScheduleContract.Meetings.LOCATION},
                                                            new int[] {R.id.group_title});
        mCoursesListView.setAdapter(mCourseCursorAdapter);
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case COURSE_CURSOR_LOADER: {
                return new CursorLoader(getActivity(), ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
            }
            case MEETINGS_CURSOR_LOADER: {
                String groupSln = args.getString(CoursesCursorTreeAdapter.BUNDLE_SLN_KEY);
                return new CursorLoader(getActivity(), ScheduleContract.Meetings.CONTENT_URI, null,
                                        ScheduleContract.Meetings.SLN + " = ?", new String[] {groupSln}, null);
            }
            default:
                throw new IllegalArgumentException("Illegal loader id requested: " + id);
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
            case MEETINGS_CURSOR_LOADER: {
                Toast.makeText(getActivity(), "Cursor returned for " + data.getCount() + " items",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                throw new IllegalArgumentException("Unrecognized loader finished: " + loader.getId());
        }
    }

    /**
     * A {@link SimpleCursorTreeAdapter} implementation to managed the nested
     * courses, meeting UI structure.
     */
    public class CoursesCursorTreeAdapter extends SimpleCursorTreeAdapter {

        /** Defines Bundle keys for cursor tree group data*/
        private static final String BUNDLE_SLN_KEY = "bundleSln";
        private static final String BUNDLE_GROUP_ID = "bundleGroupId";

        public CoursesCursorTreeAdapter(int groupLayout, String[] groupFrom,
                                        int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(getActivity(), null, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            int groupSlnIndex = groupCursor.getColumnIndex(ScheduleContract.Meetings.SLN);
            String groupSln = groupCursor.getString(groupSlnIndex);
            Bundle childRequestBundle = new Bundle();
            childRequestBundle.putString(BUNDLE_SLN_KEY, groupSln);
            childRequestBundle.putInt(BUNDLE_GROUP_ID, groupCursor.getPosition());

            Loader<Object> loader = getLoaderManager().getLoader(MEETINGS_CURSOR_LOADER);

            // Loader has not yet been initialized or has a call to reset and
            // pending a state reset / being destroyed
            if (loader == null || loader.isReset()) {
                getLoaderManager().initLoader(MEETINGS_CURSOR_LOADER, childRequestBundle, ScheduleFragment.this);
            } else { // Loader is processing and needs to be restarted
                getLoaderManager().restartLoader(MEETINGS_CURSOR_LOADER, childRequestBundle, ScheduleFragment.this);
            }

            // Allows cursor loading for this group's child to be deferred to a non-ui thread
            return null;
        }

    }
}
