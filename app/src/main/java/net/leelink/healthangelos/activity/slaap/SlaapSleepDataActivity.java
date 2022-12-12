package net.leelink.healthangelos.activity.slaap;

import android.content.Context;
import android.content.Intent;
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
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.SlaapReportBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SlaapSleepDataActivity extends BaseActivity implements OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private SleepAdapter sleepAdapter;
    List<SlaapReportBean> list = new ArrayList<>();
    private RecyclerView sleep_data_list;
    private TextView tv_more;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slaap_sleep_data);
        context = this;
        init();
        initList();
        initTime();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sleep_data_list = findViewById(R.id.sleep_data_list);
        tv_more = findViewById(R.id.tv_more);
        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
    }

    public void initList(){

        OkGo.<String>get(Urls.getInstance().SLAAP_REPORT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取21天睡眠数据列表", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("data");
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<SlaapReportBean>>(){}.getType());
                                sleepAdapter = new SleepAdapter(context,list,SlaapSleepDataActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                sleep_data_list.setAdapter(sleepAdapter);
                                sleep_data_list.setLayoutManager(layoutManager);
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

    private void initTime() {
        boolean[] time_type = {true, true, true, false, false, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
               String d = sdf.format(date);
                searchData(d);
            }
        }).setType(time_type).build();
    }

    public void searchData(String date){
        Log.d( "searchData: ",date);
        OkGo.<String>get(Urls.getInstance().SLAAP_REPORTURL)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date",date)
                .params("sn",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取简易报告", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(json);
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<SlaapReportBean>>(){}.getType());
                                sleepAdapter = new SleepAdapter(context,list,SlaapSleepDataActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                sleep_data_list.setAdapter(sleepAdapter);
                                sleep_data_list.setLayoutManager(layoutManager);
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
    public void onItemClick(View view) {
        int position  = sleep_data_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", list.get(position).getUrl());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}