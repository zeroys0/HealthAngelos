package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SkrSettingActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_delay_defend, rl_delay_alarm, rl_time, rl_defend_area_1, rl_defend_area_2, rl_defend_area_3, rl_defend_area_4, rl_defend_area_5, rl_defend_area_6, rl_defend_area_7, rl_defend_area_8, rl_start_time, rl_end_time;
    private Context context;
    private PopupWindow popuPhoneW;
    private View popview;
    private TextView tv_delay_defend, tv_delay_alarm, tv_keep_time, tv_time1, tv_time2, tv_time3, tv_time4, tv_time5, tv_time6, tv_time7, tv_time8, tv_save, tv_start_time, tv_end_time;
    List<String> list = new ArrayList<>();
    private int at1, at2, at3, at4, at5, at6, at7, at8;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private int type = 0;
    private String beginHour, beginSecond, endHour, endSecond, interval;
    private EditText ed_time_space;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_setting);
        context = this;
        createProgressBar(context);
        init();
        initDefendSetting();
        initTime();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_delay_defend = findViewById(R.id.rl_delay_defend);
        rl_delay_defend.setOnClickListener(this);
        tv_delay_defend = findViewById(R.id.tv_delay_defend);
        rl_delay_alarm = findViewById(R.id.rl_delay_alarm);
        rl_delay_alarm.setOnClickListener(this);
        tv_delay_alarm = findViewById(R.id.tv_delay_alarm);
        rl_time = findViewById(R.id.rl_time);
        rl_time.setOnClickListener(this);
        tv_keep_time = findViewById(R.id.tv_keep_time);
        rl_defend_area_1 = findViewById(R.id.rl_defend_area_1);
        rl_defend_area_1.setOnClickListener(this);
        tv_time1 = findViewById(R.id.tv_time1);
        rl_defend_area_2 = findViewById(R.id.rl_defend_area_2);
        rl_defend_area_2.setOnClickListener(this);
        tv_time2 = findViewById(R.id.tv_time2);
        rl_defend_area_3 = findViewById(R.id.rl_defend_area_3);
        rl_defend_area_3.setOnClickListener(this);
        tv_time3 = findViewById(R.id.tv_time3);
        rl_defend_area_4 = findViewById(R.id.rl_defend_area_4);
        rl_defend_area_4.setOnClickListener(this);
        tv_time4 = findViewById(R.id.tv_time4);
        rl_defend_area_5 = findViewById(R.id.rl_defend_area_5);
        rl_defend_area_5.setOnClickListener(this);
        tv_time5 = findViewById(R.id.tv_time5);
        rl_defend_area_6 = findViewById(R.id.rl_defend_area_6);
        rl_defend_area_6.setOnClickListener(this);
        tv_time6 = findViewById(R.id.tv_time6);
        rl_defend_area_7 = findViewById(R.id.rl_defend_area_7);
        rl_defend_area_7.setOnClickListener(this);
        tv_time7 = findViewById(R.id.tv_time7);
        rl_defend_area_8 = findViewById(R.id.rl_defend_area_8);
        rl_defend_area_8.setOnClickListener(this);
        tv_time8 = findViewById(R.id.tv_time8);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        ed_time_space = findViewById(R.id.ed_time_space);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_delay_defend:      //布防延时
                chooseDelay(1);
                break;
            case R.id.rl_delay_alarm:       //报警延时
                chooseDelay(2);
                break;
            case R.id.rl_time:      //报警持续时间
                chooseKeepTime();
                break;
            case R.id.rl_defend_area_1:     //防区设置
                showDefendAttribute(1);
                break;
            case R.id.rl_defend_area_2:     //防区设置
                showDefendAttribute(2);
                break;
            case R.id.rl_defend_area_3:     //防区设置
                showDefendAttribute(3);
                break;
            case R.id.rl_defend_area_4:     //防区设置
                showDefendAttribute(4);
                break;
            case R.id.rl_defend_area_5:     //防区设置
                showDefendAttribute(5);
                break;
            case R.id.rl_defend_area_6:     //防区设置
                showDefendAttribute(6);
                break;
            case R.id.rl_defend_area_7:     //防区设置
                showDefendAttribute(7);
                break;
            case R.id.rl_defend_area_8:     //防区设置
                showDefendAttribute(8);
                break;
            case R.id.rl_start_time:       //无人监控 开始时间
                type = 0;
                pvTime.show();
                break;
            case R.id.rl_end_time:      //无人监控 结束时间
                type = 1;
                pvTime.show();
                break;


            case R.id.tv_save:
                save();
                break;
        }
    }

    public void save() {
        String imei = getIntent().getStringExtra("imei");
        HttpParams httpParams = new HttpParams();
        httpParams.put("delayAlarm", tv_delay_alarm.getText().toString());
        httpParams.put("delayArming", tv_delay_defend.getText().toString());
        httpParams.put("soundTime", tv_keep_time.getText().toString());
        httpParams.put("at1", at1);
        httpParams.put("at2", at2);
        httpParams.put("at3", at3);
        httpParams.put("at4", at4);
        httpParams.put("at5", at5);
        httpParams.put("at6", at6);
        httpParams.put("at7", at7);
        httpParams.put("at8", at8);
        OkGo.<String>put(Urls.getInstance().DEFENCE + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("防区属性设置", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });


        HttpParams httpParams1 = new HttpParams();
        httpParams1.put("beginHour", beginHour);
        httpParams1.put("beginSecond", beginSecond);
        httpParams1.put("endHour", endHour);
        httpParams1.put("endSecond", endSecond);
        httpParams1.put("interval", ed_time_space.getText().toString().trim());
        OkGo.<String>put(Urls.getInstance().NOACTIVITY + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("无人活动监控", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //选择延时
    public void chooseDelay(int type) {
        List<String> organName = new ArrayList<>();
        for (int i = 0; i <= 255; i++) {
            organName.add(i + "");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (type == 1) {
                    tv_delay_defend.setText(organName.get(options1));

                }
                if (type == 2) {
                    tv_delay_alarm.setText(organName.get(options1));
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }   

    //选择持续时间
    public void chooseKeepTime() {
        List<String> organName = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            organName.add(i + "");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_keep_time.setText(organName.get(options1));
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }

    //选择防区属性
    public void showDefendAttribute(int item) {
        List<String> organName = Arrays.asList(getResources().getStringArray(R.array.defend_dttribute));

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                switch (item) {
                    case 1:
                        tv_time1.setText(organName.get(options1));
                        at1 = options1 + 1;
                        break;
                    case 2:
                        tv_time2.setText(organName.get(options1));
                        at2 = options1 + 1;
                        break;
                    case 3:
                        tv_time3.setText(organName.get(options1));
                        at3 = options1 + 1;
                        break;
                    case 4:
                        tv_time4.setText(organName.get(options1));
                        at4 = options1 + 1;
                        break;
                    case 5:
                        tv_time5.setText(organName.get(options1));
                        at5 = options1 + 1;
                        break;
                    case 6:
                        tv_time6.setText(organName.get(options1));
                        at6 = options1 + 1;
                        break;
                    case 7:
                        tv_time7.setText(organName.get(options1));
                        at7 = options1 + 1;
                        break;
                    case 8:
                        tv_time8.setText(organName.get(options1));
                        at8 = options1 + 1;
                        break;
                }

            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            showpup();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("WrongConstant")
    public void showpup() {
        popview = LayoutInflater.from(SkrSettingActivity.this).inflate(R.layout.popu_save, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title = popview.findViewById(R.id.tv_title);
        tv_title.setText("退出后未保存的信息会消失");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setText("确认退出");
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
                finish();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setText("继续修改");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new SkrSettingActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
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

    public void initDefendSetting() {
        String imei = getIntent().getStringExtra("imei");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().DEFENCE + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取布撤防信息", json.toString());
                            if (json.getInt("status") == 200) {
                                LoadDialog.start(context);
                                timer.schedule(task, 0, 3000);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
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

        OkGo.<String>get(Urls.getInstance().NOACTIVITY + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取布撤防信息", json.toString());
                            if (json.getInt("status") == 200) {

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void initData() {
        String imei = getIntent().getStringExtra("imei");
        OkGo.<String>get(Urls.getInstance().ONLINE + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备信息", json.toString());

                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.has("delayAlarm") && json.has("delayArming") && json.has("attribute") && json.has("soundTime")) {

                                    tv_delay_defend.setText(json.getString("delayArming"));
                                    tv_delay_alarm.setText(json.getString("delayAlarm"));
                                    tv_keep_time.setText(json.getString("soundTime"));
                                    String[] arr = getResources().getStringArray(R.array.defend_dttribute);
                                    at1 = json.getJSONObject("attribute").getInt("at1");
                                    tv_time1.setText(arr[at1 - 1]);
                                    at2 = json.getJSONObject("attribute").getInt("at2");
                                    tv_time2.setText(arr[at2 - 1]);
                                    at3 = json.getJSONObject("attribute").getInt("at3");
                                    tv_time3.setText(arr[at3 - 1]);
                                    at4 = json.getJSONObject("attribute").getInt("at4");
                                    tv_time4.setText(arr[at4 - 1]);
                                    at5 = json.getJSONObject("attribute").getInt("at5");
                                    tv_time5.setText(arr[at5 - 1]);
                                    at6 = json.getJSONObject("attribute").getInt("at6");
                                    tv_time6.setText(arr[at6 - 1]);
                                    at7 = json.getJSONObject("attribute").getInt("at7");
                                    tv_time7.setText(arr[at7 - 1]);
                                    at8 = json.getJSONObject("attribute").getInt("at8");
                                    tv_time8.setText(arr[at8 - 1]);
                                    if (json.getJSONObject("attribute").has("at8") && json.has("noActivitySetting")) {
                                        beginHour = json.getJSONObject("noActivitySetting").getString("beginHour");
                                        beginSecond = json.getJSONObject("noActivitySetting").getString("beginSecond");
                                        endHour = json.getJSONObject("noActivitySetting").getString("endHour");
                                        endSecond = json.getJSONObject("noActivitySetting").getString("endSecond");
                                        interval = json.getJSONObject("noActivitySetting").getString("interval");
                                        if (beginHour.equals("255")) {
                                            tv_start_time.setText("");
                                            tv_end_time.setText("");
                                        } else {
                                            tv_start_time.setText(beginHour + ":" + beginSecond);
                                            tv_end_time.setText(endHour + ":" + endSecond);
                                            ed_time_space.setText(interval);
                                        }
                                        timer.cancel();
                                    }
                                    LoadDialog.stop();
                                }

                            } else if (json.getInt("status") == 505) {
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
                        Toast.makeText(context, "错误,请检查设备连接", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initTime() {
        boolean[] time_type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                switch (type) {
                    case 0:
                        beginHour = date.getHours() + "";
                        beginSecond = date.getMinutes() + "";
                        tv_start_time.setText(sdf.format(date));
                        break;
                    case 1:
                        endHour = date.getHours() + "";
                        endSecond = date.getMinutes() + "";
                        tv_end_time.setText(sdf.format(date));
                        break;

                }

            }
        }).setType(time_type).build();
    }


    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onStop() {
        timer.cancel();
        super.onStop();

    }
}
