package app.ahreum.com.pacecounters.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.model.APIExamMapGeocode;
import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;
import app.ahreum.com.pacecounters.service.StepCountService;
import app.ahreum.com.pacecounters.ui.presenter.MonitorContractor;
import app.ahreum.com.pacecounters.ui.presenter.MonitorPresenter;


/**
 * Created by ahreum on 2016-12-06.
 */

public class FragmentForMonitorScreen extends Fragment implements View.OnClickListener, LocationListener, MonitorContractor.View{

    private MonitorPresenter monitorPresenter ;
    private View mContentView;
    private Button mBtnTrack;
    private TextView mTvWalk, mTvDistance, mTvLocation;
    private boolean isTracking;
    private APIExamMapGeocode mMapGeocode;
    private String locationMsg;
    private LocationManager locationManager;

    //When the memory is full, the fragment is destroyed and regenerated, and the parameter is not received by the constructor. However, if you use Bundle, the bundle will come back.
    public static FragmentForMonitorScreen newInstance(int index) {
        FragmentForMonitorScreen fragment = new FragmentForMonitorScreen();
        Bundle args =  new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        monitorPresenter = new MonitorPresenter();
        monitorPresenter.attachView(this);
        updateLayout(inflater, container);
        try {
            mMapGeocode = new APIExamMapGeocode(mTvLocation);
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }catch (SecurityException e){
            e.printStackTrace();
        }
        if(savedInstanceState != null){
            mTvWalk.setText(savedInstanceState.getString(PaceCounterConst.KEY_COUNT));
            mTvDistance.setText(savedInstanceState.getString(PaceCounterConst.KEY_DISTANCE));
            isTracking = savedInstanceState.getBoolean(PaceCounterConst.KEY_TRACKING);
        }
        registerCounterServiceReceiver();
        mMapGeocode.execute(locationMsg);
        return mContentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PaceCounterConst.KEY_TRACKING,isTracking);
        outState.putString(PaceCounterConst.KEY_COUNT,mTvWalk.getText().toString());
        outState.putString(PaceCounterConst.KEY_DISTANCE,mTvDistance.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        isTracking = PaceCounterUtil.getTrackState(getContext());
        changeButtonState();
        if(PaceCounterUtil.isServiceRunning(getContext())){
           PaceCounterUtil.getVRServiceBinder().endForeGround();
        }
        if(!PaceCounterUtil.isServiceRunning(getContext())){
            PaceCounterUtil.bindToService(getContext());
        }

    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.track_button){
            isTracking = !isTracking;
            PaceCounterUtil.setTrackState(getContext(), isTracking);
            changeButtonState();
            if(isTracking){
                PaceCounterUtil.makeAlarmManager(getContext());//측정을 시작했을때 매일 데이터를 저장하도록 알람매니저 등록
            }else{
                getContext().stopService(new Intent(getActivity().getApplication(), StepCountService.class));
                PaceCounterUtil.removeAlarmManager(getContext());//측정을 멈추면 알람매니저도 제거해줘야한다
            }
        }
    }

    private void updateLayout(LayoutInflater inflater, ViewGroup container) {
        mContentView = inflater.inflate(R.layout.fragment_monitor, container, false);
        mTvWalk = (TextView) mContentView.findViewById(R.id.walk_txtview);
        mTvDistance = (TextView) mContentView.findViewById(R.id.distance_txtview);
        mTvLocation = (TextView) mContentView.findViewById(R.id.location_txtview);
        mBtnTrack = (Button) mContentView.findViewById(R.id.track_button);
        mBtnTrack.setOnClickListener(this);
    }
    private void changeButtonState(){
        if(isTracking){//isTracking  state
            mBtnTrack.setText(getResources().getString(R.string.button_stop));
            mBtnTrack.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorStopButton));
        }else{//idle state
            mTvWalk.setText(String.valueOf(PaceCounterUtil.steps));
            mTvDistance.setText(PaceCounterUtil.getDistance());
            mBtnTrack.setText(getResources().getString(R.string.button_start));
            mBtnTrack.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorStartButton));
        }
    }
    private void registerCounterServiceReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(PaceCounterConst.ACTION_COUNT_SERVICE );
        getContext().registerReceiver(countServiceReceiver, filter);

    }
    private final BroadcastReceiver countServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null){
                return;
            }
            if(intent.getAction().equals(PaceCounterConst.ACTION_COUNT_SERVICE)){
                mTvWalk.setText(String.valueOf(PaceCounterUtil.steps));
                mTvDistance.setText(PaceCounterUtil.getDistance());
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(PaceCounterUtil.isServiceRunning(getContext())){
            PaceCounterUtil.getVRServiceBinder().startForeGround();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView =null;
        mBtnTrack = null;
        mTvWalk = null;
        mTvDistance = null;
        mTvLocation = null;
        monitorPresenter.detachView();
        try {
            getContext().unregisterReceiver(countServiceReceiver);
        }catch (Exception e) {
            log("onDestroy : remote ctr br is unresistered already");
        }
    }

    //LocationListener start
    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            return;
        }else {
            locationMsg = location.getLatitude()
                    + "," + location.getLongitude();
            mMapGeocode.execute(locationMsg);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
    //LocationListener end

    private void log(String msg){
        String TAG =PaceCounterConst.TAG + " : FragmentForMonitorScreen";
        if(PaceCounterConst.DEBUG) Log.i(TAG, msg);
    }
}
