package app.ahreum.com.pacecounters.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Toast;


/**
 * Created by Ahreum on 2016-12-08.
 */

public class PaceCounterUtil {
    //map
    public static String address = "";
    //glover value
    public static int steps = 0;
    public static int sensorCount = 0;
    public static int preferenceCount = 0;
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
