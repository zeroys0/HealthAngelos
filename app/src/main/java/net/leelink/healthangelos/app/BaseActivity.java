package net.leelink.healthangelos.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pattonsoft.pattonutil1_0.util.SPUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.LoginActivity;
import net.leelink.healthangelos.util.Logger;
import net.leelink.healthangelos.util.SystemBarTintManager;
import net.leelink.healthangelos.util.Utils;

import androidx.fragment.app.FragmentActivity;
import io.reactivex.functions.Consumer;

public class BaseActivity extends FragmentActivity implements View.OnClickListener {
    ProgressBar mProgressBar;
    Context context  = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.titlecolortop_1);//通知栏所需颜色
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.statubar);//通知栏所需颜色
        }
        Utils.setStatusTextColor(true, this);//通知栏字体所需颜色
        setStatusBarFullTransparent();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        changeFontSize((String) SPUtils.get(context,"font","1.0"));

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //虚拟键盘也透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public void createProgressBar(Context context) {

        //整个Activity布局的最终父布局,参见参考资料
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProgressBar = new ProgressBar(context);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(mProgressBar);

    }

    public void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void reLogin(Context context){

            SharedPreferences sp = getSharedPreferences("sp",0);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("secretKey");
            editor.remove("telephone");
            editor.apply();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        Toast.makeText(context, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();

    }
    //点击隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                Utils.hideKeyboard(ev, view, this);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    protected void changeFontSize(String spKey) {
        float scale = 1.0f;
        Configuration c = getResources().getConfiguration();
        if (!TextUtils.isEmpty(spKey)) {
            scale = Float.valueOf(spKey);
        }

        c.fontScale = scale;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = c.fontScale * metrics.density;
        getResources().updateConfiguration(c, getResources().getDisplayMetrics());
    }

    protected void checkFontSize(){
        String fontSize = (String) SPUtils.get(this,"font","");
        if(fontSize.equals("1.3")) {
            setTheme(R.style.theme_large);
        } else {
            setTheme(R.style.theme_standard);
        }
    }


    /**
     * 验证相机权限 上传头像 图片等使用
     */
    int index_rx;
    int index;

    @SuppressLint("CheckResult")
    public boolean requestPermissions() {
        index = 0;
        index_rx = 0;
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission.requestEach(
//                android.Manifest.permission.ACCESS_FINE_LOCATION//获取位置
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
                android.Manifest.permission.READ_EXTERNAL_STORAGE,//读取外部存储器
//                        Manifest.permission.READ_CALENDAR,//读取日历
//                        Manifest.permission.READ_CALL_LOG,//看电话记录
//                Manifest.permission.READ_CONTACTS,//读取通讯录
//                        Manifest.permission.READ_PHONE_STATE,//读取手机状态
//                        Manifest.permission.READ_SMS,//读取信息 　
//                          Manifest.permission.SEND_SMS,//发信息
//                Manifest.permission.CALL_PHONE,//打电话
                Manifest.permission.CAMERA
        )//照相机
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {

                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.i("用户已经同意该权限", permission.name + " is granted.");
                            index_rx++;
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Logger.i("用户拒绝了该权限,没有选中『不再询问』", permission.name + " is denied. More info should be provided.");
                            Toast.makeText(getApplicationContext(), "用户拒绝了该权限,打开权限才能上传头像", Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.i("用户拒绝了该权限,并且选中『不再询问』", permission.name + " is denied.");

                        }
                        index++;
                        if (index == 3) {
                            if (index == index_rx) {

                            } else {
                                Toast.makeText(getApplicationContext(), "您已拒绝该权限,可以在设置中重新打开相机存储权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        if(index_rx==3){
            return true;
        } else {
            return  false;
        }

    }


    @Override
    public void onClick(View v) {
        if(!Utils.isFastClick()){
            return;
        }
    }
}
