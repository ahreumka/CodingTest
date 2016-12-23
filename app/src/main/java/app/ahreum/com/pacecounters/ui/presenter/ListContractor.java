package app.ahreum.com.pacecounters.ui.presenter;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by ahreum on 2016-12-23.
 */

public interface ListContractor {
    interface View{
        void notifyAdapter();
        void setCursor(Cursor cur);
    }

    interface Presenter{
        void attachView(View view);//viewSet
        void detachView();
        void loadListData(Context context);
    }

}
