package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.MainActivity;
import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_code_login,tv_submit,tv_get_code,tv_text;
    private RelativeLayout rl_password,rl_code;
    // 获取短信验证码的页面显示
    private int time = 60;
    private Button btn_login;
    private EditText ed_telephone,ed_password,ed_sms_code;
    int login_type = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createProgressBar(this);
        init();
    }

    public void init(){
        tv_code_login = findViewById(R.id.tv_code_login);
        tv_code_login.setOnClickListener(this);
        tv_get_code = findViewById(R.id.tv_get_code);
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
        SharedPreferences sp = getSharedPreferences("sp",0);
        String token =  sp.getString("secretKey","");
        if(!token.equals("")) {
            MyApplication.token = token;
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        SpannableString spannableString = new SpannableString("已阅读并同意<<用户协议>>以及<<隐私政策>>");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this,WebActivity.class);
                intent.putExtra("type","distribution");
                intent.putExtra("url","http://api.iprecare.com:6280/h5/ambProtocol.html");
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

                Intent intent = new Intent(LoginActivity.this,WebActivity.class);
                intent.putExtra("type","distribution");
                intent.putExtra("url","http://api.iprecare.com:6280/h5/ambPrivacyPolicy.html");
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
        switch (v.getId()){
            case R.id.tv_code_login:
                if(rl_password.getVisibility() == View.VISIBLE) {
                    rl_password.setVisibility(View.GONE);
                    rl_code.setVisibility(View.VISIBLE);
                    tv_code_login.setText("密码登录");
                    login_type = 2;
                } else  {
                    rl_password.setVisibility(View.VISIBLE);
                    rl_code.setVisibility(View.GONE);
                    tv_code_login.setText("验证码登录");
                    login_type = 1;
                }
                break;
            case R.id.tv_submit:
                Intent intent = new Intent(this,RegistActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                if(login_type == 1) {
                    login();
                }else {
                    loginByCode();
                }
                break;
            case R.id.tv_get_code:
                getSmsCode();
                break;
                default:
                    break;
        }
    }


    //密码登录
    public void login(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("telephone", ed_telephone.getText().toString().trim());
            jsonObject.put("password", ed_password.getText().toString().trim());
            jsonObject.put("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "login: ", JPushInterface.getRegistrationID(LoginActivity.this) );
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
        showProgressBar();
        OkGo.<String>post(Urls.LOGIN)
                .tag(this)
                .upRequestBody(requestBody)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("用户名密码登录", json.toString());
                            if (json.getInt("status") == 200) {
                                SharedPreferences sp = getSharedPreferences("sp",0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("secretKey",json.getString("data"));
                                MyApplication.token = json.getString("data");
                                editor.putString("telephone",ed_telephone.getText().toString().trim());
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
    public void loginByCode(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("telephone", ed_telephone.getText().toString().trim());
            jsonObject.put("smsCode",ed_sms_code.getText().toString().trim() );
            jsonObject.put("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "login: ", JPushInterface.getRegistrationID(LoginActivity.this) );
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
        showProgressBar();
        OkGo.<String>post(Urls.LOGINBYCODE)
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
                                SharedPreferences sp = getSharedPreferences("sp",0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("secretKey",json.getString("data"));
                                MyApplication.token = json.getString("data");
                                editor.putString("telephone",ed_telephone.getText().toString().trim());
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
    public void getSmsCode(){
        showProgressBar();
        if (!ed_telephone.getText().toString().trim().equals("")) {
            OkGo.<String>post(Urls.SEND)
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
}
