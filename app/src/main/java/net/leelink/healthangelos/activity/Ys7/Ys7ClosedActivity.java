package net.leelink.healthangelos.activity.Ys7;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class Ys7ClosedActivity extends BaseActivity {
    private Context context;
    private Button btn_open;
    private RelativeLayout rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys7_closed);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        btn_open = findViewById(R.id.btn_open);
        btn_open.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_open:
                setSwitch();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void setSwitch(){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().YS_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .params("enable", 0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置镜头遮蔽开关", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "配置完成", Toast.LENGTH_SHORT).show();
                                stopProgressBar();
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });

    }
}