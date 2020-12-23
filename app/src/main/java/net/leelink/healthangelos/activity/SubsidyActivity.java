package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class SubsidyActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_submit,rl_progress;
    TextView tv_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidy);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_submit = findViewById(R.id.rl_submit);
        rl_submit.setOnClickListener(this);
        rl_progress = findViewById(R.id.rl_progress);
        rl_progress.setOnClickListener(this);
        tv_data = findViewById(R.id.tv_data);
        tv_data.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_submit:
                Intent intent = new Intent(this,SubmitSubsidyActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_progress:
                Intent intent1 = new Intent(this,ProgressActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_data:
                Intent intent2 = new Intent(this,SubsidyHistoryActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
