package net.leelink.healthangelos.volunteer;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class NoticeActivity extends BaseActivity {
    RelativeLayout rl_back;
    TextView tv_title,tv_content,tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        init();
    }

    public  void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getIntent().getStringExtra("title"));
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getIntent().getStringExtra("content"));
        tv_time = findViewById(R.id.tv_time);
        tv_time.setText(getIntent().getStringExtra("time"));

    }
}
