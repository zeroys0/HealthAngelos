package net.leelink.healthangelos.activity.Ys7;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BindYs7Activity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_serial, ed_code, ed_name;
    private Button btn_bind;
    private ImageView img_scan;
    EditText ed_wifi;
    private TextView tv_wifi;
    private List<ScanResult> list;
    private WiFiAdapter wiFiAdapter;
    private RecyclerView wifi_list;
    SharedPreferences sp;
    private boolean online = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_ys7);
        context = this;
        sp = getSharedPreferences("sp", 0);
        createProgressBar(context);
        init();
        initData();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_serial = findViewById(R.id.ed_serial);
        ed_code = findViewById(R.id.ed_code);
        ed_name = findViewById(R.id.ed_name);
        btn_bind = findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);
        img_scan = findViewById(R.id.img_scan);
        img_scan.setOnClickListener(this);
    }

    public void initData() {


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_bind:
//                bind();

//                check();
                add();
                break;
            case R.id.img_scan:
                doGetPermission();
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Log.e("扫描监控摄像头: ", result);
                    //  ys7 K25288978 YYADUR CS-CTQ6N-2C3WF
                    try {
                        String serial = result.substring(4, 13);
                        String code = result.substring(14, 20);
                        String name = result.substring(21);
                        ed_serial.setText(serial);
                        ed_code.setText(code);
                        ed_name.setText(name);
                    } catch (Exception e) {

                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void check() {
        OkGo.<String>get(Urls.getInstance().YS_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", ed_serial.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看信息", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "该设备已被绑定", Toast.LENGTH_SHORT).show();
                            } else if (json.getInt("status") == 203) {
                                add();
                            } else if (json.getInt("status") == 203) {
                                add();
                            } else if (json.getInt("status") == 20018) {
                                add();
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

    public void add() {
        Log.d("bind: ", "开始绑定");
        Log.d("bind: ", Urls.getInstance().YS_ADD);
        Log.d("bind: ", ed_serial.getText().toString());
        Log.d("bind: ", ed_code.getText().toString());
        Log.d("bind: ", ed_name.getText().toString());
        OkGo.<String>get(Urls.getInstance().YS_ADD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("deviceSerial", ed_serial.getText().toString())
                .params("validateCode", ed_code.getText().toString())
                .params("deviceName", ed_name.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定摄像头", json.toString());
                            LoadDialog.stop();
                            if (json.getInt("status") == 200) {

                                Toast.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else if (json.getInt("status") == 20007) {

                                showPop();
                                backgroundAlpha(0.5f);
                            } else if (json.getInt("status") == 20017) {
                                Toast.makeText(context, "设备已被绑定", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void bind() {
        OkGo.<String>get(Urls.getInstance().YS_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("deviceSerial", ed_serial.getText().toString())
                .params("validateCode", ed_code.getText().toString())
                .params("deviceName", ed_name.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定摄像头", json.toString());
                            if (json.getInt("status") == 200) {

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
        popview = LayoutInflater.from(BindYs7Activity.this).inflate(R.layout.choose_wifi_dialog_ys7, null);
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
        popuPhoneW.setOnDismissListener(new BindYs7Activity.poponDismissListener());
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

    /**
     * 根据摄像头热点链接wifi
     */
    public void connect() {
        // 开启日志
        EZWiFiConfigManager.showLog(true);
        LoadDialog.start(context);
        // step1.准备配网参数
        ApConfigParam param = new ApConfigParam();
        param.routerWifiSsid /*路由器wifi名称*/ = tv_wifi.getText().toString();
        param.routerWifiPwd /*路由器wifi密码*/ = ed_wifi.getText().toString();
        param.deviceSerial /*设备序列号*/ = ed_serial.getText().toString();
        param.deviceVerifyCode /*设备验证码*/ = ed_code.getText().toString();
        param.deviceHotspotSsid /*设备热点名称*/ = "SoftAP_" + ed_serial.getText().toString();
        param.deviceHotspotPwd /*设备热点密码*/ = "SoftAP_" + ed_code.getText().toString();
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            add();
                        }
                    }, 40000);
                    // 配网成功后，需要停止配网
                    EZWiFiConfigManager.stopAPConfig();

                }
//                if (code == EZConfigWifiInfoEnum.CONNECTED_TO_PLATFORM.code) {
//
//                    //停止配网
//
//                    // todo 提示用户配网成功
//
//
//                }

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
        popview = LayoutInflater.from(BindYs7Activity.this).inflate(R.layout.wifi_list_dialog, null);
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

    //获取权限 并扫描
    void doGetPermission() {
        AndPermission.with(context)
                .permission(
                        Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, Object data, RequestExecutor executor) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("扫描需要用户开启相机,是否同意开启相机权限");
                        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户同意去设置：
                                executor.execute();
                            }
                        });
                        //设置点击对话框外部区域不关闭对话框
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户不同意去设置：
                                executor.cancel();
                                Mytoast.show(context, "无法打开相机");

                            }
                        });
                        builder.show();
                    }

                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(Object data) {
                        try {
                            Intent intent = new Intent(context, CaptureActivity.class);
                            startActivityForResult(intent, 1);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(Object data) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, (List<String>) data)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。

                            final SettingService settingService = AndPermission.permissionSetting(context);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("相机权限已被禁止,用户将无法打开摄像头,无法进入扫描,请到\"设置\"开启");
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户同意去设置：
                                    settingService.execute();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户不同意去设置：
                                    settingService.cancel();
                                }
                            });
                            //设置点击对话框外部区域不关闭对话框
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }
                })
                .start();
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