package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ItemListAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.RecycleViewDivider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UseDetailActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_name;
    private RelativeLayout rl_back;
    private RecyclerView item_list;
    private ItemListAdapter itemListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_detail);
        context = this;
        init();
        initData();
    }

    public void init(){
        tv_name = findViewById(R.id.tv_name);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        item_list = findViewById(R.id.item_list);


    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().MEAL+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("套餐详情", json.toString());
                            if (json.getInt("status") == 200) {

                                JSONArray jsonArray = json.getJSONArray("data");
                                itemListAdapter = new ItemListAdapter(jsonArray,context);
                                RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                item_list.setAdapter(itemListAdapter);
                                item_list.setNestedScrollingEnabled(false);
                                item_list.setLayoutManager(layoutManager);
                                item_list.addItemDecoration(new RecycleViewDivider(
                                        context, LinearLayoutManager.VERTICAL, 43, getResources().getColor(R.color.grey_background)));
                            }else if(json.getInt("status") == 505){
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
