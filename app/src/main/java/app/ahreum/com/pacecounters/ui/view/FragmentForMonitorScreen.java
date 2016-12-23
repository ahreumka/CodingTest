package app.ahreum.com.pacecounters.ui.view;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.model.APIExamMapGeocode;
import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;
import app.ahreum.com.pacecounters.ui.presenter.MonitorContractor;
import app.ahreum.com.pacecounters.ui.presenter.MonitorPresenter;


/**
 * Created by ahreum on 2016-12-06.
 */

public class FragmentForMonitorScreen extends Fragment implements View.OnClickListener, LocationListener, MonitorContractor.View{
    private MonitorPresenter monitorPresenter ;
    private View mContentView;
    private Button mBtnTrack;
    private TextView mTvWalk, mTvDistance;
    public TextView mTvLocation;
    private SensorManager mSensorManager;
    private boolean isTracking;
    private APIExamMapGeocode mMapGeocode;

    //get location
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        monitorPresenter = new MonitorPresenter();
        monitorPresenter.attachView(this);
        try {
            mMapGeocode = new APIExamMapGeocode(this);
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

        }catch (SecurityException e){
            e.printStackTrace();
        }
        updateLayout(inflater, container);
        if(savedInstanceState != null){
            mTvWalk.setText(savedInstanceState.getString(PaceCounterConst.KEY_COUNT));
            mTvDistance.setText(savedInstanceState.getString(PaceCounterConst.KEY_DISTANCE));
            isTracking = savedInstanceState.getBoolean(PaceCounterConst.KEY_TRACKING);
        }
        getDeviceSensor();
        changeButtonState();
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
        mMapGeocode.execute();
        changeSensorState();
        changeButtonState();

    }
    private boolean getDeviceSensor(){
        mSensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            mSensorManager.registerListener(mSensorListener, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            PaceCounterUtil.preferenceCount = PaceCounterUtil.getPreStepCount(getContext());
            return true;
        }else{
            PaceCounterUtil.showToast(getContext(), getResources().getString(R.string.toast_msg_fail_use_sensor));
            return false;
        }
    }
    private void unregisterListeners() {
        PaceCounterUtil.setPreStepCount(getContext(),PaceCounterUtil.sensorCount);
        clearData();
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mSensorListener);
    }
    private void clearData(){
        PaceCounterUtil.steps = 0;
        if(mTvWalk==null || mTvDistance==null) return;
        mTvWalk.setText(String.valueOf(PaceCounterUtil.steps));
        mTvDistance.setText(PaceCounterUtil.getDistance());
    }
    private void updateLayout(LayoutInflater inflater, ViewGroup container) {
        mContentView = inflater.inflate(R.layout.fragment_monitor, container, false);
        mTvWalk = (TextView) mContentView.findViewById(R.id.walk_txtview);
        mTvDistance = (TextView) mContentView.findViewById(R.id.distance_txtview);
        mTvLocation = (TextView) mContentView.findViewById(R.id.location_txtview);
        mBtnTrack = (Button) mContentView.findViewById(R.id.track_button);
        mBtnTrack.setOnClickListener(this);
        mTvLocation.setText(PaceCounterUtil.address);
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

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            PaceCounterUtil.sensorCount =(int) sensorEvent.values[0];
            PaceCounterUtil.steps = PaceCounterUtil.sensorCount -PaceCounterUtil.preferenceCount;
            mTvWalk.setText(String.valueOf(PaceCounterUtil.steps));
            mTvDistance.setText(PaceCounterUtil.getDistance());
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    private void changeSensorState(){
        if(isTracking){//service need to run
            getDeviceSensor();
        }else{//stop listener
           unregisterListeners();
        }
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.track_button){
            isTracking = !isTracking;
            PaceCounterUtil.setTrackState(getContext(), !isTracking);
            changeButtonState();
            changeSensorState();
            if(isTracking){
                getDeviceSensor();
                PaceCounterUtil.makeAlarmManager(getContext());//측정을 시작했을때 매일 데이터를 저장하도록 알람매니저 등록
            }else{
                unregisterListeners();
                PaceCounterUtil.removeAlarmManager(getContext());//측정을 멈추면 알람매니저도 제거해줘야한다
            }
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
        unregisterListeners();
    }

    //LocationListener start
    @Override
    public void onLocationChanged(Location location) {
        String msg = location.getLatitude()
                      +  "," + location.getLongitude();
        mMapGeocode.setLocationCode(msg);
        mMapGeocode.execute();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);
    }

    @Override
    public void onProviderDisabled(String s) {
    }
    //LocationListener end
}
