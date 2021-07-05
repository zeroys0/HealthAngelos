package net.leelink.healthangelos.reform;

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
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BindListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.BindBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BindHistoryActivity extends BaseActivity implements OnOrderListener {
    private Context context;
    private BindListAdapter bindListAdapter;
    private RecyclerView bind_list;
    private RelativeLayout rl_back;
    private int page = 1;
    private boolean hasNextPage;
    private TwinklingRefreshLayout refreshLayout;
    List<BindBean> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_history);
        init();
        context = this;
        initList();
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
        bind_list = findViewById(R.id.bind_list);
    }

    public void initList(){

        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",page);
        httpParams.put("pageSize",10);
        OkGo.<String>get(Urls.getInstance().CIVILL_RECORD)
                .tag(this)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询绑定记录", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson= new Gson();
                                json  = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<BindBean> beans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BindBean>>(){}.getType());
                                list.addAll(beans);
                                bindListAdapter = new BindListAdapter(list,context,BindHistoryActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                bind_list.setAdapter(bindListAdapter);
                                bind_list.setLayoutManager(layoutManager);
                                if(page>1){
                                    bind_list.scrollToPosition(bindListAdapter.getItemCount()-1);
                                }
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
        int position  = bind_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context,BindDetailActivity.class);
        intent.putExtra("bean",list.get(position));
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    public void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(context);
        headerView.setTextColor(0xff745D5C);
//        refreshLayout.setHeaderView((new ProgressLayout(getActivity())));
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setBottomView(new LoadingView(context));
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
//                        list.clear();
                        page = 1;
                        initList();

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
                            initList();
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
}
