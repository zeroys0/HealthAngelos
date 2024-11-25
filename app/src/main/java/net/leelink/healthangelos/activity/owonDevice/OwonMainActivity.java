package net.leelink.healthangelos.activity.owonDevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.BatteryView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OwonMainActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private OwonStateAdapter owonStateAdapter;
    private OwonHisAdapter owonHisAdapter;
    ImageView img_head,img_connect;
    private TextView text_title,tv_device_name,tv_imei,tv_last_time,tv_unbind,tv_model,tv_temperature;
    public String imei;
    private BatteryView battery;
    private List<OwonStateBean> list;
    private List<HisBean> list_his;
    private RecyclerView state_list,history_list;
    private LinearLayout ll_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owon_main);
        context = this;
        init();
        createProgressBar(context);
        initData();
        initState();
        initHis();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(v -> finish());
        imei = getIntent().getStringExtra("imei");
        img_head = findViewById(R.id.img_head);
        if(getIntent().getStringExtra("img")!=null){
            Glide.with(context).load(Urls.getInstance().IMG_URL+"/"+getIntent().getStringExtra("img")).into(img_head);
        }
        text_title = findViewById(R.id.text_title);
        text_title.setText(getIntent().getStringExtra("name"));
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_device_name.setText(getIntent().getStringExtra("name"));
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(imei);
        battery = findViewById(R.id.battery);
        img_connect = findViewById(R.id.img_connect);
        tv_last_time = findViewById(R.id.tv_last_time);
        state_list = findViewById(R.id.state_list);
        history_list = findViewById(R.id.history_list);
        state_list.setNestedScrollingEnabled(false);
        history_list.setNestedScrollingEnabled(false);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(v ->  showPopup());
        tv_model = findViewById(R.id.tv_model);
        tv_model.setText(getIntent().getStringExtra("model"));
        ll_temp = findViewById(R.id.ll_temp);
        tv_temperature = findViewById(R.id.tv_temperature);
        if(getIntent().getStringExtra("model").equals("RT-S17-N")){
            ll_temp.setVisibility(View.VISIBLE);
        }
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().JWOTCH_STATUS + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备状态", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.has("data")){
                                    json = json.getJSONObject("data");
                                    if(json.getBoolean("status")) {
                                        battery.setPower(json.getInt("battery"));
                                        tv_last_time.setText(json.getString("updateDate"));
                                    } else {
                                        img_connect.setImageResource(R.drawable.badge_offline);
                                        tv_last_time.setText("暂无信息");
                                    }
                                } else {
                                    img_connect.setImageResource(R.drawable.badge_offline);
                                    tv_last_time.setText("暂无信息");
                                }
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
    public void initState(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().OWON_NEWEST + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备最新数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("deviceStatus");
                                Gson gson = new Gson();
                                json = json.getJSONObject("argument");
                                tv_temperature.setText(json.getString("temperature")+"℃");
                                list = gson.fromJson(jsonArray.toString(),new  TypeToken<List<OwonStateBean>>(){}.getType());
                                owonStateAdapter = new OwonStateAdapter(list,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                state_list.setLayoutManager(layoutManager);
                                state_list.setAdapter(owonStateAdapter);
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
    public void initHis(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().OWON_EVENT )
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .params("imei",imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取历史事件", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list_his = gson.fromJson(jsonArray.toString(),new  TypeToken<List<HisBean>>(){}.getType());
                                owonHisAdapter = new OwonHisAdapter(list_his,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                history_list.setLayoutManager(layoutManager);
                                history_list.setAdapter(owonHisAdapter);
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
    @SuppressLint("WrongConstant")
    public void showPopup() {

        // TODO Auto-generated method stub
        View popview = LayoutInflater.from(OwonMainActivity.this).inflate(R.layout.popu_fit_unbind, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbind();
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new OwonMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);

    }
    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BIND + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设备解绑成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
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

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }
    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }
}