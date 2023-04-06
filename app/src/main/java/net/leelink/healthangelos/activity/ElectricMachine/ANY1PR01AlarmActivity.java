package net.leelink.healthangelos.activity.ElectricMachine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ANY1PR01AlarmActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
private Context context;
private RelativeLayout rl_back;
private RecyclerView alarm_list;
private ANY1AlarmAdapter any1AlarmAdapter;
private List<ElectAlarmBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any1_pr01_alarm);
        context = this;
        createProgressBar(context);
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        alarm_list = findViewById(R.id.alarm_list);

    }

    public void initList(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ANY1_ALARM)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("familyId",getIntent().getStringExtra("familyId"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取报警信息列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<ElectAlarmBean>>(){}.getType());

                                any1AlarmAdapter = new ANY1AlarmAdapter(ANY1PR01AlarmActivity.this,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                alarm_list.setAdapter(any1AlarmAdapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        int position = alarm_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context,ANy1AlarmDetailActivity.class);
        intent.putExtra("bean",list.get(position));
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}