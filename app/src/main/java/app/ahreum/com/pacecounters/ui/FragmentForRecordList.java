package app.ahreum.com.pacecounters.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.provider.PaceCounterProvider;
import app.ahreum.com.pacecounters.util.PaceCounterConst;
import app.ahreum.com.pacecounters.util.PaceCounterUtil;

/**
 * Created by ahreum on 2016-12-06.
 */

public class FragmentForRecordList extends Fragment {
    private View mContentView;
    private ListView mListView;
    private Cursor mListCursor;
    private Context mContext;
    private SimpleCursorAdapter mCursorAdapter;
    private int[] toViewID  ;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        updateLayout(inflater, container);
        loadListData();
        toViewID = new int []{R.id.date_list, R.id.walk_list, R.id.distance_list};
        mCursorAdapter = new SimpleCursorAdapter(mContentView.getContext(), R.layout.list_item_record, mListCursor, PaceCounterConst.PACECOUNTER_SELECTION_DETAIL,toViewID);

        mListView.setAdapter(mCursorAdapter);
        return mContentView;
    }
    private void updateLayout(LayoutInflater inflater, ViewGroup container) {
        mContentView = inflater.inflate(R.layout.fragment_record, container, false);
        mListView = (ListView) mContentView.findViewById(R.id.record_listview);
    }
    private void loadListData(){
        mListCursor = mContext.getContentResolver().query(PaceCounterConst.CONTENT_URI,
                PaceCounterConst.PACECOUNTER_SELECTION_DETAIL, null, null, null);
    }
}
