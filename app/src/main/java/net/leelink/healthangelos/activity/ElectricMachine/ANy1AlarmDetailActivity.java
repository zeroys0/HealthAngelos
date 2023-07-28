package net.leelink.healthangelos.activity.ElectricMachine;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class ANy1AlarmDetailActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_time,tv_state,tv_alarm_name,tv_rule,tv_restore_rule,tv_alarm_type,tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any1_alarm_detail);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_time = findViewById(R.id.tv_time);
        ElectAlarmBean electAlarmBean = (ElectAlarmBean) getIntent().getSerializableExtra("bean");
        tv_time.setText(electAlarmBean.getAlarmTime());
        tv_state = findViewById(R.id.tv_state);
        tv_state.setText(electAlarmBean.getStatus());
        tv_alarm_name = findViewById(R.id.tv_alarm_name);
        tv_alarm_name.setText(electAlarmBean.getAlarmName());
        tv_rule = findViewById(R.id.tv_rule);
        tv_rule.setText(electAlarmBean.getAlarmRule());
        tv_restore_rule = findViewById(R.id.tv_restore_rule);
        tv_restore_rule.setText(electAlarmBean.getRestoreRule());
        tv_alarm_type = findViewById(R.id.tv_alarm_type);
        tv_alarm_type.setText(electAlarmBean.getAlarmType());
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(electAlarmBean.getDescription());

    }
}