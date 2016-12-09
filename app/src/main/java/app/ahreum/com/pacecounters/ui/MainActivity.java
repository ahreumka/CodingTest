package app.ahreum.com.pacecounters.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

import app.ahreum.com.pacecounters.R;

public class MainActivity extends AppCompatActivity {
    private FragmentForMonitorScreen mFragmentMain;
    private FragmentForRecordList mFragmentRecord;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFragmentView();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorTabGrayText), ContextCompat.getColor(this, R.color.colorTabWhiteText));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorTabRecord));
        //makeAlarmManager();
    }
    private void createFragmentView(){
        if(mFragmentMain == null){
            mFragmentMain = new FragmentForMonitorScreen();
        }
        if(mFragmentRecord == null){
            mFragmentRecord = new FragmentForRecordList();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragmentMain = null ;
        mFragmentRecord = null;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return mFragmentMain;
                case 1:
                    return mFragmentRecord;
                default:
                    return mFragmentMain;
            }
        }
        public int getItemPosition(Object object) {
            if (object.equals(mFragmentMain)) {
                return 0;
            }else if (object.equals(mFragmentRecord)) {
                return 1;
            }
            return POSITION_NONE;
        }
        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.main_tab_title);
                case 1:
                    return getString(R.string.record_tab_title);
            }
            return null;
        }
    }
//    private void makeAlarmManager(){
//
//    AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//    Intent intent = new Intent(MainActivity.this, BroadcastD.class);
//
//    PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//
//    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 12, 0);
//        mngr.setExact(AlarmManager.RTC_WAKEUP, actionTime + leftTime , mIntentSender);
//
//    //알람 예약
//    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//    }
}
