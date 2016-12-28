package app.ahreum.com.pacecounters.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;

/**
 * Created by ahreum on 2016-12-28.
 */

public class StepCountService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor countSensor;

    private float[] gravity_data = new float[3];
    private float[] accel_data = {0, 0, 0};

    private long lastTime;
    private float speed;

    private int count;
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager =(SensorManager) getApplicationContext().getSystemService(Activity.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, countSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                    Intent monitorIntent = new Intent(PaceCounterConst.ACTION_COUNT_SERVICE);
                    //monitorIntent.putExtra("counter", count);
                    sendBroadcast(monitorIntent);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    //implements SensorEventListener end
}
