package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class PendingActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        init();
        context = this;

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
    }
}
