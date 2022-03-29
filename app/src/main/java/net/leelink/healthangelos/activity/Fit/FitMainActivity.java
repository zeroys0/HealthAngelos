package net.leelink.healthangelos.activity.Fit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class FitMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,rl_cardiogram,rl_setting;
    private Context context;
    private ImageView img_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_main);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        rl_cardiogram = findViewById(R.id.rl_cardiogram);
        rl_cardiogram.setOnClickListener(this);
        rl_setting = findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_cardiogram:
                //心电图
                Intent intent = new Intent(context,FitCardiogramActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting:
                Intent intent1 = new Intent(context,FitSettingActivity.class);
                startActivity(intent1);
                break;
            default:
                break;

        }
    }
}