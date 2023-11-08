package net.leelink.healthangelos.activity.Badge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.TrackActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONException;
import org.json.JSONObject;

public class BadgeMainActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_affection_number,rl_badge_locate,rl_alarm,rl_locate_simple,rl_elect,rl_house_locate,rl_step,rl_blood_pressure,rl_heart_rate,rl_temp;
    private TextView tv_unbind,tv_imei,tv_device_name,tv_model,tv_locate,tv_last_locate,tv_last_update,text_title;
    private BatteryView battery;
    private LinearLayout ll_ring;
    private ImageView img_head,img_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_main);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_affection_number = findViewById(R.id.rl_affection_number);
        rl_affection_number.setOnClickListener(this);
        rl_badge_locate = findViewById(R.id.rl_badge_locate);
        rl_badge_locate.setOnClickListener(this);
        rl_alarm = findViewById(R.id.rl_alarm);
        img_connect = findViewById(R.id.img_connect);
        rl_alarm.setOnClickListener(this);
        ll_ring = findViewById(R.id.ll_ring);
        tv_locate = findViewById(R.id.tv_locate);
        tv_last_locate = findViewById(R.id.tv_last_locate);
        tv_last_update = findViewById(R.id.tv_last_update);
        img_head = findViewById(R.id.img_head);
        tv_model = findViewById(R.id.tv_model);
        tv_model.setText(getIntent().getStringExtra("model"));
        text_title = findViewById(R.id.text_title);
        if(getIntent().getStringExtra("model").equals("G803A")){
            rl_affection_number.setVisibility(View.VISIBLE);
        }
        if(getIntent().getStringExtra("model").equals("B2315")){
            ll_ring.setVisibility(View.VISIBLE);
            img_head.setImageResource(R.drawable.badge_ring);
            text_title.setText("智能手环");
        } else {
            ll_ring.setVisibility(View.GONE);
        }
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name"));
        tv_model = findViewById(R.id.tv_model);

        rl_locate_simple = findViewById(R.id.rl_locate_simple);
        rl_locate_simple.setOnClickListener(this);
        rl_elect = findViewById(R.id.rl_elect);
        rl_elect.setOnClickListener(this);
        battery = findViewById(R.id.battery);
        battery.setPower(47);
        rl_house_locate = findViewById(R.id.rl_house_locate);
        rl_house_locate.setOnClickListener(this);
        rl_step = findViewById(R.id.rl_step);
        rl_step.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        rl_temp = findViewById(R.id.rl_temp);
        rl_temp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_affection_number:
                Intent intent = new Intent(this,BadgeAffectionNumberActivity.class);
                intent.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent);
                break;  
            case R.id.rl_badge_locate:
                Intent intent1 = new Intent(this,BadgeLocateActivity.class);
                intent1.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent1);
                break;
            case R.id.rl_alarm:
                Intent intent2 = new Intent(this,BadgeMessageActivity.class);
                intent2.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent2);
                break;
            case R.id.rl_locate_simple:
                Intent intent3 = new Intent(this, TrackActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_elect:
                Intent intent4 = new Intent(this, ElectFenceActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_house_locate:
                Intent intent5 = new Intent(this,BadgeHouseLoacateActivity.class);
                intent5.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent5);
                break;
            case R.id.rl_step:
                Intent intent6 = new Intent(this,BadgeStepActivity.class);
                intent6.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent6);
                break;
            case R.id.rl_blood_pressure:
                Intent intent7 = new Intent(this,BadgeBpActivity.class);
                intent7.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent7);
                break;
            case R.id.rl_heart_rate:
                Intent intent8 = new Intent(this,BadgeHrActivity.class);
                intent8.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent8);
                break;
            case R.id.rl_temp:
                Intent intent9 = new Intent(this,BadgeTempActivity.class);
                intent9.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent9);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
        }
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().JWOTCH_STATUS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备状态", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.has("data")){
                                    json = json.getJSONObject("data");
                                    battery.setPower(json.getInt("battery"));
                                    tv_locate.setText("定位地址: "+json.getString("address"));
                                    tv_last_locate.setText("最后定位: "+json.getString("locaDate"));
                                    if(json.getInt("gpsType")==0 || json.getInt("gpsType")==1){
                                        tv_last_locate.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.badge_wifi),null);
                                    }
                                    if(json.getInt("gpsType")==3){
                                        tv_last_locate.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.badge_bluetooth),null);
                                    }
                                    if(json.getInt("gpsType")==2){
                                        tv_last_locate.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.badge_radar),null);
                                    }
                                    tv_last_update.setText("最后通信: "+json.getString("updateDate"));
                                    tv_last_update = findViewById(R.id.tv_last_update);
                                } else {
                                    img_connect.setImageResource(R.drawable.badge_offline);
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

    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BIND + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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
}