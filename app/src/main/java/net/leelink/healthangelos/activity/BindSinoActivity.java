package net.leelink.healthangelos.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sinocare.multicriteriasdk.MulticriteriaSDKManager;
import com.sinocare.multicriteriasdk.ScanCallBack;
import com.sinocare.multicriteriasdk.entity.SNDevice;
import com.sinocare.multicriteriasdk.entity.SnBoothType;
import com.sinocare.multicriteriasdk.utils.LogUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DeviceScanListAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BindSinoActivity extends BaseActivity {
    ListView deviceLv;
    RelativeLayout rl_back;
    DeviceScanListAdapter adapter;
    int snDeviceType;
    private SNDevice snDevice;
    Button btn_startscan;

    //Bluetooth Device scan callback.
    private final BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            LogUtils.d( "onReceive: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                adapter.addDevice(device);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_sino);
        snDeviceType = getIntent().getIntExtra("snDeviceType", 8);
        snDevice = new SNDevice(snDeviceType);
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        scanDevice(snDevice.getSnBoothType().getDesc().equals(SnBoothType.BLE) || snDevice.getSnBoothType().getDesc().equals(SnBoothType.BLE_NO_CONNECT));
    }

    protected void scanDevice(boolean isBle) {
        scanBlueTooth(isBle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    /**
     * 停止扫描
     */
    protected void stopScan() {
        MulticriteriaSDKManager.stopScan();
    }

    public void init(){
        deviceLv = findViewById(R.id.list);
        btn_startscan = findViewById(R.id.btn_startscan);
        btn_startscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan(v);
            }
        });
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new DeviceScanListAdapter(this);
        deviceLv.setAdapter(adapter);
        deviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBundleDialog(position);
            }
        });
    }

    private void showBundleDialog(final int position) {
        final BluetoothDevice device = adapter.deviceList.get(position);
        new AlertDialog.Builder(BindSinoActivity.this, R.style.common_dialog).setTitle("提示").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bindDevice(device.getAddress());


            }
        }).setMessage("您确定绑定该设备吗？\n" + "  mac:" + device.getAddress())
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * 扫描传统蓝牙设备
     *
     * @param enable
     */
    public void scanBlueTooth(final boolean enable) {

        MulticriteriaSDKManager.scanDevice(getApplication(),"", enable,100, new ScanCallBack() {

            @Override
            public void getScanResult(BluetoothDevice scanResult) {
                LogUtils.d( "getScanResult: " + scanResult.toString());
                adapter.addDevice(scanResult);
            }

            @Override
            public void complete() {

            }

            @Override
            public void getData(BluetoothDevice bluetoothDevice, byte[] data) {

            }

        });
        LogUtils.d("查找经典蓝牙", "开始扫描"+ "  "+enable);
    }

    public void startScan(View view) {
        stopScan();
        adapter.clearData();
        scanDevice(snDevice.getSnBoothType().getDesc().equals(SnBoothType.BLE));
    }

    public void bindDevice(String mac) {
        JSONObject json = new JSONObject();
        try {
            json.put("deviceId",getIntent().getIntExtra("deviceId",0));
            json.put("modelId",getIntent().getIntExtra("modelId",0));
            json.put("imei",mac);
            json.put("isBluetooth",1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "bindDevice: ", json.toString());
        OkGo.<String>post(Urls.getInstance().BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(BindSinoActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                SNDevice snDevice = new SNDevice(snDeviceType, mac);
                                Intent intent = new Intent(BindSinoActivity.this,SinoMainActivity.class);
                                ArrayList<SNDevice> snDevices = new ArrayList<>();
                                snDevices.add(snDevice);
                                intent.putExtra("snDevices", snDevices);
                                startActivity(intent);
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(BindSinoActivity.this);
                            }  else {
                                Toast.makeText(BindSinoActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
