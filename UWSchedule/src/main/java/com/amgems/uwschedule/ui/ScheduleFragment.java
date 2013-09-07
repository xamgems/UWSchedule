package com.amgems.uwschedule.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.amgems.uwschedule.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zac on 9/6/13.
 */
public class ScheduleFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> coursesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.schedule_list_card,
                                                  R.id.course_title, Arrays.asList("CSE 315", "CSE 311", "MATH 308"));
        setListAdapter(coursesAdapter);
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }
}
