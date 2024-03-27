package net.leelink.healthangelos.activity.h008;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChangeBindPhoneActivity;
import net.leelink.healthangelos.activity.ChangeNickNameActivity;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.HealthDataActivity;
import net.leelink.healthangelos.activity.NeoLocationActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

public class H008MainActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private RelativeLayout rl_nick_name, rl_wotch_phone, rl_heart_rate, rl_blood_pressure, rl_step_number, rl_location, rl_elect_fence, rl_alarm_clock;
    private RelativeLayout rl_white_list, rl_family, rl_upload_locate, rl_upload_heart_rate, rl_check, rl_locate_time, rl_volume, rl_shutdown;
    private SwitchCompat cb_gps, cb_white_list, cb_red_key, cb_yellow_key;
    private TextView tv_step_number,tv_volume,tv_model, tv_locate, tv_last_locate, tv_last_update, tv_upload_locate, tv_heart_rate, tv_upload_heartrate, tv_blood_pressure, tv_device_name,tv_name, tv_phone,tv_imei;
    public String imei;
    private Button rl_unbind;
    private BatteryView battery;
    private ImageView img_connect, img_head;
    private JSONObject json_setting;
    private boolean check = false;
    private String ringlist,whitelist,sosNumber;
    public static  int GPS_PERIOD = 1;
    public static  int HEART_RATE_PERIOD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h008_main);
        context = this;
        createProgressBar(context);
        imei = getIntent().getStringExtra("imei");
        init();
        initData();
    }

    Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_model = findViewById(R.id.tv_model);
        tv_model.setText(getIntent().getStringExtra("model"));
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name"));
        tv_volume = findViewById(R.id.tv_volume);
        tv_step_number = findViewById(R.id.tv_step_number);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(imei);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_blood_pressure = findViewById(R.id.tv_blood_pressure);

        rl_unbind = findViewById(R.id.rl_unbind);
        rl_unbind.setOnClickListener(this);

        img_head = findViewById(R.id.img_head);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(context).load(Urls.getInstance().IMG_URL + getIntent().getStringExtra("img")).into(img_head);
            }
        }, 1000);
        rl_alarm_clock = findViewById(R.id.rl_alarm_clock);
        rl_alarm_clock.setOnClickListener(this);

        rl_nick_name = findViewById(R.id.rl_nick_name);
        rl_nick_name.setOnClickListener(this);
        rl_wotch_phone = findViewById(R.id.rl_wotch_phone);
        rl_wotch_phone.setOnClickListener(this);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        rl_step_number = findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        rl_family = findViewById(R.id.rl_family);
        rl_family.setOnClickListener(this);
        rl_check = findViewById(R.id.rl_check);
        rl_check.setOnClickListener(this);

        rl_location = findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        rl_elect_fence = findViewById(R.id.rl_elect_fence);
        rl_elect_fence.setOnClickListener(this);
        rl_white_list = findViewById(R.id.rl_white_list);
        rl_white_list.setOnClickListener(this);
        rl_upload_heart_rate = findViewById(R.id.rl_upload_heart_rate);
        rl_upload_heart_rate.setOnClickListener(this);
        rl_upload_locate = findViewById(R.id.rl_upload_locate);
        rl_upload_locate.setOnClickListener(this);
        rl_shutdown = findViewById(R.id.rl_shutdown);
        rl_shutdown.setOnClickListener(this);
        rl_volume = findViewById(R.id.rl_volume);
        rl_volume.setOnClickListener(this);

        tv_locate = findViewById(R.id.tv_locate);
        tv_last_locate = findViewById(R.id.tv_last_locate);
        tv_last_update = findViewById(R.id.tv_last_update);
        img_connect = findViewById(R.id.img_connect);
        tv_upload_locate = findViewById(R.id.tv_upload_locate);
        tv_upload_heartrate = findViewById(R.id.tv_upload_heartrate);
        battery = findViewById(R.id.battery);
        battery.setPower(0);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);

        initCheck();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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
             * 步数数据
             */
            case R.id.rl_step_number:
                Intent intent21 = new Intent(this, HealthDataActivity.class);
                intent21.putExtra("type", 4);
                startActivity(intent21);
                break;
            /**
             * 跳转至定位
             */
            case R.id.rl_location:
                Intent intent2 = new Intent(this, NeoLocationActivity.class);
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
             * 闹钟配置
             */
            case R.id.rl_alarm_clock:
                Intent intent10 = new Intent(this, H008AlarmClockActivity.class);
                intent10.putExtra("data",ringlist);
                intent10.putExtra("imei",imei);
                startActivity(intent10);
                break;
            /**
             * 白名单
             */
            case R.id.rl_white_list:
                Intent intent5 = new Intent(this,H008WhiteListActivity.class);
                intent5.putExtra("data",whitelist);
                intent5.putExtra("imei",imei);
                startActivity(intent5);
                break;
            /**
             * 亲属号码
             */
            case R.id.rl_family:
                Intent intent4= new Intent(context,H008RlativeActivity.class);
                intent4.putExtra("data",sosNumber);
                intent4.putExtra("imei",imei);
                startActivity(intent4);
                break;
            /**
             * 及时健康检测
             */
            case R.id.rl_check:
                sendMsg("check");
                break;
            /**
             * 心率上传间隔
             */
            case R.id.rl_upload_heart_rate:
                showPeriod("heart");
                break;
            /**
             * gps上传间隔
             */
            case R.id.rl_upload_locate:
                showPeriod("locate");
                break;
            /**
             * 关机
             */
            case R.id.rl_shutdown:
                showShutdown();

                break;
            /**
             * 音量
             */
            case R.id.rl_volume:
                showVolume();
                break;
            /**
             * 解除绑定
             */
            case R.id.rl_unbind:
                showPopup();
                break;
        }
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().JWOTCH_STATUS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备状态", json.toString());
                            if (json.getInt("status") == 200) {
                                if (json.has("data")) {
                                    json = json.getJSONObject("data");
                                    if (json.getBoolean("status") == false) {
                                        img_connect.setImageResource(R.drawable.badge_offline);
                                    }

                                    battery.setPower(json.getInt("battery"));
                                    if(json.isNull("address")){
                                        tv_locate.setText("定位地址: " + "暂无数据");
                                    } else {
                                        tv_locate.setText("定位地址: " + json.getString("address"));
                                    }
                                    if(json.isNull("locaDate")){
                                        tv_last_locate.setText("最后定位: " + "暂无数据");
                                    } else {
                                        tv_last_locate.setText("最后定位: " + json.getString("locaDate"));
                                    }
                                    tv_last_update.setText("最后通信: " + json.getString("updateDate"));
                                    if (json.getInt("gpsType") == 0 || json.getInt("gpsType") == 1) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_wifi), null);
                                    }
                                    if (json.getInt("gpsType") == 3) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_bluetooth), null);
                                    }
                                    if (json.getInt("gpsType") == 2) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_radar), null);
                                    }


                                } else {
                                    img_connect.setImageResource(R.drawable.badge_offline);
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

    }

    public void initCheck() {
        cb_gps = findViewById(R.id.cb_gps);
        cb_gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setSwitch("gps", isChecked);
            }
        });
        cb_white_list = findViewById(R.id.cb_white_list);
        cb_white_list.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setSwitch("whitelist", isChecked);
            }
        });
        cb_red_key = findViewById(R.id.cb_red_key);
        cb_red_key.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setSwitch("red_key", isChecked);
            }
        });
        cb_yellow_key = findViewById(R.id.cb_yellow_key);
        cb_yellow_key.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setSwitch("yellow_key", isChecked);
            }
        });
    }

    /**
     * 设置各项开关
     */
    public void setSwitch(String code, boolean b) {
        int on = -1;
        if (b) {
            on = 1;
        } else {
            on = 0;
        }
        String url = "";
        if (code.equals("gps")) {
            url = Urls.getInstance().H006_GPS;
        } else if (code.equals("whitelist")) {
            url = Urls.getInstance().H006_WHITELIST;
        } else if (code.equals("red_key")) {
            url = Urls.getInstance().H006_RED;
        } else if (code.equals("yellow_key")) {
            url = Urls.getInstance().H006_YELLOW;
        }
        showProgressBar();
        OkGo.<String>post(url + "/" + imei + "/" + on)
                .tag(this)
                .headers("token", MyApplication.token)
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
     * 获取设备设置属性
     */
    public void initView() {

        OkGo.<String>get(Urls.getInstance().H006_SETTING + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取手环设置属性", json.toString());
                            if (json.getInt("status") == 200) {
                                json_setting = json.getJSONObject("data");
                                if (json_setting.getInt("whitelistSwitch") == 0) {
                                    cb_white_list.setChecked(false);
                                } else {
                                    cb_white_list.setChecked(true);
                                }
                                if (json_setting.getInt("gpsSwitch") == 0) {
                                    cb_gps.setChecked(false);
                                } else {
                                    cb_gps.setChecked(true);
                                }
                                if (json_setting.getInt("yellowSwitch") == 0) {
                                    cb_yellow_key.setChecked(false);
                                } else {
                                    cb_yellow_key.setChecked(true);
                                }
                                if (json_setting.getInt("redSwitch") == 0) {
                                    cb_red_key.setChecked(false);
                                } else {
                                    cb_red_key.setChecked(true);
                                }
                                tv_upload_locate.setText(json_setting.getString("gpsPeriod") + "分钟/次");
                                tv_upload_heartrate.setText(json_setting.getString("hrPeriod") + "分钟/次");
                                sosNumber = json_setting.getString("sosNumber");
                                whitelist = json_setting.getString("whitelist");
                                ringlist = json_setting.getString("ringlist");
                                int volume = json_setting.getInt("volume");
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

    public void showPeriod(String type) {
        List<String> typeList = new ArrayList<>();
        typeList.add("5分钟");
        typeList.add("10分钟");
        typeList.add("15分钟");
        typeList.add("30分钟");
        typeList.add("1小时");
        typeList.add("2小时");
        typeList.add("4小时");
        typeList.add("6小时");
        typeList.add("8小时");
        typeList.add("10小时");
        typeList.add("12小时");


        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                int minute = 0;
                if (typeList.size() != 0) {

                        String s = typeList.get(options1);
                        String period = "";
                        if(s.contains("分钟")){
                            s = s.replace("分钟","");
                            minute = Integer.parseInt(s);
                            period =s + "分钟/次";

                        } else if(s.contains("小时")){
                            s = s.replace("小时","");
                            int hour =Integer.parseInt(s);
                            minute = hour*60;
                            period=hour*60 + "分钟/次";
                        }
                    if(type.equals("heart")){
                        tv_upload_heartrate.setText(period);
                        setPeriod(HEART_RATE_PERIOD,minute);
                    } else {
                        tv_upload_locate.setText(period);
                        setPeriod(GPS_PERIOD,minute);
                    }

                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(typeList);
        pvOptions.show();
    }

    public void setPeriod(int type,int minute){
        String url = "";
        if(type == GPS_PERIOD){
            url = Urls.getInstance().H006_PERIOD_GPS;
        } else if(type == HEART_RATE_PERIOD) {
            url = Urls.getInstance().H006_PERIOD_HR;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("period",minute);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(url + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置频率", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show();
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
    public void showVolume() {
        List<String> typeList = new ArrayList<>();
        typeList.add("静音");
        typeList.add("音量1");
        typeList.add("音量2");
        typeList.add("音量3");
        typeList.add("音量4");
        typeList.add("音量5");
        typeList.add("音量6");
        typeList.add("音量7");


        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (typeList.size() != 0) {
                    tv_volume.setText(typeList.get(options1));
                    setVolume(options1);
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(typeList);
        pvOptions.show();
    }

    public void setVolume(int vol){
        showProgressBar();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("volume",vol);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.getInstance().H006_VOLUME + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送指令", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "音量修改成功", Toast.LENGTH_LONG).show();
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





    //发送指令
    public void sendMsg(String s) {
        String url = "";
        if (s.equals("power_off")) {
            url = Urls.getInstance().H006_POWEROFF;
        } else if (s.equals("check")) {
            url = Urls.getInstance().H006_HR;
        }
        showProgressBar();
        OkGo.<String>post(url + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送指令", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发送指令成功", Toast.LENGTH_LONG).show();
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

    @SuppressLint("WrongConstant")
    public void showShutdown() {

        // TODO Auto-generated method stub
        View popview = LayoutInflater.from(H008MainActivity.this).inflate(R.layout.popu_shutdown, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg("power_off");
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new H008MainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);

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
    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }

    @SuppressLint("WrongConstant")
    public void showPopup() {

        // TODO Auto-generated method stub
        View popview = LayoutInflater.from(H008MainActivity.this).inflate(R.layout.popu_fit_unbind, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbind();
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new H008MainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);

    }
    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BIND + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设备解绑成功", Toast.LENGTH_SHORT).show();
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }

}