package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText ed_phone,ed_code,ed_password,ed_confirm_password;
    private TextView getmsmpass_TX;
    private Button btn_confirm;
    private RelativeLayout rl_back;
    private int time = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
    }
    public void init(){
        ed_phone  = findViewById(R.id.ed_phone);
        ed_code = findViewById(R.id.ed_code);
        ed_password = findViewById(R.id.ed_password);
        ed_confirm_password = findViewById(R.id.ed_confirm_password);
        getmsmpass_TX = findViewById(R.id.getmsmpass_TX);
        getmsmpass_TX.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getmsmpass_TX:    //获取验证码
                sendSmsCode();
                break;
            case R.id.btn_confirm:      //确认修改密码
                if(ed_password.getText().toString().trim().equals(ed_confirm_password.getText().toString().trim())){
                    resetPassword();
                }else {
                    Toast.makeText(this, "两次密码输入的不一致", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_back:
                finish();
                break;
            default:
                break;
        }
    }

    //修改密码(忘记密码)
    public void resetPassword(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("telephone",ed_phone.getText().toString().trim());
            jsonObject.put("password",ed_password.getText().toString().trim());
            jsonObject.put("smsCode",ed_code.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "resetPassword: ",jsonObject.toString() );
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON,String.valueOf(jsonObject));
        OkGo.<String>post(Urls.getInstance().CHANGEPASSWORD)
                .tag(this)
                .upRequestBody(requestBody)

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("忘记密码",json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(ForgetPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                            }
                            Toast.makeText(ForgetPasswordActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //发送短信验证码
    public void sendSmsCode(){
        if(!ed_phone.getText().toString().trim().equals("")){
            OkGo.<String>post(Urls.getInstance().SEND)
                    .tag(this)
                    .params("telephone", ed_phone.getText().toString().trim())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                String body = response.body();
                                JSONObject json = new JSONObject(body);
                                Log.d("获取验证码",json.toString());
                                if (json.getInt("status") == 200) {
                                    if(time == 60) {
                                        new Thread(new ForgetPasswordActivity.TimeRun()).start();
                                    }else {
                                        getmsmpass_TX.setEnabled(false);
                                    }
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                    getmsmpass_TX.setOnClickListener(ForgetPasswordActivity.this);
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
                    getmsmpass_TX.setText("获取验证码");
                    time = 60;
                } else {
                    getmsmpass_TX.setText((--time) + "秒");
                }
            }
        };
    }
}
