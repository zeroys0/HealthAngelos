package net.leelink.healthangelos.activity.T6LS;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class UploadTimeMonitorActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_start_time, rl_end_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int beginHour ;
    int beginMinute;
    int endHour;
    int endMinute;
    int time_type;
    private TextView tv_start_time, tv_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_time_monitor);
        context = this;
        createProgressBar(context);
        init();
        initPickerView();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_start_time:
                pvTime.show();
                time_type = 1;
                break;
            case R.id.rl_end_time:
                pvTime.show();
                time_type = 2;
                break;
        }
    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("HH:mm");
        boolean[] type = {false, false, false, true, true, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_start_time.setText(sdf.format(date));
                    beginHour = date.getHours();
                    beginMinute = date.getMinutes();

                }
                if (time_type == 2) {
                    tv_end_time.setText(sdf.format(date));
                    endHour = date.getHours();
                    endMinute = date.getMinutes();
                }
                setUploadTime();
            }
        }).setType(type).build();
    }
    public void setUploadTime(){
        showProgressBar();
        JSONObject json = new JSONObject();
        try {
            json.put("beginHour", beginHour);
            json.put("beginMinute", beginMinute);
            json.put("endHour", endHour);
            json.put("endMinute", endMinute);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.getInstance().T6LS_UPTIME+"/"+getIntent().getStringExtra("imei"))
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
                            Log.d("设置上报时段", json.toString());
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

}