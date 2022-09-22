package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocateRateSetActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_rate;
    private TextView tv_rate;
    private Button btn_save;
    private int second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_rate_set);
        context = this;
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_rate = findViewById(R.id.rl_rate);
        rl_rate.setOnClickListener(this);
        tv_rate = findViewById(R.id.tv_rate);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    public void initData(){
        //获取频率初始数据
        String imei = getSharedPreferences("sp", 0).getString("imei", "");
        OkGo.<String>get(Urls.getInstance().SAAS_DEVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceList", imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询腕表基本信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = json.getJSONArray("data");
                                json = jsonArray.getJSONObject(0);
                                if (!json.isNull("fqcy")) {
                                    switch (json.getInt("fqcy")) {
                                        case 300:
                                            tv_rate.setText("5分钟");
                                            break;
                                        case 600:
                                            tv_rate.setText("10分钟");
                                            break;
                                        case 900:
                                            tv_rate.setText("15分钟");
                                            break;
                                        case 1800:
                                            tv_rate.setText("半小时");
                                            break;
                                        case 3600:
                                            tv_rate.setText("1小时");
                                            break;
                                        case 0:
                                            tv_rate.setText("关闭");
                                            break;
                                    }
                                } else {
                                    tv_rate.setText("关闭");
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_rate:
                chooseRate();
                break;
            case R.id.btn_save:
                setRate(second);
                break;
        }
    }

    public void chooseRate(){
        List<RateBean> list = new ArrayList<>();
        list.add(new RateBean(300, "5分钟"));
        list.add(new RateBean(600, "10分钟"));
        list.add(new RateBean(900, "15分钟"));
        list.add(new RateBean(1800, "半小时"));
        list.add(new RateBean(3600, "1小时"));
        List<String> choose_list = new ArrayList<>();
        for (RateBean rateBean : list) {
            choose_list.add(rateBean.getTime());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_rate.setText(list.get(options1).getTime());
                second = list.get(options1).getSecond();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(choose_list);
        pvOptions.show();
    }

    public void setRate(int second) {
        String imei = getSharedPreferences("sp", 0).getString("imei", "");
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().ECHOLOCATIONFREQUENCY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("fqCy", second)
                .params("mId", imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置定位频率", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置频率成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }
}