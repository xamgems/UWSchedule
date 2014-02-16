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

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.amgems.uwschedule.R;
import com.amgems.uwschedule.api.local.WebService;
import com.amgems.uwschedule.api.uw.CookieStore;
import com.amgems.uwschedule.model.Account;
import com.amgems.uwschedule.model.Course;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The Activity that represents a home screen for the user.
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
    private static final String TAG = "HOME";

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

        WebService service = new WebService();
        service.getAccount("shermpay", new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                Log.d(TAG, response.getStatus()+"");
                Log.d(TAG, account.toString());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, retrofitError.getMessage());
            }
        });

        service.getUserCourses("shermpay", "13wi", new Callback<List<Course>>() {
            @Override
            public void success(List<Course> courses, Response response) {
                Log.d(TAG, response.getStatus()+"");
                Log.d(TAG, courses.toString());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, retrofitError.getMessage());
            }
        });


    }

    /**
     * Helper method for enabling death penalty on strict mode.
     */
    private void enableDeathThreadPolicy() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build());
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

    /**
     * Used to allow swiping fragments on home screen.
     */
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
