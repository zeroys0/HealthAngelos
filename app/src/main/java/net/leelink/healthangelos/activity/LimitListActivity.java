package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MonitorLimitsAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LimitListActivity extends BaseActivity implements OnOrderListener {
    RecyclerView limit_list;
    RelativeLayout rl_back;
    Context context;
    List<LimitBean> list = new ArrayList<>();
    MonitorLimitsAdapter monitorLimitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_list);
        context = this;
        init();
        initData();
    }

    public void init(){
        limit_list = findViewById(R.id.limit_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public  void initData(){
        OkGo.<String>get(Urls.ELECTRADDRESS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",1)
                .params("pageSize",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取监控范围", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<LimitBean> limitBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<LimitBean>>(){}.getType());
                                list.addAll(limitBeans);
                                monitorLimitsAdapter = new MonitorLimitsAdapter(list,context,LimitListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                limit_list.setLayoutManager(layoutManager);
                                limit_list.setAdapter(monitorLimitsAdapter);

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
        int position  = limit_list.getChildLayoutPosition(view);
        Intent intent = new Intent();
        intent.putExtra("id",list.get(position).getId());
        intent.putExtra("alias",list.get(position).getAlias());
        setResult(-1,intent);
        finish();
    }

    @Override
    public void onButtonClick(View view, int position) {
        View popView = getLayoutInflater().inflate(R.layout.view_rail_scope, null);
        LinearLayout ll_edit = (LinearLayout) popView.findViewById(R.id.ll_edit);
        LinearLayout ll_delete = (LinearLayout) popView.findViewById(R.id.ll_delete);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  编辑监控范围
                pop.dismiss();
                //   editScope(map);
            }
        });
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除监控范围
                pop.dismiss();
                //   scopeInUse(Id);
            }
        });

        pop.showAsDropDown(view);
    }

}
