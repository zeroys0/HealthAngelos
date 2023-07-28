package net.leelink.healthangelos.activity.sleepace;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.just.agentweb.AgentWeb;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

public class SleepaceReportActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_top;
    LinearLayout ll1;
    AgentWeb agentweb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepace_report);
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
        ll1 = findViewById(R.id.ll_1);
        String url = Urls.H5_IP+"/#/sleepaceReport/"+getIntent().getStringExtra("elderlyNo")+"/"+ MyApplication.token;
        Log.d( "init: ",url);
        setWeb(url);
    }

    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(SleepaceReportActivity.this)
                    .setAgentWebParent(ll1, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .createAgentWeb()
                    .ready()
                    .go(url);

        } else {
            ll1.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);
            ll1.setVisibility(View.VISIBLE);
        }


    }
}