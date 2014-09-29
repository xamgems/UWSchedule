package com.amgems.uwschedule.ui;

import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amgems.uwschedule.R;
import com.amgems.uwschedule.model.Course;
import com.amgems.uwschedule.model.Meeting;
import com.amgems.uwschedule.model.Timetable;
import com.amgems.uwschedule.model.TimetableEvent;
import com.amgems.uwschedule.provider.ScheduleContract;
import com.etsy.android.grid.StaggeredGridView;
import com.etsy.android.grid.util.DynamicHeightTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

/**
 * A {@link Fragment} that displays a particular list of courses as a timetable.
 */
public class ScheduleTableFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<Cursor> {

    /** Courses cursor loader ID */
    private static final int COURSE_CURSOR_LOADER = 0;
    private static final int MEETING_CURSOR_LOADER = 1;

    private static final String BUNDLE_SLNS_KEY = "bundleSlns";

    private ArrayAdapter<TimetableEvent> mAdapter;
    private Map<String, Course> mCourseMap;
    private Map<Course, Integer> mColorMap;

    private static class ViewHolder {
        DynamicHeightTextView textView;
    }

    public ScheduleTableFragment() {
        // Required empty public constructor
    }

    public static ScheduleTableFragment newInstance() {
        return new ScheduleTableFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCourseMap = new TreeMap<String, Course>();
        getLoaderManager().initLoader(COURSE_CURSOR_LOADER, null, this);
        mColorMap = new HashMap<Course, Integer>();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.schedule_table_fragment, container, false);
        StaggeredGridView scheduleTableView = (StaggeredGridView) rootView.findViewById(R.id
                .schedule_table);
        mAdapter = new ArrayAdapter<TimetableEvent>(getActivity(),
                R.layout.schedule_table_cell, R.id.schedule_table_cell) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder vh;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.schedule_table_cell, container, false);
                    vh = new ViewHolder();
                    vh.textView = (DynamicHeightTextView) convertView.findViewById(R.id
                            .schedule_table_cell);

                    convertView.setTag(vh);
                }
                else {
                    vh = (ViewHolder) convertView.getTag();
                }

                TimetableEvent event = mAdapter.getItem(position);
                setupTextView(vh.textView, event);
                return convertView;
            }
        };
        scheduleTableView.setAdapter(mAdapter);

        return rootView;
    }

    private void setupTextView(TextView textView, TimetableEvent event) {
        textView.setText(event.toString());
        textView.setHeight(((event.getEndTime() - event.getStartTime())));
        textView.setBackgroundColor(mColorMap.get(event.getEvent().getEventGroup()));
        textView.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int beforeMargin = event.isFirstEvent() ? event.getBeforePadding() : 0;
        layoutParams.setMargins(0, beforeMargin, 0, event.getAfterPadding());
        textView.setPadding(0, 0, 0, 0);
        textView.setLayoutParams(layoutParams);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case COURSE_CURSOR_LOADER: {
                return new CursorLoader(getActivity(), ScheduleContract.Courses.CONTENT_URI, null, null, null, null);
            }
            case MEETING_CURSOR_LOADER: {
                ArrayList<String> slnList = args.getStringArrayList(BUNDLE_SLNS_KEY);
                String[] selection = slnList.toArray(new String[slnList.size()]);
                return new CursorLoader(getActivity(), ScheduleContract.Meetings.CONTENT_URI, null,
                        buildSlnWhereClause(slnList.size()), selection, null);
            }
            default:
                throw new IllegalArgumentException("Illegal loader id requested: " + id);
        }


    }

    private String buildSlnWhereClause(int times) {
        String result = ScheduleContract.Meetings.SLN + " IN (?";
        for (int i = 1; i < times; i++) {
            result += ", ?";
        }
        return result + ")";
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCourseMap = new TreeMap<String, Course>();
        mColorMap = new HashMap<Course, Integer>();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case COURSE_CURSOR_LOADER: {
                List<String> slnList = new ArrayList<String>();
                TypedArray colors = getResources().obtainTypedArray(R.array.colors);
                int colorIndex = 0;
                if (data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Course course = Course.fromCursor(data);
                        mCourseMap.put(course.getSln(), course);
                        mColorMap.put(course, colors.getColor(colorIndex++, 0));
                        slnList.add(course.getSln());
                    }
                    Bundle slnBundle = new Bundle();
                    slnBundle.putStringArrayList(BUNDLE_SLNS_KEY, (ArrayList<String>) slnList);
                    if (getLoaderManager().getLoader(MEETING_CURSOR_LOADER) == null) {
                        getLoaderManager().initLoader(MEETING_CURSOR_LOADER, slnBundle, this);
                    } else {
                        getLoaderManager().restartLoader(MEETING_CURSOR_LOADER, slnBundle, this);
                    }
                }
                break;
            }
            case MEETING_CURSOR_LOADER: {
                if (data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Meeting meeting = Meeting.fromCursor(data);
                        String sln = data.getString(data.getColumnIndex(ScheduleContract.Meetings.SLN));
                        mCourseMap.get(sln).getMeetings().add(meeting);
                    }

                    Timetable timetable = new Timetable(new ArrayList<Course>(mCourseMap.values()));
                    mAdapter.clear();
                    Queue<TimetableEvent> courseMeetingQueue = timetable.toQueue();

                    mAdapter.addAll(courseMeetingQueue);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unrecognized loader finished: " + loader.getId());
        }
    }

}
