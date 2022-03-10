package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SkrMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,rl_defence,rl_undefence,rl_holding,rl_alarm,rl_history,rl_phone,rl_timing_set,rl_setting,rl_fast_call,rl_update;
    private ImageView img_state,img_defence,img_undefence,img_holding;
    private TextView tv_state,tv_explain,tv_defence,tv_undefence,tv_holding,tv_unbind,tv_content,tv_number,tv_version;
    private Context context;
    private PopupWindow popuPhoneW = new PopupWindow();
    private View popview;
    private String cid;
    private boolean alarm_switch;
    private String telephone;
    private ImageView img_device;
    public static int update = 1;
    public static int type = 0;
    private String version_name = "";
    private Boolean auto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFontSize();
        setContentView(R.layout.activity_skr_main);
        context = this;
        createProgressBar(context);
        init();
        initData();
        initState();
        initHistory();

    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        task = new MyTask();
        timer.schedule(task,0,5000);
    }

    public void init(){

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_state = findViewById(R.id.img_state);
        rl_defence = findViewById(R.id.rl_defence);
        rl_defence.setOnClickListener(this);
        rl_undefence = findViewById(R.id.rl_undefence);
        rl_undefence.setOnClickListener(this);
        rl_holding = findViewById(R.id.rl_holding);
        rl_holding.setOnClickListener(this);
        rl_alarm = findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        tv_state = findViewById(R.id.tv_state);
        tv_explain = findViewById(R.id.tv_explain);
        img_defence = findViewById(R.id.img_defence);
        tv_defence = findViewById(R.id.tv_defence);
        img_undefence = findViewById(R.id.img_undefence);
        tv_undefence = findViewById(R.id.tv_undefence);
        img_holding = findViewById(R.id.img_holding);
        tv_holding = findViewById(R.id.tv_holding);
        rl_history = findViewById(R.id.rl_history);
        rl_history.setOnClickListener(this);
        rl_phone = findViewById(R.id.rl_phone);
        rl_phone.setOnClickListener(this);
        rl_timing_set = findViewById(R.id.rl_timing_set);
        rl_timing_set.setOnClickListener(this);
        rl_setting = findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
        rl_fast_call = findViewById(R.id.rl_fast_call);
        rl_fast_call.setOnClickListener(this);
        rl_update = findViewById(R.id.rl_update);
        rl_update.setOnClickListener(this);
        tv_version = findViewById(R.id.tv_version);

    }

    public void initState(){
        if(getIntent().getStringExtra("telephone")!=null && !getIntent().getStringExtra("telephone").equals("") ){
            myHandler.sendEmptyMessageDelayed(0, 500);
        }
    }


    /**
     * 获取设备状态
     */
    public void initData(){

        showProgressBar();
        OkGo.<String>get(Urls.getInstance().DEVICE+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
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
                                String content = json.getString("content");
                                if(content.contains("布防")){
                                    changing(1);
                                }
                                if(content.contains("撤防")){
                                    changing(2);
                                }
                                if(content.contains("留守布防")){
                                    changing(3);
                                }

                                telephone = json.getString("telephone");
                            }else if(json.getInt("status") == 505){
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
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 查询历史消息
     */
    public void initHistory(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyId",MyApplication.userInfo.getOlderlyId());
        httpParams.put("endTime","");
        httpParams.put("startTime","");
        httpParams.put("pageNum","1");
        httpParams.put("pageSize","1");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ALARM_HIS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取历史消息", json.toString());

                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                tv_content.setText(jsonArray.getJSONObject(0).getString("createTime")+jsonArray.getJSONObject(0).getString("content"));

                            }else if(json.getInt("status") == 505){
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
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_defence:   //布防

                changing(1);
                defend();
                break;
            case R.id.rl_undefence:     //撤防
                changing(2);
                undefend();
                break;
            case R.id.rl_holding:       //留守
                changing(3);
                holding();
                break;
            case R.id.rl_alarm:
                if(alarm_switch) {
                    alarm_switch = false;
                }  else {
                    alarm_switch = true;
                }
                alarm();
                break;
            case R.id.rl_history:       //历史记录
                Intent intent = new Intent(this,SkrHistroyActivity.class);
                startActivity(intent);
                timer.cancel();
                task.cancel();
                break;
            case R.id.rl_phone:     //报警电话
                Intent intent1 = new Intent(this,SkrAlarmPhoneActivity.class);
                intent1.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent1);
                timer.cancel();
                task.cancel();
                break;
            case R.id.rl_fast_call:     //快速呼叫
                Intent intent4 = new Intent(this,SkrFastCallActivity.class);
                intent4.putExtra("imei",getIntent().getStringExtra("imei"));
                intent4.putExtra("telephone",telephone);
                startActivity(intent4);
                timer.cancel();
                task.cancel();
                break;
            case R.id.rl_timing_set:        //定时布撤防
                Intent intent2 = new Intent(this,SkrTimeSetActivity.class);
                intent2.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent2);
                timer.cancel();
                task.cancel();
                break;
            case R.id.rl_setting:       //设置
                Intent intent3 = new Intent(this,SkrSettingActivity.class);
                intent3.putExtra("imei",getIntent().getStringExtra("imei"));
                startActivity(intent3);
                timer.cancel();
                task.cancel();
                break;
            case R.id.rl_update:    //跳转更新页面
                if(type ==0) {
                    Intent intent5 = new Intent(this, SkrUpdateActivity.class);
                    intent5.putExtra("version_name",version_name);
                    intent5.putExtra("imei",getIntent().getStringExtra("imei"));
                    intent5.putExtra("auto",auto);
                    startActivity(intent5);
                }else {
                    Intent intent5 = new Intent(this,SkrUpdateCompleteActivity.class);
                    intent5.putExtra("auto",auto);
                    startActivity(intent5);
                }
                break;

            case R.id.tv_unbind:
                showpup();
                break;

        }
    }

    public void changing(int type){
        switch (type){
            case 1:     //布防状态
                if(tv_state.getText().toString().equals("布 防")){
                    return;
                }
                img_state.setImageResource(R.drawable.img_defence);
                tv_state.setText("布 防");
                tv_explain.setText(R.string.defence);
                img_defence.setImageResource(R.drawable.img_defend_choose);
                tv_defence.setTextColor(getResources().getColor(R.color.red));
                img_undefence.setImageResource(R.drawable.img_undefence);
                tv_undefence.setTextColor(getResources().getColor(R.color.text_gray));
                img_holding.setImageResource(R.drawable.img_holding);
                tv_holding.setTextColor(getResources().getColor(R.color.text_gray));
                break;
            case 2:     //撤防状态
                if(tv_state.getText().toString().equals("撤 防")){
                    return;
                }
                img_state.setImageResource(R.drawable.img_undefence_state);
                tv_state.setText("撤 防");
                tv_explain.setText(R.string.undefence);
                img_defence.setImageResource(R.drawable.img_defend);
                tv_defence.setTextColor(getResources().getColor(R.color.text_gray));
                img_undefence.setImageResource(R.drawable.img_undefence_choose);
                tv_undefence.setTextColor(Color.parseColor("#f9b071"));
                img_holding.setImageResource(R.drawable.img_holding);
                tv_holding.setTextColor(getResources().getColor(R.color.text_gray));
                break;
            case 3:     //留守状态
                if(tv_state.getText().toString().equals("留 守")){
                    return;
                }
                img_state.setImageResource(R.drawable.img_holding_state);
                tv_state.setText("留 守");
                tv_explain.setText(R.string.holding);
                img_defence.setImageResource(R.drawable.img_defend);
                tv_defence.setTextColor(getResources().getColor(R.color.text_gray));
                img_undefence.setImageResource(R.drawable.img_undefence);
                tv_undefence.setTextColor(getResources().getColor(R.color.text_gray));
                img_holding.setImageResource(R.drawable.img_holding_choose);
                tv_holding.setTextColor(getResources().getColor(R.color.blue));
                break;
        }

    }

    /**
     * 布防
     */
    public void defend(){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ARMING+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设备布防", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "布防成功", Toast.LENGTH_SHORT).show();

                            }else if(json.getInt("status") == 505){
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

    /**
     * 撤防
     */
    public void undefend(){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().DISARMING+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设备撤防", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "撤防成功", Toast.LENGTH_SHORT).show();

                            }else if(json.getInt("status") == 505){
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

    /**
     *留守
     */
    public void holding(){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().LEFTARMING+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设备留守布防", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "留守布防成功", Toast.LENGTH_SHORT).show();

                            }else if(json.getInt("status") == 505){
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

    /**
     *
     * 鸣警
     */
    public void alarm(){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ALARM+"/"+getIntent().getStringExtra("imei")+"/"+alarm_switch)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("鸣警设置", json.toString());
                            if (json.getInt("status") == 200) {
                                if(alarm_switch){
                                    showAlarmPop();
                                } else {
                                    Toast.makeText(context, "鸣警: 关", Toast.LENGTH_SHORT).show();
                                }

                            }else if(json.getInt("status") == 505){
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

    @SuppressLint("WrongConstant")
    public void showpup(){
        popview = LayoutInflater.from(SkrMainActivity.this).inflate(R.layout.popu_save, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title = popview.findViewById(R.id.tv_title);
        tv_title.setText("是否解除设备绑定");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setText("确认解除");
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
                unbind();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setText("我在想想");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new SkrMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
    }

    @SuppressLint("WrongConstant")
    public void showAlarmPop(){
        popview = LayoutInflater.from(SkrMainActivity.this).inflate(R.layout.popu_alarm, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new SkrMainActivity.DismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        ImageView img_close = popview.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
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


    class DismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
            alarm_switch = false;
            alarm();
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


    /**
     * 解除绑定
     */
    public void unbind(){

        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().SKR_BIND+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(json.getInt("status") == 505){
                                reLogin(context);
                            } else {
                                Toast.makeText(context, "失败,发生了不可预知的错误", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            showDisConnect();
        }
    };


    @SuppressLint("WrongConstant")
    public void showDisConnect(){
        popview = LayoutInflater.from(SkrMainActivity.this).inflate(R.layout.popu_disconnect, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new SkrMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
//        popuPhoneW.showAsDropDown(rl_back);
        img_device = popview.findViewById(R.id.img_device);

        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("path")).into(img_device);
        TextView tv_forth = popview.findViewById(R.id.tv_forth);
        tv_forth.setText("4. 请检查是否已连接WIFI, 请检查WIFI是否正常");
        TextView tv_send_message = popview.findViewById(R.id.tv_send_message);
        tv_send_message.setText("");
        backgroundAlpha(0.5f);

    }


    /**
     * 更新提示
     * @param s
     */
    @SuppressLint("WrongConstant")
    public void showUpdate(String s){
        popview = LayoutInflater.from(SkrMainActivity.this).inflate(R.layout.pop_standard, null);
        PopupWindow popuPhoneW_update = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_content = popview.findViewById(R.id.tv_content);
        tv_content.setText(s);
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
                Intent intent = new Intent(context,SkrUpdateActivity.class);
                intent.putExtra("version_name",version_name);
                intent.putExtra("imei",getIntent().getStringExtra("imei"));
                intent.putExtra("auto",auto);
                startActivity(intent);
            }
        });
        popuPhoneW_update.setFocusable(true);
        popuPhoneW_update.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW_update.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW_update.setOutsideTouchable(true);
        popuPhoneW_update.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW_update.setOnDismissListener(new SkrMainActivity.poponDismissListener());
        popuPhoneW_update.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
    }

    /**
     * 获取更新包信息
     */

    public void getBin(){
        OkGo.<String>get(Urls.getInstance().BIN)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取更新包信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                showUpdate(json.getString("note"));

                            }else if(json.getInt("status") == 505){
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

    /**
     * 获取安防设备设置短信
     */
    public void getGPRS(){
        showProgressBar();
        String telephone = getIntent().getStringExtra("telephone");
        OkGo.<String>get(Urls.getInstance().GPRS+"/"+telephone)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设置短信", json.toString());
                            if (json.getInt("status") == 200) {
                                String data = json.getString("data");
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.SENDTO");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse("smsto:"+telephone));
                                intent.putExtra("sms_body",data);
                                startActivity(intent);

                            }else if(json.getInt("status") == 505){
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

    /**
     * 轮询获取讯息
     */
    public void loopData(){
        OkGo.<String>get(Urls.getInstance().ONLINE+"/"+getIntent().getStringExtra("imei"))
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
                                auto  = json.getBoolean("auto_update");
                                if (json.has("alarmLog")) {
                                    tv_content.setText(json.getJSONObject("alarmLog").getString("createTime")+" " +json.getJSONObject("alarmLog").getString("content"));
                                }
                                if(!json.getString("device_mcu_ver").equals(json.getString("latest_mcu_ver"))){
                                    tv_version.setText("版本:"+json.getString("device_mcu_ver")+"(点击查看可用升级)");
                                    version_name = json.getString("latest_mcu_ver");
                                    type = 0;
                                    if(update ==1) {
                                        getBin();
                                        update = 0;
                                    }
                                } else {
                                    type = 1;
                                }
                                if(popuPhoneW.isShowing()){
                                    popuPhoneW.dismiss();
                                }
                            }else if(json.getInt("status") == 204){
                                if(!popuPhoneW.isShowing()) {
                                    showDisConnect();
                                }
                            } else if(json.getInt("status") == 505){
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

    Timer timer;
    TimerTask task;

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
    }

    class MyTask extends TimerTask{
        @Override
        public void run(){
            // TODO Auto-generated method stub
            //do something
            try {
                loopData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
