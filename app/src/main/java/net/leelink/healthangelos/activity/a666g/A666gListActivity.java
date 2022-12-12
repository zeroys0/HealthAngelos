package net.leelink.healthangelos.activity.a666g;

import android.annotation.SuppressLint;
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
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.A6gBean;
import net.leelink.healthangelos.bean.A6gBloodPressureBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class A666gListActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView data_list;
    private A6gBldPressureAdapter a6gBldPressureAdapter;
    private List<A6gBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a666g_list);
        context = this;
        init();
        initList();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        data_list = findViewById(R.id.data_list);
    }

    public void initList(){
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().AALPDATETIME)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei","21K030320@B")
                .params("pageNum",1)
                .params("pageSize",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询血压日期数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                for(int i = 0;i<jsonArray.length();i++){
                                    list.add(new A6gBean(jsonArray.getJSONObject(i).getString("date")));
                                }
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                a6gBldPressureAdapter = new A6gBldPressureAdapter(context,list,A666gListActivity.this);
                                data_list.setAdapter(a6gBldPressureAdapter);
                                data_list.setLayoutManager(layoutManager);
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
                        LoadDialog.stop();
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

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onButtonClick(View view, int position) {
        if(list.get(position).getIs_show()){
            list.get(position).setIs_show(false);
            a6gBldPressureAdapter.notifyDataSetChanged();
        } else {
            list.get(position).setIs_show(true);
            getDetailData(position);
        }
    }


    public void getDetailData(int position){
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().AALPDAY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei","21K030320@B")
                .params("date",list.get(position).getName())
                .execute(new StringCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据日期查询血压详细数据", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<A6gBloodPressureBean> beans = gson.fromJson(jsonArray.toString(),new TypeToken<List<A6gBloodPressureBean>>(){}.getType());
                                list.get(position).setList(beans);
                                Log.e( "BBB: ",list.get(position).getList().size()+"" );
                                a6gBldPressureAdapter.notifyDataSetChanged();
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
                        LoadDialog.stop();
                    }
                });
    }
}