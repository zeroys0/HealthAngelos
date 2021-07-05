package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class HealthDataDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    Context context;
    private TabLayout tabLayout;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_data_detail);
        init();
        context = this;
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }
}
