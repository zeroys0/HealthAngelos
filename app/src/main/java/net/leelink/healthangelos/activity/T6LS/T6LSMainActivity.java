package net.leelink.healthangelos.activity.T6LS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import net.leelink.healthangelos.activity.NeoLocationActivity;
import net.leelink.healthangelos.activity.PromptActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

public class T6LSMainActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_nick_name, rl_wotch_phone, rl_heart_rate, rl_blood_pressure, rl_step_number, rl_location, rl_elect_fence;
    private RelativeLayout rl_temp_data,rl_notice, rl_family, rl_upload_locate, rl_upload_heart_rate, rl_upload_temp, rl_locate_time, rl_reboot, rl_shutdown, rl_search;
    private SwitchCompat cb_gps, cb_call;
    private TextView tv_name,tv_model, tv_locate, tv_last_locate, tv_last_update, tv_upload_locate, tv_upload_bo, tv_upload_heartrate, tv_upload_time,tv_phone;
    public String imei;
    private Button rl_unbind;
    private BatteryView battery;
    private ImageView img_connect, img_head;
    private JSONObject json_setting;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t6_lsmain);
        init();
        context = this;
        createProgressBar(context);
        imei = getIntent().getStringExtra("imei");
        if (imei == null) {
            Toast.makeText(context, "设备已注销", Toast.LENGTH_SHORT).show();
            finish();
        }
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    Handler handler = new Handler();

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_model = findViewById(R.id.tv_model);
        tv_model.setText(getIntent().getStringExtra("modelName"));
        img_head = findViewById(R.id.img_head);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(context).load(Urls.getInstance().IMG_URL + getIntent().getStringExtra("img")).into(img_head);
            }
        }, 1000);


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
        rl_location = findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        rl_elect_fence = findViewById(R.id.rl_elect_fence);
        rl_elect_fence.setOnClickListener(this);
        rl_notice = findViewById(R.id.rl_notice);
        rl_notice.setOnClickListener(this);
        rl_temp_data = findViewById(R.id.rl_temp_data);
        rl_temp_data.setOnClickListener(this);
        rl_family = findViewById(R.id.rl_family);
        rl_family.setOnClickListener(this);
        cb_gps = findViewById(R.id.cb_gps);
        cb_call = findViewById(R.id.cb_call);
        rl_upload_locate = findViewById(R.id.rl_upload_locate);
        rl_upload_locate.setOnClickListener(this);
        rl_upload_heart_rate = findViewById(R.id.rl_upload_heart_rate);
        rl_upload_heart_rate.setOnClickListener(this);
        rl_upload_temp = findViewById(R.id.rl_upload_temp);
        rl_upload_temp.setOnClickListener(this);
        rl_locate_time = findViewById(R.id.rl_locate_time);
        rl_locate_time.setOnClickListener(this);
        rl_reboot = findViewById(R.id.rl_reboot);
        rl_reboot.setOnClickListener(this);
        rl_shutdown = findViewById(R.id.rl_shutdown);
        rl_shutdown.setOnClickListener(this);
        rl_search = findViewById(R.id.rl_search);
        rl_search.setOnClickListener(this);
        rl_unbind = findViewById(R.id.rl_unbind);
        rl_unbind.setOnClickListener(this);
        battery = findViewById(R.id.battery);
        battery.setPower(0);
        tv_locate = findViewById(R.id.tv_locate);
        tv_name =findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_last_locate = findViewById(R.id.tv_last_locate);
        tv_last_update = findViewById(R.id.tv_last_update);
        img_connect = findViewById(R.id.img_connect);
        tv_upload_locate = findViewById(R.id.tv_upload_locate);
        tv_upload_bo = findViewById(R.id.tv_upload_bo);
        tv_upload_heartrate = findViewById(R.id.tv_upload_heartrate);
        tv_upload_time = findViewById(R.id.tv_upload_time);

        initCheck();
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
                                    tv_locate.setText("定位地址: " + json.getString("address"));
                                    tv_last_locate.setText("最后定位: " + json.getString("locaDate"));
                                    if (json.getInt("gpsType") == 0 || json.getInt("gpsType") == 1) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_wifi), null);
                                    }
                                    if (json.getInt("gpsType") == 3) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_bluetooth), null);
                                    }
                                    if (json.getInt("gpsType") == 2) {
                                        tv_last_locate.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.badge_radar), null);
                                    }
                                    tv_last_update.setText("最后通信: " + json.getString("updateDate"));
                                    tv_last_update = findViewById(R.id.tv_last_update);
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
    }

    public void initView() {
        OkGo.<String>get(Urls.getInstance().T6LS_SETTING + "/" + imei)
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
                                if (json_setting.getInt("whitelist") == 0) {
                                    cb_gps.setChecked(false);
                                } else {
                                    cb_gps.setChecked(true);
                                }
                                if (json_setting.getInt("nomoveslp") == 0) {
                                    cb_call.setChecked(false);
                                } else {
                                    cb_call.setChecked(true);
                                }
                                tv_upload_locate.setText(json_setting.getString("upload") + "分钟/次");

                                if (json_setting.getInt("tempstart") == 0) {
                                    tv_upload_bo.setText("关闭");
                                } else if (json_setting.getInt("tempstart") == 1) {
                                    tv_upload_bo.setText("单次");
                                } else {
                                    tv_upload_bo.setText(json_setting.getInt("tempstart") + "分钟/次");
                                }

                                if (json_setting.getInt("hrtstart") == 0) {
                                    tv_upload_heartrate.setText("关闭");
                                } else if (json_setting.getInt("hrtstart") == 1) {
                                    tv_upload_heartrate.setText("单次");
                                } else {
                                    tv_upload_heartrate.setText(json_setting.getInt("hrtstart") + "分钟/次");
                                }
                                String Bmin = json_setting.getInt("udtimeBMinute") < 10 ? "0" + json_setting.getInt("udtimeBMinute") : String.valueOf(json_setting.getInt("udtimeBMinute"));
                                String Emin = json_setting.getInt("udtimeEMinute") < 10 ? "0" + json_setting.getInt("udtimeEMinute") : String.valueOf(json_setting.getInt("udtimeEMinute"));
                                tv_upload_time.setText(json_setting.getInt("udtimeBHour") + ":" + Bmin + "-" + json_setting.getInt("udtimeEHour") + ":" + Emin);
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
                    setStepTarget("whitelist", isChecked);
            }
        });

        cb_call = findViewById(R.id.cb_call);
        cb_call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check)
                    setStepTarget("nomoveslp", isChecked);
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
             * 查看体温
             */
            case R.id.rl_temp_data:
                Intent intent24 = new Intent(this, HealthDataActivity.class);
                intent24.putExtra("type", 7);
                startActivity(intent24);
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
             * 定时提醒
             */
            case R.id.rl_notice:
                Intent intent4 = new Intent(this, PromptActivity.class);
                startActivity(intent4);
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
             * 位置上报频率
             */
            case R.id.rl_upload_locate:
                Intent intent7 = new Intent(this, UploadT6LSActivity.class);
                intent7.putExtra("type", 1);
                intent7.putExtra("imei", imei);
