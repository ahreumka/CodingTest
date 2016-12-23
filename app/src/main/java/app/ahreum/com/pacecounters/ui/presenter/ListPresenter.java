package app.ahreum.com.pacecounters.ui.presenter;

import android.content.Context;
import android.database.Cursor;

import app.ahreum.com.pacecounters.model.PaceCounterConst;

/**
 * Created by ahreum on 2016-12-23.
 */

public class ListPresenter implements ListContractor.Presenter {
    private ListContractor.View listView;
    @Override
    public void attachView(ListContractor.View view) {
        listView = view;
    }

    @Override
    public void detachView() {
        listView = null;
    }

    @Override
    public void loadListData(Context context) {
        Cursor listCursor = context.getContentResolver().query(PaceCounterConst.CONTENT_URI,
                PaceCounterConst.PACECOUNTER_SELECTION_DETAIL, null, null, null);
        listView.setCursor(listCursor);
        listView.notifyAdapter();

    }
}
