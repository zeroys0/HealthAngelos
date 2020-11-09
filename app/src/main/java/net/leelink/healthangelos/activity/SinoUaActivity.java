package net.leelink.healthangelos.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.sinocare.multicriteriasdk.MulticriteriaSDKManager;
import com.sinocare.multicriteriasdk.SnCallBack;
import com.sinocare.multicriteriasdk.bean.DeviceDetectionData;
import com.sinocare.multicriteriasdk.entity.BoothDeviceConnectState;
import com.sinocare.multicriteriasdk.entity.SNDevice;
import com.sinocare.multicriteriasdk.utils.LogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MsgListAdapter;
import net.leelink.healthangelos.adapter.SinoUaAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BloodSugarBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Consumer;

public class SinoUaActivity extends BaseActivity {
    private static final String TAG = SinoMainActivity.class.getSimpleName();
    List<SNDevice> snDevices = new ArrayList<>();


    public Map<String, BoothDeviceConnectState> stateHashMap = new HashMap<>();
    private int count= 0;
    private TextView tv,tv_blood_sugar;
    private RelativeLayout rl_back;
    private RecyclerView data_list;
    Context context;
    SinoUaAdapter sinoUaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sino_ua);
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
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_blood_sugar = findViewById(R.id.tv_blood_sugar);
        data_list = findViewById(R.id.data_list);

    }

    public void initData(){
        HttpParams params = new HttpParams();
        params.put("pageNum",1);
        params.put("pageSize",100);
        showProgressBar();
        OkGo.<String>get(Urls.SANNUOBLOODURICLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("血尿酸记录", json.toString());
                            if (json.getInt("status") == 200) {

                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<BloodSugarBean> bloodSugarBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BloodSugarBean>>(){}.getType());
                                sinoUaAdapter = new SinoUaAdapter(bloodSugarBeans,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                data_list.setLayoutManager(layoutManager);
                                data_list.setAdapter(sinoUaAdapter);
                            } else if(json.getInt("status") ==505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void startConnect() {
        MulticriteriaSDKManager.startConnect(snDevices, new SnCallBack() {
            @Override
            public void onDataComing(SNDevice device, DeviceDetectionData data) {
                LogUtils.d(TAG, "onDataComing: ------snDevice---" + device.toString());
                LogUtils.d(TAG, "onDataComing: -----data----" + data);
                String msg = device.getDesc() + "收到数据：(" + data.toString() + ")";
                MsgListAdapter.DeviceListItem deviceListItem = new MsgListAdapter.DeviceListItem(msg, false);
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


                    double blood_sugar = data.getSnDataEaka().uaResult;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tv_blood_sugar.setText(decimalFormat.format(blood_sugar)+ "μmol/L");
//                    backgroundAlpha(0.5f);
//                    showPopu(blood_sugar,data.getCreateTime());
                    updateData(blood_sugar, data.getCreateTime());
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

                        break;
                }

            }
        });
        MulticriteriaSDKManager.onResume();
    }

    public void updateData(double bloodSugar,String createTime){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("uric",bloodSugar);
            jsonObject.put("detectionTime",createTime+":00");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(Urls.UPLOADUA)
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
                            Log.d("上传血尿酸检测结果", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                                initData();
                            }else if(json.getInt("status") == 505){
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
