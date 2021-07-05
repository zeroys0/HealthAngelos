package net.leelink.healthangelos.reform;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class ChooseFeatureActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,rl_break,rl_build;
    private CheckBox cb_1,cb_2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_feature);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_break = findViewById(R.id.rl_break);
        rl_break.setOnClickListener(this);
        rl_build = findViewById(R.id.rl_build);
        rl_build.setOnClickListener(this);
        cb_1 = findViewById(R.id.cb_1);
        cb_1.setClickable(false);
        cb_2 = findViewById(R.id.cb_2);
        cb_2.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_break:
                cb_1.setChecked(true);
                cb_2.setChecked(false);
                break;
            case R.id.rl_build:
                cb_1.setChecked(false);
                cb_2.setChecked(true);
                break;

        }

    }
}
