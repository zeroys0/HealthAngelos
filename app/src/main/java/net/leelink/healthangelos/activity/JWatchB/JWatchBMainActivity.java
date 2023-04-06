package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ContactPersonActivity;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JWatchBMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_xingling_heart_rate, rl_blood_pressure, rl_blood_oxygen, rl_step_number, rl_weilan, rl_trail, rl_home_phone, rl_fast_call, rl_search_device;
    private RelativeLayout rl_locate, rl_source, rl_timing, rl_locate_rate, rl_switch_setting;
    private Context context;
    private String imei,uid;
    private TextView tv_imei, tv_battery,tv_time,tv_device_name;
    private BatteryView battery;
    private MapView map_view;
    private AMap aMap;
    private ImageView img_refresh;
    SharedPreferences sp;
    private Button btn_unbind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwatch_bmain);
        context = this;
        imei = getIntent().getStringExtra("imei");
        sp = getSharedPreferences("sp", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("imei", imei);
        editor.apply();
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();
        initData();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_xingling_heart_rate = findViewById(R.id.rl_xingling_heart_rate);
        rl_xingling_heart_rate.setOnClickListener(this);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name")+getIntent().getStringExtra("model"));
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        rl_blood_oxygen = findViewById(R.id.rl_blood_oxygen);
        rl_blood_oxygen.setOnClickListener(this);
        rl_step_number = findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        rl_weilan = findViewById(R.id.rl_weilan);
        rl_weilan.setOnClickListener(this);
        rl_trail = findViewById(R.id.rl_trail);
        rl_trail.setOnClickListener(this);
        rl_home_phone = findViewById(R.id.rl_home_phone);
        rl_home_phone.setOnClickListener(this);
        rl_fast_call = findViewById(R.id.rl_fast_call);
        rl_fast_call.setOnClickListener(this);
        rl_search_device = findViewById(R.id.rl_search_device);
        rl_search_device.setOnClickListener(this);
        rl_locate = findViewById(R.id.rl_locate);
        rl_locate.setOnClickListener(this);
        rl_source = findViewById(R.id.rl_source);
        rl_source.setOnClickListener(this);
        rl_timing = findViewById(R.id.rl_timing);
        rl_timing.setOnClickListener(this);
        rl_locate_rate = findViewById(R.id.rl_locate_rate);
        rl_locate_rate.setOnClickListener(this);
        rl_switch_setting = findViewById(R.id.rl_switch_setting);
        rl_switch_setting.setOnClickListener(this);
        tv_imei = findViewById(R.id.tv_imei);
        battery = findViewById(R.id.battery);
        tv_battery = findViewById(R.id.tv_battery);
        img_refresh = findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(this);

        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        btn_unbind = findViewById(R.id.btn_unbind);
        btn_unbind.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);


    }

    public void initData() {
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().SAAS_DEVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceList", imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询腕表基本信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = json.getJSONArray("data");
                                json = jsonArray.getJSONObject(0);
                                tv_imei.setText("imei: " + imei);
                                uid = json.getString("uid");
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("uid", json.getString("uid"));
                                editor.apply();
                                battery.setPower(json.getInt("b"));
                                tv_battery.setText(json.getString("b")+"%");
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                        .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                markerOption.title("老人当前位置");
                                LatLng latLng = new LatLng(json.getDouble("lat"), json.getDouble("lon"));
                                markerOption.position(latLng);
                                aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                                aMap.addMarker(markerOption);
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
                        LoadDialog.stop();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:  //点击后退
                finish();
                break;
            case R.id.rl_xingling_heart_rate:   //6041b心率页面
                Intent intent = new Intent(context, JWatchBHeartRateActivity.class);
                intent.putExtra("imei", imei);
                startActivity(intent);
                break;
            case R.id.rl_blood_pressure:    //6041b血压页面
                Intent intent1 = new Intent(context, JWatchBBloodPressureActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_blood_oxygen:      //血氧页面
                Intent intent2 = new Intent(context, JWatchBBloodOxygenActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_step_number:       //计步页面
                Intent intent3 = new Intent(context, JWatchBStepActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_weilan:       //电子围栏
                Intent intent4 = new Intent(context, ElectFenceActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_trail:     //查看足迹
                Intent intent5 = new Intent(context, TrailActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_home_phone:        //查看亲人号码
                Intent intent6 = new Intent(context, ContactPersonActivity.class);
                startActivity(intent6);
                break;
            case R.id.rl_fast_call:     //快捷拨号设置
                Intent intent7 = new Intent(context, FastCallActivity.class);
                startActivity(intent7);
                break;
            case R.id.rl_search_device:     //查找设备
                searchDevice();
                break;
            case R.id.rl_locate:        //立即定位
                locate();
                break;
            case R.id.rl_source:        //远程关机
                shutDown();
                break;
            case R.id.btn_unbind:
                unbind();
                break;
//            case R.id.rl_timing:        //定时开关机
//                Intent intent8 = new Intent(context,TimingActivity.class);
//                startActivity(intent8);
//                break;
            case R.id.rl_locate_rate:       //定位频率
                Intent intent9 = new Intent(context, LocateRateSetActivity.class);
                startActivity(intent9);
                break;
//            case R.id.rl_switch_setting:    //开关设置
//                Intent intent10 = new Intent(context,SwitchSettingActivity.class);
//                startActivity(intent10);
//                break;
            case R.id.img_refresh:      //刷新数据
                initData();
                break;
        }
    }

    public void searchDevice() {
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().FINDEQUIPMENT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("mId", imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查找设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发送指令成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }


    public void locate() {
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().ISSUEORIENTATIONI)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("uId", uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送定位指令", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "定位成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }

    public void shutDown() {
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().SHUTDOWN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("uId", uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("远程关机", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "关机指令发送成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }

    public void unbind(){
        LoadDialog.start(context);
        OkGo.<String>delete(Urls.getInstance().UNBIND+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "腕表解绑成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }


}