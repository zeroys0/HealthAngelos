package net.leelink.healthangelos.activity.ElectricMachine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ANY1PR01MainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private Button btn_alarm,btn_state,btn_user;
    private TextView tv_imei,tv_unbind,tv_time,tv_temp,tv_current,tv_volt,tv_use_power,tv_useless_power,tv_storage_current,tv_town_name,tv_village_name,tv_address;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private String family_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any1_pr01_main);
        context = this;
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new ANY1PR01MainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 30000);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_alarm = findViewById(R.id.btn_alarm);
        btn_alarm.setOnClickListener(this);
        btn_state = findViewById(R.id.btn_state);
        btn_state.setOnClickListener(this);
        btn_user = findViewById(R.id.btn_user);
        btn_user.setOnClickListener(this);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_temp = findViewById(R.id.tv_temp);
        tv_current = findViewById(R.id.tv_current);
        tv_volt = findViewById(R.id.tv_volt);
        tv_use_power = findViewById(R.id.tv_use_power);
        tv_useless_power = findViewById(R.id.tv_useless_power);
        tv_storage_current = findViewById(R.id.tv_storage_current);
        tv_town_name = findViewById(R.id.tv_town_name);
        tv_village_name = findViewById(R.id.tv_village_name);
        tv_address = findViewById(R.id.tv_address);



    }
    Timer timer;
    TimerTask task;

    public void loopData(){
        OkGo.<String>get(Urls.getInstance().ANY1_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .params("deviceId",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取脉象仪设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(!json.isNull("ts")){
                                    String time = simpleDateFormat.format(new Date(json.getLong("ts")));
                                    tv_time.setText(time);
                                }
                                tv_temp.setText(json.getString("temp")+"℃");
                                tv_current.setText(json.getString("i")+"A");
                                tv_volt.setText(json.getString("u")+"V");
                                tv_use_power.setText(json.getString("p")+"W");
                                tv_useless_power.setText(json.getString("q")+"W");
                                tv_storage_current.setText(json.getString("ri")+"W");
                                tv_town_name.setText(json.getString("areaName"));
                                tv_village_name.setText(json.getString("neighName"));
                                tv_address.setText(json.getString("comName"));
                                family_id = json.getString("familyId");


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
            case R.id.btn_alarm:
                Intent intent = new Intent(this,ANY1PR01AlarmActivity.class);
                intent.putExtra("familyId",family_id);
                startActivity(intent);
                break;
            case R.id.btn_state:
                Intent intent1 = new Intent(this,ANY1StateActivity.class);
                intent1.putExtra("familyId",family_id);
                startActivity(intent1);
                break;
            case R.id.btn_user:
                Intent intent2 = new Intent(this,ANY1UserActivity.class);
                intent2.putExtra("familyId",family_id);
                startActivity(intent2);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
        }
    }

    public void unbind(){
        OkGo.<String>post(Urls.getInstance().ANY1_DEREGISTER)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceId",getIntent().getStringExtra("imei"))
                .params("familyId",family_id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑电力脉象仪", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解除绑定成功", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }
}