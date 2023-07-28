package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.SwitchCompat;

public class R60flRadarMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_banner;
    private LinearLayout ll_leave, ll_move_state;
    private Button btn_detail_setting, btn_report_setting;
    private Context context;
    private TextView tv_connect, tv_imei, tv_scene, tv_history, tv_unbind, tv_state, tv_stay_text, tv_warning, tv_stay_warnning, tv_history_l;
    private RelativeLayout rl_scene, rl_people;
    private ImageView img_work_mode, img_state, img_stay;
    private SwitchCompat cb_stay, cb_fall;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60fl_radar_main);
        context = this;
        createProgressBar(context);
        init();
    }

    Timer timer;
    TimerTask task;

    @Override
    protected void onStart() {
        super.onStart();
        task = new R60flRadarMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 20000);
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        rl_scene = findViewById(R.id.rl_scene);
        tv_connect = findViewById(R.id.tv_connect);
        tv_scene = findViewById(R.id.tv_scene);
        tv_scene.setOnClickListener(this);
        tv_history_l = findViewById(R.id.tv_history_l);
        tv_history_l.setOnClickListener(this);
        ll_leave = findViewById(R.id.ll_leave);
        rl_people = findViewById(R.id.rl_people);
        img_stay = findViewById(R.id.img_stay);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        btn_detail_setting = findViewById(R.id.btn_detail_setting);
        btn_detail_setting.setOnClickListener(this);
        btn_report_setting = findViewById(R.id.btn_report_setting);
        btn_report_setting.setOnClickListener(this);
        rl_banner = findViewById(R.id.rl_banner);
        img_state = findViewById(R.id.img_state);
        tv_state = findViewById(R.id.tv_state);
        tv_stay_text = findViewById(R.id.tv_stay_text);
        tv_warning = findViewById(R.id.tv_warning);
        tv_history = findViewById(R.id.tv_history);
        tv_history.setOnClickListener(this);
        tv_stay_warnning = findViewById(R.id.tv_stay_warnning);
        ll_move_state = findViewById(R.id.ll_move_state);
        cb_fall = findViewById(R.id.cb_fall);
        cb_stay = findViewById(R.id.cb_stay);
        cb_fall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check) {
                    setFall(isChecked);
                }
            }
        });
        cb_stay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check) {
                    setStatic(isChecked);
                }
            }
        });
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //do something
            try {
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initData() {
        OkGo.<String>get(Urls.getInstance().R60_PARAMS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备属性", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("online") == 0) {
                                    tv_connect.setText("设备离线");
                                    ll_leave.setVisibility(View.VISIBLE);
                                } else {
                                    tv_connect.setText("设备在线");
                                    if (ll_leave.getVisibility() == View.VISIBLE) {
                                        ll_leave.setVisibility(View.GONE);
                                    }
                                }
                                /**
                                 * 场景模式
                                 */
                                if (!json.isNull("sceneMode")) {
                                    switch (json.getInt("sceneMode")) {
                                        case 1:
                                            rl_banner.setBackground(getResources().getDrawable(R.drawable.r60_keting));
                                            tv_scene.setText("客厅");
                                            break;
                                        case 2:
                                            rl_banner.setBackground(getResources().getDrawable(R.drawable.r60_bedoom));
                                            tv_scene.setText("卧室");
                                            break;
                                        case 3:
                                            rl_banner.setBackground(getResources().getDrawable(R.drawable.r60_wc));
                                            tv_scene.setText("卫生间");
                                            break;
                                    }
                                }
                                /**
                                 * 设备工作模式
                                 */
                                if (!json.isNull("workMode")) {
                                    switch (json.getInt("workMode")) {
                                        case 1:
                                            //工作模式
                                            //img_work_mode.setImageResource(R.drawable.r60_gongzuo);
                                            break;
                                        case 2:
                                            //待机模式
                                            //img_work_mode.setImageResource(R.drawable.r60_daiji);
                                            break;
                                        case 3:
                                            //测试模式
                                            //img_work_mode.setImageResource(R.drawable.r60_ceshi);
                                            break;
                                    }
                                }
                                /**
                                 * 运动状态
                                 */
                                if (!json.isNull("motionStatus")) {
                                    switch (json.getInt("motionStatus")) {
                                        case 0:
                                            //无运动状态
                                            tv_stay_text.setText("无");
                                            img_stay.setImageResource(R.drawable.img_stay_blue);
                                            break;
                                        case 1:
                                            //静止
                                            tv_stay_text.setText("静止中");
                                            img_stay.setImageResource(R.drawable.img_stay_blue);
                                            break;
                                        case 2:
                                            //活跃
                                            tv_stay_text.setText("活跃");
                                            img_stay.setImageResource(R.drawable.img_stay_orange);
                                            break;
                                    }
                                }
                                if (!json.isNull("locationOutOfBounds")) {
                                    if (json.getInt("locationOutOfBounds") == 0) {
                                        //范围内

                                    } else {
                                        //范围外

                                    }
                                }
                                /**
                                 * 人体存在监测
                                 */
                                if (!json.isNull("humanSwitch")) {
                                    if (json.getInt("humanSwitch") == 0) {

                                    } else {
                                        if (!json.isNull("someoneExists")) {
                                            if (json.getInt("someoneExists") == 0) {
                                                img_state.setImageResource(R.drawable.r60_wuren);
                                                tv_state.setText("当前无人");
                                                ll_move_state.setVisibility(View.INVISIBLE);
                                            } else {
                                                ll_move_state.setVisibility(View.VISIBLE);
                                                if (json.getInt("fallStatus") == 0) {
                                                    img_state.setImageResource(R.drawable.r60_youren);
                                                    tv_state.setText("当前有人");
                                                } else {
                                                    if (!json.isNull("residentStatus")) {
                                                        if (json.getInt("residentStatus") == 0) {
                                                            //无驻留
                                                            img_state.setImageResource(R.drawable.r60_unstay);
                                                            tv_stay_warnning.setVisibility(View.INVISIBLE);
                                                        } else {
                                                            //有驻留
                                                            img_state.setImageResource(R.drawable.r60_stay);
                                                            tv_stay_warnning.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                /**
                                 * 驻留监测
                                 */
                                if (!json.isNull("residentWarningDurationSwitch")) {
                                    if (json.getInt("residentWarningDurationSwitch") == 0) {
                                        //驻留开关未开启
                                        cb_stay.setChecked(false);
                                    } else {
                                        cb_stay.setChecked(true);

                                    }
                                }

                                /**
                                 * 跌倒监测
                                 */
                                if (!json.isNull("fallSwitch")) {
                                    if (json.getInt("fallSwitch") == 0) {
                                        //未开启
                                        cb_fall.setChecked(false);
                                    } else {
                                        //开启
                                        cb_fall.setChecked(true);
                                        if (!json.isNull("fallStatus")) {
                                            if (json.getInt("fallStatus") == 0) {
                                                //正常
                                                tv_warning.setText("当前无警示状态");
                                                tv_warning.setTextColor(Color.parseColor("#7fda63"));
                                            } else {
                                                //跌倒
                                                tv_warning.setText("人员跌倒告警");
                                                tv_warning.setTextColor(getResources().getColor(R.color.red));
                                            }
                                        }
                                    }
                                }
                                /**
                                 * 体征参数
                                 */
                                if (!json.isNull("movementSigns")) {

                                }
                                check = true;
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

    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().R60_UNBIND + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑雷达", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设备已解绑", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_detail_setting:
                Intent intent = new Intent(this, R60DetailSettingActivity.class);
                intent.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent);
                break;
            case R.id.btn_report_setting:
                Intent intent1 = new Intent(this, R60ReportSettingActivity.class);
                intent1.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent1);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
            case R.id.tv_scene: //场景模式
                Intent intent2 = new Intent(this, R60SceneAettingActivity.class);
                intent2.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent2);
                break;
            case R.id.tv_history:   //历史记录
            case R.id.tv_history_l:
                Intent intent3 = new Intent(this, R60HistoryActivity.class);
                intent3.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent3);
                break;

        }
    }

    public void setFall(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().FALLSWITCH + "/" + getIntent().getStringExtra("imei") + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置跌倒开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setStatic(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().RESIDENTWARNING + "/" + getIntent().getStringExtra("imei") + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置静止驻留开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }

}