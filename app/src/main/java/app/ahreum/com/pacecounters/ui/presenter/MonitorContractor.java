package app.ahreum.com.pacecounters.ui.presenter;

/**
 * Created by ahreum on 2016-12-23.
 */

public interface MonitorContractor {
    interface View{

    }
    interface Presenter{
        void attachView(View view);//viewSet

        void detachView();

    }
}
