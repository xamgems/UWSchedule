package com.amgems.uwschedule.ui;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

/**
 * A {@link SimpleCursorTreeAdapter} implementation to managed the nested
 * courses, meeting UI structure.
 */
public class CoursesTreeCursorAdapter extends SimpleCursorTreeAdapter {

    public CoursesTreeCursorAdapter (Context context, Cursor cursor, int groupLayout, String[] groupFrom,
                                     int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Log.d(getClass().getSimpleName(), groupCursor.getPosition() + "");
        return null;
    }

}
