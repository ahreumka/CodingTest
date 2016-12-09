package app.ahreum.com.pacecounters.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.util.APIExamMapGeocode;
import app.ahreum.com.pacecounters.util.PaceCounterConst;
import app.ahreum.com.pacecounters.util.PaceCounterUtil;

/**
 * Created by ahreum on 2016-12-06.
 */

public class FragmentForMonitorScreen extends Fragment implements View.OnClickListener{
    private View mContentView;
    private Button mBtnTrack;
    private TextView mTvWalk, mTvDistance, mTvLocation;
    private SensorManager mSensorManager;
    private boolean isTracking;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        updateLayout(inflater, container);
        getDeviceSensor();
        if(savedInstanceState != null){
            mTvWalk.setText(savedInstanceState.getString(PaceCounterConst.KEY_COUNT));
            mTvDistance.setText(savedInstanceState.getString(PaceCounterConst.KEY_DISTANCE));
        }
        return mContentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PaceCounterConst.KEY_COUNT,mTvWalk.getText().toString());
        outState.putString(PaceCounterConst.KEY_DISTANCE,mTvDistance.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        isTracking = PaceCounterUtil.getTrackState(getContext());
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
        new APIExamMapGeocode().start();
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
        unregisterListeners();
    }

}
