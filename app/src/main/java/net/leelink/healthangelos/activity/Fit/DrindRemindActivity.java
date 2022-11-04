package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.config.DrinkWaterConfig;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.bean.DrinkRemind;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class DrindRemindActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_start_time,rl_end_time,rl_interval;
    private SwitchCompat cb_drink;
    private TimePickerView pvTime, pvTime1;
    private SimpleDateFormat sdf, sdf1;
    private TextView tv_start_time,tv_end_time,tv_time_space;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Date start_date, end_date;
    private boolean is_open;
    private int interval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drind_remind);
        context = this;
        init();
        initPickerView();
        initClose();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cb_drink = findViewById(R.id.cb_drink);
        cb_drink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    is_open = true;
                } else {
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
        rl_interval = findViewById(R.id.rl_interval);
        rl_interval.setOnClickListener(this);
        tv_time_space = findViewById(R.id.tv_time_space);
    }

    public void initData(){
        DrinkWaterConfig drinkWaterConfig = mWristbandManager.getWristbandConfig().getDrinkWaterConfig();
        tv_time_space.setText(drinkWaterConfig.getInterval() + "分钟");
        cb_drink.setChecked(drinkWaterConfig.isEnable());
        String start_time = Utils.getTimeFromMinutes(drinkWaterConfig.getStart());
        tv_start_time.setText(start_time);
        String end_time = Utils.getTimeFromMinutes(drinkWaterConfig.getEnd());
        tv_end_time.setText(end_time);
        try {
            start_date = sdf.parse(start_time);
            end_date = sdf.parse(end_time);
            interval = drinkWaterConfig.getInterval();
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
            case R.id.rl_end_time:  //结束时间
                pvTime1.show();
                break;
            case R.id.rl_interval:  //设置时间间隔
                chooseInterval();
                break;
        }
    }

    public void chooseInterval(){
        List<Integer> integerList = new ArrayList<>();
        integerList.add(30);
        integerList.add(60);
        integerList.add(90);
        integerList.add(120);
        integerList.add(150);
        integerList.add(180);
        List<String> intervalList = new ArrayList<>();
        for(int i =30;i<180;i+=30){
            integerList.add(i);
            intervalList.add(i+"分钟");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_time_space.setText(intervalList.get(options1));
                interval = integerList.get(options1);
                EventBus.getDefault().post(new DrinkRemind(interval));
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(intervalList);
        pvOptions.show();
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
        if(start_date != null && end_date!=null && interval !=0) {
            drink_water_config();
        }
    }

    @SuppressLint("CheckResult")
    public void drink_water_config() {
        if (mWristbandManager.isConnected()) {
            DrinkWaterConfig config = mWristbandManager.getWristbandConfig().getDrinkWaterConfig();
            config.setStart(start_date.getHours() * 60 + start_date.getMinutes());
            config.setEnd(end_date.getHours() * 60 + end_date.getMinutes());
            config.setInterval(interval);//Interval 30min
            config.setEnable(is_open);

            mWristbandManager.setDrinkWaterConfig(config)
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