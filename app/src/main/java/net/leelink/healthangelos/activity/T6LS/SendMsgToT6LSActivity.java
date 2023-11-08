package net.leelink.healthangelos.activity.T6LS;

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

public class SendMsgToT6LSActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView text_title,tv_content;
    private Button btn_send;
    String url = "";

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
        rl_back.setOnClickListener(this);
        text_title = findViewById(R.id.text_title);
        tv_content = findViewById(R.id.tv_content);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        if(getIntent().getStringExtra("type").equals("reboot")) {
            text_title.setText("重启");
            tv_content.setText("提示点击应用即可下发指令重启设备");
            url = Urls.getInstance().T6LS_RESET;
        }
        if(getIntent().getStringExtra("type").equals("shutdown")) {
            text_title.setText("关机");
            tv_content.setText("提示点击应用即可下发指令关闭设备");
            url = Urls.getInstance().T6LS_POWEEROFF;
        }
        if(getIntent().getStringExtra("type").equals("search")) {
            text_title.setText("查找设备");
            tv_content.setText("提示点击应用即可下发指令查找设备");
            url = Urls.getInstance().T6LS_FIND;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_send:
                sendMsg();
                break;
        }
    }

    public void sendMsg() {


        showProgressBar();
        OkGo.<String>post(url+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
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