package app.ahreum.com.pacecounters.ui.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.model.PaceCounterAdapter;
import app.ahreum.com.pacecounters.model.PaceCounterConst;
import app.ahreum.com.pacecounters.model.PaceCounterUtil;
import app.ahreum.com.pacecounters.ui.presenter.ListContractor;
import app.ahreum.com.pacecounters.ui.presenter.ListPresenter;

/**
 * Created by ahreum on 2016-12-06.
 */

public class FragmentForRecordList extends Fragment implements ListContractor.View {
    private ListPresenter listPresenter;
    private View mContentView;
    private RecyclerView mListView;
    private Cursor mListCursor;
    private Context mContext;
    private PaceCounterAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        listPresenter = new ListPresenter();
        listPresenter.attachView(this);
        mContext = getContext();
        mContentView = inflater.inflate(R.layout.fragment_record, container, false);

        listPresenter.loadListData(getContext());//loadListData();//move to preaenter
        mAdapter = new PaceCounterAdapter(mContext, mListCursor);
        mListView = (RecyclerView) mContentView.findViewById(R.id.record_listview);
        mListView.setAdapter(mAdapter);
        return mContentView;
    }
//insert db data need move to service
    private void insertListData(){
        ContentValues values = new ContentValues();
        values.put( PaceCounterConst.KEY_DATE,  new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        values.put( PaceCounterConst.KEY_COUNT, String.valueOf(PaceCounterUtil.steps));
        values.put( PaceCounterConst.KEY_DISTANCE,  String.valueOf(PaceCounterUtil.getDistance()));
       mContext.getContentResolver().insert(PaceCounterConst.CONTENT_URI,values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listPresenter.detachView();
        mContentView = null;
        mListView = null;
        mContext = null;
        if(mListCursor != null){
            mListCursor.close();
            mListCursor = null;
        }
    }
    @Override
    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setCursor(Cursor cur) {
        mListCursor = cur;
    }
}
