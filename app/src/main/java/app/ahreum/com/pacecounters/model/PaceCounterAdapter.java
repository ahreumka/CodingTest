package app.ahreum.com.pacecounters.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import app.ahreum.com.pacecounters.R;
import app.ahreum.com.pacecounters.ui.view.FragmentForRecordList;

/**
 * Created by ahreum on 2016-12-23.
 * adapter have (list)view
 * strictly, it is not a model
 * need to divide with (list) and data
 */
public class PaceCounterAdapter extends RecyclerView.Adapter<PaceCounterAdapter.ViewHolder>{
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
//                    holder.dateTextView.setText(date);
//                    holder.walkTextView.setText(walk);
//                    holder.distanceTextView.setText(distance);
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

}

