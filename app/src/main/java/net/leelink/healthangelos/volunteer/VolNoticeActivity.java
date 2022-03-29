package net.leelink.healthangelos.volunteer;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class VolNoticeActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView tv_title,tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_notice);
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
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getIntent().getStringExtra("title"));
        tv_content = findViewById(R.id.tv_content);

        tv_content.setText(Html.fromHtml(getIntent().getStringExtra("content")));
    }

}