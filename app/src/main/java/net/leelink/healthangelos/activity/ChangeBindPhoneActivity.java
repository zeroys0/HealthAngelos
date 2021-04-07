package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ChangeBindPhoneActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, img_add;
    private EditText ed_nick_name;
    private ImageView img_clear;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bind_phone);
        init();
        context = this;
        createProgressBar(context);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ed_nick_name = findViewById(R.id.ed_nick_name);
        img_clear = findViewById(R.id.img_clear);
        img_clear.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_clear:
                ed_nick_name.setText("");
                break;
            case R.id.img_add:
                changeNickName();
                break;
        }
    }

    public void changeNickName() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BINDPHONE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .params("bindphone",ed_nick_name.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改设备绑定号码", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("bindPhone",ed_nick_name.getText().toString().trim());
                                setResult(1,intent);
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
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
