package net.leelink.healthangelos.activity.NBdevice;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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

import androidx.appcompat.widget.SwitchCompat;

public class NBSettingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back,rl_open_setting,rl_close_setting;
    private SwitchCompat cb_alarm;
    private EditText ed_interval;
    private TextView tv_start_time,tv_end_time;
    private SimpleDateFormat sdf, sdf1;
    private TimePickerView pvTime, pvTime1;
    private int beginHour,beginSecond,endHour,endSecond;
    public int ini_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nbsetting);
        context = this;
        init();
        createProgressBar(context);
        initPickerView();
        initClose();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cb_alarm = findViewById(R.id.cb_alarm);
        cb_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(ini_state==1) {
                        setting();
                    }
                }
            }
        });
        rl_open_setting = findViewById(R.id.rl_open_setting);
        rl_open_setting.setOnClickListener(this);
        rl_close_setting = findViewById(R.id.rl_close_setting);
        rl_close_setting.setOnClickListener(this);
        ed_interval = findViewById(R.id.ed_interval);
        ed_interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ini_state==1) {
                    setting();
                }
            }
        });
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().INNOPRO_ATTRIBUTE+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备设置", json.toString());
                            if(json.has("data")){
                                json = json.getJSONObject("data");
                                StringBuilder sb = new StringBuilder();
                                beginHour = json.getInt("beginHour");
                                if(json.getInt("beginHour")<10){
                                    sb.append("0");
                                }
                                    sb.append(json.getInt("beginHour"));
                                    sb.append(":");
                                beginSecond = json.getInt("beginSecond");
                                if(json.getInt("beginSecond")<10){
                                    sb.append("0");
                                }
                                    sb.append(json.getInt("beginSecond"));

                                tv_start_time.setText(sb.toString());
                                sb.delete(0,sb.length());
                                endHour = json.getInt("endHour");
                                if(json.getInt("endHour")<10){
                                    sb.append("0");
                                }
                                    sb.append(json.getInt("endHour"));
                                    sb.append(":");
                                endSecond = json.getInt("endSecond");
                                if(json.getInt("endSecond")<10){
                                    sb.append("0");
                                }
                                    sb.append(json.getInt("endSecond"));
                                tv_end_time.setText(sb.toString());
                                ed_interval.setText(json.getString("intervalTime"));
                                if(json.getInt("deviceState")==1){
                                    cb_alarm.setChecked(true);
                                } else {
                                    cb_alarm.setChecked(false);
                                }
                                ini_state = 1;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void setting(){
        showProgressBar();
        JSONObject json = new JSONObject();
        try {
            json.put("imei",getIntent().getStringExtra("imei"));
            json.put("beginHour",beginHour);
            json.put("beginSecond",beginSecond);
            json.put("endHour",endHour);
            json.put("endSecond",endSecond);
            json.put("intervalTime",ed_interval.getText().toString());
            if(cb_alarm.isChecked()){
                json.put("deviceState",1);
            } else {
                json.put("deviceState",0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.getInstance().INNOPRO_UPDATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置设备参数", json.toString());
                            Toast.makeText(NBSettingActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_open_setting:
                pvTime.show();
                break;
            case R.id.rl_close_setting:
                pvTime1.show();
                break;
        }
    }

    private void initPickerView() {
        boolean[] type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_start_time.setText(sdf.format(date));
                beginHour = date.getHours();
                beginSecond = date.getMinutes();
                setting();
            }
        }).setType(type).build();
    }

    private void initClose() {

        boolean[] type = {false, false, false, true, true, false};
        sdf1 = new SimpleDateFormat("HH:mm");
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_end_time.setText(sdf1.format(date));
                endHour = date.getHours();
                endSecond = date.getMinutes();
                setting();
            }
        }).setType(type).build();

    }
}