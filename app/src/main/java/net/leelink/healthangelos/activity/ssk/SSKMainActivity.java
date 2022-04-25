package net.leelink.healthangelos.activity.ssk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import igs.android.protocol.bluetooth.ProtocolBluetooth;
import igs.android.protocol.bluetooth.bean.shared.ConfigStateBean;
import igs.android.protocol.bluetooth.bean.shared.ConnectStateBean;
import igs.android.protocol.bluetooth.bean.shared.DeviceStateBean;
import igs.android.protocol.bluetooth.bean.shared.SensorBean;
import igs.android.protocol.bluetooth.constants.shared.ConstantsBluetooth;
import igs.android.protocol.bluetooth.interfaces.shared.ProtocolBluetoothCallbackInterface;
import igs.android.protocol.constants.shared.TimeSpace;
import igs.android.protocol.tool.LogTool;
import igs.android.protocol.tool.OnFinishListener;
import igs.android.protocol.tool.ToastCustom;

public class SSKMainActivity extends BaseActivity implements View.OnClickListener, OnOrderListener, ProtocolBluetoothCallbackInterface,OnPassWayClick {
    private RelativeLayout rl_back,rl_pass_way,rl_code;
    private Context context;
    private LinearLayout ll_wifi, ll_rgb, ll_sleep_chart, ll_health_report;
    private RecyclerView wifi_list,alarm_list;
    private TextView tv_unbind, tv_heart, tv_breath, tv_time, tv_wifi, tv_state,tv_number,tv_pa,tv_code;
    EditText ed_wifi;
    private WiFiAdapter wiFiAdapter;
    private ImageView img_head;
    //wifi列表
    private List<ScanResult> list;
    String sensorID;
    private List<SSKBean> data_list;
    private SSKDataAdapter sskDataAdapter;

    private boolean bool_BLE = false;//是否使用低功耗蓝牙
    private static final String ApiKey = "1b482efa715e448e9b659c69e548e046";
    private boolean Bool_ConnectSensor = false;// 传感器连接是否成功
    private ArrayList<BluetoothDevice> BluetoothDeviceList = null;// 蓝牙设备列表
    private ArrayList<SensorBean> SensorList = null;// 传感器列表
    private SensorBean DeviceSensor_Selected = null;// 选择的传感器
    private BluetoothAdapter mBluetoothAdapter = null;
    private SSKMainActivity.MyBroadcastReceiver receiver = null;
    SharedPreferences sp;

