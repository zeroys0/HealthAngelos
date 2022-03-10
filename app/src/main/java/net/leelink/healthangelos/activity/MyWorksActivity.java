package net.leelink.healthangelos.activity;

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
import net.leelink.healthangelos.adapter.MyWorkAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.WorkBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyWorksActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
private RelativeLayout rl_back;
private Context context;
private MyWorkAdapter myWorkAdapter;
private RecyclerView work_list;
private List<WorkBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_works);
        context = this;
        createProgressBar(context);
        init();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        work_list = findViewById(R.id.work_list);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    public void initList(){

        showProgressBar();
        OkGo.<String>get(Urls.getInstance().PRODUCTION_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",1)
                .params("pageSize",100)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("我的作品列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson=  new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<WorkBean>>(){}.getType());
                                myWorkAdapter = new MyWorkAdapter(context,MyWorksActivity.this,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                work_list.setAdapter(myWorkAdapter);
                                work_list.setLayoutManager(layoutManager);
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
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
        int position  = work_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context,WorkDetailActivity.class);
        intent.putExtra("work_id",list.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
