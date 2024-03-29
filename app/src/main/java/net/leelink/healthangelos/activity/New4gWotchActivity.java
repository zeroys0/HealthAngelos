package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class New4gWotchActivity extends BaseActivity implements View.OnClickListener {
    Context context;
    private TextView tv_close,tv_name,tv_phone,tv_profile;
    private RelativeLayout rl_back,rl_time_check,rl_sleep_data,rl_nick_name,rl_wotch_phone,rl_heart_rate;
    private PopupWindow popuPhoneW,popupPhoneW1;
    private View popview,popview1;
    private Button btn_album, btn_photograph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new4g_wotch);
        context = this;
        init();
        popu_head();
    }

    public void init(){
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_time_check = findViewById(R.id.rl_time_check);
        rl_time_check.setOnClickListener(this);
        rl_sleep_data = findViewById(R.id.rl_sleep_data);
        rl_sleep_data.setOnClickListener(this);
        rl_nick_name = findViewById(R.id.rl_nick_name);
        rl_nick_name.setOnClickListener(this);
        tv_name = findViewById(R.id.tv_name);
        rl_wotch_phone = findViewById(R.id.rl_wotch_phone);
        rl_wotch_phone.setOnClickListener(this);
        tv_phone = findViewById(R.id.tv_phone);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        tv_profile = findViewById(R.id.tv_profile);
        tv_profile.setOnClickListener(this);
    }

    @Override

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_close:
                showPopup();
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_album:    //取消
                popuPhoneW.dismiss();
                break;
            case R.id.btn_photograph:   //确定关闭
                popuPhoneW.dismiss();
                break;
            case R.id.rl_time_check:    //检测时间设定
                Intent intent = new Intent(context,SetCheckTimeActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_sleep_data:    //睡眠数据
                Intent intent1 = new Intent(context,SleepDataActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_nick_name:     //修改设备名称
                Intent intent2 = new Intent(this, ChangeNickNameActivity.class);
                intent2.putExtra("imei", getIntent().getStringExtra("imei"));
                startActivityForResult(intent2, 1);
                break;
            case R.id.tv_profile:       //切换情景模式
                showPopop1();
                break;

        }
    }

    public void showPopup(){
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
    }

    public void showPopop1(){
        popuPhoneW.showAtLocation(rl_back,Gravity.BOTTOM,0,0);
        backgroundAlpha(0);
    }

    //获取图片
    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(New4gWotchActivity.this).inflate(R.layout.pop_ask, null);
        btn_album = (Button) popview.findViewById(R.id.btn_album);
        btn_photograph = (Button) popview.findViewById(R.id.btn_photograph);
        btn_album.setOnClickListener(this);
        btn_photograph.setOnClickListener(this);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new New4gWotchActivity.poponDismissListener());
    }

    //情景模式
    @SuppressLint("WrongConstant")
    private void popu_profile() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(New4gWotchActivity.this).inflate(R.layout.pop_ask, null);
        btn_album = (Button) popview.findViewById(R.id.btn_album);
        btn_photograph = (Button) popview.findViewById(R.id.btn_photograph);
        btn_album.setOnClickListener(this);
        btn_photograph.setOnClickListener(this);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new New4gWotchActivity.poponDismissListener());
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
