package app.ahreum.com.pacecounters.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.ahreum.com.pacecounters.util.PaceCounterConst;

/**
 * Created by ahreum on 2016-12-06.
 */

public class PaceCounterProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(PaceCounterConst.AUTHORITY,  PaceCounterConst.DB_APP_NAME, PaceCounterConst.DB_ITEMS);
        mUriMatcher.addURI(PaceCounterConst.AUTHORITY,  PaceCounterConst.DB_APP_NAME + "/#", PaceCounterConst.DB_ITEM_ID);
    }

    private SQLiteDatabase mDataBase;
    private PaceCounterDatabaseHelper dbHelper;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        dbHelper =  new PaceCounterDatabaseHelper(mContext);
        mDataBase = dbHelper.getWritableDatabase();
        return (mDataBase == null) ? false: true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PaceCounterConst.DB_TABLE);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs, null, null, sort);
        if (ret != null) {
            ret.setNotificationUri(mContext.getContentResolver(), uri);
        }
        return ret;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        ContentValues values;
        if (contentValues != null) {
            values = new ContentValues(contentValues);
        } else {
            values = new ContentValues();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(PaceCounterConst.DB_TABLE, null, values);
        if (rowID < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(PaceCounterConst.CONTENT_URI, rowID);
        mContext.getContentResolver().notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
    private static class PaceCounterDatabaseHelper extends SQLiteOpenHelper{
        public PaceCounterDatabaseHelper(Context context) {
            super(context, PaceCounterConst.DB_NAME, null, PaceCounterConst.DB_VERSION);
        }

        public PaceCounterDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

       private ContentValues insertDefault() {
            ContentValues values = new ContentValues();
            values.put( PaceCounterConst.KEY_DATE,  new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            values.put( PaceCounterConst.KEY_COUNT, "0");
            values.put( PaceCounterConst.KEY_DISTANCE, "0");
            return values;
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL( PaceCounterConst.DATABASE_CREATE);
			insertDefault();
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  PaceCounterConst.DB_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

}
