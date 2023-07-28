package net.leelink.healthangelos.activity.sleepace;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.appcompat.widget.SwitchCompat;

public class SleepaceSettingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_rest, tv_hr_fast, tv_fast_time, tv_hr_slow, tv_hr_slow_time, tv_br_limit, tv_br_limit_time, tv_br_slow, tv_br_slow_time, tv_breath_stop_time;
    private TextView tv_action_time, tv_action_stop, tv_lay_time, tv_sleep_time, tv_wake_time;
    private TextView tv_save;
    private SwitchCompat cb_rest, cb_hr_fast, cb_hr_slow, cb_breath_fast, cb_breath_slow, cb_breath_stop, cb_action_time, cb_action_stop, cb_sit_alarm, cb_lay_time;
    List<String> organName = new ArrayList<>();
    private String rest_time, fast_time, hr_slow_time, br_fast_time, br_slow_time, br_stop_time, action_time, action_stop_time, lay_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int time_type, leftBedStartHour, leftBedStartMinute, leftBedEndHour, leftBedEndMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepace_setting);
        context = this;
        init();
        createProgressBar(context);
        initPickerView();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_rest = findViewById(R.id.tv_rest);
        tv_rest.setOnClickListener(this);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        tv_sleep_time = findViewById(R.id.tv_sleep_time);
        tv_sleep_time.setOnClickListener(this);
        tv_wake_time = findViewById(R.id.tv_wake_time);
        tv_wake_time.setOnClickListener(this);
        cb_rest = findViewById(R.id.cb_rest);
        cb_hr_fast = findViewById(R.id.cb_hr_fast);
        cb_hr_slow = findViewById(R.id.cb_hr_slow);
        cb_breath_fast = findViewById(R.id.cb_breath_fast);
        cb_breath_slow = findViewById(R.id.cb_breath_slow);
        cb_breath_stop = findViewById(R.id.cb_breath_stop);
        cb_action_time = findViewById(R.id.cb_action_time);
        cb_action_stop = findViewById(R.id.cb_action_stop);
        cb_sit_alarm = findViewById(R.id.cb_sit_alarm);
        cb_lay_time = findViewById(R.id.cb_lay_time);
        tv_hr_fast = findViewById(R.id.tv_hr_fast);
        tv_hr_fast.setOnClickListener(this);
        tv_fast_time = findViewById(R.id.tv_fast_time);
        tv_fast_time.setOnClickListener(this);
        tv_hr_slow = findViewById(R.id.tv_hr_slow);
        tv_hr_slow.setOnClickListener(this);
        tv_hr_slow_time = findViewById(R.id.tv_hr_slow_time);
        tv_hr_slow_time.setOnClickListener(this);
        tv_br_limit = findViewById(R.id.tv_br_limit);
        tv_br_limit.setOnClickListener(this);
        tv_br_limit_time = findViewById(R.id.tv_br_limit_time);
        tv_br_limit_time.setOnClickListener(this);
        tv_br_slow = findViewById(R.id.tv_br_slow);
        tv_br_slow.setOnClickListener(this);
        tv_br_slow_time = findViewById(R.id.tv_br_slow_time);
        tv_br_slow_time.setOnClickListener(this);
        tv_breath_stop_time = findViewById(R.id.tv_breath_stop_time);
        tv_breath_stop_time.setOnClickListener(this);
        tv_action_time = findViewById(R.id.tv_action_time);
        tv_action_time.setOnClickListener(this);
        tv_action_stop = findViewById(R.id.tv_action_stop);
        tv_action_stop.setOnClickListener(this);
        tv_lay_time = findViewById(R.id.tv_lay_time);
        tv_lay_time.setOnClickListener(this);
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().SLEEPACE_GET)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyNo", getIntent().getStringExtra("elderlyNo"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看警报信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                               if(json.getInt("onbedFlag")==0){
                                   cb_lay_time.setChecked(false);
                               } else {
                                   cb_lay_time.setChecked(true);
                               }
                                tv_breath_stop_time.setText(json.getString("breathPauseDuration"));
                                if(json.getInt("nobodyMoveFlag")==0){
                                    cb_action_stop.setChecked(false);
                                } else {
                                    cb_action_stop.setChecked(true);
                                }
                                br_slow_time = json.getString("breathRateSlowDuration");
                                tv_br_slow_time.setText(minutetoString(br_slow_time));
                                if(json.getInt("bodyMoveFlag")==0){
                                    cb_action_time.setChecked(false);
                                } else {
                                    cb_action_time.setChecked(true);
                                }
                                fast_time = json.getString("heartRateFastDuration");
                                tv_hr_fast.setText(minutetoString(fast_time));
                                if(json.getInt("breathPauseFlag")==0){
                                    cb_breath_stop.setChecked(false);
                                } else {
                                    cb_breath_stop.setChecked(true);
                                }
                                if(json.getInt("heartRateSlowFlag")==0){
                                    cb_hr_slow.setChecked(false);
                                } else {
                                    cb_hr_slow.setChecked(true);
                                }
                                if(json.getInt("situpFlag")==0){
                                    cb_sit_alarm.setChecked(false);
                                } else {
                                    cb_sit_alarm.setChecked(true);
                                }
                                tv_action_time.setText(json.getString("bodyMoveDuration"));
                                tv_sleep_time.setText(json.getString("leftBedStartHour")+":"+json.getString("leftBedStartMinute"));
                                leftBedStartHour = json.getInt("leftBedStartHour");
                                leftBedEndHour = json.getInt("leftBedEndHour");
                                leftBedStartMinute = json.getInt("leftBedStartMinute");
                                leftBedEndMinute = json.getInt("leftBedEndMinute");
                                tv_wake_time.setText(json.getString("leftBedEndHour")+":"+json.getString("leftBedEndMinute"));
                                if(json.getInt("leftBedFlag")==0){
                                    cb_rest.setChecked(false);
                                } else {
                                    cb_rest.setChecked(true);
                                }
                                tv_hr_fast.setText(json.getString("maxHeartRate"));
                                rest_time = json.getString("leftBedDuration");
                                tv_rest.setText(minutetoString(rest_time));
                                if(json.getInt("breathRateFastFlag")==0){
                                    cb_breath_fast.setChecked(false);
                                } else {
                                    cb_breath_fast.setChecked(true);
                                }
                                br_fast_time = json.getString("breathRateFastDuration");
                                tv_br_limit_time.setText(minutetoString(br_fast_time));
                                tv_br_slow.setText(json.getString("minBreathRate"));
                                lay_time = json.getString("onbedDuration");
                                tv_lay_time.setText(minutetoString(lay_time));
                                if(json.getInt("heartRateFastFlag")==0){
                                    cb_hr_fast.setChecked(false);
                                } else {
                                    cb_hr_fast.setChecked(true);
                                }
                                tv_hr_slow.setText(json.getString("minHeartRate"));
                                hr_slow_time = json.getString("heartRateSlowDuration");
                                tv_hr_slow_time.setText(minutetoString(hr_slow_time));
                                if(json.getInt("breathRateSlowFlag")==0){
                                    cb_breath_slow.setChecked(false);
                                } else {
                                    cb_breath_slow.setChecked(true);
                                }
                                tv_br_limit.setText(json.getString("maxBreathRate"));
                                tv_action_stop.setText(json.getString("nobodyMoveDuration"));
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_sleep_time:
                time_type = 1;
                pvTime.show();
                break;
            case R.id.tv_wake_time:
                time_type = 2;
                pvTime.show();
                break;
            case R.id.tv_rest:
                showPop1(0);
                break;
            case R.id.tv_hr_fast:
                showPop1(1);
                break;
            case R.id.tv_fast_time:
                showPop1(2);
                break;
            case R.id.tv_hr_slow:
                showPop1(3);
                break;
            case R.id.tv_hr_slow_time:
                showPop1(4);
                break;
            case R.id.tv_br_limit:
                showPop1(5);
                break;
            case R.id.tv_br_limit_time:
                showPop1(6);
                break;
            case R.id.tv_br_slow:
                showPop1(7);
                break;
            case R.id.tv_br_slow_time:
                showPop1(8);
                break;
            case R.id.tv_breath_stop_time:
                showPop1(9);
                break;
            case R.id.tv_action_time:
                showPop1(10);
                break;
            case R.id.tv_action_stop:
                showPop1(11);
                break;
            case R.id.tv_lay_time:
                showPop1(12);
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }

    public void save() {
        showProgressBar();
        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyNo", getIntent().getStringExtra("elderlyNo"));
        httpParams.put("deviceId", getIntent().getStringExtra("deviceId"));
        httpParams.put("bodyMoveDuration", tv_action_time.getText().toString());
        httpParams.put("bodyMoveFlag", cb_action_time.isChecked() ? 1 : 0);
        httpParams.put("breathPauseDuration", tv_breath_stop_time.getText().toString());
        httpParams.put("breathPauseFlag", cb_breath_stop.isChecked() ? 1 : 0);
        httpParams.put("breathRateFastDuration", br_fast_time);
        httpParams.put("breathRateFastFlag", cb_breath_fast.isChecked() ? 1 : 0);
        httpParams.put("breathRateSlowDuration", br_slow_time);
        httpParams.put("breathRateSlowFlag", cb_breath_slow.isChecked() ? 1 : 0);
        httpParams.put("heartRateFastDuration", fast_time);
        httpParams.put("heartRateFastFlag", cb_hr_fast.isChecked() ? 1 : 0);
        httpParams.put("heartRateSlowDuration", hr_slow_time);
        httpParams.put("heartRateSlowFlag", cb_hr_slow.isChecked() ? 1 : 0);
        httpParams.put("leftBedDuration", rest_time);
        httpParams.put("leftBedFlag", cb_rest.isChecked() ? 1 : 0);
        httpParams.put("leftBedStartHour", leftBedStartHour);
        httpParams.put("leftBedStartMinute", leftBedStartMinute);
        httpParams.put("leftBedEndHour", leftBedEndHour);
        httpParams.put("leftBedEndMinute", leftBedEndMinute);
        httpParams.put("maxBreathRate", tv_br_limit.getText().toString());
        httpParams.put("minBreathRate", tv_br_slow.getText().toString());
        httpParams.put("maxHeartRate", tv_hr_fast.getText().toString());
        httpParams.put("minHeartRate", tv_hr_slow.getText().toString());
        httpParams.put("nobodyMoveDuration", action_stop_time);
        httpParams.put("nobodyMoveFlag", cb_action_stop.isChecked() ? 1 : 0);
        httpParams.put("onbedDuration", lay_time);
        httpParams.put("onbedFlag", cb_lay_time.isChecked() ? 1 : 0);
        httpParams.put("situpFlag", cb_sit_alarm.isChecked() ? 1 : 0);
        Log.d("save: ", httpParams.toString());
        OkGo.<String>post(Urls.getInstance().SLEEPACE_UPDATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("床垫报警设置", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);

                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });

    }

    List<String> list;

    public void showPop1(int type) {
        if (organName.size() > 0) {
            organName.clear();
        }
        if (type == 0) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_rest));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 1) {
            organName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_fast)));
        }
        if (type == 2) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_fast_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 3) {
            organName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_slow)));

        }
        if (type == 4) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_slow_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 5) {
            organName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sleepace_br_limit)));
        }
        if (type == 6) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_slow_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 7) {
            organName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sleepace_br_slow)));
        }
        if (type == 8) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_hr_slow_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 9) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_breath_stop_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }
        if (type == 10) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_action_time));
            for (String s : list) {
                organName.add(changeMinute(s));
            }
        }
        if (type == 11) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_action_stop));
            for (String s : list) {
                organName.add(changeMinute(s));
            }
        }
        if (type == 12) {
            list = Arrays.asList(getResources().getStringArray(R.array.sleepace_lay_time));
            for (String s : list) {
                organName.add(minutetoString(s));
            }
        }

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (type == 0) {
                    rest_time = list.get(options1);
                    Integer minute = Integer.parseInt(rest_time);
                    tv_rest.setText(String.valueOf(minute / 60));
                } else if (type == 1) {
                    tv_hr_fast.setText(organName.get(options1));
                } else if (type == 2) {
                    fast_time = list.get(options1);
                    Integer minute = Integer.parseInt(fast_time);
                    tv_fast_time.setText(String.valueOf(minute / 60));
                } else if (type == 3) {
                    tv_hr_slow.setText(organName.get(options1));
                } else if (type == 4) {
                    hr_slow_time = list.get(options1);
                    int minute = Integer.parseInt(hr_slow_time);
                    tv_hr_slow_time.setText(String.valueOf(minute / 60));
                } else if (type == 5) {
                    tv_br_limit.setText(organName.get(options1));
                } else if (type == 6) {
                    br_fast_time = list.get(options1);
                    Integer minute = Integer.parseInt(br_fast_time);
                    tv_br_limit_time.setText(String.valueOf(minute / 60));
                } else if (type == 7) {
                    tv_br_slow.setText(organName.get(options1));
                } else if (type == 8) {
                    br_slow_time = list.get(options1);
                    Integer minute = Integer.parseInt(br_slow_time);
                    tv_br_slow_time.setText(String.valueOf(minute / 60));
                } else if (type == 9) {
                    br_stop_time = list.get(options1);
                    tv_breath_stop_time.setText(br_stop_time);
                } else if (type == 10) {
                    action_time = list.get(options1);
                    tv_action_time.setText(action_time);
                } else if (type == 11) {
                    action_stop_time = list.get(options1);
                    tv_action_stop.setText(action_stop_time);
                } else if (type == 12) {
                    lay_time = list.get(options1);
                    Integer minute = Integer.parseInt(lay_time);
                    tv_lay_time.setText(String.valueOf(minute / 60));
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }

    public String minutetoString(String time) {
        Integer t = Integer.parseInt(time);
        StringBuilder sb = new StringBuilder();
        if (t >= 3600) {
            sb.append(t / 3600).append("小时");
            if (t % 3600 != 0)
                sb.append(t % 3600 / 60).append("分钟");
            if (t % 3600 % 60 != 0)
                sb.append(t % 3600 % 60).append("秒");
        } else if (t >= 60) {
            sb.append(t / 60).append("分钟");
            if (t % 60 != 0)
                sb.append(t % 60).append("秒");
        } else if (t > 0) {
            sb.append(t % 60).append("秒");
        } else if (t == 0) {
            sb.append("立即");
        }
        return sb.toString();
    }

    public String changeMinute(String time) {
        Integer t = Integer.parseInt(time);
        StringBuilder sb = new StringBuilder();
        if (t >= 60) {
            sb.append(t / 60).append("小时");
            if (t % 60 != 0)
                sb.append(t % 60).append("分钟");
        } else if (t > 0) {
            sb.append(t % 60).append("分钟");
        }
        return sb.toString();
    }


    private void initPickerView() {
        sdf = new SimpleDateFormat("HH:mm");
        boolean[] type = {false, false, false, true, true, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_sleep_time.setText(sdf.format(date));
                    leftBedStartHour = date.getHours();
                    leftBedStartMinute = date.getMinutes();

                }
                if (time_type == 2) {
                    tv_wake_time.setText(sdf.format(date));
                    leftBedEndHour = date.getHours();
                    leftBedEndMinute = date.getSeconds();
                }
            }
        }).setType(type).build();
    }
}