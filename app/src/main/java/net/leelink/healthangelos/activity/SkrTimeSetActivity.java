package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.RemindTimesManager;
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

public class SkrTimeSetActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_defence_time1, rl_undefence_time1, rl_repeat_1, rl_defence_time2, rl_undefence_time2, rl_repeat_2;
    private TextView tv_save, tv_defence_time1, tv_undefence_time1, tv_defence_time2, tv_undefence_time2, tv_time1, tv_time2;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private int type = 0; //0  布防时间一      1  撤防时间一    2  布防时间二       3  撤防时间二
    int Year = 2018;
    int Month = 1;
    int Day = 1;
    int Hour = -1;
    int Minute = -1;
    int Type = -1;
    int Type1 = -1;
    String armHour_1, armHour_2, armSecond_1, armSecond_2, disHour_1, disHour_2, disSecond_1, disSecond_2;
    private SwitchCompat tv_time_set1, tv_time_set2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_time_set);
        context = this;
        createProgressBar(context);
        init();
        initTime();
        initDefendTime();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        rl_defence_time1 = findViewById(R.id.rl_defence_time1);
        rl_defence_time1.setOnClickListener(this);
        rl_undefence_time1 = findViewById(R.id.rl_undefence_time1);
        rl_undefence_time1.setOnClickListener(this);
        tv_undefence_time1 = findViewById(R.id.tv_undefence_time1);
        rl_repeat_1 = findViewById(R.id.rl_repeat_1);
        rl_repeat_1.setOnClickListener(this);
        tv_defence_time1 = findViewById(R.id.tv_defence_time1);
        rl_defence_time2 = findViewById(R.id.rl_defence_time2);
        rl_defence_time2.setOnClickListener(this);
        tv_defence_time2 = findViewById(R.id.tv_defence_time2);
        rl_undefence_time2 = findViewById(R.id.rl_undefence_time2);
        rl_undefence_time2.setOnClickListener(this);
        tv_undefence_time2 = findViewById(R.id.tv_undefence_time2);
        tv_time1 = findViewById(R.id.tv_time1);
        rl_repeat_2 = findViewById(R.id.rl_repeat_2);
        rl_repeat_2.setOnClickListener(this);
        tv_time2 = findViewById(R.id.tv_time2);
        tv_time_set1 = findViewById(R.id.tv_time_set1);
        tv_time_set2 = findViewById(R.id.tv_time_set2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
            case R.id.rl_defence_time1:     //设置防区
                type = 0;
                pvTime.show();
                break;
            case R.id.rl_undefence_time1:       //撤防
                type = 1;
                pvTime.show();
                break;
            case R.id.rl_defence_time2:     //布防时间2
                type = 2;
                pvTime.show();
                break;
            case R.id.rl_undefence_time2:       //撤防2
                type = 3;
                pvTime.show();
                break;
            case R.id.rl_repeat_1:  //重复
                Intent intent = new Intent(context, SelectRemindDateActivity.class);
                intent.putExtra("Year", Year);
                intent.putExtra("Month", Month);
                intent.putExtra("Day", Day);
                intent.putExtra("type", Type);
                intent.putExtra("hide", 1);
                startActivityForResult(intent, 1000);
                break;
            case R.id.rl_repeat_2:  //重复2
                Intent intent1 = new Intent(context, SelectRemindDateActivity.class);
                intent1.putExtra("Year", Year);
                intent1.putExtra("Month", Month);
                intent1.putExtra("Day", Day);
                intent1.putExtra("type", Type1);
                intent1.putExtra("hide", 1);
                startActivityForResult(intent1, 1001);
                break;

        }
    }

    public void initDefendTime() {
        String imei = getIntent().getStringExtra("imei");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().TIMING + "/" + imei)
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == -1) {
            Year = data.getIntExtra("Year", 2018);
            Month = data.getIntExtra("Month", 1);
            Day = data.getIntExtra("Day", 1);
            Type = data.getIntExtra("type", -1);
            String s = getRepeat(Type);
            tv_time1.setText(s);

        }
        if (requestCode == 1001 && resultCode == -1) {
            Year = data.getIntExtra("Year", 2018);
            Month = data.getIntExtra("Month", 1);
            Day = data.getIntExtra("Day", 1);
            Type1 = data.getIntExtra("type", -1);
            String s = getRepeat(Type1);
            tv_time2.setText(s);

        }
    }

    /**
     * 根据int二进制获取重复周期
     *
     * @param type
     * @return
     */
    public String getRepeat(int type) {
        if (type == 0) {
            return "重复一次(" + Year + "-" + Month + "" + Day + ")";
        } else if (type == 127) {
            return "每天重复";
        } else {
            String text = "(每周 ";
            if (RemindTimesManager.ifSundayHave(type)) text += "日 ";
            if (RemindTimesManager.ifMondayHave(type)) text += "一 ";
            if (RemindTimesManager.ifTuesdayHave(type)) text += "二 ";
            if (RemindTimesManager.ifWednesdayHave(type)) text += "三 ";
            if (RemindTimesManager.ifThursdayHave(type)) text += "四 ";
            if (RemindTimesManager.ifFridayHave(type)) text += "五 ";
            if (RemindTimesManager.ifSaturdayHave(type)) text += "六 ";
            text += ")重复";
            return text;
        }
    }

    public void save() {
        String imei = getIntent().getStringExtra("imei");
        String[] arm = tv_defence_time1.getText().toString().split(":");
        String[] dis = tv_undefence_time1.getText().toString().split(":");
        if (tv_time_set1.isChecked()) {
            if (arm.length > 0) {
                armHour_1 = arm[0];
                armSecond_1 = arm[1];
            }
            if (dis.length > 0) {
                disHour_1 = dis[0];
                disSecond_1 = dis[1];
            }
        } else {
            armHour_1 = "255";
            armSecond_1 = "255";
            disHour_1 = "255";
            disSecond_1 = "255";
        }

        HttpParams httpParams = new HttpParams();
        httpParams.put("armHour", armHour_1);
        httpParams.put("armSecond", armSecond_1);
        httpParams.put("disHour", disHour_1);
        httpParams.put("disSecond", disSecond_1);
        httpParams.put("index", 1);
        httpParams.put("repeat", Type);
        OkGo.<String>put(Urls.getInstance().TIMING + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("定时布撤防设置", json.toString());
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

        showProgressBar();
        String[] arm_2 = tv_defence_time2.getText().toString().split(":");
        if(tv_time_set2.isChecked()) {
            if (arm_2.length > 0) {
                armHour_2 = arm_2[0];
                armSecond_2 = arm_2[1];
            }
            String[] dis_2 = tv_undefence_time2.getText().toString().split(":");
            if (dis_2.length > 0) {
                disHour_2 = dis_2[0];
                disSecond_2 = dis_2[1];
            }
        }else {
            armHour_2 = "255";
            armSecond_2 = "255";
            disHour_2 = "255";
            disSecond_2 = "255";
        }
        HttpParams httpParams1 = new HttpParams();
        httpParams1.put("armHour", armHour_2);
        httpParams1.put("armSecond", armSecond_2);
        httpParams1.put("disHour", disHour_2);
        httpParams1.put("disSecond", disSecond_2);
        httpParams1.put("index", 2);
        httpParams1.put("repeat", Type1);
        OkGo.<String>put(Urls.getInstance().TIMING + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("定时布撤防设置2", json.toString());
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
                                if (json.has("timingArming1")) {
                                    armHour_1 = json.getJSONObject("timingArming1").getString("armHour");
                                    armSecond_1 = json.getJSONObject("timingArming1").getString("armSecond");
                                    disHour_1 = json.getJSONObject("timingArming1").getString("disHour");
                                    disSecond_1 =  json.getJSONObject("timingArming1").getString("disSecond");
                                    Type = json.getJSONObject("timingArming1").getInt("repeat");
                                    tv_time1.setText(getRepeat(Type));
                                    if(armHour_1.equals("255")){
                                        tv_time_set1.setChecked(false);
                                        tv_defence_time1.setText("");
                                        tv_undefence_time1.setText("");
                                    } else {
                                        tv_defence_time1.setText(armHour_1+ ":" + armSecond_1);
                                        tv_undefence_time1.setText( disHour_1+ ":" +disSecond_1);
                                    }
                                }
                                if (json.has("timingArming2")) {
                                    armHour_2 = json.getJSONObject("timingArming2").getString("armHour");
                                    armSecond_2 = json.getJSONObject("timingArming2").getString("armSecond");
                                    disHour_2 = json.getJSONObject("timingArming2").getString("disHour");
                                    disSecond_2 = json.getJSONObject("timingArming2").getString("disSecond");
                                    Type1 = json.getJSONObject("timingArming2").getInt("repeat");
                                    tv_time2.setText(getRepeat(Type1));
                                    timer.cancel();
                                    LoadDialog.stop();
                                    if(armHour_2.equals("255")){
                                        tv_time_set2.setChecked(false);
                                        tv_defence_time2.setText("");
                                        tv_undefence_time2.setText("");
                                    } else {
                                        tv_defence_time2.setText(armHour_2+ ":" +armSecond_2 );
                                        tv_undefence_time2.setText( disHour_2+ ":" +disSecond_2 );
                                    }
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
                        tv_defence_time1.setText(sdf.format(date));
                        break;
                    case 1:
                        tv_undefence_time1.setText(sdf.format(date));
                        break;
                    case 2:
                        tv_defence_time2.setText(sdf.format(date));
                        break;
                    case 3:
                        tv_undefence_time2.setText(sdf.format(date));
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
