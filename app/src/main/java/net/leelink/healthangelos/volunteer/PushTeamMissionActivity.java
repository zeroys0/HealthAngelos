package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.TeamMissionBean;

import org.json.JSONException;
import org.json.JSONObject;

public class PushTeamMissionActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TeamMissionBean volunteerEventBean;
    private int state;
    private TextView tv_title,tv_start_time,tv_end_time,tv_address,tv_people,tv_phone,tv_content,tv_state,tv_num;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_team_mission);
        context = this;
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = findViewById(R.id.tv_title);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_address = findViewById(R.id.tv_address);
        tv_people = findViewById(R.id.tv_people);
        tv_phone = findViewById(R.id.tv_phone);
        tv_content = findViewById(R.id.tv_content);
        tv_state = findViewById(R.id.tv_state);
        tv_num = findViewById(R.id.tv_num);


    }

    public void initData() {
        volunteerEventBean = (TeamMissionBean) getIntent().getSerializableExtra("mission");
        tv_title.setText(volunteerEventBean.getServTitle());
        tv_start_time.setText(volunteerEventBean.getStartTime());
        tv_end_time.setText(volunteerEventBean.getEndTime());
        tv_num.setText(volunteerEventBean.getNum()+"");
        String s = volunteerEventBean.getServAddress();
        try {
            JSONObject jsonObject = new JSONObject(s);
            tv_address.setText(jsonObject.getString("fullAddress"));
        } catch (JSONException e) {
            e.printStackTrace();
            tv_address.setText(volunteerEventBean.getServAddress());
        }
        tv_people.setText(volunteerEventBean.getServName());
        tv_phone.setText(volunteerEventBean.getServTelephone());
        tv_content.setText(volunteerEventBean.getServContent());
        state = volunteerEventBean.getState();
        switch (state){
            case 1:     //可领取

                break;
            case 2:     //待开始打卡

                tv_state.setText("待开始打卡");
                break;
            case 3:     //待结束打卡

                tv_state.setText("任务进行中");
                break;
            case 4:     //等待审核

                tv_state.setText("等待审核");
                break;
            case 5:     //审核已通过

                tv_state.setText("任务完成");
                break;
            case 6:     //审核未通过

                //   tv_reason.setText(volunteerEventBean.getCause());
                tv_state.setText("审核未通过");
                break;
            case 7:     //二次审核中

                //   tv_reason.setText(volunteerEventBean.getCause());
                tv_state.setText("二次审核中");

                break;
            case 8:     //结束
                tv_state.setText("任务完成");
                break;
            case 9:     //二次审核未通过
                tv_state.setText("二次审核未通过");
                break;
        }
    }

}
