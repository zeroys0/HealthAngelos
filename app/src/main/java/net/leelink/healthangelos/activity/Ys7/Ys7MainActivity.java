package net.leelink.healthangelos.activity.Ys7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezviz.sdk.configwifi.EZConfigWifiErrorEnum;
import com.ezviz.sdk.configwifi.EZConfigWifiInfoEnum;
import com.ezviz.sdk.configwifi.EZWiFiConfigManager;
import com.ezviz.sdk.configwifi.ap.ApConfigParam;
import com.ezviz.sdk.configwifi.common.EZConfigWifiCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.activity.ssk.WiFiAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Ys7MainActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back, rl_remind, rl_turn, rl_wifi;
    private Context context;
    private Button btn_unbind;
    private SwitchCompat cb_track, cb_private;
    EditText ed_wifi;
    private List<ScanResult> list;
    private TextView tv_wifi,tv_scene;
    SharedPreferences sp;
    private WiFiAdapter wiFiAdapter;
    private RecyclerView wifi_list;
    private String validateCode;
    private boolean is_track = false;
    private boolean is_private = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys7_main);
        context = this;
        sp = getSharedPreferences("sp", 0);
        createProgressBar(context);
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_remind = findViewById(R.id.rl_remind);
        rl_remind.setOnClickListener(this);
        btn_unbind = findViewById(R.id.btn_unbind);
        btn_unbind.setOnClickListener(this);
        rl_wifi = findViewById(R.id.rl_wifi);
        rl_wifi.setOnClickListener(this);
        cb_track = findViewById(R.id.cb_track);
        cb_track.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_track)
                    setTrack(isChecked);

            }
        });
        rl_turn = findViewById(R.id.rl_turn);
        rl_turn.setOnClickListener(this);
        cb_private = findViewById(R.id.cb_private);
        cb_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (is_private)
                    setPrivate(isChecked);
            }
        });
        tv_scene = findViewById(R.id.tv_scene);
    }

    public void initData() {
        OkGo.<String>get(Urls.getInstance().YS_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
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
                                validateCode = json.getString("validateCode");
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
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().YS_TRACK)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取人形追踪开关信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("enable") == 0) {
                                    cb_track.setChecked(false);
                                } else {
                                    cb_track.setChecked(true);
                                }
                                is_track = true;
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

        OkGo.<String>get(Urls.getInstance().YS_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取隐私开关信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("enable") == 0) {
                                    cb_private.setChecked(false);
                                } else {
                                    cb_private.setChecked(true);
                                }
                                is_private = true;
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
                    }
                });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_remind:
                Intent intent = new Intent(this, Ys7RemindModeActivity.class);
                intent.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_unbind:
                unbind();
                break;
            case R.id.rl_turn:
                setMirror();
                break;
            case R.id.rl_wifi:
                showPop();
                backgroundAlpha(0.5f);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            int type = data.getIntExtra("type",0);
            switch (type){
                case 0:
                    tv_scene.setText("告警音");
                    break;
                case 1:
                    tv_scene.setText("提示音");
                    break;
                case 2:
                    tv_scene.setText("静音");
                    break;
            }
        }
    }

    public void setTrack(boolean checked) {
        int enable;
        if (checked) {
            enable = 1;
        } else {
            enable = 0;
        }
        createProgressBar(context);
        OkGo.<String>put(Urls.getInstance().YS_TRACK)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .params("enable", enable)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置人形追踪", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "配置完成", Toast.LENGTH_SHORT).show();
                                stopProgressBar();
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

    public void setMirror() {
        createProgressBar(context);
        OkGo.<String>put(Urls.getInstance().YS_MIRROR)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置画面翻转", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                                stopProgressBar();
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

    public void setPrivate(boolean checked) {
        int enable;
        if (checked) {
            enable = 1;
        } else {
            enable = 0;
        }
        createProgressBar(context);
        OkGo.<String>put(Urls.getInstance().YS_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .params("enable", enable)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置镜头遮蔽开关", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "配置完成", Toast.LENGTH_SHORT).show();
                                stopProgressBar();
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
        OkGo.<String>get(Urls.getInstance().YS_UNBIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑摄像头", json.toString());
                            if (json.getInt("status") == 200 || json.getInt("status") ==20018) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
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

    /**
     * 选择wifi对话框
     */
    @SuppressLint("WrongConstant")
    public void showPop() {
        PopupWindow popuPhoneW;
        View popview;
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(Ys7MainActivity.this).inflate(R.layout.choose_wifi_dialog_ys7, null);
        Button btn_cancel = (Button) popview.findViewById(R.id.btn_cancel);
        ed_wifi = popview.findViewById(R.id.ed_wifi);

        Button btn_confirm = popview.findViewById(R.id.btn_confirm);

        RelativeLayout rl_wifi = popview.findViewById(R.id.rl_wifi);
        rl_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWifi();
            }
        });
        tv_wifi = popview.findViewById(R.id.tv_wifi);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new Ys7MainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ProtocolBluetooth.sendData_ConfigDeviceParams(tv_wifi.getText().toString(), ed_wifi.getText().toString());
                connect();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(tv_wifi.getText().toString(), ed_wifi.getText().toString());
                editor.apply();
                popuPhoneW.dismiss();
                Toast.makeText(context, "连接WiFi中...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void connect() {
        // 开启日志
        EZWiFiConfigManager.showLog(true);

        // step1.准备配网参数
        ApConfigParam param = new ApConfigParam();
        param.routerWifiSsid /*路由器wifi名称*/ = tv_wifi.getText().toString();
        param.routerWifiPwd /*路由器wifi密码*/ = ed_wifi.getText().toString();
        param.deviceSerial /*设备序列号*/ = getIntent().getStringExtra("imei");
        param.deviceVerifyCode /*设备验证码*/ = validateCode;
        param.deviceHotspotSsid /*设备热点名称*/ = "SoftAP_" + getIntent().getStringExtra("imei");
        param.deviceHotspotPwd /*设备热点密码*/ = "SoftAP_" + validateCode;
        param.autoConnect /*是否自动连接到设备热点*/ = true;
        EZWiFiConfigManager.stopAPConfig();
        // step2.开始配网
        EZWiFiConfigManager.startAPConfig(MyApplication.getContext(), param, new EZConfigWifiCallback() {
            @Override
            public void onInfo(int code, String message) {
                super.onInfo(code, message);

                // step3.结束配网
                if (code == EZConfigWifiInfoEnum.CONNECTING_SENT_CONFIGURATION_TO_DEVICE.code) {
                    // todo 提示用户配网成功

                    // 配网成功后，需要停止配网
                    EZWiFiConfigManager.stopAPConfig();
                }
                // 更多消息码请见枚举类 EZConfigWifiInfoEnum 相关说明
            }

            @Override
            public void onError(int code, String description) {
                super.onError(code, description);

                // step3.结束配网
                if (code == EZConfigWifiErrorEnum.CONFIG_TIMEOUT.code) {
                    // todo 提示用户配网超时
                    // 配网失败后，需要停止配网
                    EZWiFiConfigManager.stopAPConfig();
                } else if (code == EZConfigWifiErrorEnum.MAY_LACK_LOCATION_PERMISSION.code) {
                    // todo 提示用户授予app定位权限，并打开定位开关
                } else if (code == EZConfigWifiErrorEnum.WRONG_DEVICE_VERIFY_CODE.code) {
                    // todo 提示用户验证码输入错误
                }
                // 更多错误码请见枚举类 EZConfigWifiErrorEnum 相关说明
            }
        });
    }

    /**
     * 搜寻附近wifi
     *
     * @return
     */
    public List<ScanResult> searchWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                Log.e("wifi", "搜索的wifi-ssid:" + scanResult.SSID);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        } else {

        }
        return wifiList;
    }

    /**
     * 选择wifi
     */
    PopupWindow wifi_popuPhoneW;

    @SuppressLint("WrongConstant")
    public void showWifi() {
        View popview;
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(Ys7MainActivity.this).inflate(R.layout.wifi_list_dialog, null);
        //搜索WiFi并展示列表
        list = searchWifi();
        wifi_list = popview.findViewById(R.id.wifi_list);
        wiFiAdapter = new WiFiAdapter(list, context, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        wifi_list.setAdapter(wiFiAdapter);
        wifi_list.setLayoutManager(layoutManager);
        TextView tv_find_wifi = popview.findViewById(R.id.tv_find_wifi);
        tv_find_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", Urls.getInstance().WIFI_PROBLEM + MyApplication.token);
                startActivity(intent);
            }
        });


        wifi_popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wifi_popuPhoneW.setFocusable(true);
        wifi_popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        wifi_popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        wifi_popuPhoneW.setOutsideTouchable(true);
        wifi_popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        wifi_popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);


    }

    @Override
    public void onItemClick(View view) {
        int position = wifi_list.getChildLayoutPosition(view);
        tv_wifi.setText(list.get(position).SSID);
        String pwd = sp.getString(list.get(position).SSID, "");
        ed_wifi.setText(pwd);
        wifi_popuPhoneW.dismiss();
    }

    @Override
    public void onButtonClick(View view, int position) {

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
}