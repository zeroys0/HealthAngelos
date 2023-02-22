package net.leelink.healthangelos.activity.ZW011;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChangeBindPhoneActivity;
import net.leelink.healthangelos.activity.ChangeNickNameActivity;
import net.leelink.healthangelos.activity.ContactPersonActivity;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.HealthDataActivity;
import net.leelink.healthangelos.activity.LocationActivity;
import net.leelink.healthangelos.activity.PromptActivity;
import net.leelink.healthangelos.activity.VoiceBroadcastActivity;
import net.leelink.healthangelos.adapter.ChooseAdapter;
import net.leelink.healthangelos.adapter.OnDeviceChooseListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ZW011MainActivity extends BaseActivity implements View.OnClickListener, OnDeviceChooseListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_name, tv_phone, tv_scene, tv_call, tv_imei, tv_sos, tv_step_target, tv_low_power, tv_upload_locate, tv_upload_step, tv_upload_heart_rate, tv_upload_bo;
    private TextView tv_heart_rate, tv_blood_pressure, tv_step_number, tv_sleep_data, tv_blood_oxygen;
    private RecyclerView user_list;
    private SwitchCompat cb_gps, cb_sos_sms, cb_call, cb_sleep, cb_fall;
    PopupWindow pop;
    private ChooseAdapter chooseAdapter;
    List<String> list = new ArrayList<>();
    private int type = 0;
    private ImageView img_head;
    private int position;
    public static int SCENE_MODE = 1;
    public String[] scene_mode = {"P1_VIBRATE", "P2_RINGER", "P3_VIBRATE_RINGER", "P4_VIBRATE_SILENT"};
    public String[] answer_mode = {"M0_OFF", "M1_AUTO", "M2_AUTO_30", "M3_AUTO_15"};
    public static int CALL_MODE = 2;
    public static int SOS_COUNT = 3;
    public static int STEP_TARGET = 4;
    public static int LOW_POWER_REMIND = 5;
    public String imei;
    private Button rl_unbind;
    private JSONObject json_setting;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zw011_main2);
        context = this;
        imei = getIntent().getStringExtra("imei");
        if (imei == null) {
            Toast.makeText(context, "设备已注销", Toast.LENGTH_SHORT).show();
            finish();
        }
        init();
        createProgressBar(context);
        initView();
        EventBus.getDefault().register(this);
    }


    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        if (getIntent().getStringExtra("img") != null && !getIntent().getStringExtra("img").equals("null")) {
            Glide.with(context).load(Urls.getInstance().IMG_URL + getIntent().getStringExtra("img")).into(img_head);
        }
        RelativeLayout rl_nick_name = findViewById(R.id.rl_nick_name);
        rl_nick_name.setOnClickListener(this);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        RelativeLayout rl_wotch_phone = findViewById(R.id.rl_wotch_phone);
        rl_wotch_phone.setOnClickListener(this);
        RelativeLayout rl_location = findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        RelativeLayout rl_elect_fence = findViewById(R.id.rl_elect_fence);
        rl_elect_fence.setOnClickListener(this);
        RelativeLayout rl_notice = findViewById(R.id.rl_notice);
        rl_notice.setOnClickListener(this);
        RelativeLayout rl_broadcast = findViewById(R.id.rl_broadcast);
        rl_broadcast.setOnClickListener(this);
        RelativeLayout rl_family = findViewById(R.id.rl_family);
        rl_family.setOnClickListener(this);
        RelativeLayout rl_scene = findViewById(R.id.rl_scene);
        rl_scene.setOnClickListener(this);
        RelativeLayout rl_call = findViewById(R.id.rl_call);
        rl_call.setOnClickListener(this);
        RelativeLayout rl_sos = findViewById(R.id.rl_sos);
        rl_sos.setOnClickListener(this);
        RelativeLayout rl_step_target = findViewById(R.id.rl_step_target);
        rl_step_target.setOnClickListener(this);
        RelativeLayout rl_low_power_remind = findViewById(R.id.rl_low_power_remind);
        rl_low_power_remind.setOnClickListener(this);
        RelativeLayout rl_upload_locate = findViewById(R.id.rl_upload_locate);
        rl_upload_locate.setOnClickListener(this);
        RelativeLayout rl_upload_step = findViewById(R.id.rl_upload_step);
        rl_upload_step.setOnClickListener(this);
        RelativeLayout rl_upload_heart_rate = findViewById(R.id.rl_upload_heart_rate);
        rl_upload_heart_rate.setOnClickListener(this);
        RelativeLayout rl_upload_bo = findViewById(R.id.rl_upload_bo);
        rl_upload_bo.setOnClickListener(this);
        RelativeLayout rl_reboot = findViewById(R.id.rl_reboot);
        rl_reboot.setOnClickListener(this);
        RelativeLayout rl_shutdown = findViewById(R.id.rl_shutdown);
        rl_shutdown.setOnClickListener(this);
        RelativeLayout rl_measure_hr = findViewById(R.id.rl_measure_hr);
        rl_measure_hr.setOnClickListener(this);
        RelativeLayout rl_measure_bp = findViewById(R.id.rl_measure_bp);
        rl_measure_bp.setOnClickListener(this);
        RelativeLayout rl_search = findViewById(R.id.rl_search);
        rl_search.setOnClickListener(this);
        RelativeLayout rl_measure_bo = findViewById(R.id.rl_measure_bo);
        rl_measure_bo.setOnClickListener(this);
        RelativeLayout rl_sleep_time = findViewById(R.id.rl_sleep_time);
        rl_sleep_time.setOnClickListener(this);
        RelativeLayout rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        RelativeLayout rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        RelativeLayout rl_blood_oxygen = findViewById(R.id.rl_blood_oxygen);
        rl_blood_oxygen.setOnClickListener(this);
        RelativeLayout rl_step_number = findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        RelativeLayout rl_sleep_data = findViewById(R.id.rl_sleep_data);
        rl_sleep_data.setOnClickListener(this);

        tv_scene = findViewById(R.id.tv_scene);
        tv_call = findViewById(R.id.tv_call);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText("IMEI:" + imei);
        tv_sos = findViewById(R.id.tv_sos);
        tv_step_target = findViewById(R.id.tv_step_target);
        tv_low_power = findViewById(R.id.tv_low_power);
        rl_unbind = findViewById(R.id.rl_unbind);
        rl_unbind.setOnClickListener(this);
        tv_upload_locate = findViewById(R.id.tv_upload_locate);
        tv_upload_step = findViewById(R.id.tv_upload_step);
        tv_upload_heart_rate = findViewById(R.id.tv_upload_heartrate);
        tv_upload_bo = findViewById(R.id.tv_upload_bo);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_blood_pressure = findViewById(R.id.tv_blood_pressure);
        tv_step_number = findViewById(R.id.tv_step_number);
        tv_sleep_data = findViewById(R.id.tv_sleep_data);
        tv_blood_oxygen = findViewById(R.id.tv_blood_oxygen);
        initCheck();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SettingBean settingBean) {
        int i = 30;
        if(settingBean.getFreq().equals("5分钟")){
            i = 5;
        }else if(settingBean.getFreq().equals("15分钟")){
            i = 15;
        }else if(settingBean.getFreq().equals("30分钟")){
            i = 30;
        }else if(settingBean.getFreq().equals("1小时")){
            i = 60;
        }else if(settingBean.getFreq().equals("2小时")){
            i = 120;
        }else if(settingBean.getFreq().equals("3小时")){
            i = 180;
        }
        switch (settingBean.getType()) {
            case 1:
                if (settingBean.cb_check) {
                    tv_upload_locate.setText(settingBean.getFreq());
                } else {
                    tv_upload_locate.setText("关闭");
                }
                try {
                    if(settingBean.cb_check) {
                        json_setting.put("arg17_109_on_off_trace", 1);
                    }else {
                        json_setting.put("arg17_109_on_off_trace", 0);
                    }

                    json_setting.put("arg14_106_freq_location",i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                if (settingBean.cb_check) {
                    tv_upload_step.setText(settingBean.getFreq());
                } else {
                    tv_upload_step.setText("关闭");
                }
                try {
                    if(settingBean.cb_check) {
                        json_setting.put("arg18_110_on_off_steps", 1);
                    }else {
                        json_setting.put("arg18_110_on_off_steps", 0);
                    }
                    json_setting.put("arg15_107_freq_steps",i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                if (settingBean.cb_check) {
                    tv_upload_heart_rate.setText(settingBean.getFreq());
                } else {
                    tv_upload_heart_rate.setText("关闭");
                }
                try {
                    if(settingBean.cb_check) {
                        json_setting.put("arg20_112_on_off_hr", 1);
                    }else {
                        json_setting.put("arg20_112_on_off_hr", 0);
                    }
                    json_setting.put("arg43_143_freq_hr",i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                if (settingBean.cb_check) {
                    tv_upload_bo.setText(settingBean.getFreq());
                } else {
                    tv_upload_bo.setText("关闭");
                }
                try {
                    if(settingBean.cb_check) {
                        json_setting.put("arg22_114_on_off_spo", 1);
                    }else {
                        json_setting.put("arg22_114_on_off_spo", 0);
                    }
                    json_setting.put("arg26_118_freq_spo",i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void initView() {
        OkGo.<String>get(Urls.getInstance().BIND_DETAILS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_name.setText(json.getString("nickName"));
                                if (json.isNull("nickName")) {
                                    tv_name.setText("");
                                }
                                tv_phone.setText(json.getString("bindphone"));
                                if (json.isNull("bindphone")) {
                                    tv_phone.setText("");
                                }
                                tv_heart_rate.setText(json.getString("heartRateTimes") + "次/分钟");
                                if (json.isNull("heartRateTimes")) {
                                    tv_heart_rate.setText("");
                                }
                                if (!json.isNull("bloodPressureDataVo")) {
                                    String s = "";
                                    JSONObject j = json.getJSONObject("bloodPressureDataVo");
                                    s = j.getString("diastolic") + "/" + j.getString("systolic");
                                    tv_blood_pressure.setText(s);
                                } else {
                                    tv_blood_pressure.setText("");
                                }
                                tv_step_number.setText(json.getString("stepTimes") + "步");
                                if (json.isNull("stepTimes")) {
                                    tv_step_number.setText("");
                                }
                                tv_sleep_data.setText(json.getString("sleepTimes"));
                                if (json.isNull("sleepTimes")) {
                                    tv_sleep_data.setText("");
                                }
                                if (!json.isNull("bloodOxygenTimes")) {
                                    tv_blood_oxygen.setText(json.getString("bloodOxygenTimes"));
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });

        OkGo.<String>get(Urls.getInstance().ZW_SETTING)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取腕表设置属性", json.toString());
                            if (json.getInt("status") == 200) {
                                json_setting = json.getJSONObject("data");
                                switch (json_setting.getInt("arg13_105_profile")) {
                                    case 1:
                                        tv_scene.setText("铃声");
                                        break;
                                    case 2:
                                        tv_scene.setText("震动");
                                        break;
                                    case 3:
                                        tv_scene.setText("铃声+震动");
                                        break;
                                    case 4:
                                        tv_scene.setText("无声无震");
                                        break;
                                }
                                switch (json_setting.getInt("arg33_130_answer_mode")) {
                                    case 0:
                                        tv_call.setText("禁止自动接听");
                                        break;
                                    case 1:
                                        tv_call.setText("立刻接听");
                                        break;
                                    case 2:
                                        tv_call.setText("30秒后自动接听");
                                        break;
                                    case 3:
                                        tv_call.setText("15秒后自动接听");
                                        break;
                                }
                                if (json_setting.getInt("arg34_131_SOS_count") == 9) {
                                    tv_sos.setText("循环播报");
                                } else {
                                    tv_sos.setText(json_setting.getInt("arg34_131_SOS_count") + "次");
                                }
                                tv_step_target.setText(json_setting.getString("arg37_135_steps_target") + "步");
                                if (json_setting.getInt("arg19_111_on_off_gps") == 0) {
                                    cb_gps.setChecked(false);
                                } else {
                                    cb_gps.setChecked(true);
                                }
                                if (json_setting.getInt("arg31_125_on_off_SOS_SMS") == 0) {
                                    cb_sos_sms.setChecked(false);
                                } else {
                                    cb_sos_sms.setChecked(true);
                                }
                                if (json_setting.getInt("arg32_128_on_off_call") == 0) {
                                    cb_call.setChecked(false);
                                } else {
                                    cb_call.setChecked(true);
                                }
                                if (json_setting.getInt("arg23_115_on_off_sleep") == 0) {
                                    cb_sleep.setChecked(false);
                                } else {
                                    cb_sleep.setChecked(true);
                                }
                                if (json_setting.getInt("arg24_116_on_off_fall") == 0) {
                                    cb_fall.setChecked(false);
                                } else {
                                    cb_fall.setChecked(true);
                                }
                                if (json_setting.getInt("arg17_109_on_off_trace") == 0) {
                                    tv_upload_locate.setText("关闭");
                                } else {
                                    tv_upload_locate.setText(json_setting.getString("arg14_106_freq_location") + "分钟/次");
                                }
                                if (json_setting.getInt("arg18_110_on_off_steps") == 0) {
                                    tv_upload_step.setText("关闭");
                                } else {
                                    tv_upload_step.setText(json_setting.getString("arg15_107_freq_steps") + "分钟/次");
                                }
                                if (json_setting.getInt("arg20_112_on_off_hr") == 0) {
                                    tv_upload_heart_rate.setText("关闭");
                                } else {
                                    tv_upload_heart_rate.setText(json_setting.getString("arg43_143_freq_hr") + "分钟/次");
                                }
                                if (json_setting.getInt("arg22_114_on_off_spo") == 0) {
                                    tv_upload_bo.setText("关闭");
                                } else {
                                    tv_upload_bo.setText(json_setting.getString("arg26_118_freq_spo") + "分钟/次");
                                }
                                tv_low_power.setText(json_setting.getString("arg16_108_limit_battery"));
                                check = true;
                            } else if (json_setting.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json_setting.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void initCheck() {
        cb_gps = findViewById(R.id.cb_gps);
        cb_gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("ZW111_GPS", isChecked);
            }
        });
        cb_sos_sms = findViewById(R.id.cb_sos_sms);
        cb_sos_sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("ZW125_SOS_SMS", isChecked);
            }
        });
        cb_call = findViewById(R.id.cb_call);
        cb_call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("ZW128_CALL", isChecked);
            }
        });
        cb_sleep = findViewById(R.id.cb_sleep);
        cb_sleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("ZW115_SLEEP", isChecked);
            }
        });
        cb_fall = findViewById(R.id.cb_fall);
        cb_fall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("ZW116_FALL", isChecked);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            /**
             * 设备别称
             */
            case R.id.rl_nick_name:
                Intent intent = new Intent(this, ChangeNickNameActivity.class);
                intent.putExtra("imei", imei);
                startActivityForResult(intent, 1);
                break;
            /**
             * 绑定手机号
             */
            case R.id.rl_wotch_phone:
                Intent intent1 = new Intent(this, ChangeBindPhoneActivity.class);
                intent1.putExtra("imei", imei);
                startActivityForResult(intent1, 2);
                break;
            /**
             * 跳转至定位
             */
            case R.id.rl_location:
                Intent intent2 = new Intent(this, LocationActivity.class);
                intent2.putExtra("imei", imei);
                startActivity(intent2);
                break;
            /**
             * 电子围栏
             */
            case R.id.rl_elect_fence:
                Intent intent3 = new Intent(this, ElectFenceActivity.class);
                startActivity(intent3);
                break;
            /**
             * 定时提醒
             */
            case R.id.rl_notice:
                Intent intent4 = new Intent(this, PromptActivity.class);
                startActivity(intent4);
                break;
            /**
             * 语音播报
             */
            case R.id.rl_broadcast:
                Intent intent5 = new Intent(this, VoiceBroadcastActivity.class);
                startActivity(intent5);
                break;
            /**
             * 亲人号码
             */
            case R.id.rl_family:
                Intent intent6 = new Intent(this, ContactPersonActivity.class);
                intent6.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent6);
                break;
            /**
             * 情景模式
             */
            case R.id.rl_scene:
                backgroundAlpha(0.5f);
                showRun(SCENE_MODE);
                break;
            /**
             * 来电接听模式
             */
            case R.id.rl_call:
                backgroundAlpha(0.5f);
                showRun(CALL_MODE);
                break;
            /**
             * 设置sos轮播次数
             */
            case R.id.rl_sos:
                backgroundAlpha(0.5f);
                showRun(SOS_COUNT);
                break;
            /**
             * 设置运动目标
             */
            case R.id.rl_step_target:
                backgroundAlpha(0.5f);
                showRun(STEP_TARGET);
                break;
            /**
             * 低电量提醒
             */
            case R.id.rl_low_power_remind:
                backgroundAlpha(0.5f);
                showRun(LOW_POWER_REMIND);
                break;
            /**
             * 位置上报频率
             */
            case R.id.rl_upload_locate:
                Intent intent7 = new Intent(this, UploadFreqActivity.class);
                intent7.putExtra("type", 1);
                intent7.putExtra("imei", imei);
                try {
                    intent7.putExtra("cb", json_setting.getInt("arg17_109_on_off_trace"));
                    intent7.putExtra("fre", json_setting.getString("arg14_106_freq_location"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent7);
                break;
            /**
             * 计步上报频率
             */
            case R.id.rl_upload_step:
                Intent intent8 = new Intent(this, UploadFreqActivity.class);
                intent8.putExtra("type", 2);
                intent8.putExtra("imei", imei);
                try {
                    intent8.putExtra("cb", json_setting.getInt("arg18_110_on_off_steps"));
                    intent8.putExtra("fre", json_setting.getString("arg15_107_freq_steps"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent8);
                break;
            /**
             * 心率上报频率
             */
            case R.id.rl_upload_heart_rate:
                Intent intent9 = new Intent(this, UploadFreqActivity.class);
                intent9.putExtra("type", 3);
                intent9.putExtra("imei", imei);
                try {
                    intent9.putExtra("cb", json_setting.getInt("arg20_112_on_off_hr"));
                    intent9.putExtra("fre", json_setting.getString("arg43_143_freq_hr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent9);
                break;
            /**
             * 血氧上报频率
             */
            case R.id.rl_upload_bo:
                Intent intent10 = new Intent(this, UploadFreqActivity.class);
                intent10.putExtra("type", 4);
                intent10.putExtra("imei", imei);
                try {
                    intent10.putExtra("cb", json_setting.getInt("arg22_114_on_off_spo"));
                    intent10.putExtra("fre", json_setting.getString("arg26_118_freq_spo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent10);
                break;
            /**
             * 重启设备
             */
            case R.id.rl_reboot:
                Intent intent11 = new Intent(this, SendMsgToZW011Activity.class);
                intent11.putExtra("type", "reboot");
                intent11.putExtra("imei", imei);
                startActivity(intent11);
                break;
            /**
             * 设备关机
             */
            case R.id.rl_shutdown:
                Intent intent12 = new Intent(this, SendMsgToZW011Activity.class);
                intent12.putExtra("type", "shutdown");
                intent12.putExtra("imei", imei);
                startActivity(intent12);
                break;
            /**
             * 测试心率
             */
            case R.id.rl_measure_hr:
                Intent intent13 = new Intent(this, SendMsgToZW011Activity.class);
                intent13.putExtra("type", "measure_hr");
                intent13.putExtra("imei", imei);
                startActivity(intent13);
                break;
            /**
             * 测试血压
             */
            case R.id.rl_measure_bp:
                Intent intent14 = new Intent(this, SendMsgToZW011Activity.class);
                intent14.putExtra("type", "measure_bp");
                intent14.putExtra("imei", imei);
                startActivity(intent14);
                break;
            /**
             * 查找设备
             */
            case R.id.rl_search:
                Intent intent15 = new Intent(this, SendMsgToZW011Activity.class);
                intent15.putExtra("type", "search");
                intent15.putExtra("imei", imei);
                startActivity(intent15);
                break;
            /**
             * 测量血氧
             */
            case R.id.rl_measure_bo:
                Intent intent16 = new Intent(this, SendMsgToZW011Activity.class);
                intent16.putExtra("type", "measure_bo");
                intent16.putExtra("imei", imei);
                startActivity(intent16);
                break;
            /**
             * 睡眠监测时段
             */
            case R.id.rl_sleep_time:
                Intent intent17 = new Intent(this, SleepTimeMonitorActivity.class);
                intent17.putExtra("imei", imei);
                startActivity(intent17);
                break;
            /**
             * 查看心率
             */
            case R.id.rl_heart_rate:
                Intent intent18 = new Intent(this, HealthDataActivity.class);
                intent18.putExtra("type", 1);
                startActivity(intent18);
                break;
            /**
             * 查看血压
             */
            case R.id.rl_blood_pressure:
                Intent intent19 = new Intent(this, HealthDataActivity.class);
                intent19.putExtra("type", 0);
                startActivity(intent19);
                break;

            /**
             * 查看血氧
             */
            case R.id.rl_blood_oxygen:
                Intent intent20 = new Intent(this, HealthDataActivity.class);
                intent20.putExtra("type", 2);
                startActivity(intent20);
                break;
            /**
             * 步数数据
             */
            case R.id.rl_step_number:
                Intent intent21 = new Intent(this, HealthDataActivity.class);
                intent21.putExtra("type", 4);
                startActivity(intent21);
                break;
            /**
             * 查看睡眠数据
             */
            case R.id.rl_sleep_data:
                Intent intent22 = new Intent(this, HealthDataActivity.class);
                intent22.putExtra("type", 12);
                startActivity(intent22);
                break;

            /**
             * 解除绑定
             */
            case R.id.rl_unbind:
                unbind();
                break;
            case R.id.tv_confirm:
                if (type == SCENE_MODE) {
                    setSceneMode();
                }
                if (type == CALL_MODE) {
                    setCallMode();
                }
                if (type == SOS_COUNT) {
                    setSosCount();
                }
                if (type == STEP_TARGET) {
                    setStepTarget();
                }
                if (type == LOW_POWER_REMIND) {
                    setBatteryLimit();
                }
                pop.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                tv_name.setText(data.getStringExtra("nickName"));
            }
            if (requestCode == 2) {
                tv_phone.setText(data.getStringExtra("bindPhone"));
            }
        }
    }

    /**
     * 设置情景模式
     */
    public void setSceneMode() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().PROFILE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("profile", scene_mode[position])
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置情景模式", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_scene.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    /**
     * 设置来电接听模式
     */
    public void setCallMode() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ANSWERMODE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("mode", answer_mode[position])
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置来电接听模式", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_call.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    /**
     * 设置SOS频率
     */
    public void setSosCount() {
        int count = position + 1;
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().SOSCOUNT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("count", count)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置sos轮播次数", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_sos.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    /**
     * 设置运动目标
     */
    public void setStepTarget() {
        int count = (position + 1) * 1000;
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ZW_RUNTARGET)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("target", count)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置运动目标", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_step_target.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    /**
     * 设置低电量提醒
     */
    public void setBatteryLimit() {
        int count = 10 + position * 5;
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().BATTERYLIMIT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("battery", count)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置低电量提醒", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_low_power.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    /**
     * 设置各项开关
     */
    public void setStepTarget(String code, boolean b) {
        String on = "";
        if (b) {
            on = "ON";
        } else {
            on = "OFF";
        }
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ZW_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("code", code)
                .params("onOff", on)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置开关", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BIND + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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


    public void showRun(int type) {
        View popView = getLayoutInflater().inflate(R.layout.popu_target, null);

        user_list = popView.findViewById(R.id.user_list);
        TextView tv_title = popView.findViewById(R.id.tv_title);

        String[] run = {};
        switch (type) {
            case 1:
                run = getResources().getStringArray(R.array.scene_mode);
                tv_title.setText("情景模式");
                break;
            case 2:
                run = getResources().getStringArray(R.array.call_mode);
                tv_title.setText("来电接听模式");
                break;
            case 3:
                run = getResources().getStringArray(R.array.sos_count);
                tv_title.setText("SOS轮播次数");
                break;
            case 4:
                run = getResources().getStringArray(R.array.step_target);
                tv_title.setText("计步目标");
                break;
            case 5:
                run = getResources().getStringArray(R.array.low_power);
                tv_title.setText("低电量提醒");
                break;
            default:
                break;
        }
        TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        TextView tv_confirm = popView.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        list = Arrays.asList(run);
        chooseAdapter = new ChooseAdapter(list, context, ZW011MainActivity.this, type);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        user_list.setAdapter(chooseAdapter);
        user_list.setLayoutManager(layoutManager);
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, 600, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new ZW011MainActivity.poponDismissListener());

        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 100);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(View view, int type) {
        int position = user_list.getChildLayoutPosition(view);
        chooseAdapter.setChecked(position);
        this.type = type;
        this.position = position;
    }

    @Override
    public void onChooseClick(View view, int position) {

    }

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}