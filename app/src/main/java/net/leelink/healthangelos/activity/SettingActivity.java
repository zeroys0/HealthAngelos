package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_unlogin,rl_xieyi,rl_private,rl_about_us,get_version;
    private TextView tv_ver_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_unlogin = findViewById(R.id.rl_unlogin);
        rl_unlogin.setOnClickListener(this);
        rl_xieyi = findViewById(R.id.rl_xieyi);
        rl_xieyi.setOnClickListener(this);
        rl_private = findViewById(R.id.rl_private);
        rl_private.setOnClickListener(this);
        rl_about_us = findViewById(R.id.rl_about_us);
        rl_about_us.setOnClickListener(this);
        get_version = findViewById(R.id.get_version);
        get_version.setOnClickListener(this);
        tv_ver_name = findViewById(R.id.tv_ver_name);
        tv_ver_name.setText(Utils.getVerName(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_unlogin:
                unlogin();
                break;
            case R.id.rl_xieyi:
                Intent intent = new Intent(this,WebActivity.class);
                intent.putExtra("type","distribution");
                intent.putExtra("url","http://www.llky.net.cn/health/protocol.html");
                startActivity(intent);
                break;
            case R.id.rl_private:
                Intent intent1 = new Intent(this,WebActivity.class);
                intent1.putExtra("type","distribution");
                intent1.putExtra("url","http://www.llky.net.cn/health/privacyPolicy.html");
                startActivity(intent1);
                break;
            case R.id.rl_about_us:
                Intent intent2 = new Intent(this,WebActivity.class);
                intent2.putExtra("type","distribution");
                intent2.putExtra("url","http://api.llky.net.cn/aboutus.html");
                startActivity(intent2);
                break;
            case R.id.get_version:
                checkVersion();
                break;

        }
    }

    public void unlogin(){
        Intent intent4 = new Intent(SettingActivity.this,LoginActivity.class);
        SharedPreferences sp = getSharedPreferences("sp",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("secretKey");
        editor.remove("telephone");
        editor.remove("clientId");
        editor.apply();
        intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent4);
        finish();
    }

    public void checkVersion() {
        OkGo.<String>get(Urls.VERSION)
                .tag(this)
                .params("appName", "健康天使")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取版本信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(Utils.getVersionCode(SettingActivity.this)<json.getInt("version")) {
                                    AllenVersionChecker
                                            .getInstance()
                                            .downloadOnly(
                                                    UIData.create().setDownloadUrl(json.getString("apkUrl"))
                                                            .setTitle("检测到新的版本")
                                                            .setContent("检测到您当前不是最新版本,是否要更新?")
                                            )
                                            .executeMission(SettingActivity.this);
                                } else {
                                    Toast.makeText(SettingActivity.this, "您当前已经是最新版本", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SettingActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }
}
