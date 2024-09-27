package net.leelink.healthangelos.activity.ahaFit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.just.agentweb.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import io.reactivex.disposables.Disposable;

public class AhaFitHeartRateActivity extends BaseActivity {

    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_data;
    AgentWeb agentweb;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mTestingHealthyDisposable;
    private PopupWindow popuPhoneW;
    private View popview;
    private int heartRate;

    private TextView text_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_heart_rate);
        context = this;
        init();
        popu_head();
    }

    public void init() {
        ll_data = findViewById(R.id.ll_data);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_title = findViewById(R.id.text_title);
        text_title.setText("ahafit 腕表");
        setWeb(Urls.getInstance().FIT_H5 + "/HeartRate/index/" + MyApplication.userInfo.getOlderlyId() + "/" + MyApplication.token);
    }

    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(AhaFitHeartRateActivity.this)
                    .setAgentWebParent(ll_data, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App", this)
                    .addJavascriptInterface("$App", this)
                    .createAgentWeb()
                    .ready()
                    .go(url);
            agentweb.clearWebCache();
        } else {
            ll_data.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);

            ll_data.setVisibility(View.VISIBLE);
        }

    }

    BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic mWritableCharacteristic;

    @JavascriptInterface
    public void startMeasureWatch(String msg) {


        //做原生操作
        Log.e("getDataFormVue: ", msg);
        BluetoothDevice bluetoothDevice = BleManager.getInstance().getConnectedDevice();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mCallback);
        }
        if (mBluetoothGatt == null) {
            Toast.makeText(context, "gatt空", Toast.LENGTH_SHORT).show();
        }

//        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E"));
//        if(service ==null){
//            Toast.makeText(context, "service空", Toast.LENGTH_SHORT).show();
//        }


//        services.get(0).getCharacteristic(UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E"));

//        List<BluetoothGattService> services = mBluetoothGatt.getServices();
//        for (BluetoothGattService service : services) {
//            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
//            for (BluetoothGattCharacteristic characteristic : characteristics) {
//                // 寻找具有写权限的特性
//                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
//                    // 找到了可写的特性
//                    mWritableCharacteristic = characteristic;
//                }
//            }
//        }

    }

    private void sendCommand(byte[] command) {
        if (mWritableCharacteristic != null && mBluetoothGatt != null) {
            mWritableCharacteristic.setValue(command);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            boolean status = mBluetoothGatt.writeCharacteristic(mWritableCharacteristic);
            if (!status) {
                Log.e("TAG", "Failed to write to characteristic.");
            }
        } else {
            Log.e("TAG", "Characteristic or GATT not initialized.");
        }
    }

    BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("BluetoothGatt", status + "\n" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.d("Bluetooth", "Device connected. Discovering services...");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("Bluetooth", "Device disconnected.");
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 服务发现成功
                handleServicesDiscovered();
            } else {
                // 服务发现失败
                Log.e("Bluetooth", "Service discovery failed with status: " + status);
            }
        }
    };

    // 处理服务发现后的逻辑
    private void handleServicesDiscovered() {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E"));
            if (service != null) {
                Log.i("Bluetooth", "Service found: " + service.getUuid());
                // 进一步处理服务
                mWritableCharacteristic = service.getCharacteristic(UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E"));
                byte[] command = new byte[]{0x51, 0x01};
                sendCommand(command);
            } else {
                Log.e("Bluetooth", "Service not found: 6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E");
            }
        } else {
            Log.e("Bluetooth", "mBluetoothGatt is null");
        }
    }

    //点击历史数据
    @JavascriptInterface
    public void getListByTimeHeartRate(String msg, String id) {
        Log.e("getListByTime: ", msg);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("time", msg);
        bundle.putString("id", id);
        message.setData(bundle);
        message.what = 3;
        myHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                popuPhoneW.dismiss();
            } else if (msg.what == 3) {
                String time = msg.getData().getString("time");
                String id = msg.getData().getString("id");
                setWeb(Urls.getInstance().FIT_H5 + "/HeartRateHistory/" + time + "/" + id + "/" + MyApplication.token);
            } else {
                popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
            }

        }
    };

    public void upLoad() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject data = new JSONObject();
        try {
            data.put("heartRate", heartRate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            data.put("testTime", time);
            jsonArray.put(data);
            jsonObject.put("fitHeartRateList", jsonArray);
            jsonObject.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().FIT_UPLOAD)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传心率数据", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "心率上传成功", Toast.LENGTH_SHORT).show();
                                agentweb.getWebCreator().getWebView().reload();
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


    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(AhaFitHeartRateActivity.this).inflate(R.layout.popu_fit_health, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new AhaFitHeartRateActivity.poponDismissListener());
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