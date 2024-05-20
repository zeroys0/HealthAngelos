package net.leelink.healthangelos.activity.Badge;

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
import net.leelink.healthangelos.activity.Badge.bean.AlertBean;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BadgeMessageActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView message_list;
    private BadgeMessageAdapter badgeMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_message);
        context = this;
        createProgressBar(context);
        init();
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
        message_list = findViewById(R.id.message_list);

    }
    public void initList(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ALERT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .params("pageNum",1)
                .params("pageSize",100)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询报警消息", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<AlertBean> list = gson.fromJson(jsonArray.toString(),new TypeToken<List<AlertBean>>(){}.getType());
                                badgeMessageAdapter = new BadgeMessageAdapter(list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                message_list.setAdapter(badgeMessageAdapter);
                                message_list.setLayoutManager(layoutManager);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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
}