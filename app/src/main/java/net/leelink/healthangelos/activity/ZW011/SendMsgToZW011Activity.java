package net.leelink.healthangelos.activity.ZW011;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMsgToZW011Activity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView text_title,tv_content;
    private Button btn_send;
    private String s = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg_to_zw011);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_title = findViewById(R.id.text_title);
        tv_content = findViewById(R.id.tv_content);
        btn_send = findViewById(R.id.btn_send);
        if(getIntent().getStringExtra("type").equals("reboot")){
            text_title.setText("重启");
            tv_content.setText("提示点击应用即可下发指令重启设备");
            s = "CMD_02_REBOOT";
        }
        if(getIntent().getStringExtra("type").equals("shutdown")){
            text_title.setText("关机");
            tv_content.setText("提示点击应用即可下发指令关闭设备");
            s = "CMD_03_POWER_OFF";
        }
        if(getIntent().getStringExtra("type").equals("measure_hr")){
            text_title.setText("心率测量");
            tv_content.setText("提示点击应用即可下发指令测量心率");
            s = "CMD_05_HR";
        }
        if(getIntent().getStringExtra("type").equals("measure_bp")){
            text_title.setText("血压测量");
            tv_content.setText("提示点击应用即可下发指令测量血压");
            s = "CMD_06_BP";
        }
        if(getIntent().getStringExtra("type").equals("search")){
            text_title.setText("查找设备");
            tv_content.setText("提示点击应用即可下发指令查找设备");
            s = "CMD_09_ALARM";
        }
        if(getIntent().getStringExtra("type").equals("measure_bo")){
            text_title.setText("血氧测量");
            tv_content.setText("提示点击应用即可下发指令测量血氧");
            s = "CMD_11_SPO";
        }
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ZW_ACTION)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .params("cmd",s)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送指令", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发送指令成功", Toast.LENGTH_LONG).show();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }

}