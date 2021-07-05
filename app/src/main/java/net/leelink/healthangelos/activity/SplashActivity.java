package net.leelink.healthangelos.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Logger;

import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    SharedPreferences sp;
    private ImageView img_back;
    private PopupWindow popuPhoneW;
    private View popview;
    TextView tv_cancel, tv_confirm, tv_agreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        requestPermissions();
        gotoLogin();
        img_back = findViewById(R.id.img_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        popu_head();
        sp = getSharedPreferences("sp", 0);
    }

    private void gotoLogin() {
        // TODO Auto-generated method stub
        myHandler.sendEmptyMessageDelayed(0, 1500);
    }

    private void login() {
        // TODO Auto-generated method stub
        myHandler.sendEmptyMessageDelayed(0, 0);
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            SharedPreferences sp = getSharedPreferences("sp", 0);
            boolean agreement = sp.getBoolean("agreement", false);
            if (agreement) {
                MyApplication app = (MyApplication) getApplication();
                app.initSdk();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                popuPhoneW.showAtLocation(img_back, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
            }

        }
    };


    static int index_rx = 0;

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(SplashActivity.this);
        rxPermission.requestEach(Manifest.permission.ACCESS_FINE_LOCATION,//获取位置
                Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
                Manifest.permission.READ_EXTERNAL_STORAGE,//读取外部存储器
//                        Manifest.permission.READ_CALENDAR,//读取日历
//                        Manifest.permission.READ_CALL_LOG,//看电话记录
//                Manifest.permission.READ_CONTACTS,//读取通讯录
//                        Manifest.permission.READ_PHONE_STATE,//读取手机状态
//                        Manifest.permission.READ_SMS,//读取信息 　
//                          Manifest.permission.SEND_SMS,//发信息
//                Manifest.permission.CALL_PHONE,//打电话
                Manifest.permission.CAMERA)//照相机
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.i("用户已经同意该权限", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Logger.i("用户拒绝了该权限,没有选中『不再询问』", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.i("用户拒绝了该权限,并且选中『不再询问』", permission.name + " is denied.");
                        }
                        index_rx++;
                        if (index_rx == 4) {
                            gotoLogin();
                            index_rx = 0;
                        }
                    }
                });

    }

    //获取图片
    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(SplashActivity.this).inflate(R.layout.popu_rule, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(SplashActivity.this);
        tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(SplashActivity.this);
        tv_agreement = popview.findViewById(R.id.tv_agreement);
        String text1 = getResources().getString(R.string.agreement);
        SpannableString spannableString1 = new SpannableString(text1);
        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, WebActivity.class);
                intent.putExtra("type", "distribution");
                intent.putExtra("url", "http://www.llky.net.cn/health/protocol.html");
                startActivity(intent);
            }
        }, 93, 99, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, WebActivity.class);
                intent.putExtra("type", "distribution");
                intent.putExtra("url", "http://www.llky.net.cn/health/privacyPolicy.html");
                startActivity(intent);
            }
        }, 100, 106, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_agreement.setText(spannableString1);
        tv_agreement.setMovementMethod(LinkMovementMethod.getInstance());
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new poponDismissListener());
    }

    @Override
    public void onClick(View v) {
        MyApplication app = (MyApplication) getApplication();
        switch (v.getId()) {
            case R.id.tv_cancel:    //不同意
                app.exit();
                break;
            case R.id.tv_confirm:   //同意条款
                //是否已登录
                app.initSdk();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("agreement", true);
                editor.apply();
                login();
                break;
            default:
                break;
        }
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


}
