package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SkrAlarmPhoneActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private EditText ed_phone_1,ed_phone_2,ed_phone_3,ed_phone_4,ed_phone_5,ed_phone_6;
    private TextView tv_save;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_alarm_phone);
        context = this;
        createProgressBar(context);
        init();
        initPhone();

    }
    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_phone_1 = findViewById(R.id.ed_phone_1);
        ed_phone_2 = findViewById(R.id.ed_phone_2);
        ed_phone_3 = findViewById(R.id.ed_phone_3);
        ed_phone_4 = findViewById(R.id.ed_phone_4);
        ed_phone_5 = findViewById(R.id.ed_phone_5);
        ed_phone_6 = findViewById(R.id.ed_phone_6);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }

    public void initData(){
        String imei = getIntent().getStringExtra("imei");
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
                                if(json.has("number1")) {
                                    ed_phone_1.setText(json.getString("number1"));
                                }
                                if(json.has("number2")) {
                                    ed_phone_2.setText(json.getString("number2"));
                                }
                                if(json.has("number3")) {
                                    ed_phone_3.setText(json.getString("number3"));
                                }
                                if(json.has("number4")) {
                                    ed_phone_4.setText(json.getString("number4"));
                                }
                                if(json.has("number5")) {
                                    ed_phone_5.setText(json.getString("number5"));
                                }
                                if(json.has("number6")) {
                                    ed_phone_6.setText(json.getString("number6"));
                                    timer.cancel();
                                    LoadDialog.stop();
                                }

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
                        Toast.makeText(context, "错误,请检查设备连接", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void initPhone(){
        String imei = getIntent().getStringExtra("imei");
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().NUMBERS+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取报警电话", json.toString());
                            if (json.getInt("status") == 200) {
                                LoadDialog.start(context);
                              timer.schedule(task,0,3000);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }

    public void save (){
        String imei = getIntent().getStringExtra("imei");
        showProgressBar();
        HttpParams httpParams = new HttpParams();
        httpParams.put("number1",ed_phone_1.getText().toString().trim());
        httpParams.put("number2",ed_phone_2.getText().toString().trim());
        httpParams.put("number3",ed_phone_3.getText().toString().trim());
        httpParams.put("number4",ed_phone_4.getText().toString().trim());
        httpParams.put("number5",ed_phone_5.getText().toString().trim());
        httpParams.put("number6",ed_phone_6.getText().toString().trim());
        OkGo.<String>put(Urls.getInstance().NUMBERS+"/"+imei)
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
                            Log.d("报警电话设置", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();

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

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
               initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onStop() {
        timer.cancel();
        super.onStop();

    }
}
