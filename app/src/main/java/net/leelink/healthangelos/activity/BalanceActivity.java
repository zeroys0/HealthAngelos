package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BalanceAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.BalanceBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BalanceActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    RecyclerView cost_list;
    BalanceAdapter balanceAdapter;
    Button btn_add;
    private int page = 1;
    List<BalanceBean> list = new ArrayList<>();
    Context context;
    private TextView tv_balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        context = this;
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cost_list = findViewById(R.id.cost_list);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        tv_balance = findViewById(R.id.tv_balance);
        tv_balance.setText(getIntent().getStringExtra("balance"));
    }

    public void initData() {

        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",page);
        httpParams.put("pageSize",10);
        OkGo.<String>get(Urls.getInstance().ACCOUNT)
                .params(httpParams)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("充值消费记录: ", j.toString());
                            if (j.getInt("status") == 200) {
                                Gson gson = new Gson();
                                j = j.getJSONObject("data");
                                JSONArray jsonArray = j.getJSONArray("list");
                                List<BalanceBean> balanceBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BalanceBean>>(){}.getType());
                                balanceAdapter = new BalanceAdapter(context,balanceBeans);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_add:
                Intent intent = new Intent(this, InvestActivity.class);
                startActivity(intent);
                break;
        }
    }
}
