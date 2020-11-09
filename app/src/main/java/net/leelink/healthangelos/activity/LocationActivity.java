package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import net.leelink.healthangelos.adapter.LocationListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.LocationBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocationActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    RecyclerView location_list;
    RelativeLayout rl_back;
    TextView tv_location;
    Context context = this;
    List<LocationBean> list = new ArrayList<>();
    LocationListAdapter locationListAdapter;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        init();
        initData(page);
        createProgressBar(this);
        initRefreshLayout();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_location = findViewById(R.id.tv_location);
        tv_location.setOnClickListener(this);
        location_list = findViewById(R.id.location_list);
    }

    public void initData(int page) {

        OkGo.<String>get(Urls.GPSRECORD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", page)
                .params("pageSize", 10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("gps定位列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                List<LocationBean> locationBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<LocationBean>>() {
                                }.getType());
                                list.addAll(locationBeans);
                                locationListAdapter = new LocationListAdapter(list, context, LocationActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                location_list.setLayoutManager(layoutManager);
                                location_list.setAdapter(locationListAdapter);
//                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            } else if (json.getInt("status") == 505) {
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
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_location:
                sendGps();
                break;
        }
    }

    public void sendGps() {
        showProgressBar();
        OkGo.<String>get(Urls.OPENGPS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", MyApplication.userInfo.getJwotchImei())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送gps定位", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                list.clear();
                                initData(1);
                            } else if (json.getInt("status") == 505) {
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
    public void onItemClick(View view) {
        int position = location_list.getChildLayoutPosition(view);
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("lat", list.get(position).getLatitude());
        intent.putExtra("lon", list.get(position).getLongitude());
        intent.putExtra("address", list.get(position).getAddress());
        intent.putExtra("type",list.get(position).getGpsType());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

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
                        initData(page);

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
                            initData(page);
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
