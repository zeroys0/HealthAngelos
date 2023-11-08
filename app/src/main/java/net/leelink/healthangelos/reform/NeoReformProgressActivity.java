package net.leelink.healthangelos.reform;

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
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.reform.adapter.NeoProgressAdapter;
import net.leelink.healthangelos.reform.bean.ProcessBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NeoReformProgressActivity extends BaseActivity implements OnOrderListener {
    Context context;
    private RecyclerView progress_list;
    private RelativeLayout rl_back;
    private NeoProgressAdapter neoProgressAdapter;
    List<ProcessBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_reform_progress);
        init();
        context = this;
        createProgressBar(context);
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progress_list = findViewById(R.id.progress_list);
    }

    public void initList(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().CIVILL_PROCESS+"/"+getIntent().getStringExtra("applyId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询改造申请进度", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray ja = json.getJSONArray("data");
                                Gson gson = new Gson();
                                list = gson.fromJson(ja.toString(),new TypeToken<List<ProcessBean>>(){}.getType());
                                neoProgressAdapter = new NeoProgressAdapter(context,list,NeoReformProgressActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                progress_list.setAdapter(neoProgressAdapter);
                                progress_list.setLayoutManager(layoutManager);
                            } else if (json.getInt("status") == 505) {

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
    public void onItemClick(View view) {
        int position =  progress_list.getChildLayoutPosition(view);

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
