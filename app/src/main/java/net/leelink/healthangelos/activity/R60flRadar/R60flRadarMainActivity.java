package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.Timer;
import java.util.TimerTask;

public class R60flRadarMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Button btn_detail_setting,btn_report_setting;
    private Context context;
    private TextView tv_connect,tv_imei,tv_scene,tv_param,tv_sport,tv_scope,tv_people,tv_stay,tv_fall,tv_unbind;
    private RelativeLayout rl_scene,rl_people,rl_stay,rl_fall;
    private ImageView img_scene,img_work_mode,img_people,img_stay,img_fall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60fl_radar_main);
        context = this;
        createProgressBar(context);
        init();
    }

    Timer timer;
    TimerTask task;
    @Override
    protected void onStart() {
        super.onStart();
        task = new R60flRadarMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 20000);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        rl_scene = findViewById(R.id.rl_scene);
        tv_connect = findViewById(R.id.tv_connect);
        tv_scene = findViewById(R.id.tv_scene);
        img_scene = findViewById(R.id.img_scene);
        img_work_mode = findViewById(R.id.img_work_mode);
        tv_param = findViewById(R.id.tv_param);
        tv_sport = findViewById(R.id.tv_sport);
        tv_scope = findViewById(R.id.tv_scope);
        tv_people = findViewById(R.id.tv_people);
        img_people = findViewById(R.id.img_people);
        tv_stay = findViewById(R.id.tv_stay);
        rl_people = findViewById(R.id.rl_people);
        rl_stay = findViewById(R.id.rl_stay);
        img_stay = findViewById(R.id.img_stay);
        rl_fall = findViewById(R.id.rl_fall);
        tv_fall = findViewById(R.id.tv_fall);
        img_fall = findViewById(R.id.img_fall);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        btn_detail_setting = findViewById(R.id.btn_detail_setting);
        btn_detail_setting.setOnClickListener(this);
        btn_report_setting = findViewById(R.id.btn_report_setting);
        btn_report_setting.setOnClickListener(this);
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
        OkGo.<String>get(Urls.getInstance().R60_PARAMS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备属性", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(json.getInt("online")==0){
                                    tv_connect.setText("设备离线");
                                } else {
                                    tv_connect.setText("设备在线");
                                }
                                if(!json.isNull("sceneMode")) {
                                    switch (json.getInt("sceneMode")){
                                        case 1:
                                            img_scene.setImageResource(R.drawable.r60_keting);
                                            tv_scene.setText("客厅");
                                            break;
                                        case 2:
                                            img_scene.setImageResource(R.drawable.r60_bedoom);
                                            tv_scene.setText("卧室");
                                            break;
                                        case 3:
                                            img_scene.setImageResource(R.drawable.r60_wc);
                                            tv_scene.setText("卫生间");
                                            break;
                                    }
                                }
                                if(!json.isNull("workMode")) {
                                    switch (json.getInt("workMode")){
                                        case 1:
                                            img_work_mode.setImageResource(R.drawable.r60_gongzuo);
                                            tv_scene.setText("工作模式");
                                            break;
                                        case 2:
                                            img_work_mode.setImageResource(R.drawable.r60_daiji);
                                            tv_scene.setText("待机模式");
                                            break;
                                        case 3:
                                            img_work_mode.setImageResource(R.drawable.r60_ceshi);
                                            tv_scene.setText("测试模式");
                                            break;
                                    }
                                }
                                if(!json.isNull("motionStatus")) {
                                    switch (json.getInt("motionStatus")){
                                        case 0:
                                            tv_sport.setText("无");
                                            break;
                                        case 1:
                                            tv_sport.setText("静止");
                                            break;
                                        case 2:
                                            tv_sport.setText("活跃");
                                            break;
                                    }
                                }
                                if(!json.isNull("locationOutOfBounds")){
                                    if(json.getInt("locationOutOfBounds")==0){
                                        tv_scope.setText("范围外");
                                    } else {
                                        tv_scope.setText("范围内");
                                    }
                                }
                                /**
                                 * 人体存在监测
                                 */
                                if(!json.isNull("humanSwitch")){
                                    if(json.getInt("humanSwitch")==0){
                                        tv_people.setText("未开启");
                                        rl_people.setBackground(getResources().getDrawable(R.drawable.r60_grey_round));
                                    } else {
                                        rl_people.setBackground(getResources().getDrawable(R.drawable.r60_main_round));
                                        if(!json.isNull("someoneExists")){
                                            if(json.getInt("someoneExists")==0){
                                                tv_people.setText("无人");
                                                img_people.setImageResource(R.drawable.r60_wuren);
                                            } else {
                                                tv_people.setText("有人");
                                                img_people.setImageResource(R.drawable.r60_youren);
                                            }
                                        }
                                    }
                                }

                                /**
                                 * 驻留监测
                                 */
                                if(!json.isNull("residentWarningDurationSwitch")){
                                    if(json.getInt("residentWarningDurationSwitch")==0){
                                        tv_stay.setText("未开启");
                                        rl_stay.setBackground(getResources().getDrawable(R.drawable.r60_grey_round));
                                    } else {
                                        rl_stay.setBackground(getResources().getDrawable(R.drawable.r60_main_round));
                                        if(!json.isNull("residentStatus")){
                                            if(json.getInt("residentStatus")==0){
                                                tv_stay.setText("无驻留");
                                                img_stay.setImageResource(R.drawable.r60_unstay);
                                            } else {
                                                tv_stay.setText("有驻留");
                                                img_stay.setImageResource(R.drawable.r60_stay);
                                            }
                                        }
                                    }
                                }

                                /**
                                 * 跌倒监测
                                 */
                                if(!json.isNull("fallSwitch")){
                                    if(json.getInt("fallSwitch")==0){
                                        tv_fall.setText("未开启");
                                        rl_fall.setBackground(getResources().getDrawable(R.drawable.r60_grey_round));
                                    } else {
                                        rl_fall.setBackground(getResources().getDrawable(R.drawable.r60_main_round));
                                        if(!json.isNull("fallStatus")){
                                            if(json.getInt("fallStatus")==0){
                                                tv_fall.setText("正常");
                                                img_fall.setImageResource(R.drawable.r60_unfall);
                                            } else {
                                                tv_fall.setText("跌倒");
                                                img_fall.setImageResource(R.drawable.r60_fall);
                                            }
                                        }
                                    }
                                }
                                if(!json.isNull("movementSigns")){
                                    tv_param.setText(json.getString("movementSigns"));
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    public void unbind(){
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().R60_UNBIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑雷达", json.toString());
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_detail_setting:
                Intent intent=  new Intent(this,R60DetailSettingActivity.class);
                intent.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent);
                break;
            case R.id.btn_report_setting:
                Intent intent1 = new Intent(this,R60ReportSettingActivity.class);
                intent1.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent1);
                break;
            case R.id.tv_unbind:
                unbind();
                break;

        }
    }

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }

}