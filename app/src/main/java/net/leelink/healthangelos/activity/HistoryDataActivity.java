package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DataHistoryAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryDataActivity extends BaseActivity implements OnOrderListener {
    RecyclerView history_list;
    Context context;
    DataHistoryAdapter dataHistoryAdapter;
    RelativeLayout rl_back;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);
        init();
        initData();
        context = this;
    }

    public void init(){
        history_list = findViewById(R.id.history_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initData(){
        OkGo.<String>get(Urls.INPUTLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("录入记录", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = json.getJSONArray("data");
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                history_list.setLayoutManager(layoutManager);
                                dataHistoryAdapter = new DataHistoryAdapter(jsonArray,context,HistoryDataActivity.this);
                                history_list.setAdapter(dataHistoryAdapter);
                            }else if (json.getInt("status") == 505) {
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

    @Override
    public void onItemClick(View view) {
        int position = history_list.getChildLayoutPosition(view);
        Intent intent = new Intent(this,HistoryDataDetailActivity.class);
        try {
            intent.putExtra("data",jsonArray.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {
        Intent intent = new Intent(this,HistoryDataDetailActivity.class);
        try {
            intent.putExtra("data",jsonArray.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
