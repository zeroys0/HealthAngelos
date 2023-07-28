package net.leelink.healthangelos.activity.R60flRadar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class R60HistoryActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_left, rl_right;
    private TextView tv_left, tv_right;
    private ImageView img_left, img_right;
    private TwinklingRefreshLayout refreshLayout;
    private int page = 1;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private List<R60TimeBean> r60TimeBeanList = new ArrayList<>();
    private R60HistoryAdapter r60HistoryAdapter;
    private RecyclerView msg_list;
    private Boolean hasNextPage;
    private int choose = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60_history);
        context = this;
        createProgressBar(context);
        init();
        initRefreshLayout();
        initList();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_left = findViewById(R.id.rl_left);
        rl_left.setOnClickListener(this);
        rl_right = findViewById(R.id.rl_right);
        rl_right.setOnClickListener(this);
        tv_left = findViewById(R.id.tv_left);
        img_left = findViewById(R.id.img_left);
        tv_right = findViewById(R.id.tv_right);
        img_right = findViewById(R.id.img_right);
        msg_list = findViewById(R.id.msg_list);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_left:
                choose = 0;
                r60TimeBeanList.clear();
                groupedData.clear();
                page = 1;
                rl_left.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));
                tv_left.setTextColor(getResources().getColor(R.color.white));
                img_left.setImageResource(R.drawable.img_stay_white);
                rl_right.setBackground(getResources().getDrawable(R.drawable.bg_white_radius));
                tv_right.setTextColor(getResources().getColor(R.color.red));
                img_right.setImageResource(R.drawable.img_fall);
                initList();
                break;
            case R.id.rl_right:
                choose = 1;
                r60TimeBeanList.clear();
                warnData.clear();
                page = 1;
                rl_left.setBackground(getResources().getDrawable(R.drawable.bg_white_radius));
                tv_left.setTextColor(getResources().getColor(R.color.text_black));
                img_left.setImageResource(R.drawable.img_stay_blue);
                rl_right.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));
                tv_right.setTextColor(getResources().getColor(R.color.white));
                img_right.setImageResource(R.drawable.img_fall_white);
                showWarnList();
                break;

        }
    }

    Map<String, List<R60Bean>> groupedData = new HashMap<>();

    public void initList() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().R60_SOMEONEEXISTS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceId", getIntent().getStringExtra("imei"))
                .params("pageNum", page)
                .params("pageSize", 20)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取存在记录", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                JSONArray js = json.getJSONArray("list");
                                List<R60Bean> list = gson.fromJson(js.toString(), new TypeToken<List<R60Bean>>() {
                                }.getType());

                                Calendar calendar = Calendar.getInstance();
                                for (R60Bean r60Bean : list) {
                                    String createTimeString = r60Bean.getCreateTime();
                                    String date = sdf.format(sdf.parse(createTimeString));
                                    if (groupedData.containsKey(date)) {
                                        groupedData.get(date).add(r60Bean);
                                    } else {
                                        List<R60Bean> r60Beans = new ArrayList<>();
                                        r60Beans.add(r60Bean);
                                        groupedData.put(date, r60Beans);
                                    }
                                }
                                r60TimeBeanList.clear();
                                for (Map.Entry<String, List<R60Bean>> entry : groupedData.entrySet()) {
                                    String date = entry.getKey();
                                    List<R60Bean> beans = entry.getValue();
                                    calendar.setTime(sdf.parse(date));
                                    int year = calendar.get(Calendar.YEAR);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    r60TimeBeanList.add(new R60TimeBean(year, month, day, beans));
                                }

                                r60HistoryAdapter = new R60HistoryAdapter(context, r60TimeBeanList, 1);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                msg_list.setAdapter(r60HistoryAdapter);
                                msg_list.setLayoutManager(layoutManager);
                                if (page > 1) {
                                    r60HistoryAdapter.notifyDataSetChanged();
                                }

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | ParseException e) {
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

    Map<String, List<R60Bean>> warnData = new HashMap<>();

    public void showWarnList() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().R60_FALLSTATUS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceId", getIntent().getStringExtra("imei"))
                .params("pageNum", page)
                .params("pageSize", 20)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取告警记录", json.toString());
                            if (json.getInt("status") == 200) {

                                json = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                JSONArray js = json.getJSONArray("list");
                                List<R60Bean> list = gson.fromJson(js.toString(), new TypeToken<List<R60Bean>>() {
                                }.getType());

                                Calendar calendar = Calendar.getInstance();
                                for (R60Bean r60Bean : list) {
                                    String createTimeString = r60Bean.getCreateTime();
                                    String date = sdf.format(sdf.parse(createTimeString));
                                    if (warnData.containsKey(date)) {
                                        warnData.get(date).add(r60Bean);
                                    } else {
                                        List<R60Bean> r60Beans = new ArrayList<>();
                                        r60Beans.add(r60Bean);
                                        warnData.put(date, r60Beans);
                                    }
                                }
                                r60TimeBeanList.clear();
                                for (Map.Entry<String, List<R60Bean>> entry : warnData.entrySet()) {
                                    String date = entry.getKey();
                                    List<R60Bean> beans = entry.getValue();
                                    calendar.setTime(sdf.parse(date));
                                    int year = calendar.get(Calendar.YEAR);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    r60TimeBeanList.add(new R60TimeBean(year, month, day, beans));
                                }

                                r60HistoryAdapter = new R60HistoryAdapter(context, r60TimeBeanList, 2);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                msg_list.setAdapter(r60HistoryAdapter);
                                msg_list.setLayoutManager(layoutManager);
                                if (page > 1) {
                                    r60HistoryAdapter.notifyDataSetChanged();
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | ParseException e) {
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
                        r60TimeBeanList.clear();
                        page = 1;

                        if (choose == 0) {
                            initList();
                        } else if (choose == 1) {
                            showWarnList();
                        }
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
                            if (choose == 0) {
                                initList();
                            } else if (choose == 1) {
                                showWarnList();
                            }
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
