package com.amgems.uwschedule.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.amgems.uwschedule.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zac on 9/4/13.
 */
public class DrawerListAdapter extends BaseExpandableListAdapter {

    List<Group> mGroups;

    Context mContext;
    LayoutInflater mInflater;

    public DrawerListAdapter(Context context, List<Group> groups) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroups = groups;
    }

    public static class Group {
        private final int mStringResId;
        private List<String> mChildren;

        public Group(int stringResId) {
            mStringResId = stringResId;
        }

        public void setChildren(List<String> children) {
            mChildren = children;
        }

        public int getStringResId(){
            return mStringResId;
        }
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        List<String> children = ((Group) getGroup(groupPosition)).mChildren;
        return children == null ? 0 : children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((Group) getGroup(groupPosition)).mChildren.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_group_item, parent, false);
        }
        TextView groupTitleTextView = (TextView) convertView.findViewById(R.id.group_title);
        // Gets string pointed to by resource identifier
        String groupTitleText = mContext.getString(((Group) getGroup(groupPosition)).getStringResId());
        groupTitleTextView.setText(groupTitleText);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView,
                                                                                        ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
