package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MyActionAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.ActionBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyActionActivity extends BaseActivity implements OnOrderListener {
    RecyclerView action_list;
    RelativeLayout rl_back;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    Context context;
    MyActionAdapter myActionAdapter;
    List<ActionBean> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_action);
        init();
        context = this;
        createProgressBar(context);
        initData();
        initRefreshLayout();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initData(){
        action_list = findViewById(R.id.action_list);

        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ACTION_MINE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",page)
                .params("pageSize",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("我的社区活动", json.toString());
                            if (json.getInt("status") == 200) {

                                json = json.getJSONObject("data");
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<ActionBean> actionBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<ActionBean>>(){}.getType());
                                list.addAll(actionBeans);
                                hasNextPage = json.getBoolean("hasNextPage");
                                myActionAdapter = new MyActionAdapter(list,context,MyActionActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                action_list.setLayoutManager(layoutManager);
                                action_list.setAdapter(myActionAdapter);

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
    public void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(this);
        headerView.setTextColor(0xff745D5C);
//        refreshLayout.setHeaderView((new ProgressLayout(getActivity())));
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setBottomView(new LoadingView(this));
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        list.clear();
                        page = 1;
                        initData();

                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                        if (hasNextPage) {
                            page++;
                            initData();
                        }
                    }
                }, 1000);
            }

        });
        // 是否允许开启越界回弹模式
        refreshLayout.setEnableOverScroll(false);
        //禁用掉加载更多效果，即上拉加载更多
        refreshLayout.setEnableLoadmore(true);
        // 是否允许越界时显示刷新控件
        refreshLayout.setOverScrollRefreshShow(true);


    }

    @Override
    public void onItemClick(View view) {


//        Intent intent = new Intent(this,WebActivity.class);
//        String url  = Urls.getInstance().COMMUNITY_WEB+list.get(position).getActivityId()+"/"+MyApplication.userInfo.getOlderlyId()+"/"+list.get(position).getState()+"/"+MyApplication.token;
//        intent.putExtra("url",url);
//        startActivity(intent);
//
//        if(list.get(position).getState()==4){
//            Intent intent1 = new Intent(this,ActionCommentActivity.class);
//            startActivity(intent1);
//        }
        int position = action_list.getChildLayoutPosition(view);
        Intent intent = new Intent(this,ActionDetailActivity.class);
        intent.putExtra("activity_id",list.get(position).getActivityId());
        intent.putExtra("sign",list.get(position).getSign());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
