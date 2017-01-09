package app.ahreum.com.pacecounters.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;

/**
 * management Tabbed layout
 * doesn't access any model -> presenter not needed *
 **/

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
        registerAlramManagerReceiver();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorTabGrayText), ContextCompat.getColor(this, R.color.colorTabWhiteText));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorTabRecord));
    }
    private BroadcastReceiver mAlramManagerReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(PaceCounterConst.SAVE_COUNT_DATE)) {
                PaceCounterUtil.insertListData(MainActivity.this);  //알람매니저로 부터 data저장요청을 받아 수행한다.
            }
        }
    };
    private void registerAlramManagerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PaceCounterConst.SAVE_COUNT_DATE);
        registerReceiver(mAlramManagerReciever, filter);
    }

    private void createFragmentView(){
        if(mFragmentMain == null){
            mFragmentMain = FragmentForMonitorScreen.newInstance(PaceCounterConst.MONITOR_FRAGMENT_INDEX);
        }
        if(mFragmentRecord == null){
            mFragmentRecord = FragmentForRecordList.newInstance(PaceCounterConst.LIST_FRAGMENT_INDEX);
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
        unregisterReceiver(mAlramManagerReciever);
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
                case PaceCounterConst.MONITOR_FRAGMENT_INDEX:
                    return mFragmentMain;
                case PaceCounterConst.LIST_FRAGMENT_INDEX:
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
                case PaceCounterConst.MONITOR_FRAGMENT_INDEX:
                    return getString(R.string.main_tab_title);
                case PaceCounterConst.LIST_FRAGMENT_INDEX:
                    return getString(R.string.record_tab_title);
            }
            return null;
        }
    }
}
