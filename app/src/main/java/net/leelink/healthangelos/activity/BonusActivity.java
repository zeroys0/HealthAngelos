package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BonusAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BonusBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BonusActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private RecyclerView cost_list;
    private BonusAdapter balanceAdapter;
    int page = 1;
    Context context;
    private TextView tv_profit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);
        context = this;
        init();
        initData();
       // getUserInfo();
    }


    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cost_list = findViewById(R.id.cost_list);
        tv_profit = findViewById(R.id.tv_profit);
        tv_profit.setText(getIntent().getStringExtra("profit"));
    }

    public void initData(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",page);
        httpParams.put("pageSize",50);
        OkGo.<String>get(Urls.getInstance().INTEGRAL)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("积分记录: ", j.toString());
                            if (j.getInt("status") == 200) {
                                Gson gson = new Gson();
                                j = j.getJSONObject("data");
                                JSONArray jsonArray = j.getJSONArray("list");
                                List<BonusBean> balanceBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BonusBean>>(){}.getType());
                                balanceAdapter = new BonusAdapter(context,balanceBeans);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                cost_list.setLayoutManager(layoutManager);
                                cost_list.setAdapter(balanceAdapter);
                            }else if(j.getInt("status") == 505){
                                reLogin(context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

//    public void getUserInfo(){
//        OkGo.<String>get(Urls.CUSTOMERINFO)
//                .tag(this)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        try {
//                            String body = response.body();
//                            JSONObject json = new JSONObject(body);
//                            Log.d("获取个人信息", json.toString());
//                            if (json.getInt("status") == 200) {
//                                json = json.getJSONObject("data");
//                                Gson gson = new Gson();
//                                CommunityClientApplication.myInfo = gson.fromJson(json.toString(), MyInfo.class);
//                                tv_profit.setText(CommunityClientApplication.myInfo.getOldProfit());
//
//
//                            } else if(json.getInt("status") == 505) {
//
//                            }
//
//                            else {
//                                Toast.makeText(BonusActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//    }
}
