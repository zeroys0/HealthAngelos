package net.leelink.healthangelos.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
import net.leelink.healthangelos.adapter.SinoBloodSugarAdapter;
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

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Consumer;

public class SinoBloodSugarActivity extends BaseActivity {

    private static final String TAG = SinoMainActivity.class.getSimpleName();
    List<SNDevice> snDevices = new ArrayList<>();


    public Map<String, BoothDeviceConnectState> stateHashMap = new HashMap<>();
    private int count= 0;
    private TextView tv,tv_blood_sugar;
    private RelativeLayout rl_back;
    private RecyclerView data_list;
    Context context;
    SinoBloodSugarAdapter sinoBloodSugarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sino_blood_sugar);
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
                    Toast.makeText(SinoBloodSugarActivity.this, "请先给设备赋权限", Toast.LENGTH_SHORT);
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
        OkGo.<String>get(Urls.getInstance().SANNUOBLOODSUGARLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("血糖记录", json.toString());
                            if (json.getInt("status") == 200) {

                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<BloodSugarBean> bloodSugarBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BloodSugarBean>>(){}.getType());
                                sinoBloodSugarAdapter = new SinoBloodSugarAdapter(bloodSugarBeans,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                data_list.setLayoutManager(layoutManager);
                                data_list.setAdapter(sinoBloodSugarAdapter);
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
                    double blood_sugar = data.getSnDataEaka().glucose;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tv_blood_sugar.setText(decimalFormat.format(blood_sugar)+ "mmol/L");
                    backgroundAlpha(0.5f);
                    showPopu(blood_sugar,data.getCreateTime());
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

    public void showPopu(double bloodSugar,String createTime){
        View popView = getLayoutInflater().inflate(R.layout.pop_update, null);
        ImageView img_close = popView.findViewById(R.id.img_close);
        RadioButton rb_before = popView.findViewById(R.id.rb_before);
        RadioButton rb_after = popView.findViewById(R.id.rb_after);
        Button btn_cancel = popView.findViewById(R.id.btn_cancel);
        Button btn_confirm = popView.findViewById(R.id.btn_confirm);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pop.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb_before.isChecked()) {
                    updateData(bloodSugar, createTime,1);
                }
                if(rb_after.isChecked()){
                    updateData(bloodSugar, createTime,2);
                }
                pop.dismiss();

            }
        });

        pop.showAtLocation(rl_back, Gravity.CENTER,0,0);
        pop.setOnDismissListener(new SinoBloodSugarActivity.poponDismissListener());
    }

    public void updateData(double bloodSugar,String createTime,int suCondition){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("bloodSugar",bloodSugar);
            jsonObject.put("detectionTime",createTime+":00");
            jsonObject.put("suCondition",suCondition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().UPLOADBLOODSUGAR)
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
                            Log.d("上传血糖记录", json.toString());
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


    @Override
    public void finish() {
        super.finish();
        MulticriteriaSDKManager.disConectDevice(snDevices);
        snDevices.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy: ");
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

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }
}
