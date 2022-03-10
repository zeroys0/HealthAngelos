package net.leelink.healthangelos.activity;

import android.content.Context;
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
import net.leelink.healthangelos.adapter.AlarmAdapter;
import net.leelink.healthangelos.adapter.NeoAlarmAdapter;
import net.leelink.healthangelos.adapter.OnAlarmChangeListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.AlarmBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmListActivity extends BaseActivity implements OnAlarmChangeListener {
    RecyclerView alarm_list;
    RelativeLayout rl_back;
    AlarmAdapter alarmAdapter;
    NeoAlarmAdapter neoAlarmAdapter;
    Context context;
    List<AlarmBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        init();
        context = this;
        createProgressBar(context);
        initList();
    }

    public void init() {
        alarm_list = findViewById(R.id.alarm_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initList() {

        OkGo.<String>get(Urls.getInstance().SERVICE)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("获取所有可以订阅的服务: ", j.toString());
                            if (j.getInt("status") == 200) {
                                Gson gson = new Gson();
                                JSONArray jsonArray = j.getJSONArray("data");
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<AlarmBean>>() {
                                }.getType());
//                                alarmAdapter = new AlarmAdapter(list,context,AlarmListActivity.this);
//                                alarm_list.setAdapter(alarmAdapter);
                                neoAlarmAdapter = new NeoAlarmAdapter(context, AlarmListActivity.this, list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                alarm_list.setLayoutManager(layoutManager);
                                alarm_list.setAdapter(neoAlarmAdapter);
                            } else if (j.getInt("status") == 505) {
                                reLogin(context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

//    @Override
//    public void onItemClick(View view) {
//
//    }
//
//    @Override
//    public void onButtonClick(View view, int position) {
//        Intent intent = new Intent(this, AlarmDetailActivity.class);
//        intent.putExtra("price", list.get(position).getPrice());
//        intent.putExtra("id", list.get(position).getId());
//        intent.putExtra("name", list.get(position).getProductName());
//        startActivity(intent);
//    }

    @Override
    public void Onclick(View view) {

    }

    @Override
    public void OnChangeListener(View view, int position, boolean exist) {
        String id = list.get(position).getId();
        setChange(id,exist);
    }

    public void setChange(String id,boolean exist){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().SERVICE+"/"+id+"/"+exist)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("设置服务订阅: ", j.toString());
                            if (j.getInt("status") == 200) {

                            } else if (j.getInt("status") == 505) {
                                reLogin(context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
