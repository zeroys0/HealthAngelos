package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.content.Intent;
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
import java.util.Timer;
import java.util.TimerTask;

public class JWatchBBloodPressureActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_rate_set;
    private TextView tv_data, tv_sbp, tv_dbp, tv_rate;
    private Button btn_get_data;
    Timer timer;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwatch_bblood_pressure);
        context = this;
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_data = findViewById(R.id.tv_data);
        tv_data.setOnClickListener(this);
        tv_sbp = findViewById(R.id.tv_sbp);
        tv_dbp = findViewById(R.id.tv_dbp);
        tv_rate = findViewById(R.id.tv_rate);
        btn_get_data = findViewById(R.id.btn_get_data);
        btn_get_data.setOnClickListener(this);
        rl_rate_set = findViewById(R.id.rl_rate_set);
        rl_rate_set.setOnClickListener(this);
    }

    public void initData() {
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
                                    tv_sbp.setText(json.getString("x"));
                                    tv_dbp.setText(json.getString("y"));
                                    if (!json.isNull("hrt")) {
                                        switch (json.getInt("hrt")) {
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
                                            case 7200:
                                                tv_rate.setText("2小时");
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
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_data:      //查看历史记录
                Intent intent = new Intent(context, JWatchbBloodPressureHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_get_data:     //发送测量血压指令
                setRate(1);
                if (task == null) {
                    task = new MyTask();
                    timer = new Timer();
                    timer.schedule(task, 30000, 30000);
                }
                break;
            case R.id.rl_rate_set:
                chooseRate();
                break;
        }
    }

    public void chooseRate() {
        List<RateBean> list = new ArrayList<>();
        list.add(new RateBean(0, "关闭"));
        list.add(new RateBean(300, "5分钟"));
        list.add(new RateBean(600, "10分钟"));
        list.add(new RateBean(900, "15分钟"));
        list.add(new RateBean(1800, "半小时"));
        list.add(new RateBean(3600, "1小时"));
        list.add(new RateBean(7200, "2小时"));
        List<String> choose_list = new ArrayList<>();
        for (RateBean rateBean : list) {
            choose_list.add(rateBean.getTime());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_rate.setText(list.get(options1).getTime());
                setRate(list.get(options1).getSecond());
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


    class MyTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //do something
            try {
                loopData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loopData() {
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
                                tv_sbp.setText(json.getString("x"));
                                tv_dbp.setText(json.getString("y"));
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

    public void setRate(int second) {
        String uid = getSharedPreferences("sp", 0).getString("uid", "");
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().ADDHEARTRATETIME)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("hrt", second)
                .params("uId",uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送单次测量血压", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发送指令成功", Toast.LENGTH_SHORT).show();
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