package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ProgressAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressActivity extends BaseActivity {
    RelativeLayout rl_back;
    Context context;
    RecyclerView progress_list;
    ProgressAdapter progressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        context = this;
        createProgressBar(context);
        init();
        initView();
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

    public void initView(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().EVALUTION)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看高龄补贴进度", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(json.getJSONObject("applyReault"));
                                jsonArray.put(json.getJSONObject("workReault"));
                                jsonArray.put(json.getJSONObject("evaResult"));
                                jsonArray.put(json.getJSONObject("communityResult"));
                                jsonArray.put(json.getJSONObject("streetResult"));
                                jsonArray.put(json.getJSONObject("civilResult"));
                                Log.e( "onSuccess: ", jsonArray.toString());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                progressAdapter = new ProgressAdapter(jsonArray,context);
                                progress_list.setLayoutManager(layoutManager);
                                progress_list.setAdapter(progressAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
