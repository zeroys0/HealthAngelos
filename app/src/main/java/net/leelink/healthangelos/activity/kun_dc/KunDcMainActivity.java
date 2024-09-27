package net.leelink.healthangelos.activity.kun_dc;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.just.agentweb.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.SwitchCompat;

public class KunDcMainActivity extends BaseActivity {
    RelativeLayout rl_back;
    LinearLayout ll_setting,ll_1;
    RadioButton kun_left, kun_right, kun_center;
    RadioGroup rg_kun;

    TextView tv_unbind;

    ImageView img_offline;

    String imei;

    EditText ed_far, ed_near;

    SwitchCompat report_switch, update_switch;

    TextView tv_report_time, tv_start_time, tv_end_time,tv_imei,tv_state;

    private Context context;

    LinearLayout ll1;
    AgentWeb agentweb;


    Timer timer;
    TimerTask task;

    private TimePickerView pvTime;
    private SimpleDateFormat sdf;

    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kun_dc_main);
        context = this;
        init();
        initTime();
        task = new KunDcMainActivity.MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 30000);
    }

    public void
    init() {
        imei = getIntent().getStringExtra("imei");
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(v -> finish());
        ll1 = findViewById(R.id.ll_1);
        kun_left = findViewById(R.id.kun_left);
        kun_right = findViewById(R.id.kun_right);
        kun_center = findViewById(R.id.kun_center);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        rg_kun = findViewById(R.id.rg_kun);
        ll_setting = findViewById(R.id.ll_setting);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(imei);
        tv_state = findViewById(R.id.tv_state);
        img_offline = findViewById(R.id.img_offline);



        Bundle args = new Bundle();
        args.putString("imei", imei);
        rg_kun.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.kun_left:
                        ll_setting.setVisibility(View.GONE);
                        ll1.setVisibility(View.VISIBLE);
                        String url1 = Urls.H5_IP+"/#/sleepVitalSigns/"+imei+"/"+ MyApplication.token;
                        setWeb(url1);
                        Log.d( "实时睡眠: ",url1);
                        kun_left.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.kun_center:
                        ll_setting.setVisibility(View.GONE);
                        ll1.setVisibility(View.VISIBLE);
                        String url = Urls.H5_IP+"/#/sleepaceReportNoContact/"+imei+"/"+ MyApplication.token;
                        setWeb(url);
                        Log.d( "睡眠报告: ",url);
                        kun_center.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.kun_right:
                        ll_setting.setVisibility(View.VISIBLE);
                        ll1.setVisibility(View.GONE);
                        kun_right.setTextColor(getResources().getColor(R.color.white));
                        break;
                    // 其他按钮
                }
            }
        });

        ed_far = findViewById(R.id.ed_far);
        ed_far.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    setMaxDistance(ed_far.getText().toString());
                }
                return false;
            }
        });
        ed_near = findViewById(R.id.ed_near);
        ed_near.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    setMinDistance();
                }
                return false;
            }
        });
        report_switch = findViewById(R.id.report_switch);
        report_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setReportSwitch(isChecked);
            }
        });
        update_switch = findViewById(R.id.update_switch);
        update_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUploadSwitch(isChecked);
            }
        });
        tv_report_time = findViewById(R.id.tv_report_time);
        tv_report_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
    }

    public void initData() {
        Log.d( "initData: ",Urls.getInstance().ZEN_ONLINE + "/" + imei);
        OkGo.<String>get(Urls.getInstance().ZEN_ONLINE + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取睡眠设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_state.setText("设备在线");
                                img_offline.setVisibility(View.INVISIBLE);
                                json = json.getJSONObject("data");
                                json = json.getJSONObject("deviceInfo");
                                ed_far.setText(json.getString("maxDistance"));
                                ed_near.setText(json.getString("minDistance"));
                                report_switch.setChecked(json.getInt("reportSwitch") != 0);
                                update_switch.setChecked(json.getInt("uploadSwitch") != 0);
                                tv_report_time.setText(json.getJSONObject("reportTime").getString("value"));
                                tv_start_time.setText(json.getJSONObject("beginSleep").getString("value"));
                                tv_end_time.setText(json.getJSONObject("endSleep").getString("value"));
                            }  else if (json.getInt("status") == 204) {
                              tv_state.setText("设备离线");
                              img_offline.setVisibility(View.VISIBLE);
                                img_offline.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v) {
                                        showpopu();
                                        backgroundAlpha(0.5f);
                                    }
                                });
                                kun_left.setClickable(false);
                                kun_right.setClickable(false);
                                kun_center.setChecked(true);
                            }
                            else if (json.getInt("status") == 505) {
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
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_unbind:
                unbind();
                break;
            case R.id.tv_report_time:
                type = 1;
                pvTime.show();
                break;
            case R.id.tv_start_time:
                type = 2;
                pvTime.show();
                break;
            case R.id.tv_end_time:
                type = 3;
                pvTime.show();
                break;
        }
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

    private void initTime() {
        boolean[] time_type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String d = sdf.format(date);
                switch (type) {
                    case 1:
                        tv_report_time.setText(d);
                        setReportTime();
                        break;
                    case 2:
                        tv_start_time.setText(d);
                        setSleepTime();
                        break;
                    case 3:
                        tv_end_time.setText(d);
                        setSleepTime();
                        break;
                    default:
                        break;
                }
            }
        }).setType(time_type).build();
    }

    public String getImei(){
        return getIntent().getStringExtra("imei");
    }

    public void setMaxDistance(String maxDistance) {
        Integer max = Integer.parseInt(maxDistance);
        if(max>300){
            ed_far.setText("300");
            max = 300;
        }
        OkGo.<String>get(Urls.getInstance().ZEN_MAX_DISTANCE)
                .tag(this)
                .params("distance",max)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置最远检测距离", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();

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

    public void setMinDistance() {
        Integer min = Integer.parseInt(ed_near.getText().toString());
        if(min<50){
            ed_near.setText("50");
            min = 50;
        }
        OkGo.<String>get(Urls.getInstance().ZEN_MIN_DISTANCE)
                .tag(this)
                .params("distance",min)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置最近监测距离", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();

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

    public void setReportSwitch(boolean isChecked) {
        int on = -1;
        on =  isChecked?1:0;
        OkGo.<String>get(Urls.getInstance().ZEN_REPORT_SWITCH)
                .tag(this)
                .params("onOff",on)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置报告生成开关", json.toString());
                            if (json.getInt("status") == 200) {

                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
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

    public void setUploadSwitch(boolean isChecked){
        int on = -1;
        on =  isChecked?1:0;
        OkGo.<String>get(Urls.getInstance().ZEN_UPLOAD_SWITCH)
                .tag(this)
                .params("onOff",on)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置无人数据上报", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
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
    public void setReportTime(){
        String time = tv_report_time.getText().toString();
        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3,5));
        OkGo.<String>get(Urls.getInstance().ZEN_REPORT_TIME)
                .tag(this)
                .params("hour",hour)
                .params("minute",min)
                .params("second",0)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置睡眠报告生成时间", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
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

    public void setSleepTime(){
        String begintime = tv_start_time.getText().toString();
        String endtime = tv_end_time.getText().toString();
        int beginHour = Integer.parseInt(begintime.substring(0,2));
        int beginMinute = Integer.parseInt(begintime.substring(3,5));
        int endHour = Integer.parseInt(endtime.substring(0,2));
        int endMinute = Integer.parseInt(endtime.substring(3,5));
        OkGo.<String>get(Urls.getInstance().ZEN_REPORT_TIME)
                .tag(this)
                .params("beginHour",beginHour)
                .params("beginMinute",beginMinute)
                .params("beginSecond","00")
                .params("endHour",endHour)
                .params("endMinute",endMinute)
                .params("imei",imei)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置睡眠监测时间", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
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

    public void unbind(){
        OkGo.<String>delete(Urls.getInstance().ZEN_BIND+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑睡眠设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
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
                });
    }

    public void showpopu() {
        View popView = getLayoutInflater().inflate(R.layout.popu_offline_remind, null);
        Button btn_login = popView.findViewById(R.id.btn_login);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new KunDcMainActivity.poponDismissListener());
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
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

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
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
    @Override
    public void onStop() {
        super.onStop();
        task.cancel();
        timer.cancel();
    }

    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(this)
                    .setAgentWebParent(ll1, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .createAgentWeb()
                    .ready()
                    .go(url);
            agentweb.clearWebCache();

        } else {
            ll1.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);
            ll1.setVisibility(View.VISIBLE);
        }


    }

}