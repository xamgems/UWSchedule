package com.amgems.uwschedule.ui;

import android.content.res.Configuration;
import android.os.Bundle;
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
import com.amgems.uwschedule.provider.ScheduleDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zac on 9/2/13.
 */
public class HomeActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayoutRoot;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView mDrawerListView;
    private ViewPager mCoursesViewPager;
    private TextView mDrawerEmailTextView;
    private ScheduleDatabaseHelper mDatabase;

    private CookieStore mCookieStore;
    private String mUsername;

    public static final String EXTRAS_HOME_USERNAME = "mUsername";
    private static final String USER_EMAIL_POSTFIX = "@u.washington.edu";
    private static final String TAG = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // TODO Remove strict mode when database debugging is complete
        // Death penalty for all strict mode violations
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyDeath()
//                .build());

        // Initialize inbound data
        mUsername = getIntent().getStringExtra(EXTRAS_HOME_USERNAME);
        mCookieStore = CookieStore.getInstance(getApplicationContext());

        // An example of the ScheduleProvider API
//         Cursor accountsCursor = getContentResolver().query(ScheduleContract.Accounts.CONTENT_URI, null, null, null, null);
//        try {
//            if (accountsCursor.moveToFirst()) {
//                String username = accountsCursor.getString(accountsCursor.getColumnIndex(ScheduleContract.Accounts.STUDENT_USERNAME));
//                String studentName = accountsCursor.getString(accountsCursor.getColumnIndex(ScheduleContract.Accounts.STUDENT_NAME));
//                long lastUpdateTime = accountsCursor.getLong(accountsCursor.getColumnIndex(ScheduleContract.Accounts.USER_LAST_UPDATE));
//                String accountText = new Account(username, studentName, lastUpdateTime).toString();
//                Toast.makeText(this, accountText, Toast.LENGTH_SHORT).show();
//
//                Cursor coursesCursor = getContentResolver().query(ScheduleContract.Courses.CONTENT_URI, null, ScheduleContract.Courses.STUDENT_USERNAME + " = ?",
//                                                                  new String[] {mUsername}, null);
//                while (coursesCursor.moveToNext()) {
//                    String sln = coursesCursor.getString(coursesCursor.getColumnIndex(ScheduleContract.Courses.SLN));
//                    String department = coursesCursor.getString(coursesCursor.getColumnIndex(ScheduleContract.Courses.DEPARTMENT_CODE));
//                    String number = coursesCursor.getString(coursesCursor.getColumnIndex(ScheduleContract.Courses.COURSE_NUMBER));
//
//                    Toast.makeText(this, "sln: " + sln + ", dept: " + department + ", number: " + number, Toast.LENGTH_SHORT).show();
//                }
//
//            } else {
//                Account testAccount = new Account(mUsername, "Zachary Iqbal");
//                getContentResolver().insert(ScheduleContract.Accounts.CONTENT_URI, testAccount.toContentValues());
//                Toast.makeText(this, "Added new account: " + testAccount, Toast.LENGTH_SHORT).show();
//
//                Course testCourse = Course.newInstance("123123", "CSE", 332, "A", 5, "DATA ABSTRACTIONS", Course.Type.LC, new LinkedList<Meeting>());
//                getContentResolver().insert(ScheduleContract.Courses.CONTENT_URI, testCourse.toContentValues(mUsername));
//                Toast.makeText(this, "Added new course: " + testCourse, Toast.LENGTH_SHORT).show();
//            }
//        } finally {
//            accountsCursor.close();
//        }

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
