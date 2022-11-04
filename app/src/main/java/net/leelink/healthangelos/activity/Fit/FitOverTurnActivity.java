package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.config.TurnWristLightingConfig;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.widget.SwitchCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class FitOverTurnActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_start_time,rl_end_time;
    private SwitchCompat cb_turn_over;
    private boolean is_open;
    private TimePickerView pvTime, pvTime1;
    private SimpleDateFormat sdf, sdf1;
    private Date start_date, end_date;
    private TextView tv_start_time,tv_end_time;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_over_turn);
        context = this;
        init();
        initPickerView();
        initClose();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cb_turn_over = findViewById(R.id.cb_turn_over);
        cb_turn_over.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    is_open = true;
                }else {
                    is_open = false;
                }
            }
        });
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
    }

    public void initData(){
        TurnWristLightingConfig config = mWristbandManager.getWristbandConfig().getTurnWristLightingConfig();
        cb_turn_over.setChecked(config.isEnable());
        String start_time = Utils.getTimeFromMinutes(config.getStart());
        tv_start_time.setText(start_time);
        String end_time = Utils.getTimeFromMinutes(config.getEnd());
        tv_end_time.setText(end_time);
        try {
            start_date = sdf.parse(start_time);
            end_date = sdf.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_start_time:    //开始时间
                pvTime.show();
                break;
            case R.id.rl_end_time:  //结束时间点
                pvTime1.show();
                break;
        }
    }

    private void initPickerView() {
        boolean[] type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_start_time.setText(sdf.format(date));
                start_date = date;
            }
        }).setType(type).build();
    }

    private void initClose() {

        boolean[] type = {false, false, false, true, true, false};
        sdf1 = new SimpleDateFormat("HH:mm");
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_end_time.setText(sdf1.format(date));
                end_date = date;
            }
        }).setType(type).build();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(start_date != null && end_date!=null) {
            turn_wrist_lighting_config();
        }
    }

    @SuppressLint("CheckResult")
    public void turn_wrist_lighting_config() {
        if (mWristbandManager.isConnected()) {
            TurnWristLightingConfig config = mWristbandManager.getWristbandConfig().getTurnWristLightingConfig();
            config.setStart(start_date.getHours() * 60 + start_date.getMinutes());//Start Time:9:00
            config.setEnd(end_date.getHours() * 60 + end_date.getMinutes());//End Time:23:59
            config.setEnable(is_open);

            mWristbandManager.setTurnWristLightingConfig(config)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            Toast.makeText(context, "数据已保存", Toast.LENGTH_SHORT).show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("sample", "", throwable);
                        }
                    });
        } else {
            Toast.makeText(context, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }

}