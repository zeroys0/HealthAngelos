package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.MainActivity;
import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.UserNameAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.im.util.Util;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private TextView tv_code_login, tv_submit, tv_get_code, tv_text, tv_forget, tv_code;
    private RelativeLayout rl_password, rl_code;
    // 获取短信验证码的页面显示
    private int time = 60;
    private Button btn_login;
    private EditText ed_telephone, ed_password, ed_sms_code;
    int login_type = 1;
    private ImageView img_user_name;
    private Context context;
    private RecyclerView user_list;
    PopupWindow pop;
    private CheckBox cb_agree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createProgressBar(this);
        context = this;
        init();
    }

    public void init() {
        //其他设备登录
        int type = getIntent().getIntExtra("type", 0);
        if (type == 9) {
            Toast.makeText(getApplicationContext(), "账号在其他设备登录", Toast.LENGTH_LONG).show();
            SharedPreferences sp = getSharedPreferences("sp", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("secretKey");
            editor.remove("telephone");
            editor.remove("clientId");
            editor.apply();
        }
        tv_code_login = findViewById(R.id.tv_code_login);
        tv_code_login.setOnClickListener(this);
        tv_get_code = findViewById(R.id.tv_get_code);
        cb_agree = findViewById(R.id.cb_agree);
        tv_get_code.setOnClickListener(this);
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(this);
        rl_password = findViewById(R.id.rl_password);
        rl_code = findViewById(R.id.rl_code);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        ed_telephone = findViewById(R.id.ed_telephone);
        ed_password = findViewById(R.id.ed_password);
        ed_sms_code = findViewById(R.id.ed_sms_code);
        tv_text = findViewById(R.id.tv_text);
        tv_forget = findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(this);
        tv_code = findViewById(R.id.tv_code);
        tv_code.setOnClickListener(this);
        img_user_name = findViewById(R.id.img_user_name);
        img_user_name.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("sp", 0);
        String token = sp.getString("secretKey", "");
        String ip = sp.getString("ip", "");
        Urls.IP = ip;
        String h5_ip = sp.getString("h5_ip", "");
        Urls.H5_IP = h5_ip;
        String c_ip = sp.getString("c_ip", "");
        Urls.C_IP = c_ip;
        if (!token.equals("") && !ip.equals("")) {
            MyApplication.token = token;
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        SpannableString spannableString = new SpannableString("已阅读并同意<<用户协议>>以及<<隐私政策>>");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra("type", "distribution");
                intent.putExtra("url", "http://www.llky.net.cn/health/protocol.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue)); //设置颜色
            }
        }, 6, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra("type", "distribution");
                intent.putExtra("url", "http://www.llky.net.cn/health/privacyPolicy.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue)); //设置颜色
            }
        }, 16, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_text.append(spannableString);
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());  //很重要，点击无效就是由于没有设置这个引起
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_code_login:
                if (rl_password.getVisibility() == View.VISIBLE) {
                    rl_password.setVisibility(View.GONE);
                    rl_code.setVisibility(View.VISIBLE);
                    tv_code_login.setText("密码登录");
                    login_type = 2;
                } else {
                    rl_password.setVisibility(View.VISIBLE);
                    rl_code.setVisibility(View.GONE);
                    tv_code_login.setText("验证码登录");
                    login_type = 1;
                }
                break;
            case R.id.tv_submit:
                Intent intent = new Intent(this, RegistActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                if(cb_agree.isChecked()) {
                    if (login_type == 1) {
                        login();
                    } else {
                        loginByCode();
                    }
                }else {
                    Toast.makeText(context, "请先阅读并同意隐私政策", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_get_code:
                getSmsCode();
                break;
            case R.id.tv_forget:
                Intent intent1 = new Intent(this, ForgetPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_code:
                backgroundAlpha(0.5f);
                showPopup();
                break;
            case R.id.img_user_name:
                backgroundAlpha(0.5f);
                showPopup1();
                break;
            default:
                break;
        }
    }


    //密码登录
    public void login() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("telephone", ed_telephone.getText().toString().trim());
            jsonObject.put("password", ed_password.getText().toString().trim());
            jsonObject.put("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("login: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().LOGIN)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("用户名密码登录", json.toString());
                            if (json.getInt("status") == 200) {
                                SharedPreferences sp = getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString("secretKey",json.getString("data"));
//                                MyApplication.token = json.getString("data");
                                json = json.getJSONObject("data");
                                editor.putString("secretKey", json.getString("token"));
                                editor.putString("clientId", json.getString("clientId"));
                                saveUsername(ed_telephone.getText().toString().trim());
                                MyApplication.token = json.getString("token");
                                MyApplication.clientId = json.getString("clientId");
                                Util.setId(json.getString("clientId"));
                                editor.putString("telephone", ed_telephone.getText().toString().trim());
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //验证码登录
    public void loginByCode() {
        if (Urls.IP.equals("")) {
            Toast.makeText(this, "请输入商户编码", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("telephone", ed_telephone.getText().toString().trim());
            jsonObject.put("smsCode", ed_sms_code.getText().toString().trim());
            jsonObject.put("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("login: ", JPushInterface.getRegistrationID(LoginActivity.this));
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().LOGINBYCODE)
                .tag(this)
                .upRequestBody(requestBody)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("短信验证码登录", json.toString());
                            if (json.getInt("status") == 200) {
                                SharedPreferences sp = getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString("secretKey",json.getString("data"));
//                                MyApplication.token = json.getString("data");
                                json = json.getJSONObject("data");
                                editor.putString("secretKey", json.getString("token"));
                                editor.putString("clientId", json.getString("clientId"));
                                MyApplication.token = json.getString("token");
                                MyApplication.clientId = json.getString("clientId");
                                Util.setId(json.getString("clientId"));
                                editor.putString("telephone", ed_telephone.getText().toString().trim());
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //发送短信验证码
    public void getSmsCode() {
        if (Urls.IP.equals("")) {
            Toast.makeText(this, "请输入商户编码", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressBar();
        if (!ed_telephone.getText().toString().trim().equals("")) {
            OkGo.<String>post(Urls.getInstance().SEND)
                    .tag(this)
                    .params("telephone", ed_telephone.getText().toString().trim())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            stopProgressBar();
                            try {
                                String body = response.body();
                                JSONObject json = new JSONObject(body);
                                Log.d("获取验证码", json.toString());
                                if (json.getInt("status") == 200) {
                                    if (time == 60) {
                                        new Thread(new LoginActivity.TimeRun()).start();
                                    } else {
                                        tv_get_code.setEnabled(false);
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {

                            super.onError(response);
                        }
                    });

        } else {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(View view) {
        int position = user_list.getChildLayoutPosition(view);
        SharedPreferences sp = getSharedPreferences("sp", 0);
        String user_list = sp.getString("user_name", "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(user_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            ed_telephone.setText(jsonArray.getJSONObject(position).getString("user_name"));
            pop.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    private class TimeRun implements Runnable {
        @Override
        public void run() {
            while (true) {
                mHandler.sendEmptyMessage(0);
                if (time == 0) {
                    tv_get_code.setOnClickListener(LoginActivity.this);
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }

        @SuppressLint("HandlerLeak")
        private Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (time == 0) {
                    tv_get_code.setText("获取验证码");
                    time = 60;
                } else {
                    tv_get_code.setText((--time) + "秒");
                }
            }
        };
    }

    /**
     * 根据商户编码获取地址
     */
    public void getCode(String code) {
        showProgressBar();
        OkGo.<String>get(Urls.PARTNER_CODE + code)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取商户编码", json.toString());
                            if (json.getInt("status") == 200) {
                                if (!json.isNull("data")) {
                                    json = json.getJSONObject("data");
                                    SharedPreferences sp = getSharedPreferences("sp", 0);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("ip", json.getString("apiUrl"));
                                    editor.putString("h5_ip", json.getString("h5Url"));
                                    editor.putString("ws", json.getString("websocketUrl"));
                                    editor.putString("c_ip", json.getString("clientInfoUrl"));
                                    Urls.IP = json.getString("apiUrl");
                                    Urls.H5_IP = json.getString("h5Url");
                                    Urls.C_IP = json.getString("clientInfoUrl");
                                    Log.e("login: ", Urls.IP);
                                    editor.putString("code", code);
                                    editor.apply();
                                } else {
                                    Toast.makeText(LoginActivity.this, "商户编码错误", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        super.onError(response);
                        Toast.makeText(LoginActivity.this, "网络不给力啊", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("WrongConstant")
    public void showPopup() {
        View popview = LayoutInflater.from(LoginActivity.this).inflate(R.layout.pop_add_team, null);
        final EditText ed_name = popview.findViewById(R.id.ed_name);
        Button btn_confirm = popview.findViewById(R.id.btn_confirm);
        final PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new LoginActivity.poponDismissListener());
        SharedPreferences sp = getSharedPreferences("sp", 0);
        ed_name.setText(sp.getString("code", ""));
        popuPhoneW.showAtLocation(rl_code, Gravity.CENTER, 0, 0);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_name.getText().toString().equals("")) {
                    String s = ed_name.getText().toString().trim();
                    getCode(s);
                    popuPhoneW.dismiss();
                }
            }
        });
    }

    public void showPopup1() {
        View popView = getLayoutInflater().inflate(R.layout.popu_choose_user, null);

        user_list = popView.findViewById(R.id.user_list);

        SharedPreferences sp = getSharedPreferences("sp", 0);
        String userList = sp.getString("user_name", "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(userList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserNameAdapter userNameAdapter = new UserNameAdapter(jsonArray, LoginActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        user_list.setAdapter(userNameAdapter);
        user_list.setLayoutManager(layoutManager);
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new LoginActivity.poponDismissListener());

        pop.showAtLocation(ed_telephone, Gravity.BOTTOM, 0, 100);
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

    public void saveUsername(String user_name) {
        SharedPreferences sp = getSharedPreferences("sp", 0);
        String user_list = sp.getString("user_name", "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(user_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("user_name").equals(user_name)) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            jsonArray = new JSONArray();
        }
        JSONObject json = new JSONObject();
        try {
            json.put("user_name", user_name);
            jsonArray.put(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", jsonArray.toString());
        editor.apply();

    }
}
