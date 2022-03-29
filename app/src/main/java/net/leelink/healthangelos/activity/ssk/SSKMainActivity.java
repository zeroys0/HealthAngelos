package net.leelink.healthangelos.activity.ssk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SSKMainActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back;
    private Context context;
    private LinearLayout ll_wifi, ll_rgb, ll_sleep_chart;
    private RecyclerView wifi_list;
    private TextView tv_unbind, tv_heart, tv_breath, tv_time,tv_wifi;
    private WiFiAdapter wiFiAdapter;
    //wifi列表
    private List<ScanResult> list;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sskmain);
        context = this;
        createProgressBar(context);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new SSKMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 5000);
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
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_heart = findViewById(R.id.tv_heart);
        tv_breath = findViewById(R.id.tv_breath);
        tv_time = findViewById(R.id.tv_time);

    }

    public void loopData() {
        //获取设备连接状态

        OkGo.<String>get(Urls.getInstance().LISTREID)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sensorId", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备状态", json.toString());
                            if (json.getInt("status") == 200) {

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

        //获取实时数据
        OkGo.<String>get(Urls.getInstance().LISTRMS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sensorId", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取实时数据", json.toString());
                            if (json.getInt("status") == 200) {
//                                tv_heart = findViewById(R.id.tv_heart);
//                                tv_breath = findViewById(R.id.tv_breath);
//                                tv_time = findViewById(R.id.tv_time);
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
                Intent intent1 = new Intent(context, WebActivity.class);
                String url = "http://api.llky.net:7966/#/SleepCharts/index/"+getIntent().getStringExtra("imei")+"/"+MyApplication.token;
                intent1.putExtra("url",url);
                startActivity(intent1);
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
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btn_confirm = popview.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        popuPhoneW.setOnDismissListener(new SSKMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(ll_wifi, Gravity.CENTER, 0, 0);



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
        list  = searchWifi();
        wifi_list = popview.findViewById(R.id.wifi_list);
        wiFiAdapter = new WiFiAdapter(list,context,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        wifi_list.setAdapter(wiFiAdapter);
        wifi_list.setLayoutManager(layoutManager);
        TextView tv_find_wifi = popview.findViewById(R.id.tv_find_wifi);
        tv_find_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WebActivity.class);
                intent.putExtra("url",Urls.getInstance().WIFI_PROBLEM+MyApplication.token);
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

    @Override
    public void onItemClick(View view) {
        int position = wifi_list.getChildLayoutPosition(view);
        tv_wifi.setText(list.get(position).SSID);
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
}
