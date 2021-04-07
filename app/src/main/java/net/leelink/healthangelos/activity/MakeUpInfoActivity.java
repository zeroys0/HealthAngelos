package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class MakeUpInfoActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView tv_escape;
    private Button btn_makeup;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_up_info);
        context = this;
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
        tv_escape = findViewById(R.id.tv_escape);
        tv_escape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_makeup = findViewById(R.id.btn_makeup);
        btn_makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