    /**
     * 是否正在扫描BLE设备
     */
    private boolean mScanning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sskmain);
        context = this;
        sp = getSharedPreferences("sp",0);
        createProgressBar(context);
        init();
        try {
            bool_BLE = SSKMainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
            //启用调试模式，显示Toast，控制台输出日志，日志未输出到文件
            ProtocolBluetooth.enableDebugModel();
//            //初始化接口
            ProtocolBluetooth.init(this, this, ApiKey, bool_BLE);
//            ProtocolBluetooth.enableDebugModel(LogPath_Absolute);//启用调试模式，显示Toast，控制台输出日志，同时输出到指定文件
//            ProtocolBluetooth.disableLog();//禁用日志输出
//            ProtocolBluetooth.disableToast();//禁用Toast显示
        } catch (Exception e) {
            e.printStackTrace();
        }
        connect();
        getSensorId();

    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new SSKMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 10000);
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ll_wifi = findViewById(R.id.ll_wifi);
        ll_wifi.setOnClickListener(this);
        ll_rgb = findViewById(R.id.ll_rgb);
        ll_rgb.setOnClickListener(this);
        ll_sleep_chart = findViewById(R.id.ll_sleep_chart);
        ll_sleep_chart.setOnClickListener(this);
        ll_health_report = findViewById(R.id.ll_health_report);
        ll_health_report.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_heart = findViewById(R.id.tv_heart);
        tv_breath = findViewById(R.id.tv_breath);
        tv_time = findViewById(R.id.tv_time);
        img_head = findViewById(R.id.img_head);
        img_head.setOnClickListener(this);
        tv_state = findViewById(R.id.tv_state);
        alarm_list = findViewById(R.id.alarm_list);
        tv_number = findViewById(R.id.tv_number);
        tv_number.setText("编号: "+getIntent().getStringExtra("imei"));

        SensorList = new ArrayList<>();
        //定义广播事件监听
        receiver = new SSKMainActivity.MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsBluetooth.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        SSKMainActivity.this.registerReceiver(receiver, filter);

        BluetoothDeviceList = new ArrayList<>();

    }

    @SuppressLint("MissingPermission")
    public void connect() {
        ProtocolBluetooth.closeConnect_Bluetooth();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Toast.makeText(context, "硬件不支持蓝牙管理者！", Toast.LENGTH_LONG).show();
            finish();
        } else {
            mBluetoothAdapter = bluetoothManager.getAdapter();
//		    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                ToastCustom.showToast(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT);
                SSKMainActivity.this.finish();
            } else if (!mBluetoothAdapter.isEnabled()) {// 如果本地蓝牙没有开启，则开启
                ToastCustom.showToast(context, "打开蓝牙！", Toast.LENGTH_SHORT);
                mBluetoothAdapter.enable();
            } else {
                if (bool_BLE) {
                    if (!mScanning) {
                        ToastCustom.showToast(context, "正在搜索附近的睡睡康设备...", TimeSpace.SECOND1);
                        BluetoothDeviceList.clear();
                        SensorList.clear();

                        mScanning = true;
                        mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mScanning = false;
                                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
                            }
                        }, TimeSpace.SECOND10);
                    }
                } else {
                    if (!mBluetoothAdapter.isDiscovering()) {
                        ToastCustom.showToast(context, "正在搜索附近的睡睡康设备...", TimeSpace.SECOND1);
                        BluetoothDeviceList.clear();
                        SensorList.clear();

                        mBluetoothAdapter.startDiscovery();// 开始发现过程;
                    }
                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectDevice();
            }
        }, 2000);


    }

    @SuppressLint("MissingPermission")
    public void connectDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        String imei = getIntent().getStringExtra("imei");
        Log.e("connectDevice: ", SensorList.size() + "");
        for (SensorBean sensorBean : SensorList) {
            Log.e("connectDevice: ", sensorBean.Device.DeviceNick);
            Log.e("connectDevice: ", imei);
            if (sensorBean.Device.DeviceNick.equals(imei)) {
                Bool_ConnectSensor = false;
                DeviceSensor_Selected = sensorBean;
                BluetoothDevice device = sensorBean.Device.Device_Bluetooth;
                if (bool_BLE) {
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);

                    ProtocolBluetooth.connectSensor(DeviceSensor_Selected);
                } else {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        ProtocolBluetooth.connectSensor(DeviceSensor_Selected);
                    } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        ProtocolBluetooth.bondDevice(device);
                    } else {
                        ToastCustom.showToast(SSKMainActivity.this, "正在配对蓝牙，请稍候...",
                                Toast.LENGTH_SHORT);
                    }
                }
                break;
            }
        }
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {//发现蓝牙设备
                    BluetoothDevice device = result.getDevice();
                    LogTool.println("发现BLE蓝牙" + device.getName() + "[" + device.getAddress() + "]" + device.getType(),
                            LogTool.DebugImportant);

                    addSSKList(device);
                }
            });
        }
    };

    private void addSSKList(final BluetoothDevice device) {
        if (BluetoothDeviceList.indexOf(device) == -1) {
            BluetoothDeviceList.add(device);

            ProtocolBluetooth.requestSensorListByDevice(device, new OnFinishListener<List<SensorBean>>() {
                @Override
                public void onComplete() {
                    LogTool.printException("蓝牙" + device.getAddress() + "的传感器列表请求完成！" + this.getResult());
                }

                @Override
                public void onSucceed(List<SensorBean> data) {
                    for (SensorBean sensorBean : data) {
                        boolean bool_add = true;
                        for (int j = 0; j < SensorList.size(); j++) {
                            if (sensorBean.SensorID.equals(SensorList.get(j).SensorID)) {
                                bool_add = false;
                                break;
                            }
                        }
                        if (bool_add) {
                            sensorBean.Device.Device_Bluetooth = device;
                            SensorList.add(sensorBean);
                            ToastCustom.showToast(context, "发现睡睡康设备：" + sensorBean.getSensorName(), TimeSpace.SECOND1);
                        }
                    }
                }

                @Override
                public void onFail(String message) {
                 //   ToastCustom.showToast(context, "蓝牙[" + device.getName() + "]获取传感器信息失败！" + message, Toast.LENGTH_SHORT);
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onException(int state, Exception e) {

                }
            });
        }
    }

    public void getSensorId(){
        //根据老人id获取设备id
        OkGo.<String>get(Urls.getInstance().LISTSENSORID)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备ID", json.toString());
                            if (json.getInt("status") == 200) {
                                sensorID = json.getString("data");
                                getData();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, "失败,发生了不可预知的错误", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void loopData() {
        //获取设备连接状态

        OkGo.<String>get(Urls.getInstance().LISTREID)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备状态", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson= new Gson();
                                data_list = gson.fromJson(jsonArray.toString(),new TypeToken<List<SSKBean>>(){}.getType());
                                sskDataAdapter = new SSKDataAdapter(data_list,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                alarm_list.setAdapter(sskDataAdapter);
                                alarm_list.setLayoutManager(layoutManager);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, "失败,发生了不可预知的错误", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });

       getData();
    }

    public void getData(){
        //获取实时数据
        OkGo.<String>get(Urls.getInstance().LISTRMS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sensorID",sensorID )
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取实时数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_heart.setText(json.getString("heartRate"));
                                tv_breath.setText(json.getString("breatheRate"));
                                String time = json.getString("time");
                                time = time.substring(11, 16);
                                tv_time.setText(time);
                                int state = json.getInt("state");
                                switch (state) {
                                    case 0:
                                        tv_state.setText("设备离线");
                                        break;
                                    case 1:
                                        tv_state.setText("在床(静卧)");
                                        break;
                                    case 2:
                                        tv_state.setText("在床(体动)");
                                        break;
                                    case 3:
                                        tv_state.setText("用户离床");
                                        break;
                                    case 4:
                                        tv_state.setText("心率过高");
                                        break;
                                    case 5:
                                        tv_state.setText("暂时无响应");
                                        break;
                                    case 6:
                                        tv_state.setText("正在分析状态");
                                        break;
                                    case 7:
                                        tv_state.setText("设备意外下线");
                                        break;
                                    case 8:
                                        tv_state.setText("离床未归");
                                        break;
                                    case 9:
                                        tv_state.setText("体动过少");
                                        break;
                                    case 10:
                                        tv_state.setText("体动过多");
                                        break;
                                    case 16:
                                        tv_state.setText("呼吸率过低");
                                        break;
                                    case 17:
                                        tv_state.setText("呼吸率过高");
                                        break;
                                    default:
                                        break;
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {

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
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_wifi:
                showPop();
                backgroundAlpha(0.5f);
                break;
            case R.id.ll_rgb:
                Intent intent = new Intent(context, LampRgbActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
            case R.id.ll_sleep_chart:
                //睡眠趋势
                Intent intent1 = new Intent(context, SSKWebActivity.class);
                String url = Urls.H5_IP + "/#/SleepCharts/index/" + MyApplication.userInfo.getOlderlyId() + "/" + MyApplication.token;
                intent1.putExtra("title","睡眠趋势");
                intent1.putExtra("url", url);
                startActivity(intent1);
                break;
            case R.id.ll_health_report:
                Intent intent2 = new Intent(context, SSKWebActivity.class);
                String url1 = Urls.H5_IP + "/#/SSKHealthReport/index/" + MyApplication.userInfo.getOlderlyId() + "/"+MyApplication.token;
                intent2.putExtra("url", url1);
                intent2.putExtra("title","健康报告");
                intent2.putExtra("his",1);
                startActivity(intent2);
                break;
            case R.id.img_head:
                //读取状态
                ProtocolBluetooth.sendData_ReadDeviceStatus();
                break;

            default:
                break;
        }
    }

    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().SSK_UNBIND + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, "失败,发生了不可预知的错误", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
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
        popview = LayoutInflater.from(SSKMainActivity.this).inflate(R.layout.choose_wifi_dialog, null);
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
        rl_pass_way = popview.findViewById(R.id.rl_pass_way);
        tv_pa = popview.findViewById(R.id.tv_pa);
        rl_pass_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassWay();
            }
        });
        rl_code = popview.findViewById(R.id.rl_code);
        rl_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCode();
            }
        });
        tv_code = popview.findViewById(R.id.tv_code);


        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new SSKMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(ll_wifi, Gravity.CENTER, 0, 0);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocolBluetooth.sendData_ConfigDeviceParams(tv_wifi.getText().toString(), ed_wifi.getText().toString());
                SharedPreferences.Editor editor =sp.edit();
                editor.putString(tv_wifi.getText().toString(),ed_wifi.getText().toString());
                editor.apply();
                popuPhoneW.dismiss();
                Toast.makeText(context, "连接WiFi中...", Toast.LENGTH_SHORT).show();
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
        popview = LayoutInflater.from(SSKMainActivity.this).inflate(R.layout.wifi_list_dialog, null);
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
        wifi_popuPhoneW.showAtLocation(ll_wifi, Gravity.CENTER, 0, 0);


    }

    RecyclerView way_list;
    String[] strings = {"OPEN","WPA AES","WPA2 AES","WPA/WPA2 AES","WPA TKIP","WPA2 TKIP","WPA/WPA2 TKIP","WEP"};
    List<String> wp_list = Arrays.asList(strings);
    PassWayAdapter passWayAdapter;
    @SuppressLint("WrongConstant")
    public void showPassWay() {
        Log.e( "showPassWay: ", wp_list.size()+"");
        View popview;
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(SSKMainActivity.this).inflate(R.layout.pass_way_dialog, null);
        //搜索WiFi并展示列表
        list = searchWifi();
        way_list = popview.findViewById(R.id.way_list);
        passWayAdapter = new PassWayAdapter(wp_list, context, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        way_list.setAdapter(passWayAdapter);
        way_list.setLayoutManager(layoutManager);


        wifi_popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wifi_popuPhoneW.setFocusable(true);
        wifi_popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        wifi_popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        wifi_popuPhoneW.setOutsideTouchable(true);
        wifi_popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        wifi_popuPhoneW.showAsDropDown(rl_pass_way);


    }

    @SuppressLint("WrongConstant")
    public void showCode() {
        Log.e( "showPassWay: ", wp_list.size()+"");
        View popview;
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(SSKMainActivity.this).inflate(R.layout.code_dialog, null);
        RelativeLayout rl_0 = popview.findViewById(R.id.rl_0);



        wifi_popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wifi_popuPhoneW.setFocusable(true);
        wifi_popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        wifi_popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        wifi_popuPhoneW.setOutsideTouchable(true);
        wifi_popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        wifi_popuPhoneW.showAsDropDown(rl_code);
        rl_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_code.setText("utf-8");
                wifi_popuPhoneW.dismiss();
            }
        });
        RelativeLayout rl_1 = popview.findViewById(R.id.rl_1);
        rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_code.setText("GBK");
                wifi_popuPhoneW.dismiss();
            }
        });

    }

    //点击wifi
    @Override
    public void onItemClick(View view) {
        int position = wifi_list.getChildLayoutPosition(view);
        tv_wifi.setText(list.get(position).SSID);
        String pwd = sp.getString(list.get(position).SSID,"");
        ed_wifi.setText(pwd);
        wifi_popuPhoneW.dismiss();
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    @Override
    public void doWork_ConnectBluetoothReturn(ConnectStateBean connectStateBean) {

    }

    @Override
    public void doWork_ConfigDeviceParamsReturn(ConfigStateBean configStateBean) {

    }

    @Override
    public void doWork_ReadDeviceStateReturn(DeviceStateBean deviceStateBean) {
        switch (deviceStateBean.netState) {
            case Net_OK:
                ToastCustom.showToast(context, "设备联网正常！！！", Toast.LENGTH_SHORT);
                break;
            case Net_Connecting:
                ToastCustom.showToast(context, "设备正在尝试联网...", Toast.LENGTH_SHORT);
                break;
            case Net_Failure:
                ToastCustom.showToast(context, "设备联网失败！！！可能是参数不正确！！！", Toast.LENGTH_LONG);
                break;

            default:
                ToastCustom.showToast(context, "设备状态未知==" + deviceStateBean.netState.getValue() + "，请稍候...", Toast.LENGTH_SHORT);
                break;
        }
    }

    //点击加密方式
    @Override
    public void OnWayClick(View view) {
        int position = way_list.getChildLayoutPosition(view);
        tv_pa.setText(wp_list.get(position));
        wifi_popuPhoneW.dismiss();
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

    Timer timer;
    TimerTask task;

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //do something
            try {
                loopData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    BluetoothDevice device;
                    switch (action) {
                        case BluetoothDevice.ACTION_FOUND:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            LogTool.println("发现蓝牙" + device.getName() + "[" + device.getAddress() + "]" + device.getType(),
                                    LogTool.DebugImportant);

                            addSSKList(device);
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            ToastCustom.showToast(SSKMainActivity.this, "搜索设备完毕。",
                                    Toast.LENGTH_SHORT);
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            if (mBluetoothAdapter.isEnabled()) {

                            } else {
                                ToastCustom.showToast(SSKMainActivity.this, "蓝牙已关闭！", Toast.LENGTH_SHORT);
                            }
                            break;
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            switch (device.getBondState()) {
                                case BluetoothDevice.BOND_BONDING:
                                    LogTool.println("正在配对......");
                                    break;
                                case BluetoothDevice.BOND_BONDED:
                                    LogTool.println("完成配对");
                                    if (DeviceSensor_Selected != null
                                            && DeviceSensor_Selected.Device.Device_Bluetooth.equals(device)) {
                                        ProtocolBluetooth.connectSensor(DeviceSensor_Selected);
                                    } else {
                                        ToastCustom.showToast(SSKMainActivity.this, "配对成功，请点击设备进行连接！",
                                                Toast.LENGTH_SHORT);
                                    }
                                    break;
                                case BluetoothDevice.BOND_NONE:
                                    LogTool.println("取消配对");
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case ConstantsBluetooth.ACTION_PAIRING_REQUEST:
                            ToastCustom.showToast(SSKMainActivity.this, "请输入蓝牙PIN码，默认0000", Toast.LENGTH_SHORT);
                            break;

                        default:
                            LogTool.println("未处理的广播Action！==" + action, LogTool.DebugImportant);
                            break;
                    }
                }
            }
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
