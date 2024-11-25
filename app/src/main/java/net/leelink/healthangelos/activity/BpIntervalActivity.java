package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BpIntervalAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BpIntervalBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BpIntervalActivity extends BaseActivity implements OnOrderListener {
private Context context;
private RelativeLayout rl_back;
private RecyclerView bp_interval_list;

private BpIntervalAdapter bp_interval_adapter;
List<BpIntervalBean> list;
String data;
private TextView tv_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_interval);
        context = this;
        createProgressBar(context);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(v -> finish());
        bp_interval_list = findViewById(R.id.bp_interval_list);
        tv_add = findViewById(R.id.tv_add);
        tv_add.setOnClickListener(v -> {
            if(list.size()>=3){
                Toast.makeText(context,"最多添加3个时间段",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, BpIntervalDetailActivity.class);
            intent.putExtra("type",BpIntervalDetailActivity.ADD_INTERVAL);
            intent.putExtra("imei",getIntent().getStringExtra("imei"));
            intent.putExtra("data",data);
            startActivityForResult(intent,1);
        });
    }

    public void initList(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BP_PERIOD + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取血压定时监测时段 ", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.has("data")){
                                    JSONArray ja = json.getJSONArray("data");
                                    data = ja.toString();
                                    Gson gson = new Gson();
                                    list = gson.fromJson(ja.toString(),new TypeToken<List<BpIntervalBean>>(){}.getType());
                                    bp_interval_adapter = new BpIntervalAdapter(list,context,BpIntervalActivity.this);
                                    bp_interval_list.setAdapter(bp_interval_adapter);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                    bp_interval_list.setLayoutManager(layoutManager);
                                } else {

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
                });
    }

    @Override
    public void onItemClick(View view) {
        int position = bp_interval_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, BpIntervalDetailActivity.class);
        intent.putExtra("bean",list.get(position));
        intent.putExtra("position",position);
        intent.putExtra("data",data);
        intent.putExtra("type",BpIntervalDetailActivity.EDIT_INTERVAL);
        intent.putExtra("imei",getIntent().getStringExtra("imei"));
        startActivity(intent);

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}