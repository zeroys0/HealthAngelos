package net.leelink.healthangelos.activity.NBdevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.NBdevice.adapter.TiBean;
import net.leelink.healthangelos.activity.NBdevice.adapter.TiTimeAdapter;
import net.leelink.healthangelos.activity.NBdevice.adapter.TiTimeBean;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NBMainActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private Context context;
    private TextView text_title, tv_device_name, tv_imei, tv_model, tv_setting, tv_newest_time, tv_value;
    private ImageView img_head;
    private RecyclerView alarm_list;
    private BatteryView battery;
    private String deviceType, imei;
    private int pageNum = 1;
    Map<String, List<NbMsgBean>> groupedData = new HashMap<>();
    LinkedHashMap<String, List<TiBean>> groupedData_t = new LinkedHashMap<>();
    private NbMsgAdapter nbMsgAdapter;
    private TiTimeAdapter tiTimeAdapter;
    private List<NbMsgTimeBean> nbMsgTimeBeanList = new ArrayList<>();
    private List<TiTimeBean> tiTimeBeanList = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    RecyclerView.OnScrollListener scrollListener;
    private boolean hasNextPage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nbmain);
        context = this;
        init();
        if (getIntent().getStringExtra("model").equals("TiBTN01")) {
            initListB();
        } else {
            initList();
        }
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        text_title = findViewById(R.id.text_title);
        text_title.setText(getIntent().getStringExtra("name"));
        img_head = findViewById(R.id.img_head);
        Glide.with(context).load(Urls.getInstance().IMG_URL + getIntent().getStringExtra("img")).into(img_head);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name"));
        tv_imei = findViewById(R.id.tv_imei);
        tv_newest_time = findViewById(R.id.tv_newest_time);
        imei = getIntent().getStringExtra("imei");
        tv_imei.setText(imei);
        tv_model = findViewById(R.id.tv_model);
        tv_value = findViewById(R.id.tv_value);
        alarm_list = findViewById(R.id.alarm_list);
        tv_model.setText(getIntent().getStringExtra("model"));
        tv_setting = findViewById(R.id.tv_setting);
        switch (getIntent().getStringExtra("model")) {
            case "EB16":
                deviceType = "34007";
                break;
            case "EC91":
                deviceType = "34003";
                break;
            case "ED672N":
                deviceType = "34005";
                tv_setting.setVisibility(View.VISIBLE);
                break;
            case "ED713":
                deviceType = "60001";
                break;
            case "ED719":
                deviceType = "60002";
                break;
        }
        tv_setting.setOnClickListener(this);
        battery = findViewById(R.id.battery);
        mRunnable.run();

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition == totalItemCount - 1) {
                    recyclerView.removeOnScrollListener(this); // Remove the scroll listener
                    // 在此处加载下一页数据
                    if (hasNextPage) {
                        pageNum++;
                        // 将新数据添加到适配器的数据集中
                        // 通知适配器数据已更改
                        initList();
                    }
                }
            }
        };
        alarm_list.addOnScrollListener(scrollListener);
    }

    public void initData() {
        String url = Urls.getInstance().INNOPRO_NEWEST;
        if (getIntent().getStringExtra("model").equals("TiBTN01")) {
            url = Urls.getInstance().TI_EVENT_NEWEST;
        }
        OkGo.<String>get(url + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新一条数据", json.toString());
                            if (json.has("data")) {
                                json = json.getJSONObject("data");
                                tv_newest_time.setText(json.getString("createTime"));

                                if (getIntent().getStringExtra("model").equals("TiBTN01")) { //判断是钛极一键报警器
                                    tv_value.setText(json.getString("eventDec"));
                                } else {
                                    tv_value.setText(json.getString("eventContext"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initList() {
        OkGo.<String>get(Urls.getInstance().INNOPRO_EVENT)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceType", deviceType)
                .params("imei", imei)
                .params("pageSize", 20)
                .params("pageNum", pageNum)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取消息列表", json.toString());
                            json = json.getJSONObject("data");
                            hasNextPage = json.getBoolean("hasNextPage");
                            Gson gson = new Gson();
                            JSONArray ja = json.getJSONArray("list");
                            List<NbMsgBean> list = gson.fromJson(ja.toString(), new TypeToken<List<NbMsgBean>>() {
                            }.getType());
                            Calendar calendar = Calendar.getInstance();
                            for (NbMsgBean nbMsgBean : list) {
                                String createTimeString = nbMsgBean.getCreateTime();
                                String date = sdf.format(sdf.parse(createTimeString));
                                if (groupedData.containsKey(date)) {
                                    //日期相同,添加到当前日期下
                                    groupedData.get(date).add(nbMsgBean);
                                } else {
                                    //日期不同,创建新日期的列表
                                    List<NbMsgBean> nbMsgBeans = new ArrayList<>();
                                    nbMsgBeans.add(nbMsgBean);
                                    groupedData.put(date, nbMsgBeans);
                                }
                            }
                            Collections.reverse(list);

                            nbMsgTimeBeanList.clear();
                            for (Map.Entry<String, List<NbMsgBean>> entry : groupedData.entrySet()) {
                                String date = entry.getKey();
                                List<NbMsgBean> beans = entry.getValue();
                                calendar.setTime(sdf.parse(date));
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                nbMsgTimeBeanList.add(new NbMsgTimeBean(year, month, day, beans));
                            }
                            Collections.reverse(nbMsgTimeBeanList);
                            if (pageNum > 1) {
                                nbMsgAdapter.notifyDataSetChanged();
                                alarm_list.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        alarm_list.addOnScrollListener(scrollListener); // 重新添加滚动监听器

                                    }
                                });
                            } else {
                                if (deviceType.equals("60002")) {
                                    nbMsgAdapter = new NbMsgAdapter(context, nbMsgTimeBeanList, 2);
                                } else {
                                    nbMsgAdapter = new NbMsgAdapter(context, nbMsgTimeBeanList, 1);
                                }
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                alarm_list.setAdapter(nbMsgAdapter);
                                alarm_list.setLayoutManager(layoutManager);
                            }
//                            if (pageNum > 1) {
//                                nbMsgAdapter.notifyDataSetChanged();
//                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void initListB() {
        OkGo.<String>get(Urls.getInstance().TI_EVENT_LIST + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceType", deviceType)
                .params("imei", imei)
                .params("pageSize", 20)
                .params("pageNum", pageNum)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取消息列表", json.toString());
                            json = json.getJSONObject("data");
                            hasNextPage = json.getBoolean("hasNextPage");
                            Gson gson = new Gson();
                            JSONArray ja = json.getJSONArray("list");
                            List<TiBean> list = gson.fromJson(ja.toString(), new TypeToken<List<TiBean>>() {
                            }.getType());
                            Calendar calendar = Calendar.getInstance();
                            for (TiBean nbMsgBean : list) {
                                String createTimeString = nbMsgBean.getCreateTime();
                                String date = sdf.format(sdf.parse(createTimeString));
                                if (groupedData_t.containsKey(date)) {
                                    //日期相同,添加到当前日期下
                                    groupedData_t.get(date).add(nbMsgBean);
                                } else {
                                    //日期不同,创建新日期的列表
                                    List<TiBean> nbMsgBeans = new ArrayList<>();
                                    nbMsgBeans.add(nbMsgBean);
                                    groupedData_t.put(date, nbMsgBeans);
                                }
                            }
                            Collections.reverse(list);

                            tiTimeBeanList.clear();
                            for (Map.Entry<String, List<TiBean>> entry : groupedData_t.entrySet()) {
                                String date = entry.getKey();
                                List<TiBean> beans = entry.getValue();
                                calendar.setTime(sdf.parse(date));
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                tiTimeBeanList.add(new TiTimeBean(year, month, day, beans));
                            }
                            Collections.reverse(tiTimeBeanList);
                            if (pageNum > 1) {
                                tiTimeAdapter.notifyDataSetChanged();
                                alarm_list.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        alarm_list.addOnScrollListener(scrollListener); // 重新添加滚动监听器
                                    }
                                });
                            } else {
                                tiTimeAdapter = new TiTimeAdapter(context, tiTimeBeanList);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                alarm_list.setAdapter(tiTimeAdapter);
                                alarm_list.setLayoutManager(layoutManager);
                            }
//                            if (pageNum > 1) {
//                                nbMsgAdapter.notifyDataSetChanged();
//                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_setting:
                Intent intent = new Intent(context, NBSettingActivity.class);
                intent.putExtra("imei", imei);
                startActivity(intent);
                break;
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            initData();
            mHandler.postDelayed(this, 30000); // 30秒后再次执行任务

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable); // 停止任务
    }
}