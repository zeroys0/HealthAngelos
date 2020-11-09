package net.leelink.healthangelos.volunteer;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MissionListAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeBankActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    TextView tv_mission_type,tv_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    RecyclerView mission_list;
    MissionListAdapter missionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_bank);
        init();
        initList();
        initPickerView();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_mission_type = findViewById(R.id.tv_mission_type);
        tv_mission_type.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        mission_list = findViewById(R.id.mission_list);

    }

    public void initList(){
        missionListAdapter = new MissionListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mission_list.setLayoutManager(layoutManager);
        mission_list.setAdapter(missionListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_mission_type:

                showpopu();
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
        }
    }

    private void initPickerView(){
        sdf = new SimpleDateFormat("yyyy-MM");
        boolean[] type = {true, true, false, false, false, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));

            }
        }).setType(type).build();
    }

    public void showpopu(){
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = (LinearLayout) popView.findViewById(R.id.ll_create_plan);
        LinearLayout ll_create_scope = (LinearLayout) popView.findViewById(R.id.ll_create_scope);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_mission_type.setText("团队任务");
                pop.dismiss();
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_mission_type.setText("个人任务");
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_mission_type);
    }
}
