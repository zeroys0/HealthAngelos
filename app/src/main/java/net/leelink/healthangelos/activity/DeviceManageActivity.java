package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ChooseAdapter;
import net.leelink.healthangelos.adapter.OnDeviceChooseListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceManageActivity extends BaseActivity implements View.OnClickListener, OnDeviceChooseListener {
    private RelativeLayout rl_back, rl_nick_name, rl_wotch_phone, rl_location, rl_elect_fence, rl_notice, rl_broadcast, rl_family, rl_heart_rate, rl_blood_pressure, rl_step_number, rl_pressure, rl_sleep_data, rl_run_target, rl_sleep_target, rl_sleep_time;
    private TextView tv_name, tv_phone, tv_heart_rate, tv_blood_pressure, tv_step_number, tv_pressure, tv_sleep_data, tv_run_target,tv_sleep_target,tv_sleep_time,tv_confirm,tv_type,tv_imei;
    private Context context;
    private RecyclerView user_list;
    PopupWindow pop;
    List<String> list = new ArrayList<>();
    private ChooseAdapter chooseAdapter;
    private int type = 0;
    private ImageView img_head;
    private int position;
    private Button rl_unbind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        context = this;
        init();
        createProgressBar(context);
        initView();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_nick_name = findViewById(R.id.rl_nick_name);
        rl_nick_name.setOnClickListener(this);
        tv_name = findViewById(R.id.tv_name);
        rl_wotch_phone = findViewById(R.id.rl_wotch_phone);
        rl_wotch_phone.setOnClickListener(this);
        tv_phone = findViewById(R.id.tv_phone);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_blood_pressure = findViewById(R.id.tv_blood_pressure);
        tv_step_number = findViewById(R.id.tv_step_number);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_sleep_data = findViewById(R.id.tv_sleep_data);
        rl_location = findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        rl_elect_fence = findViewById(R.id.rl_elect_fence);
        rl_elect_fence.setOnClickListener(this);
        rl_notice = findViewById(R.id.rl_notice);
        rl_notice.setOnClickListener(this);
        rl_broadcast = findViewById(R.id.rl_broadcast);
        rl_broadcast.setOnClickListener(this);
        rl_family = findViewById(R.id.rl_family);
        rl_family.setOnClickListener(this);
        rl_unbind = findViewById(R.id.rl_unbind);
        rl_unbind.setOnClickListener(this);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        rl_step_number = findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        rl_pressure = findViewById(R.id.rl_pressure);
        rl_pressure.setOnClickListener(this);
        rl_sleep_data = findViewById(R.id.rl_sleep_data);
        rl_sleep_data.setOnClickListener(this);
        rl_run_target = findViewById(R.id.rl_run_target);
        rl_run_target.setOnClickListener(this);
        rl_sleep_target = findViewById(R.id.rl_sleep_target);
        rl_sleep_target.setOnClickListener(this);
        rl_sleep_time = findViewById(R.id.rl_sleep_time);
        rl_sleep_time.setOnClickListener(this);
        tv_run_target = findViewById(R.id.tv_run_target);
        tv_sleep_target = findViewById(R.id.tv_sleep_target);
        tv_sleep_time = findViewById(R.id.tv_sleep_time);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setText(getIntent().getStringExtra("name"));
        tv_imei = findViewById(R.id.tv_imei);
        img_head = findViewById(R.id.img_head);
        if(getIntent().getStringExtra("img")!=null && !getIntent().getStringExtra("img").equals("null")) {
            Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("img")).into(img_head);
        }
    }

    public void initView() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BIND_DETAILS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {


                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_name.setText(json.getString("nickName"));
                                if(json.isNull("nickName")) {
                                    tv_name.setText("");
                                }
                                tv_phone.setText(json.getString("bindphone"));
                                if(json.isNull("bindphone")) {
                                    tv_phone.setText("");
                                }
                                tv_heart_rate.setText(json.getString("heartRateTimes") + "次/分钟");
                                if(json.isNull("heartRateTimes")) {
                                    tv_heart_rate.setText("");
                                }
                                if(!json.isNull("bloodPressureDataVo")) {
                                    String s = "";
                                    JSONObject j = json.getJSONObject("bloodPressureDataVo");
                                    s = j.getString("diastolic")+"/"+j.getString("systolic");
                                    tv_blood_pressure.setText(s);
                                } else  {
                                    tv_blood_pressure.setText("");
                                }
                                tv_step_number.setText(json.getString("stepTimes") + "步");
                                if(json.isNull("stepTimes")) {
                                    tv_step_number.setText("");
                                }
                                tv_pressure.setText(json.getString("pressureTimes"));
                                if(json.isNull("pressureTimes")) {
                                    tv_pressure.setText("");
                                }
                                tv_sleep_data.setText(json.getString("sleepTimes"));
                                if(json.isNull("sleepTimes")) {
                                    tv_sleep_data.setText("");
                                }
                                tv_run_target.setText(json.getString("runTarget")+"公里");
                                if(json.isNull("runTarget")) {
                                    tv_run_target.setText("");
                                }
                                tv_sleep_target.setText(json.getString("sleepTarget")+"小时");
                                if(json.isNull("sleepTarget")) {
                                    tv_sleep_target.setText("");
                                }
                                tv_sleep_time.setText(json.getString("latencyTarget")+":00");
                                if(json.isNull("latencyTarget")) {
                                    tv_sleep_time.setText("");
                                }
                                tv_imei.setText("IMEI:"+json.getString("imei"));


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
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_nick_name:
                Intent intent = new Intent(this, ChangeNickNameActivity.class);
                intent.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_wotch_phone:
                Intent intent1 = new Intent(this, ChangeBindPhoneActivity.class);
                intent1.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivityForResult(intent1, 2);
                break;
            case R.id.rl_location:      //定位
                Intent intent2 = new Intent(this, LocationActivity.class);
                intent2.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent2);
                break;
            case R.id.rl_elect_fence:   //电子围栏
                Intent intent3 = new Intent(this, ElectFenceActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_notice:    //定时提醒
                Intent intent4 = new Intent(this, PromptActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_broadcast: //语音播报
                Intent intent5 = new Intent(this, VoiceBroadcastActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_family:    //亲人号码
                Intent intent6 = new Intent(this, ContactPersonActivity.class);
                intent6.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivity(intent6);
                break;
            case R.id.rl_unbind:
                unbind();
                break;
            case R.id.rl_heart_rate:    //心率数据
                Intent intent7 = new Intent(this, HealthDataActivity.class);
                intent7.putExtra("type", 1);
                startActivity(intent7);
                break;
            case R.id.rl_blood_pressure:    //血压数据
                Intent intent8 = new Intent(this, HealthDataActivity.class);
                intent8.putExtra("type", 0);
                startActivity(intent8);
                break;
            case R.id.rl_step_number:   //步数
                Intent intent9 = new Intent(this, HealthDataActivity.class);
                intent9.putExtra("type", 4);
                startActivity(intent9);
                break;
            case R.id.rl_pressure:  //压力
                Intent intent10 = new Intent(this, HealthDataActivity.class);
                intent10.putExtra("type", 13);
                startActivity(intent10);
                break;
            case R.id.rl_sleep_data:    //睡眠数据
                Intent intent11 = new Intent(this, HealthDataActivity.class);
                intent11.putExtra("type", 12);
                startActivity(intent11);
                break;
            case R.id.rl_run_target:     //跑步目标
                backgroundAlpha(0.5f);
                showRun(1);
                break;
            case R.id.rl_sleep_target:  //睡眠目标
                backgroundAlpha(0.5f);
                showRun(2);
                break;
            case R.id.rl_sleep_time:    //入睡时间
                backgroundAlpha(0.5f);
                showRun(3);
                break;
            case R.id.tv_confirm:   //确认选择
                if (type == 1) {
                    setRunTarget();
                }
                if (type == 2) {
                    setSleepTarget();

                }
                if (type == 3) {
                    setSleetTime();

                }
                pop.dismiss();
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                tv_name.setText(data.getStringExtra("nickName"));
            }
            if (requestCode == 2) {
                tv_phone.setText(data.getStringExtra("bindPhone"));
            }
        }
    }

    public void showRun(int type) {
        View popView = getLayoutInflater().inflate(R.layout.popu_target, null);

        user_list = popView.findViewById(R.id.user_list);
        TextView tv_title = popView.findViewById(R.id.tv_title);

        String[] run = {};
        switch (type) {
            case 1:
                run = getResources().getStringArray(R.array.run_target);
                tv_title.setText("跑步目标");
                break;
            case 2:
                run = getResources().getStringArray(R.array.sleep_target);
                tv_title.setText("睡眠目标");
                break;
            case 3:
                run = getResources().getStringArray(R.array.sleep_time);
                tv_title.setText("入睡时间");
                break;
            default:
                break;
        }
        TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        tv_confirm = popView.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        list = Arrays.asList(run);
        chooseAdapter = new ChooseAdapter(list, context,DeviceManageActivity.this, type);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        user_list.setAdapter(chooseAdapter);
        user_list.setLayoutManager(layoutManager);
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new DeviceManageActivity.poponDismissListener());

        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 100);
    }

    public void unbind() {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BIND + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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

    public void setRunTarget(){
        int number= 0;
        switch (position){
            case 0:
                number = 2;
                break;
            case 1:
                number = 5;
                break;
            case 2:
                number = 10;
                break;
            case 3:
                number = 20;
                break;
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().RUN_TARGET + "?imei=" + getIntent().getStringExtra("imei")+"&runTarget="+number)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置跑步目标", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_run_target.setText(list.get(position));
                                Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show();

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

    public void setSleepTarget(){
        int number= position+6;
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().SLEEP_TARGET + "?imei=" + getIntent().getStringExtra("imei")+"&sleepTarget="+number)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置睡眠目标", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_sleep_target.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setSleetTime(){
        int number= (position+19)%24;
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().SLEEP_LATENCY + "?imei=" + getIntent().getStringExtra("imei")+"&sleepLatency="+number)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置入睡时间", json.toString());
                            if (json.getInt("status") == 200) {
                                tv_sleep_time.setText(list.get(position));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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
    public void onItemClick(View view, int type) {
        int position = user_list.getChildLayoutPosition(view);
        chooseAdapter.setChecked(position);
        this.type = type;
        this.position = position;
    }

    @Override
    public void onChooseClick(View view, int position) {

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
