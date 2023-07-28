package net.leelink.healthangelos.activity.sleepace;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SleepaceAlarmListActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView alarm_list;
    private SleepaceAlarmAdapter sleepaceAlarmAdapter;
    private TextView tv_start_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int time_type;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<AlarmBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepace_alarm_list);
        context = this;
        createProgressBar(context);
        init();
      //  initList();
        initPickerView();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        alarm_list = findViewById(R.id.alarm_list);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_start_time:
                pvTime.show();
                time_type = 1;
                break;
        }
    }

    public void initList(){
        showProgressBar();
        Log.d( "initList: ",start+"");
        Log.d( "initList: ",end+"");
        OkGo.<String>get(Urls.getInstance().SLEEPACE_ALARMLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyNo",getIntent().getStringExtra("elderlyNo"))
                .params("startTime",start)
                .params("endTime",end)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取报警信息列表", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<AlarmBean>>(){}.getType());
                                sleepaceAlarmAdapter = new SleepaceAlarmAdapter(list);
                                alarm_list.setAdapter(sleepaceAlarmAdapter);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }
    int start,end;
    private void initPickerView() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean[] type = {true, true, true, false, false, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_start_time.setText(sdf.format(date));
                    start = (int) (date.getTime()/1000);
                    end = (int) (date.getTime()/1000) + (24*3600);
                }
                initList();
            }
        }).setType(type).build();
    }


}