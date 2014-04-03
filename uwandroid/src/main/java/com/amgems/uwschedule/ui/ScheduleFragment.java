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
import android.support.v4.app.ListFragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.amgems.uwschedule.R;
import com.amgems.uwschedule.provider.ScheduleContract;

/**
 * A fragment used to show a list of courses making up a student schedule.
 */
public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CursorAdapter mCourseCursorAdapter;
    private ViewGroup mProgressGroup;

    /** Courses cursor loader ID */
    private static final int COURSE_CURSOR_LOADER = 0;

    /** Defines all incoming Course columns */
    private static final String[] FROM_COLUMNS = new String[] { ScheduleContract.Courses.COURSE_NUMBER };

    /** Defines all mappings from Course to View IDs */
    private static final int[] TO_VIEWS = new int[] { R.id.course_title };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_list_fragment, container, false);
        mProgressGroup = (ViewGroup) rootView.findViewById(R.id.schedule_progress_group);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(COURSE_CURSOR_LOADER, null, this);
        mCourseCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.schedule_list_card, null, FROM_COLUMNS,
                                                       TO_VIEWS, SimpleCursorAdapter.NO_SELECTION);
        setListAdapter(mCourseCursorAdapter);
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
            getListView().setVisibility(View.VISIBLE);
            mProgressGroup.setVisibility(View.GONE);
        }
    }
}
