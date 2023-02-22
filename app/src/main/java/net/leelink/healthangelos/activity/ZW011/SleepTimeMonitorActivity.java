package net.leelink.healthangelos.activity.ZW011;

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

public class SleepTimeMonitorActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_start_time, rl_end_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private TextView tv_start_time, tv_end_time;
    String start = "";
    String end = "";
    int time_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time_monitor);
        context = this;
        createProgressBar(context);
        init();
        initPickerView();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().HEALTHDATALIMIT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取睡眠时间段", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (!json.isNull("beginTime")) {
                                    start = json.getString("beginTime");
                                    tv_start_time.setText(getTime(json.getString("beginTime")));
                                }
                                if (!json.isNull("endTime")) {
                                    end = json.getString("endTime");
                                    tv_end_time.setText(getTime(json.getString("endTime")));
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
                        stopProgressBar();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    String getTime(String time) {
        time = time.substring(0, 2) + ":" + time.substring(2, 4);
        return time;
    }

    String getString(String time) {
        String[] arr = time.split(":");
        StringBuilder sb = new StringBuilder();
        sb.append(arr[0]);
        sb.append(arr[1]);
        sb.append("00");
        return  sb.toString();
    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("HH:mm");
        boolean[] type = {false, false, false, true, true, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_start_time.setText(sdf.format(date));
                    start = getString(sdf.format(date));

                }
                if (time_type == 2) {
                    tv_end_time.setText(sdf.format(date));
                    end = getString(sdf.format(date));
                }
                setSleepTime();
            }
        }).setType(type).build();
    }

    public void setSleepTime() {
        if (start.equals("")) {
            return;
        }
        if (end.equals("")) {
            return;
        }
        Log.d("setSleepTime: ", start);
        Log.d("setSleepTime: ", end);
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().SLEEPPERIOD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .params("beginTime", start)
                .params("endTime", end)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置睡眠时段", json.toString());
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