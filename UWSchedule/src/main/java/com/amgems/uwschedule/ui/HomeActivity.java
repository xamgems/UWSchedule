package com.amgems.uwschedule.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amgems.uwschedule.R;
import com.amgems.uwschedule.api.uw.CookieStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zac on 9/2/13.
 */
public class HomeActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayoutRoot;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView mDrawerListView;
    private ViewPager mCoursesViewPager;
    private TextView mDrawerEmailTextView;

    private CookieStore mCookieStore;
    private String mUsername;

    public static final String EXTRAS_HOME_USERNAME = "mUsername";
    private static final String USER_EMAIL_POSTFIX = "@u.washington.edu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Initialize inbound data
        mUsername = getIntent().getStringExtra(EXTRAS_HOME_USERNAME);
        mCookieStore = CookieStore.getInstance(getApplicationContext());

        // Initialize view references
        mDrawerLayoutRoot = (DrawerLayout) findViewById(R.id.home_drawer_root);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayoutRoot, R.drawable.ic_drawer,
                                                  R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayoutRoot.setDrawerListener(mDrawerToggle);
        mCoursesViewPager = (ViewPager) findViewById(R.id.courses_pager);
        mDrawerListView = (ExpandableListView) findViewById(R.id.home_drawer_listview);
        mDrawerEmailTextView = (TextView) findViewById(R.id.home_drawer_email);

        // Set up navigation drawer items
        List<DrawerListAdapter.Group> drawerGroups = new ArrayList<DrawerListAdapter.Group>();
        drawerGroups.add(new DrawerListAdapter.Group(R.string.drawer_group_home, R.drawable.ic_nav_home));
        drawerGroups.add(new DrawerListAdapter.Group(R.string.drawer_group_friends, R.drawable.ic_nav_friends));
        drawerGroups.add(new DrawerListAdapter.Group(R.string.drawer_group_favorites, R.drawable.ic_nav_favorites));
        mDrawerListView.setAdapter(new DrawerListAdapter(this, drawerGroups));
        mCoursesViewPager.setAdapter(new CoursesFragmentPagerAdapter(getSupportFragmentManager()));

        mDrawerEmailTextView.setText(mUsername + USER_EMAIL_POSTFIX);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private static class CoursesFragmentPagerAdapter extends FragmentPagerAdapter {

        private static int TAB_COUNT = 2;

        public CoursesFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            return ScheduleFragment.newInstance();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
