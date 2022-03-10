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

public class SkrFastCallActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_phone_1, ed_phone_2, ed_phone_3, ed_phone_4;
    private String telephone;
    private TextView tv_save;
    JSONObject jsonObject = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_fast_call);
        init();
        context = this;
        createProgressBar(context);
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_phone_1 = findViewById(R.id.ed_phone_1);
        ed_phone_2 = findViewById(R.id.ed_phone_2);
        ed_phone_3 = findViewById(R.id.ed_phone_3);
        ed_phone_4 = findViewById(R.id.ed_phone_4);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }

    public void initData() {

        OkGo.<String>get(Urls.getInstance().SHORTCUTS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取快捷呼叫号码 ", json.toString());
                            if (json.getInt("status") == 200) {
                                LoadDialog.start(context);
                                timer.schedule(task, 0, 3000);
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
        String data = "";
        telephone = getIntent().getStringExtra("telephone");
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
//            case R.id.rl_send_1:    //绑定电话号码1
//                data = "12345635" + "1" + ed_phone_1.getText().toString();
//                sendMessage(telephone,data);
//                break;
//            case R.id.rl_send_2:    //绑定电话号码2
//                data = "12345635" + "2" + ed_phone_2.getText().toString();
//                sendMessage(telephone,data);
//                break;
//            case R.id.rl_send_3:    //绑定电话号码3
//                data = "12345635" + "3" + ed_phone_3.getText().toString();
//                sendMessage(telephone,data);
//                break;
//            case R.id.rl_send_4:    //绑定电话号码4
//                data = "12345635" + "4" + ed_phone_4.getText().toString();
//                sendMessage(telephone,data);
//                break;
        }

    }

    public void save() {
        HttpParams httpParams= new HttpParams();
        httpParams.put("telephone1",ed_phone_1.getText().toString());
        httpParams.put("telephone2",ed_phone_2.getText().toString());
        httpParams.put("telephone3",ed_phone_3.getText().toString());
        httpParams.put("telephone4",ed_phone_4.getText().toString());
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().SHORTCUTS + "/" + getIntent().getStringExtra("imei"))
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
                            Log.d("获取设备信息", json.toString());

                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();

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

    public void loopData() {
        String imei = getIntent().getStringExtra("imei");
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
                                if (json.has("telephone1")) {
                                    ed_phone_1.setText(json.getString("telephone1"));
                                }
                                if (json.has("telephone2")) {
                                    ed_phone_2.setText(json.getString("telephone2"));
                                }
                                if (json.has("telephone3")) {
                                    ed_phone_3.setText(json.getString("telephone3"));
                                }
                                if (json.has("telephone4")) {
                                    ed_phone_4.setText(json.getString("telephone4"));
                                    timer.cancel();
                                    LoadDialog.stop();
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

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                loopData();
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
