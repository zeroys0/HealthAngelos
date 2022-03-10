package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
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
import net.leelink.healthangelos.view.HorzProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.SwitchCompat;

public class SkrUpActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private View popview;
    private TextView tv_version;
    private HorzProgressView progress;
    private SwitchCompat cb_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_up);
        init();
        context = this;
        update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        task = new SkrUpActivity.MyTask();
    }

    public void init() {
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("正在更新至:V" + getIntent().getStringExtra("version_name"));
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        progress = findViewById(R.id.progress);
        progress.setCurrentNum(0);
        cb_update = findViewById(R.id.cb_update);
        cb_update.setChecked(getIntent().getBooleanExtra("auto", false));
        cb_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               autoUpdate(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                showpup();
                break;
        }
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
        popview = LayoutInflater.from(SkrUpActivity.this).inflate(R.layout.pop_standard, null);
        PopupWindow popuPhoneW_update = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title = popview.findViewById(R.id.tv_title);
        tv_title.setText("将返回设备页面");
        TextView tv_content = popview.findViewById(R.id.tv_content);
        tv_content.setText("即将返回设备页面，固件升级期间无法对该设备进行操作。");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setText("取消");
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setText("确定");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
                finish();
            }
        });
        popuPhoneW_update.setFocusable(true);
        popuPhoneW_update.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW_update.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW_update.setOutsideTouchable(true);
        popuPhoneW_update.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW_update.setOnDismissListener(new SkrUpActivity.poponDismissListener());
        popuPhoneW_update.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
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

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }

    public void autoUpdate(boolean auto) {
        OkGo.<String>put(Urls.getInstance().AUTOUPDATE + "/" + getIntent().getStringExtra("imei")+"/"+auto)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("开关自动升级", json.toString());
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void update() {
        OkGo.<String>get(Urls.getInstance().UPDATE + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设备升级", json.toString());
                            if (json.getInt("status") == 200) {

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

    /**
     * 轮询获取讯息
     */
    public void loopData() {
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
                                progress.setMax(json.getDouble("pagetotal"));
                                progress.setCurrentNum(json.getDouble("pageidx"));
                                if (json.getDouble("pageidx") >= json.getDouble("pagetotal")) {
                                    Intent intent = new Intent(context, SkrUpdateCompleteActivity.class);
                                    startActivity(intent);
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


    Timer timer;
    TimerTask task;

    @Override
    protected void onStop() {
        timer.cancel();
        task.cancel();
        super.onStop();
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
}
