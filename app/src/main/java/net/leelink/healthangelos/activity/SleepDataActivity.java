package net.leelink.healthangelos.activity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class SleepDataActivity extends BaseActivity {
    private LineChart lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_data);
        init();
    }

    public void init(){
        lc = findViewById(R.id.lc);

    }
}
