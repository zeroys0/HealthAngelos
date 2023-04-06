package net.leelink.healthangelos.activity.Badge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.LocationActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class BadgeMainActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_affection_number,rl_badge_locate,rl_alarm,rl_locate_simple,rl_elect;
    private TextView tv_unbind,tv_imei,tv_device_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_main);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_affection_number = findViewById(R.id.rl_affection_number);
        rl_affection_number.setOnClickListener(this);
        rl_badge_locate = findViewById(R.id.rl_badge_locate);
        rl_badge_locate.setOnClickListener(this);
        rl_alarm = findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        if(getIntent().getStringExtra("model").equals("G803A")){
            rl_affection_number.setVisibility(View.VISIBLE);
        }
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name"));
        rl_locate_simple = findViewById(R.id.rl_locate_simple);
        rl_locate_simple.setOnClickListener(this);
        rl_elect = findViewById(R.id.rl_elect);
        rl_elect.setOnClickListener(this);
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
                Intent intent3 = new Intent(this, LocationActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_elect:
                Intent intent4 = new Intent(this, ElectFenceActivity.class);
                startActivity(intent4);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
        }
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