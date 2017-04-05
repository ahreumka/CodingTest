package app.ahreum.com.pacecounters.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import app.ahreum.com.pacecounters.R;

/**
 * Created by P16086 on 2017-04-03.
 */

public class RetroMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_monitor);
    }
}
