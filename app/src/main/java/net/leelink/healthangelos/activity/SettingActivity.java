package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_unlogin,rl_xieyi,rl_private,rl_about_us;

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
                intent.putExtra("url","http://api.iprecare.com:6280/h5/ambProtocol.html");
                startActivity(intent);
                break;
            case R.id.rl_private:
                Intent intent1 = new Intent(this,WebActivity.class);
                intent1.putExtra("type","distribution");
                intent1.putExtra("url","http://api.iprecare.com:6280/h5/ambPrivacyPolicy.html");
                startActivity(intent1);
                break;
            case R.id.rl_about_us:
                Intent intent2 = new Intent(this,WebActivity.class);
                intent2.putExtra("type","distribution");
                intent2.putExtra("url","http://api.llky.net.cn/aboutus.html");
                startActivity(intent2);
                break;

        }
    }

    public void unlogin(){
        Intent intent4 = new Intent(SettingActivity.this,LoginActivity.class);
        SharedPreferences sp = getSharedPreferences("sp",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("secretKey");
        editor.remove("telephone");
        editor.apply();
        intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent4);
        finish();
    }
}