//                try {
//                    intent7.putExtra("cb", json_setting.getInt("arg17_109_on_off_trace"));
//                    intent7.putExtra("fre", json_setting.getString("arg14_106_freq_location"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                startActivity(intent7);
                break;
            /**
             * 心率上报频率
             */
            case R.id.rl_upload_heart_rate:
                Intent intent9 = new Intent(this, UploadT6LSActivity.class);
                intent9.putExtra("type", 3);
                intent9.putExtra("imei", imei);
//                try {
//                    intent9.putExtra("cb", json_setting.getInt("arg20_112_on_off_hr"));
//                    intent9.putExtra("fre", json_setting.getString("arg43_143_freq_hr"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                startActivity(intent9);
                break;
            case R.id.rl_upload_temp:
                Intent intent10 = new Intent(this, UploadT6LSActivity.class);
                intent10.putExtra("type", 5);
                intent10.putExtra("imei", imei);
//                try {
//                    intent9.putExtra("cb", json_setting.getInt("arg20_112_on_off_hr"));
//                    intent9.putExtra("fre", json_setting.getString("arg43_143_freq_hr"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                startActivity(intent10);
                break;
            /**
             * 定位上传时段
             */
            case R.id.rl_locate_time:
                Intent intent17 = new Intent(this, UploadTimeMonitorActivity.class);
                intent17.putExtra("imei", imei);
                startActivity(intent17);
                break;
            /**
             * 重启设备
             */
            case R.id.rl_reboot:
                Intent intent11 = new Intent(this, SendMsgToT6LSActivity.class);
                intent11.putExtra("type", "reboot");
                intent11.putExtra("imei", imei);
                startActivity(intent11);
                break;
            /**
             * 设备关机
             */
            case R.id.rl_shutdown:
                Intent intent12 = new Intent(this, SendMsgToT6LSActivity.class);
                intent12.putExtra("type", "shutdown");
                intent12.putExtra("imei", imei);
                startActivity(intent12);
                break;
            /**
             * 查找设备
             */
            case R.id.rl_search:
                Intent intent15 = new Intent(this, SendMsgToT6LSActivity.class);
                intent15.putExtra("type", "search");
                intent15.putExtra("imei", imei);
                startActivity(intent15);
                break;
            /**
             * 解除绑定
             */
            case R.id.rl_unbind:
                unbind();
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
     * 设置各项开关
     */
    public void setStepTarget(String code, boolean b) {
        int on = -1;
        if (b) {
            on = 1;
        } else {
            on = 0;
        }
        String url = "";
        if (code.equals("whitelist")) {
            url = Urls.getInstance().T6LS_WHITE_LIST;
        } else if (code.equals("nomoveslp")) {
            url = Urls.getInstance().T6LS_NOMOVESLP;
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

}