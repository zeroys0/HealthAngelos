package net.leelink.healthangelos.activity.sleepace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SleepaceMainActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_alarm_setting, rl_sleep_setting, rl_alarm_list, rl_sleep_report;
    private TextView tv_connect, tv_unbind, tv_heart_rate, tv_sensor_state, tv_breath, tv_number;
    private ImageView img_heart_rate, img_breath, img_action_number, img_sit, img_leave, img_sensor, img_alarm, img_sleep_setting;
    private String state = "";
    Timer timer;
    TimerTask task;
    String elderlyNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepace_main);
        context = this;
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new SleepaceMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 30000);
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_alarm_setting = findViewById(R.id.rl_alarm_setting);
        rl_alarm_setting.setOnClickListener(this);
        rl_sleep_setting = findViewById(R.id.rl_sleep_setting);
        rl_sleep_setting.setOnClickListener(this);
        rl_alarm_list = findViewById(R.id.rl_alarm_list);
        rl_alarm_list.setOnClickListener(this);
        rl_sleep_report = findViewById(R.id.rl_sleep_report);
        rl_sleep_report.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        TextView tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        tv_connect = findViewById(R.id.tv_connect);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        img_heart_rate = findViewById(R.id.img_heart_rate);
        img_breath = findViewById(R.id.img_breath);
        img_action_number = findViewById(R.id.img_action_number);
        img_sit = findViewById(R.id.img_sit);
        img_leave = findViewById(R.id.img_leave);
        img_sensor = findViewById(R.id.img_sensor);
        img_alarm = findViewById(R.id.img_alarm);
        img_sleep_setting = findViewById(R.id.img_sleep_setting);
        tv_sensor_state = findViewById(R.id.tv_sensor_state);
        tv_breath = findViewById(R.id.tv_breath);
        tv_number = findViewById(R.id.tv_number);
    }
    private String deviceId;

    public void initData() {
        OkGo.<String>get(Urls.getInstance().SLEEPACE_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询设备在线状态", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                deviceId = json.getString("deviceId");
                                if (json.getBoolean("connectionStatus")) {
                                    tv_connect.setText("在线");
                                    if (json.getInt("initStatus") == 1) {
                                        tv_connect.setText("设备初始化中");
                                        if (!state.equals(tv_connect.getText())) {
                                            setOffline();
                                        }
                                    } else {
                                        elderlyNo = json.getString("elderlyNo");
                                        if (!state.equals(tv_connect.getText())) {
                                            img_alarm.setImageResource(R.drawable.sleepace_alarm);
                                            img_sleep_setting.setImageResource(R.drawable.sleepace_sleep_setting);
                                        }
                                        if (json.getInt("sensorStatus") == 0) {
                                            if (!tv_sensor_state.getText().toString().equals("脱落")) {
                                                tv_sensor_state.setText("脱落");
                                                img_heart_rate.setImageResource(R.drawable.sleepace_hr_offline);
                                                img_breath.setImageResource(R.drawable.sleepace_breath_offline);
                                                img_action_number.setImageResource(R.drawable.sleepace_action_number_offline);
                                                img_sit.setImageResource(R.drawable.sleepace_sit_offline);
                                                img_leave.setImageResource(R.drawable.sleepace_leave_offline);
                                                img_sensor.setImageResource(R.drawable.sleepace_sensor_offline);
                                            }
                                        } else {
                                            if (!tv_sensor_state.getText().toString().equals("正常")) {
                                                tv_sensor_state.setText("正常");
                                                img_heart_rate.setImageResource(R.drawable.sleepace_hr);
                                                img_breath.setImageResource(R.drawable.sleepace_breath);
                                                img_action_number.setImageResource(R.drawable.sleepace_action_number);
                                                if (json.getInt("sitUp") == 1) {
                                                    img_sit.setImageResource(R.drawable.sleepace_sit);
                                                } else {
                                                    img_sit.setImageResource(R.drawable.sleepace_unsit);
                                                }
                                                if (json.getInt("sitUp") == 1) {
                                                    img_leave.setImageResource(R.drawable.sleepace_leave);
                                                } else {
                                                    img_leave.setImageResource(R.drawable.sleepace_unleave);
                                                }
                                                img_sensor.setImageResource(R.drawable.sleepace_sensor);
                                            }
                                            tv_heart_rate.setText(json.getString("heart"));
                                            tv_breath.setText(json.getString("breath"));
                                            tv_number.setText(json.getString("bodyMove"));
                                        }
                                    }
                                } else {
                                    tv_connect.setText("离线");
                                    tv_connect.setTextColor(getResources().getColor(R.color.red));
                                    if (!state.equals(tv_connect.getText())) {
                                        setOffline();
                                    }
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void setOffline() {
        tv_heart_rate.setText("--");
        img_heart_rate.setImageResource(R.drawable.sleepace_hr_offline);
        img_breath.setImageResource(R.drawable.sleepace_breath_offline);
        img_action_number.setImageResource(R.drawable.sleepace_action_number_offline);
        img_sit.setImageResource(R.drawable.sleepace_sit_offline);
        img_leave.setImageResource(R.drawable.sleepace_leave_offline);
        img_sensor.setImageResource(R.drawable.sleepace_sensor_offline);
        img_alarm.setImageResource(R.drawable.sleepace_alarm_offline);
        img_sleep_setting.setImageResource(R.drawable.sleepace_sleep_setting_offline);
        state = tv_connect.getText().toString();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_alarm_setting:
                Intent intent = new Intent(context, SleepaceSettingActivity.class);
                intent.putExtra("elderlyNo",elderlyNo);
                intent.putExtra("deviceId",deviceId);
                startActivity(intent);
                break;
            case R.id.rl_sleep_setting:
                Intent intent1 = new Intent(context, SleepaceSettingBActivity.class);
                intent1.putExtra("deviceId",deviceId);
                startActivity(intent1);
                break;
            case R.id.rl_alarm_list:
                Intent intent2 = new Intent(context, SleepaceAlarmListActivity.class);
                intent2.putExtra("deviceId",deviceId);
                intent2.putExtra("elderlyNo",elderlyNo);
                startActivity(intent2);
                break;
            case R.id.rl_sleep_report:
                Intent intent3 = new Intent(context, SleepaceReportActivity.class);
                intent3.putExtra("elderlyNo",elderlyNo);
                startActivity(intent3);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
        }
    }

    public void unbind() {
        OkGo.<String>delete(Urls.getInstance().SLEEPACE_UNBIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑床垫", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解除绑定成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    class MyTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //do something
            try {
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }
}