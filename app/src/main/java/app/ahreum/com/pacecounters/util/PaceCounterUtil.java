package app.ahreum.com.pacecounters.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.ahreum.com.pacecounters.ui.MainActivity;


/**
 * Created by Ahreum on 2016-12-08.
 */

public class PaceCounterUtil {
    //map
    public static String address = "";
    //glover value
    public static int steps = 0; //distance를 계산하기위한 base value
    public static int sensorCount = 0; //센서에서 넘어오는값, 사용자가 중지해도 증가하고 있기때문에 정지했을 때 preferenceCount에 저장해둬야한다
    public static int preferenceCount = 0;//사용자가 재시작했을 지점과 기존에 중지했을 때의 차이를 계산하기위한값
    public static double average_stride = 0.8; // default step stride 0.8m
    //service const
    public static boolean  isUserStopTrck = false;
    //general toast
    public static Toast mToast = null;

    public static void showToast(Context context, CharSequence msg){
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
    //현재 걸음 추적상태인지, 몇 걸음을 걸었는지 계산하기 위한 SharedPreferences
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PaceCounterConst.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    static public void setTrackState(Context context, boolean isTracking){
        SharedPreferences prefTrack = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefTrack.edit();
        editor.putBoolean(PaceCounterConst.KEY_TRACK_STATE, isTracking);
        editor.commit();
    }
    public static boolean getTrackState(Context context) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getBoolean(PaceCounterConst.KEY_TRACK_STATE , false);
    }
    static public void setPreStepCount(Context context, int preStep){
        SharedPreferences prefTrack = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefTrack.edit();
        editor.putInt(PaceCounterConst.KEY_STEP_COUNT, preStep);
        editor.commit();
    }
    public static int getPreStepCount(Context context) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getInt(PaceCounterConst.KEY_STEP_COUNT , 0);
    }

    //save daily data
    public static void makeAlarmManager(Context context){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MainActivity.class); //지정된 시간에 시작될 작업
        intent.setAction(PaceCounterConst.SAVE_COUNT_DATE);
        PendingIntent sender = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//현재것을 추가해서 사용한다
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), AlarmManager.INTERVAL_DAY, sender);//
    }
    public static void removeAlarmManager(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public static void insertListData(Context context){
        ContentValues values = new ContentValues();
        values.put( PaceCounterConst.KEY_DATE,  new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        values.put( PaceCounterConst.KEY_COUNT, String.valueOf(PaceCounterUtil.steps));
        values.put( PaceCounterConst.KEY_DISTANCE,  String.valueOf(PaceCounterUtil.getDistance()));
        context.getContentResolver().insert(PaceCounterConst.CONTENT_URI,values);
    }

    //calculating distance
    public static String getDistance(){
        double totalDistant = PaceCounterUtil.average_stride *PaceCounterUtil.steps;
        if(totalDistant > 1000){
            return String.format( "%.1f km",  totalDistant/1000  );
        }else{
            return String.format( "%.1f m",  totalDistant  );
        }
    }
    public static boolean getOverlayWindowState(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(context)) {
            return false; //오버레이 권한없음
        } else {
            return true;
        }
    }
    public static long getTimeNow() {
        return SystemClock.elapsedRealtime();
    }
}
