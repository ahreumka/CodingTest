package app.ahreum.com.pacecounters.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;

/**
 * Created by ahreum on 2016-12-28.
 */

public class StepCountService extends Service implements SensorEventListener, Runnable {
    private SensorManager sensorManager;
    private Sensor countSensor;
    private LinearLayout miniLayout ;
    private WindowManager mWindowManager ;
    private WindowManager.LayoutParams myParam;
    private TextView walkTV, distanceTV;
    private float[] gravity_data = new float[3];
    private float[] accel_data = {0, 0, 0};

    private long lastTime;
    private float speed;

    private Handler mHandlerCount = new Handler();
    private int count;
    private final IBinder mCRBinder = new CountServiceBinder();
    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        miniLayout = (LinearLayout) inflater.inflate(R.layout.activity_mini, null);
        walkTV = (TextView) miniLayout.findViewById(R.id.walk_txtview);
        distanceTV = (TextView) miniLayout.findViewById(R.id.distance_txtview);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createMiniWindow();

        sensorManager =(SensorManager) getApplicationContext().getSystemService(Activity.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, countSensor,SensorManager.SENSOR_DELAY_NORMAL);
        Thread thr = new Thread(null, this, "StepCountService");
        thr.start();
    }

    private void createMiniWindow(){
        Point mPoint = new Point();
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        mDisplay.getSize(mPoint);

        myParam = new WindowManager.LayoutParams(
                mPoint.x/3,
                mPoint.y/4,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        myParam.gravity = Gravity.CENTER_VERTICAL;
        myParam.verticalMargin = 0.1f;
        myParam.horizontalMargin = 0.1f;
    }
    public void startForeGround(){
        if(!PaceCounterUtil.getOverlayWindowState(getApplicationContext())){
            mWindowManager.addView(miniLayout,myParam);
        }else{
            PaceCounterUtil.showToast(getApplicationContext(),"you need to allow window overlay permission");
        }
    }
    public void endForeGround(){
        try {
            mWindowManager.removeView(miniLayout);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
        if(mHandlerCount != null){
            mHandlerCount = null;
        }
        if(miniLayout != null){
            miniLayout = null;
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCRBinder;
    }

    //implements SensorEventListener start
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis();
                long gabOfTime = (currentTime - lastTime);

                if (gabOfTime > 300) {
                    lastTime = currentTime;

                    gravity_data[0] = PaceCounterConst.ALPHA * gravity_data[0] + (1 - PaceCounterConst.ALPHA) * sensorEvent.values[0];
                    gravity_data[1] = PaceCounterConst.ALPHA * gravity_data[1] + (1 - PaceCounterConst.ALPHA) * sensorEvent.values[1];
                    gravity_data[2] = PaceCounterConst.ALPHA * gravity_data[2] + (1 - PaceCounterConst.ALPHA) * sensorEvent.values[2];

                    accel_data[0] = sensorEvent.values[0] - gravity_data[0];
                    accel_data[1] = sensorEvent.values[1] - gravity_data[1];
                    accel_data[2] = sensorEvent.values[2] - gravity_data[2];

                    speed = Math.abs(accel_data[0] + accel_data[1] + accel_data[2]);

                    if (speed > PaceCounterConst.SHAKE_THRESHOLD) {
                        count++;
                    }

                    PaceCounterUtil.steps = count;//  PaceCounterUtil.getDistance()
                }
            }
        }
    }
    public class CountServiceBinder extends Binder{
        public StepCountService getService(){
            return StepCountService.this;
        }
    }
    @Override
    public void run() {
        if(walkTV!=null) walkTV.setText(String.valueOf(PaceCounterUtil.steps));
        if(distanceTV!=null) distanceTV.setText(String.valueOf(PaceCounterUtil.getDistance()));
        mHandlerCount.postAtTime(this, SystemClock.uptimeMillis());
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    //implements SensorEventListener end
}
