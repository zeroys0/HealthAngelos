package net.leelink.healthangelos.activity.hck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

public class HCKMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_defence, rl_undefence, rl_holding, rl_alarm, rl_history;
    private ImageView img_state, img_defence, img_undefence, img_holding;
    private TextView tv_state, tv_explain, tv_defence, tv_undefence, tv_holding, tv_unbind, tv_content;
    private Context context;
    private PopupWindow popuPhoneW = new PopupWindow();
    private View popview;
    private String cid;
    private boolean alarm_switch;
    private String telephone;
    private ImageView img_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFontSize();
        setContentView(R.layout.activity_hckmain);
        context = this;
        createProgressBar(context);
        init();
        initState();
        initHistory();

    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new MyTask();
        timer = new Timer();
        timer.schedule(task, 0, 5000);
    }

    public void init() {

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
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
    }


    /**
     * 获取设备状态
     */
    public void loopData() {

        OkGo.<String>get(Urls.getInstance().HCK_ONLINE + "/" + getIntent().getStringExtra("imei"))
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
                                String content = json.getString("content");
                                if (content.contains("布防")) {
                                    changing(1);
                                }
                                if (content.contains("撤防")) {
                                    changing(2);
                                }
                                if (popuPhoneW.isShowing()) {
                                    popuPhoneW.dismiss();
                                }
                                tv_content.setText(json.getString("alarmTime")+" "+json.getString("content"));
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
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 查询历史消息
     */
    public void initHistory() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        httpParams.put("endTime", "");
        httpParams.put("startTime", "");
        httpParams.put("pageNum", "1");
        httpParams.put("pageSize", "1");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().HCK_HIS)
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
                                tv_content.setText(jsonArray.getJSONObject(0).getString("createTime") + jsonArray.getJSONObject(0).getString("content"));

                            } else if (json.getInt("status") == 204) {
                                showDisConnect();
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
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                holding();
                break;
            case R.id.rl_alarm:
                if (alarm_switch) {
                    alarm_switch = false;
                } else {
                    alarm_switch = true;
                }
                alarm();
                break;
            case R.id.rl_history:       //历史记录
                Intent intent = new Intent(this, HCKHistroyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_unbind:
                showpup();
                break;

        }
    }

    public void changing(int type) {
        switch (type) {
            case 1:     //布防状态
                if (tv_state.getText().toString().equals("布 防")) {
                    return;
                }
                img_state.setImageResource(R.drawable.img_defence);
                tv_state.setText("布 防");
                tv_explain.setText(R.string.defence);
                img_defence.setImageResource(R.drawable.img_defend_choose);
                tv_defence.setTextColor(getResources().getColor(R.color.red));
                img_undefence.setImageResource(R.drawable.img_undefence);
                tv_undefence.setTextColor(getResources().getColor(R.color.text_gray));
                break;
            case 2:     //撤防状态
                if (tv_state.getText().toString().equals("撤 防")) {
                    return;
                }
                img_state.setImageResource(R.drawable.img_undefence_state);
                tv_state.setText("撤 防");
                tv_explain.setText(R.string.undefence);
                img_defence.setImageResource(R.drawable.img_defend);
                tv_defence.setTextColor(getResources().getColor(R.color.text_gray));
                img_undefence.setImageResource(R.drawable.img_undefence_choose);
                tv_undefence.setTextColor(Color.parseColor("#f9b071"));

                break;
        }

    }

    /**
     * 布防
     */
    public void defend() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().HCK_ACTION + "/" + getIntent().getStringExtra("imei") + "/1")
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

    /**
     * 撤防
     */
    public void undefend() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().HCK_ACTION + "/" + getIntent().getStringExtra("imei") + "/2")
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

    /**
     * 留守
     */
    public void holding() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().HCK_ACTION + "/" + getIntent().getStringExtra("imei") + "/6")
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("开启鸣警", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "开启鸣警", Toast.LENGTH_SHORT).show();

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

    /**
     * 鸣警
     */
    public void alarm() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().HCK_ACTION + "/" + getIntent().getStringExtra("imei") + "/7")
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("关闭鸣警", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已关闭", Toast.LENGTH_SHORT).show();

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

    @SuppressLint("WrongConstant")
    public void showpup() {
        popview = LayoutInflater.from(HCKMainActivity.this).inflate(R.layout.popu_save, null);
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
        popuPhoneW.setOnDismissListener(new HCKMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
    }

    @SuppressLint("WrongConstant")
    public void showAlarmPop() {
        popview = LayoutInflater.from(HCKMainActivity.this).inflate(R.layout.popu_alarm, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new HCKMainActivity.DismissListener());
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
    public void unbind() {

        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().HCK_BIND + "/" + getIntent().getStringExtra("imei"))
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
                            } else if (json.getInt("status") == 505) {
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

    public void initState() {
        if (getIntent().getStringExtra("telephone") != null && !getIntent().getStringExtra("telephone").equals("")) {
            myHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            showDisConnect();
        }
    };


    @SuppressLint("WrongConstant")
    public void showDisConnect() {
        popview = LayoutInflater.from(HCKMainActivity.this).inflate(R.layout.popu_disconnect, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new HCKMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        img_device = popview.findViewById(R.id.img_device);
        Glide.with(context).load(Urls.getInstance().IMG_URL + getIntent().getStringExtra("path")).into(img_device);
        TextView tv_forth = popview.findViewById(R.id.tv_forth);
        tv_forth.setText("4. 请检查是否已连接WIFI, 请检查WIFI是否正常");
        TextView tv_send_message = popview.findViewById(R.id.tv_send_message);
        tv_send_message.setText("");
        backgroundAlpha(0.5f);

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
