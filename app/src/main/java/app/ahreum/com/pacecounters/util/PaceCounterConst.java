package app.ahreum.com.pacecounters.util;

import android.net.Uri;


/**
 * Created by ahreum on 2016-12-06.
 */

public class PaceCounterConst {
    private PaceCounterConst (){
    }
    //map
    public static final String MAP_API_KEY ="NAcK62L8mj2R97icZSeN";
    public static final String MAP_SECRET_KEY ="vJw_gVbwkb";

    public static final String PACKAGE_STRING ="app.ahreum.com.pacecounters";
    public static final int PACE_COUNTER_TAB_MAIN_ID = 0;
    public static final int PACE_COUNTER_TAB_RECORD_ID = 1;
    public static final String MINI_SHOW_STRING ="show_mini";
    public static final String MINI_HIDE_STRING ="hide_mini";
    //preference
    public static final String SHARED_PREFS_NAME = "app.ahreum.com.pacecounters_preferences";
    public static final String KEY_TRACK_STATE = "track_state";
    public static final String KEY_STEP_COUNT = "pre_step_count";
    public static final String KEY_AVERAGE_STRIDE = "average_stride";

    //database
    public static final String AUTHORITY = "app.ahreum.com.pacecounters.provider";
    public static final Uri CONTENT_URI =  Uri.parse("content://"+ AUTHORITY);

    public static final int DB_ITEMS = 1;
    public static final int DB_ITEM_ID = 2;
    public static final int DB_VERSION = 1;

    public static final String DB_APP_NAME = "pacecounter";
    public static final String DB_NAME = DB_APP_NAME + ".db";
    public static final String DB_TABLE = DB_APP_NAME + "_table";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_ID =  "_id";
    public static final String KEY_DATE =  "date";
    public static final String KEY_COUNT =  "count";
    public static final String KEY_DISTANCE =  "distance";

    public static final String DATABASE_CREATE =
            "CREATE TABLE " + DB_TABLE
                    + " (" + KEY_ID + " INTEGER PRIMARY KEY autoincrement"
                    + ", " + KEY_DATE + " TEXT"
                     + ", " + KEY_COUNT + " TEXT"
                    + ", " + KEY_DISTANCE + " TEXT"
                    + ");";
    public static final String INSERT_DEFAULT =
            "INSERT INTO " + DB_TABLE + "("
                    + KEY_DATE + ","
                    + KEY_COUNT + ","
                    + KEY_DISTANCE + ") VALUES";
    public static final String[] PACECOUNTER_SELECTION_DETAIL = new String[]{
            PaceCounterConst.KEY_ID,
            PaceCounterConst.KEY_DATE,
            PaceCounterConst.KEY_COUNT,
            PaceCounterConst.KEY_DISTANCE
    };

}
