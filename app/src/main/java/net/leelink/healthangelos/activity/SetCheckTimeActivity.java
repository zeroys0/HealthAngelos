package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.SetTimeAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import androidx.recyclerview.widget.RecyclerView;

public class SetCheckTimeActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private SetTimeAdapter setTimeAdapter;
    private RecyclerView time_set_list;
    private Button btn_confirm;
    private TextView tv_time1,tv_time2,tv_time3,tv_time4,tv_time5,tv_time6,tv_time7,tv_time8,tv_time9,tv_time10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_check_time);
        init();
        context  = this;
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_time1 = findViewById(R.id.tv_time1);
        tv_time1.setOnClickListener(this);
        tv_time2 = findViewById(R.id.tv_time2);
        tv_time2.setOnClickListener(this);
        tv_time3 = findViewById(R.id.tv_time3);
        tv_time3.setOnClickListener(this);
        tv_time4 = findViewById(R.id.tv_time4);
        tv_time4.setOnClickListener(this);
        tv_time5 = findViewById(R.id.tv_time5);
        tv_time5.setOnClickListener(this);
        tv_time6 = findViewById(R.id.tv_time6);
        tv_time6.setOnClickListener(this);
        tv_time7 = findViewById(R.id.tv_time7);
        tv_time7.setOnClickListener(this);
        tv_time8 = findViewById(R.id.tv_time8);
        tv_time8.setOnClickListener(this);
        tv_time9 = findViewById(R.id.tv_time9);
        tv_time9.setOnClickListener(this);
        tv_time10 = findViewById(R.id.tv_time10);
        tv_time10.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                break;
            case R.id.tv_time1:

                break;
        }
    }
}
