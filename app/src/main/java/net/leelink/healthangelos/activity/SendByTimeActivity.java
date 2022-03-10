package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SendByTimeActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    TextView tv_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private Button btn_save;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_by_time);
        init();
        context = this;
        initTime();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
            case R.id.btn_save:
                sendByTime();
                break;
        }
    }

    public void sendByTime(){
        JSONObject json = new JSONObject();

        try {
            json.put("imei", MyApplication.userInfo.getJwotchImei());
            json.put("msg",getIntent().getStringExtra("content"));
            json.put("sendTime",tv_time.getText().toString().trim()+":00");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().SENDMESSAGEBYTIME)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送定时消息", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initTime() {
        boolean[] type = {true, true, true, true, true, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
            }
        }).setType(type).build();
    }
}
