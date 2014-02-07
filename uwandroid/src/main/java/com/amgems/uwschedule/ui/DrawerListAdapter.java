package com.amgems.uwschedule.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
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
    Resources mResources;

    public static class Group {
        private final int mStringResId;
        private final int mIconResId;
        private List<String> mChildren;

        public Group(int stringResId, int iconResId) {
            mStringResId = stringResId;
            mIconResId = iconResId;
        }

        public void setChildren(List<String> children) {
            mChildren = children;
        }

        public int getStringResId(){
            return mStringResId;
        }

        public int getIconResId(){
            return mIconResId;
        }
    }

    public DrawerListAdapter(Context context, List<Group> groups) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = mContext.getResources();
        mGroups = groups;
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
        Group itemGroup = (Group) getGroup(groupPosition);

        if (groupPosition == 0) {
            convertView.setBackgroundColor(Color.parseColor("#FFA300"));
        }

        // Sets group icon drawable
        ImageView groupIconImageView = (ImageView) convertView.findViewById(R.id.group_icon);
        groupIconImageView.setImageDrawable(mResources.getDrawable(itemGroup.getIconResId()));

        // Sets group title text
        TextView groupTitleTextView = (TextView) convertView.findViewById(R.id.group_title);
        groupTitleTextView.setText(mResources.getString(itemGroup.getStringResId()));

        // Sets group expand/collapse icon if children exist
        ImageView indicatorImageView = (ImageView) convertView.findViewById(R.id.group_expand_indicator);
        //if (getChildrenCount(groupPosition) > 0) {
        if (groupPosition >= 2) {
            indicatorImageView.setVisibility(View.VISIBLE);
            int indicatorIconResId = isExpanded ? R.drawable.ic_nav_expand : R.drawable.ic_nav_collapse;
            indicatorImageView.setImageDrawable(mResources.getDrawable(indicatorIconResId));
        } else {
            indicatorImageView.setVisibility(View.GONE);
        }

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
