package net.leelink.healthangelos.activity.slaap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.SlaapAlarmBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SlaapMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private SlaapAlarmAdapter slaapAlarmAdapter;
    private RecyclerView alarm_list;
    private Button btn_setting,btn_sleep_data,btn_wifi;
    private TextView tv_unbind,tv_sn,tv_number,tv_breath_number,tv_state,tv_breath_state,tv_connect;
    private String imei;
    List<SlaapAlarmBean> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slaap_main);
        context = this;
        init();
        initList();
    }

    Timer timer;
    TimerTask task;
    @Override
    protected void onStart() {
        super.onStart();
        task = new MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 10000);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        btn_sleep_data = findViewById(R.id.btn_sleep_data);
        btn_sleep_data.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_sn = findViewById(R.id.tv_sn);
        tv_number = findViewById(R.id.tv_number);
        tv_breath_number = findViewById(R.id.tv_breath_number);
        tv_state = findViewById(R.id.tv_state);
        tv_breath_state = findViewById(R.id.tv_breath_state);
        tv_connect = findViewById(R.id.tv_connect);
        btn_wifi = findViewById(R.id.btn_wifi);
        btn_wifi.setOnClickListener(this);



        if(getIntent().getStringExtra("imei")!=null){
            imei = getIntent().getStringExtra("imei");
            tv_sn.setText(imei);
        } else {
            Toast.makeText(context, "获取设备信息失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void initList(){
        alarm_list = findViewById(R.id.alarm_list);


        OkGo.<String>get(Urls.getInstance().SLAAP_ALARM_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn",getIntent().getStringExtra("imei"))
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取异常消息列表", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<SlaapAlarmBean>>(){}.getType());
                                slaapAlarmAdapter = new SlaapAlarmAdapter(context,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                alarm_list.setAdapter(slaapAlarmAdapter);
                                alarm_list.setLayoutManager(layoutManager);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_setting:      //睡带设置
                Intent intent = new Intent(context,SlaapSettingActivity.class);
                intent.putExtra("imei",imei);
                startActivity(intent);
                break;
            case R.id.btn_sleep_data:   //睡眠数据
                Intent intent1 = new Intent(context,SlaapSleepDataActivity.class);
                intent1.putExtra("imei",imei);
                startActivity(intent1);
                break;
            case R.id.tv_unbind:    //设备解绑
                unbind();
                break;
            case R.id.btn_wifi:
                Intent intent2 = new Intent(context,SlaapWifiActivity.class);
                startActivity(intent2);
                break;
        }
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //do something
            try {
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().SLAAP_REALDATA)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取床垫信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                json = json.getJSONObject("realData");
                                tv_number.setText(json.getString("heart"));
                                tv_breath_number.setText(json.getString("breathe"));
                                int heart_state = json.getInt("heartStatus");
                                switch (heart_state){
                                    case 0:
                                        tv_state.setText("正常");
                                        break;
                                    case 1:
                                        tv_state.setText("偏高");
                                        break;
                                    case 2:
                                        tv_state.setText("偏低");
                                        break;
                                    default:
                                        break;
                                }
                                switch (json.getInt("breatheStatus")){
                                    case 0:
                                        tv_breath_state.setText("正常");
                                        break;
                                    case 1:
                                        tv_breath_state.setText("偏高");
                                        break;
                                    case 2:
                                        tv_breath_state.setText("偏低");
                                        break;
                                    default:
                                        break;
                                }
                                switch (json.getInt("status")){
                                    case 0:
                                        tv_connect.setText("在床");
                                        break;
                                    case 1:
                                        tv_connect.setText("离床");
                                        break;
                                    case 2:
                                        tv_connect.setText("无信号");
                                        break;
                                    case 3:
                                        tv_connect.setText("计算中");
                                        break;
                                    default:
                                        break;
                                }
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
    public void unbind(){
        OkGo.<String>delete(Urls.getInstance().SLAAP_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑床垫", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设备已解绑", Toast.LENGTH_SHORT).show();
                                finish();
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

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }
}