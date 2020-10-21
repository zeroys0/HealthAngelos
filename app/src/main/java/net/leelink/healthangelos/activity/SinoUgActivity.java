package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Consumer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinocare.multicriteriasdk.MulticriteriaSDKManager;
import com.sinocare.multicriteriasdk.SnCallBack;
import com.sinocare.multicriteriasdk.bean.DeviceDetectionData;
import com.sinocare.multicriteriasdk.entity.BoothDeviceConnectState;
import com.sinocare.multicriteriasdk.entity.SNDevice;
import com.sinocare.multicriteriasdk.utils.LogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MsgListAdapter;
import net.leelink.healthangelos.adapter.SinoBloodSugarAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinoUgActivity extends BaseActivity {
    private static final String TAG = SinoUgActivity.class.getSimpleName();
    List<SNDevice> snDevices = new ArrayList<>();


    public Map<String, BoothDeviceConnectState> stateHashMap = new HashMap<>();
    private int count= 0;
    private TextView tv,tv_mac,tv_connect,tv_blood_sugar;
    private RelativeLayout rl_back;
    private RecyclerView data_list;
    Context context;
    SinoBloodSugarAdapter sinoBloodSugarAdapter;
    private RelativeLayout rl_blood_sugar,rl_ua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sino_ug);
        setTitle("测量界面");
        context = this;
        createProgressBar(this);
        tv = findViewById(R.id.tv);
        ArrayList<SNDevice> bleDevices = getIntent().getParcelableArrayListExtra("snDevices");
        if (bleDevices == null) {
            Toast.makeText(this, "设备选择不正确", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        snDevices.addAll(bleDevices);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startConnect();
                } else {
                    Toast.makeText(context, "请先给设备赋权限", Toast.LENGTH_SHORT);
                }
            }
        });
        init();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_mac = findViewById(R.id.tv_mac);
        tv_connect = findViewById(R.id.tv_connect);
        tv_blood_sugar = findViewById(R.id.tv_blood_sugar);
        data_list = findViewById(R.id.data_list);
        tv_mac.setText(snDevices.get(0).getMac());
        rl_blood_sugar = findViewById(R.id.rl_blood_sugar);
        rl_blood_sugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SinoBloodSugarActivity.class);
                intent.putExtra("snDevices", getIntent().getParcelableArrayListExtra("snDevices"));
                startActivity(intent);
            }
        });
        rl_ua = findViewById(R.id.rl_ua);
        rl_ua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SinoUaActivity.class);
                intent.putExtra("snDevices", getIntent().getParcelableArrayListExtra("snDevices"));
                startActivity(intent);
            }
        });
    }


    /**
     * 开始测量
     *
     * @return void
     * @author zhongzhigang
     * @time 2019/2/26 20:50
     */
    private void startConnect() {
        MulticriteriaSDKManager.startConnect(snDevices, new SnCallBack() {
            @Override
            public void onDataComing(SNDevice device, DeviceDetectionData data) {
                LogUtils.d(TAG, "onDataComing: ------snDevice---" + device.toString());
                LogUtils.d(TAG, "onDataComing: -----data----" + data);
                String msg = device.getDesc() + "收到数据：(" + data.toString() + ")";
                MsgListAdapter.DeviceListItem deviceListItem = new MsgListAdapter.DeviceListItem(msg, false);
                float f = data.getSnDataEaka().uaResult;
                if(data.getSnDataEaka().HI) {
                    tv_blood_sugar.setText("过高");
//                    backgroundAlpha(0.5f);
//                    showPopu("过高",data.getCreateTime());
                    return;
                }
                else if(data.getSnDataEaka().Lo) {
                    tv_blood_sugar.setText("过低");
//                    backgroundAlpha(0.5f);
//                    showPopu("过低",data.getCreateTime());
                    return;

                } else {
                    double blood_sugar = data.getSnDataEaka().glucose;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tv_blood_sugar.setText(decimalFormat.format(blood_sugar)+ "mmol/L");
//                    backgroundAlpha(0.5f);
//                    showPopu(blood_sugar,data.getCreateTime());
                }

            }

            @Override
            public void onDeviceStateChange(SNDevice device, BoothDeviceConnectState state) {
                LogUtils.d(TAG, "onDeviceStateChange: -----snDevice----" + device.toString());
                switch (state.getmState()) {
                    case BoothDeviceConnectState.DEVICE_STATE_CONNECTED:
                    case BoothDeviceConnectState.DEVICE_STATE_DISCONNECTED:
                        BoothDeviceConnectState state1 = stateHashMap.get(device.getMac());
                        if (state1 != null && state1.getmState() == state.getmState()) {
                            return;
                        }
                        stateHashMap.put(device.getMac(), state);
                        String extendString = "";
                        if (device.getType() == SNDevice.DEVICE_GPRINT) {
                            extendString = "（点击打印样板）";
                        }
                        String msg = device.getDesc() + "(" + state.getDesc() + ")";
                        boolean isSiri = false;
                        MsgListAdapter.DeviceListItem deviceListItem = new MsgListAdapter.DeviceListItem(msg + extendString, isSiri);
                        deviceListItem.setSnDevice(device);
                        deviceListItem.setState(state);
                        tv_connect.setText(deviceListItem.getState().getDesc());

                        break;
                    case BoothDeviceConnectState.DEVICE_STATE_START_TEST:
                    case BoothDeviceConnectState.DEVICE_STATE_SHUTDOWN:
                    case BoothDeviceConnectState.DEVICE_STATE_BLOOD_SPARKLING:
                    case BoothDeviceConnectState.DEVICE_STATE_TIME_SET_SUCCESS:
                    case BoothDeviceConnectState.DEVICE_STATE_CLEAN_DATA_FAIL:
                    case BoothDeviceConnectState.DEVICE_STATE_CLEAN_DATA_SUCCESS:
                    case BoothDeviceConnectState.DEVICE_STATE_CONNECTION_SUCCESS:
                    case BoothDeviceConnectState.DEVICE_STATE_NO_DATA:
                        MsgListAdapter.DeviceListItem deviceList = new MsgListAdapter.DeviceListItem(device.getDesc() + "(" + state.getDesc() + ")" + "", false);
                        deviceList.setSnDevice(device);
                        deviceList.setState(state);
                        tv_connect.setText(deviceList.getState().getDesc());
                        break;
                }

            }
        });
        MulticriteriaSDKManager.onResume();
    }
}
