package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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

import androidx.appcompat.widget.SwitchCompat;

public class SkrUpdateCompleteActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    Context context;
    private SwitchCompat cb_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_update_complete);
        init();
        context = this;
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cb_update = findViewById(R.id.cb_update);
        cb_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                autoUpdate(isChecked);
            }
        });
        cb_update.setChecked(getIntent().getBooleanExtra("auto", false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void autoUpdate(boolean auto) {
        OkGo.<String>put(Urls.getInstance().AUTOUPDATE + "/" + getIntent().getStringExtra("imei") + "/" + auto)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("开关自动升级", json.toString());
                            if (json.getInt("status") == 200) {

                            } else if (json.getInt("status") == 505) {
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
