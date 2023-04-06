package net.leelink.healthangelos.activity.ssk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Logger;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
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
import io.reactivex.functions.Consumer;

public class BindSSKActivity extends BaseActivity implements View.OnClickListener, ProtocolBluetoothCallbackInterface, OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView device_list;
    private Button btn_search;
    private static final String ApiKey = "1b482efa715e448e9b659c69e548e046";
    private boolean bool_BLE = false;//是否使用低功耗蓝牙
    private boolean Bool_ConnectSensor = false;// 传感器连接是否成功
    private ArrayList<BluetoothDevice> BluetoothDeviceList = null;// 蓝牙设备列表
    private ArrayList<SensorBean> SensorList = null;// 传感器列表
    private SensorBean DeviceSensor_Selected = null;// 选择的传感器
    private ArrayList<SSKDeviceBean> list = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter = null;
    DeviceListAdapter adapter;
    private MyBroadcastReceiver receiver = null;
    private TextView tv_unbind;

    /**
     * 是否正在扫描BLE设备
     */
    private boolean mScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_ssk);
        context = this;
        init();
        createProgressBar(context);
        try {
            bool_BLE = BindSSKActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
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
        requestPermissions();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        device_list = findViewById(R.id.device_list);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        SensorList = new ArrayList<>();
        adapter = new DeviceListAdapter(context, list, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        device_list.setAdapter(adapter);
        device_list.setLayoutManager(layoutManager);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);

        //定义广播事件监听
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsBluetooth.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        BindSSKActivity.this.registerReceiver(receiver, filter);
        BluetoothDeviceList = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_search:
                scanDevice();
                break;
            case R.id.tv_unbind:
                doGetPermission();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                    Log.e( "onActivityResult: ", result);
                    bindDevice(result);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void scanDevice() {
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
                BindSSKActivity.this.finish();
            } else if (!mBluetoothAdapter.isEnabled()) {// 如果本地蓝牙没有开启，则开启
                ToastCustom.showToast(context, "打开蓝牙！", Toast.LENGTH_SHORT);
                mBluetoothAdapter.enable();
            } else {
                if (bool_BLE) {
                    if (!mScanning) {
                        ToastCustom.showToast(context, "正在搜索附近的睡睡康设备...", TimeSpace.SECOND1);
                        BluetoothDeviceList.clear();
                        SensorList.clear();
                        adapter.notifyDataSetChanged();

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
                        adapter.notifyDataSetChanged();

                        mBluetoothAdapter.startDiscovery();// 开始发现过程;
                    }
                }
            }
        }
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
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
                            getOnlineState(sensorBean);
                            ToastCustom.showToast(context, "发现睡睡康设备：" + sensorBean.getSensorName(), TimeSpace.SECOND1);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFail(String message) {
//                    ToastCustom.showToast(context, "蓝牙[" + device.getName() + "]获取传感器信息失败！" + message, Toast.LENGTH_SHORT);
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

    public void getOnlineState(SensorBean sensorBean){
        //获取设备在线状态
        OkGo.<String>get(Urls.getInstance().CHECKDEVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceIds",sensorBean.Device.DeviceNick)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray=  json.getJSONArray("data");
                                boolean is_bind;
                                if(jsonArray.getJSONObject(0).isNull("elderlyId")){
                                    is_bind = false;
                                } else {
                                    is_bind = true;
                                }
                                list.add(new SSKDeviceBean(sensorBean,jsonArray.getJSONObject(0).getBoolean("isOnline"),is_bind));
                                adapter.notifyDataSetChanged();
                            } else if (json.getInt("status") == 505) {

                            } else {

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


    /**
     * 蓝牙连接结果返回
     *
     * @param connectStateBean 蓝牙连接状态对象
     */
    @Override
    public void doWork_ConnectBluetoothReturn(ConnectStateBean connectStateBean) {
        Bool_ConnectSensor = connectStateBean.bool_ConnectResult;
        if (Bool_ConnectSensor) {
//			DeviceBean deviceBean_Connect = connectStateBean.deviceBean_Connect;
            SensorBean sensorBean_Connect = connectStateBean.sensorBean_Connect;
            //   ToastCustom.showToast(context, "设备" + sensorBean_Connect.getSensorName() + "连接成功！！！", Toast.LENGTH_SHORT);

            bindDevice(sensorBean_Connect.Device.DeviceNick);

        } else {
            boolean bool_NeedScan = connectStateBean.bool_NeedScan;
            if (bool_NeedScan) {
                new AlertDialog.Builder(context)
                        .setTitle("警告")
                        .setMessage("发现设备失败，请确保设备在附近并且电源已开启，您可以点击搜索重新扫描附近的设备。")
                        .setPositiveButton("确 定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).show();
            } else {
                int count = connectStateBean.tryTimes;
                if (count > 0) {
                    ToastCustom.showToast(context,
                            "连接设备失败，重新尝试连接...剩余尝试次数：" + count, TimeSpace.SECOND0_5);
                } else {
                    ToastCustom.showToast(context, "设备已断开连接！",
                            TimeSpace.SECOND1);
                }
            }
        }

        //设备列表更新
        // adapter.notifyDataSetInvalidated();
    }

    /**
     * 处理配置设备参数结果
     *
     * @param configStateBean 配置参数结果对象
     */
    @Override
    public void doWork_ConfigDeviceParamsReturn(ConfigStateBean configStateBean) {
        if (configStateBean.bool_ConfigResult) {
            ToastCustom.showToast(context, "配置参数返回成功！！！", Toast.LENGTH_SHORT);
        } else {
            ToastCustom.showToast(context, "配置参数返回失败！！！", Toast.LENGTH_LONG);
        }
    }

    /**
     * 处理读取设备状态结果
     *
     * @param deviceStateBean 设备状态对象
     */
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

    @Override
    public void onItemClick(View view) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onButtonClick(View view, int position) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        SensorBean dataBean = SensorList.get(position);
        Bool_ConnectSensor = false;
        DeviceSensor_Selected = dataBean;
        BluetoothDevice device = dataBean.Device.Device_Bluetooth;

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
                ToastCustom.showToast(BindSSKActivity.this, "正在配对蓝牙，请稍候...",
                        Toast.LENGTH_SHORT);
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
                            ToastCustom.showToast(BindSSKActivity.this, "搜索设备完毕。",
                                    Toast.LENGTH_SHORT);
                            Log.e( "onReceive: ", "搜索完毕");
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            if (mBluetoothAdapter.isEnabled()) {
                                scanDevice();
                            } else {
                                ToastCustom.showToast(BindSSKActivity.this, "蓝牙已关闭！", Toast.LENGTH_SHORT);
                            }
                            break;
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                            adapter.notifyDataSetChanged();
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
                                        ToastCustom.showToast(BindSSKActivity.this, "配对成功，请点击设备进行连接！",
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
                            ToastCustom.showToast(BindSSKActivity.this, "请输入蓝牙PIN码，默认0000", Toast.LENGTH_SHORT);
                            break;

                        default:
                            LogTool.println("未处理的广播Action！==" + action, LogTool.DebugImportant);
                            break;
                    }
                }
            }
        }
    }

    public void bindDevice(String id) {

        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        httpParams.put("imei", id);
        Log.e("elderlyId: ", MyApplication.userInfo.getOlderlyId());
        Log.e("imei: ", id);
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().SSK_BIND)
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
                            Log.d("绑定ssk设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "绑定完成", Toast.LENGTH_SHORT).show();
                                showsucess(id);
                                Intent intent = new Intent(context, SSKMainActivity.class);
                                intent.putExtra("imei", id);
                                startActivity(intent);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
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


    @SuppressLint("WrongConstant")
    public void showsucess(String name) {
        View popview = LayoutInflater.from(BindSSKActivity.this).inflate(R.layout.popu_sucess, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new BindSSKActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        ImageView img_close = popview.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        TextView tv_number = popview.findViewById(R.id.tv_number);
        tv_number.setText("设备编号:" + name);
        backgroundAlpha(0.5f);
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

            finish();
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

    @SuppressLint("CheckResult")
    public boolean requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(BindSSKActivity.this);
        rxPermission.requestEach(
                Manifest.permission.ACCESS_FINE_LOCATION)   //获取位置
                .subscribe(new Consumer<com.tbruyelle.rxpermissions2.Permission>() {
                    @Override
                    public void accept(com.tbruyelle.rxpermissions2.Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.i("用户已经同意该权限", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Logger.i("用户拒绝了该权限,没有选中『不再询问』", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.i("用户拒绝了该权限,并且选中『不再询问』", permission.name + " is denied.");
                            Toast.makeText(context, "您已经拒绝该权限,并选择不再询问,请在权限管理中开启权限使用本功能", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BindSSKActivity.this.unregisterReceiver(receiver);
    }
}
