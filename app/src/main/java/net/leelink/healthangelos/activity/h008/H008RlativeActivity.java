package net.leelink.healthangelos.activity.h008;

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
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class H008RlativeActivity extends BaseActivity {
    Context context;
    RelativeLayout rl_back;
    private EditText ed_phone_red,ed_phone_yellow,ed_phone_green;
    JSONObject jsonObject;
    private TextView tv_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h008_rlative);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_phone_red = findViewById(R.id.ed_phone_red);
        ed_phone_yellow = findViewById(R.id.ed_phone_yellow);
        ed_phone_green = findViewById(R.id.ed_phone_green);
        if(getIntent().getStringExtra("data")!=null){
            try {
                jsonObject = new JSONObject(getIntent().getStringExtra("data"));
                ed_phone_red.setText(jsonObject.getString("red"));
                ed_phone_yellow.setText(jsonObject.getString("yellow"));
                ed_phone_green.setText(jsonObject.getString("green"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            jsonObject = new JSONObject();
        }

        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }
    public void save(){
        JSONObject json = new JSONObject();
        try {
            json.put("green",ed_phone_green.getText().toString());
            json.put("red",ed_phone_red.getText().toString());
            json.put("yellow",ed_phone_yellow.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().H006_SOS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改白名单", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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