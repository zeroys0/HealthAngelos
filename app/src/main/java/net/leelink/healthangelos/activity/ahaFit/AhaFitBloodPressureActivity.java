package net.leelink.healthangelos.activity.ahaFit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
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

import androidx.core.app.ActivityCompat;
import io.reactivex.disposables.Disposable;

public class AhaFitBloodPressureActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_data;
    AgentWeb agentweb;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mTestingHealthyDisposable;
    private PopupWindow popuPhoneW;
    private View popview;
    private int pressure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_blood_pressure);
        context = this;
        init();
        mBluetoothGatt = BleManager.getInstance().getBluetoothGatt();
        mWritableCharacteristic = BleManager.getInstance().getWritableCharacteristic();
        popu_head();
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("com.ble.bloodPressure"));
    }

    public void init(){
        ll_data = findViewById(R.id.ll_data);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setWeb(Urls.getInstance().FIT_H5+"/Blood/index/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }

    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(AhaFitBloodPressureActivity.this)
                    .setAgentWebParent(ll_data, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App", this)
                    .createAgentWeb()
                    .ready()
                    .go(url);
            agentweb.clearWebCache();
        } else {
            agentweb.clearWebCache();
            Log.e("getDataFormVue: ", "刷新");
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
        byte[] command = new byte[]{0x60, 0x01, 0x01};
        sendCommand(command);


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
            myHandler.sendEmptyMessageDelayed(0, 0);
            if (!status) {
                Log.e("TAG", "Failed to write to characteristic.");
            }
        } else {
            Log.e("TAG", "Characteristic or GATT not initialized.");
        }
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            byte bytetype = data[0];
            int type = bytetype & 0xFF;
            Log.d( "TAG: ",type + "");
            if (type == 225) {
                //血氧测量结果
                byte byteValue5 = data[3];
                int dia =(byteValue5 & 0xFF);
                byte byteValue6 = data[2];
                int sys =(byteValue6 & 0xFF);
                Log.d( "TAG: ",dia + "/"+sys);

                Message message = new Message();
                message.what = 225;
                Bundle bundle = new Bundle();
                bundle.putInt("dia", dia);
                bundle.putInt("sys", sys);
                message.setData(bundle);
                myHandler.sendMessage(message);
            } else {
                Log.e( "TAG: ", "error");
            }
        }
    };


    //点击历史数据
    @JavascriptInterface
    public void getListByTimeBlood(String msg,String id) {
        Log.e("getListByTime: ", msg);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("time",msg);
        message.setData(bundle);
        message.what = 3;
        myHandler.sendMessage(message);
    }

    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(AhaFitBloodPressureActivity.this).inflate(R.layout.popu_fit_health, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new AhaFitBloodPressureActivity.poponDismissListener());
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==225){
                int dia = msg.getData().getInt("dia");
                int sys = msg.getData().getInt("sys");
                upLoad(dia,sys);
            }else if(msg.what ==3){
                String time = msg.getData().getString("time");
                setWeb(Urls.getInstance().FIT_H5+"/BloodHistory/"+time+"/"+MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
            } else {
                popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
            }

        }
    };

    public void upLoad(int dia,int sys){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject bloodpressure = new JSONObject();
        try {
            bloodpressure.put("dbp",dia);
            bloodpressure.put("sbp",sys);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            bloodpressure.put("testTime",time);
            jsonArray.put(bloodpressure);
            jsonObject.put("fitBloodPressureList",jsonArray);
            jsonObject.put("elderlyId",MyApplication.userInfo.getOlderlyId());
            jsonObject.put("imei", getIntent().getStringExtra("imei"));
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
                            Log.d("上传血压数据", json.toString());
                            if (json.getInt("status") == 200) {
                                popuPhoneW.dismiss();
                                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                                agentweb.getWebCreator().getWebView().reload();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        popuPhoneW.dismiss();
                        Toast.makeText(context, "数据上传失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
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