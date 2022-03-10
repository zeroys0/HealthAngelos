package net.leelink.healthangelos.activity.hck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

public class BindHCKActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private Context context;
    private Button btn_confirm;

    private PopupWindow popuPhoneW,popupWindow1;
    private View popview,popview1;
    private ImageView img_device;
    int type = 1;
    private EditText ed_phone,ed_device_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_hck);
        context = this;
        createProgressBar(context);
        init();
        initState();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        ed_phone = findViewById(R.id.ed_phone);
        ed_device_number = findViewById(R.id.ed_device_number);
        img_device = findViewById(R.id.img_device);
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("path")).into(img_device);

    }

    public void initState(){
        if(getIntent().getStringExtra("telephone")!=null && !getIntent().getStringExtra("telephone").equals("") ){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:

//                if(type==1) {
//                    showSucesspup();
//                    type=2;
//                } else {
//                    showfailedpop();
//                    type = 1;
//                }

                bindDevice();


                break;
            default:
                break;
        }
    }



    public void bindDevice(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        httpParams.put("telephone",ed_phone.getText().toString());
        httpParams.put("imei",ed_device_number.getText().toString());
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().HCK_BIND)
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
                            Log.d("绑定hck设备", json.toString());
                            if (json.getInt("status") == 200) {
                                finish();
                                Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
                            }else if(json.getInt("status") == 505){
                                reLogin(context);
                            } else {
                                showfailedpop();
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
    public void showfailedpop(){
        popview = LayoutInflater.from(BindHCKActivity.this).inflate(R.layout.popu_failed, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new BindHCKActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        ImageView img_close = popview.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        TextView tv_number = popview.findViewById(R.id.tv_number);
        tv_number.setText("设备编号:"+getIntent().getStringExtra("deviceModel"));
        backgroundAlpha(0.5f);
    }

    @SuppressLint("WrongConstant")
    public void showDisConnect(){

        popview = LayoutInflater.from(BindHCKActivity.this).inflate(R.layout.popu_disconnect, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new BindHCKActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        img_device = popview.findViewById(R.id.img_device);
        TextView tv_forth = popview.findViewById(R.id.tv_forth);
        tv_forth.setText("4. 请检查是否已连接WIFI, 请检查WIFI是否正常");
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("path")).into(img_device);
        TextView tv_send_message = popview.findViewById(R.id.tv_send_message);
        tv_send_message.setText("");

        backgroundAlpha(0.5f);

    }


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
