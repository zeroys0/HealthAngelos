package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.config.DrinkWaterConfig;
import com.htsmart.wristband2.bean.config.FunctionConfig;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.bean.DrinkRemind;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class FitSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_alarm_clock, rl_drink_remind, rl_sit_remind, rl_overturn, rl_sport_target, rl_hand, rl_time_style,rl_search;
    private LinearLayout ll_step_target;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private TextView tv_step_number, tv_interval, tv_apparel, tv_time_form,tv_sit_interval;
    private SwitchCompat cb_weather;
    private boolean open_weather;
    private int step = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_setting);
        context = this;
        init();
        synData();
        EventBus.getDefault().register(this);
        getTarget();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DrinkRemind drinkRemind) {
        tv_interval.setText(drinkRemind.getInterval()+"分钟提醒一次");
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ll_step_target = findViewById(R.id.ll_step_target);
        ll_step_target.setOnClickListener(this);
        rl_alarm_clock = findViewById(R.id.rl_alarm_clock);
        rl_alarm_clock.setOnClickListener(this);
        rl_drink_remind = findViewById(R.id.rl_drink_remind);
        rl_drink_remind.setOnClickListener(this);
        rl_sit_remind = findViewById(R.id.rl_sit_remind);
        rl_sit_remind.setOnClickListener(this);
        tv_apparel = findViewById(R.id.tv_apparel);
        rl_hand = findViewById(R.id.rl_hand);
        rl_hand.setOnClickListener(this);
        rl_overturn = findViewById(R.id.rl_overturn);
        rl_overturn.setOnClickListener(this);
        tv_step_number = findViewById(R.id.tv_step_number);
        tv_interval = findViewById(R.id.tv_interval);
        rl_time_style = findViewById(R.id.rl_time_style);
        rl_time_style.setOnClickListener(this);
        tv_time_form = findViewById(R.id.tv_time_form);
        cb_weather = findViewById(R.id.cb_weather);
        cb_weather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SetConfig(FunctionConfig.FLAG_WEATHER_SWITCH, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_WEATHER_SWITCH, false);
                }
            }
        });
        tv_sit_interval = findViewById(R.id.tv_sit_interval);
        rl_search = findViewById(R.id.rl_search);
        rl_search.setOnClickListener(this);
    }

    public void synData() {
        FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_WEATHER_SWITCH)) {        //判断天气选项开关
            cb_weather.setChecked(true);
        } else {
            cb_weather.setChecked(false);
        }
        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_WEAR_WAY)) {        //获取佩戴习惯
            tv_apparel.setText("右手");
        } else {
            tv_apparel.setText("左手");
        }
        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_HOUR_STYLE)) {        //获取时间格式
            tv_time_form.setText("12小时制");
        } else {
            tv_time_form.setText("24小时制");
        }
        DrinkWaterConfig drinkWaterConfig = mWristbandManager.getWristbandConfig().getDrinkWaterConfig();
        tv_interval.setText(drinkWaterConfig.getInterval() + "分钟提醒一次");

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_step_target:   //运动目标
                showStep();
                break;
            case R.id.rl_alarm_clock:   //闹钟
                Intent intent = new Intent(context, FitAlarmClockActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_drink_remind:  //喝水提醒
                Intent intent1 = new Intent(context, DrindRemindActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_sit_remind:    //久坐提醒
                Intent intent2 = new Intent(context, SitRemindActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_overturn:      //翻腕亮屏
                Intent intent3 = new Intent(context, FitOverTurnActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_hand:      //佩戴方式
                showHand();
                break;
            case R.id.rl_time_style:        //时间格式
                chooseTimeStyle();
                break;
            case R.id.rl_search:       //查找腕表
                findWatch();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void showStep() {
        List<Integer> integerList = new ArrayList<>();
        List<String> stepList = new ArrayList<>();
        for(int i =2000;i<30000;i+=1000){
            integerList.add(i);
            stepList.add(i+"步");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_step_number.setText(stepList.get(options1));
                step = integerList.get(options1)/1000;
                mWristbandManager.setExerciseTarget(integerList.get(options1), 0, 0);
                changeTarget();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(stepList);
        pvOptions.show();
    }

    public void showHand() {
        List<String> hand_list = new ArrayList<>();
        hand_list.add("左手");
        hand_list.add("右手");

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_apparel.setText(hand_list.get(options1));
                if (tv_apparel.getText().toString().equals("右手")) {
                    SetConfig(FunctionConfig.FLAG_WEAR_WAY, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_WEAR_WAY, false);
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(hand_list);
        pvOptions.show();
    }

    public void chooseTimeStyle() {
        List<String> hand_list = new ArrayList<>();
        hand_list.add("12小时制");
        hand_list.add("24小时制");

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_time_form.setText(hand_list.get(options1));
                if (tv_time_form.getText().toString().equals("12小时制")) {
                    SetConfig(FunctionConfig.FLAG_HOUR_STYLE, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_HOUR_STYLE, false);
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(hand_list);
        pvOptions.show();
    }

    @SuppressLint("CheckResult")
    public void SetConfig(int FLAG, boolean state) {
        if (mWristbandManager.isConnected()) {
            FunctionConfig config = mWristbandManager.getWristbandConfig().getFunctionConfig();
            config.setFlagEnable(FLAG, state);//Wear right hand
            mWristbandManager.setFunctionConfig(config)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
//                            Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                            Log.d("run: ", "成功");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }

    public void findWatch(){
        mWristbandManager.findWristband().blockingGet();
        Log.e( "onClick: ", "查找腕表");
    }

    public void changeTarget(){
        OkGo.<String>put(Urls.getInstance().RUNTARGET+"/"+MyApplication.userInfo.getOlderlyId()+"/"+step)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置运动目标", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置目标成功", Toast.LENGTH_SHORT).show();

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getTarget(){
        OkGo.<String>get(Urls.getInstance().RUNTARGET+"/"+MyApplication.userInfo.getOlderlyId())
                .tag(this)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取运动目标", json.toString());
                            if (json.getInt("status") == 200) {
                                step = json.getInt("data");
                                tv_step_number.setText(step*1000+"步");

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        mWristbandManager.close();
    }
}