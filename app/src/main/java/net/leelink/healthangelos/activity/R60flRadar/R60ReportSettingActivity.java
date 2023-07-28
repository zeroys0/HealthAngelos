package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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

import androidx.appcompat.widget.SwitchCompat;

public class R60ReportSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_save;
    private SwitchCompat cb_work_time, cb_out_board, cb_body, cb_sport_info, cb_sport_param, cb_body_range, cb_body_direction,cb_body_switch;
    boolean check =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60_report_setting);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        cb_work_time = findViewById(R.id.cb_work_time);
        cb_out_board = findViewById(R.id.cb_out_board);
        cb_body = findViewById(R.id.cb_body);
        cb_sport_info = findViewById(R.id.cb_sport_info);
        cb_sport_param = findViewById(R.id.cb_sport_param);
        cb_body_range = findViewById(R.id.cb_body_range);
        cb_body_direction = findViewById(R.id.cb_body_direction);
        cb_body_switch = findViewById(R.id.cb_body_switch);
        cb_body_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check) {
                    setBody(isChecked);
                }
            }
        });
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().R60_LIMIT + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取开关设置", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(!json.isNull("workDuration")){
                                    if(json.getInt("workDuration")==1){
                                        cb_work_time.setChecked(false);
                                    } else {
                                        cb_work_time.setChecked(true);
                                    }
                                }
                                if(!json.isNull("locationOutOfBounds")){
                                    if(json.getInt("locationOutOfBounds")==1){
                                        cb_out_board.setChecked(false);
                                    } else {
                                        cb_out_board.setChecked(true);
                                    }
                                }
                                if(!json.isNull("someoneExists")){
                                    if(json.getInt("someoneExists")==1){
                                        cb_body.setChecked(false);
                                    } else {
                                        cb_body.setChecked(true);
                                    }
                                }
                                if(!json.isNull("motionStatus")){
                                    if(json.getInt("motionStatus")==1){
                                        cb_sport_info.setChecked(false);
                                    } else {
                                        cb_sport_info.setChecked(true);
                                    }
                                }
                                if(!json.isNull("movementSigns")){
                                    if(json.getInt("movementSigns")==1){
                                        cb_sport_param.setChecked(false);
                                    } else {
                                        cb_sport_param.setChecked(true);
                                    }
                                }
                                if(!json.isNull("humanDistance")){
                                    if(json.getInt("humanDistance")==1){
                                        cb_body_range.setChecked(false);
                                    } else {
                                        cb_body_range.setChecked(true);
                                    }
                                }
                                if(!json.isNull("humanPosition")){
                                    if(json.getInt("humanPosition")==1){
                                        cb_body_direction.setChecked(false);
                                    } else {
                                        cb_body_direction.setChecked(true);
                                    }
                                }

                                check = true;
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
                                if(!json.isNull("humanSwitch")){
                                    if(json.getInt("humanSwitch")==0){
                                        cb_body_switch.setChecked(false);
                                    } else {
                                        cb_body_switch.setChecked(true);
                                    }
                                }
                                check = true;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                saveSetting();
                break;
        }
    }

    public void saveSetting() {
        showProgressBar();

        OkGo.<String>post(Urls.getInstance().LIMIT + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(getJSON())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("限制功能属性上报", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!cb_body_range.isChecked()) {
                jsonObject.put("humanDistance", 1);
            } else {
                jsonObject.put("humanDistance", 0);
            }
            if (!cb_body_direction.isChecked()) {
                jsonObject.put("humanPosition", 1);
            } else {
                jsonObject.put("humanPosition", 0);
            }
            if (!cb_out_board.isChecked()) {
                jsonObject.put("locationOutOfBounds", 1);
            } else {
                jsonObject.put("locationOutOfBounds", 0);
            }
            if (!cb_sport_info.isChecked()) {
                jsonObject.put("motionStatus", 1);
            } else {
                jsonObject.put("motionStatus", 0);
            }
            if (!cb_sport_param.isChecked()) {
                jsonObject.put("movementSigns", 1);
            } else {
                jsonObject.put("movementSigns", 0);
            }
            if (!cb_body.isChecked()) {
                jsonObject.put("someoneExists", 1);
            } else {
                jsonObject.put("someoneExists", 0);
            }
            if (!cb_work_time.isChecked()) {
                jsonObject.put("workDuration", 1);
            } else {
                jsonObject.put("workDuration", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

    public void setBody(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().HUMANSWITCH + "/" + getIntent().getStringExtra("imei") + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置人体功能开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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
}