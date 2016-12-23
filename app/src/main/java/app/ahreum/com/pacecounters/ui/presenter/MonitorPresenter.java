package app.ahreum.com.pacecounters.ui.presenter;

/**
 * Created by ahreum on 2016-12-23.
 */

public class MonitorPresenter implements MonitorContractor.Presenter {
    private MonitorContractor.View monitorView;

    @Override
    public void attachView(MonitorContractor.View view) {
        monitorView = view;
    }

    @Override
    public void detachView() {
        monitorView = null;
    }

}
