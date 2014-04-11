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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import com.amgems.uwschedule.R;
import com.amgems.uwschedule.provider.ScheduleContract;

/**
 * A fragment used to show a list of courses making up a student schedule.
 */
public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorTreeAdapter mCourseCursorAdapter;
    private ViewGroup mProgressGroup;
    private ExpandableListView mCoursesListView;


    /** Courses cursor loader ID */
    private static final int COURSE_CURSOR_LOADER = 0;

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

        mCourseCursorAdapter = new CoursesTreeCursorAdapter(getActivity(), null, R.layout.schedule_list_card,
                                                            FROM_COLUMNS, TO_VIEWS, 0, null, null);
        mCoursesListView.setAdapter(mCourseCursorAdapter);
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Removes reference to old cursor adapter
        mCourseCursorAdapter.changeCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mCourseCursorAdapter.changeCursor(data);
            mCoursesListView.setVisibility(View.VISIBLE);
            mProgressGroup.setVisibility(View.GONE);
        }
    }
}
