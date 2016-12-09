package app.ahreum.com.pacecounters.ui;

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
import android.widget.CursorAdapter;
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
    private RecyclerView mListView;
    private Cursor mListCursor;
    private Context mContext;
    private PaceCounterAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        updateLayout(inflater, container);
        loadListData();
        mAdapter = new PaceCounterAdapter(mContext, mListCursor);
        mListView = (RecyclerView) mContentView.findViewById(R.id.record_listview);
        mListView.setAdapter(mAdapter);
        return mContentView;
    }
    private void updateLayout(LayoutInflater inflater, ViewGroup container) {
        mContentView = inflater.inflate(R.layout.fragment_record, container, false);

    }
    private void loadListData(){
        insertListData();
        mListCursor = mContext.getContentResolver().query(PaceCounterConst.CONTENT_URI,
                PaceCounterConst.PACECOUNTER_SELECTION_DETAIL, null, null, null);
    }
    private void insertListData(){
        ContentValues values = new ContentValues();
        values.put( PaceCounterConst.KEY_DATE,  new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        values.put( PaceCounterConst.KEY_COUNT, String.valueOf(PaceCounterUtil.steps));
        values.put( PaceCounterConst.KEY_DISTANCE,  String.valueOf(PaceCounterUtil.getDistance()));
       mContext.getContentResolver().insert(PaceCounterConst.CONTENT_URI,values);
    }
    private class PaceCounterAdapter extends RecyclerView.Adapter<FragmentForRecordList.ViewHolder>{
        private CursorAdapter mCursorAdapter;
        private Cursor mCursor = null;
        private Context mContext;
        private ViewHolder holder;

        public PaceCounterAdapter(Context context, Cursor cursor){
            super();
            mContext = context;
            mCursor = cursor;
            mCursorAdapter = new CursorAdapter(mContext, mCursor, 0) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                    View v = LayoutInflater.from(context)
                            .inflate(R.layout.list_item_record, viewGroup, false);
                    return v;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(PaceCounterConst.KEY_DATE));
                    String walk = cursor.getString(cursor.getColumnIndexOrThrow(PaceCounterConst.KEY_COUNT));
                    String distance = cursor.getString(cursor.getColumnIndexOrThrow(PaceCounterConst.KEY_DISTANCE));
                    holder.dateTextView.setText(date);
                    holder.walkTextView.setText(walk);
                    holder.distanceTextView.setText(distance);
                }
            };
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contentView = inflater.inflate(R.layout.list_item_record, parent, false);
            holder = new ViewHolder(contentView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursorAdapter.getCursor().moveToPosition(position);
            mCursorAdapter.bindView(holder.itemView ,mContext,mCursorAdapter.getCursor());
         //   holder.bindData(mCursor, position);
        }

        @Override
        public int getItemCount() {
            return mCursorAdapter.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateTextView;
        public TextView walkTextView;
        public TextView distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_list);
            walkTextView = (TextView) itemView.findViewById(R.id.walk_list);
            distanceTextView = (TextView) itemView.findViewById(R.id.distance_list);

        }
//        public void bindData(Cursor cusror, int position){
//            if(cusror != null){
//                cusror.getString(position);
//
//            }
//
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContentView = null;
        mListView = null;
        mContext = null;
        if(mListCursor != null){
            mListCursor.close();
            mListCursor = null;
        }
    }
}
