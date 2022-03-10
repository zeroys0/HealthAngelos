package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.Rect;
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
import net.leelink.healthangelos.adapter.SkrHistoryAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.SkrHistoryBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SkrHistroyActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private RecyclerView history_list;
    private SkrHistoryAdapter skrHistoryAdapter;
    private List<SkrHistoryBean> list = new ArrayList<>();
    private int page =  1;
    private TwinklingRefreshLayout refreshLayout;
    boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_histroy);
        context = this;
        createProgressBar(context);
        init();
        initList();
        initRefreshLayout();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        history_list = findViewById(R.id.history_list);
        SkrHistroyActivity.SpacesItemDecoration decoration = new SkrHistroyActivity.SpacesItemDecoration(16);
        history_list.addItemDecoration(decoration);
    }

    public void initList(){


        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        httpParams.put("endTime","");
        httpParams.put("startTime","");
        httpParams.put("pageNum",page);
        httpParams.put("pageSize","10");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ALARM_HIS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取历史消息", json.toString());

                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                List<SkrHistoryBean> historyBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<SkrHistoryBean>>(){}.getType());
                                list.addAll(historyBeans);

                                skrHistoryAdapter = new SkrHistoryAdapter(context,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                history_list.setAdapter(skrHistoryAdapter);
                                history_list.setLayoutManager(layoutManager);


                                if(page >1) {
                                    history_list.scrollToPosition(list.size()-1);
                                }
                            }else if(json.getInt("status") == 505){
                                reLogin(context);
                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;


        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
