package com.amgems.uwschedule.ui;



import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.amgems.uwschedule.R;
import com.etsy.android.grid.StaggeredGridView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ScheduleTableFragment extends Fragment {


    public ScheduleTableFragment() {
        // Required empty public constructor
    }

    public static ScheduleTableFragment newInstance() {
        return new ScheduleTableFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.schedule_table_fragment, container, false);
        StaggeredGridView scheduleTableView = (StaggeredGridView) rootView.findViewById(R.id
                .schedule_table);
        if (scheduleTableView == null) {
            Log.d(getClass().getSimpleName(), scheduleTableView + "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.schedule_table_cell, R.id.schedule_table_cell);
        scheduleTableView.setAdapter(adapter);
        adapter.add("hello");
        adapter.add("hello");
        adapter.add("hello");
        return rootView;
    }

}
