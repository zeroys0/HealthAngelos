package net.leelink.healthangelos.reform;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class AuditingActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView text_title,tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditing);
        init();
    }

    public void init(){
        rl_back  = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_title = findViewById(R.id.text_title);
        tv_content = findViewById(R.id.tv_content);
    }
}
