package net.leelink.healthangelos.activity.Fit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.just.agentweb.AgentWeb;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

public class FitStepActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_data;
    AgentWeb agentweb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_step);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ll_data = findViewById(R.id.ll_data);
        setWeb(Urls.getInstance().FIT_H5+"/Steps/index/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.rl_back:
                finish();
                break;
        }
    }

    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(FitStepActivity.this)
                    .setAgentWebParent(ll_data, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .createAgentWeb()
                    .ready()
                    .go(url);
        } else {
            ll_data.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);

            ll_data.setVisibility(View.VISIBLE);
        }

    }

    //点击历史数据
    @JavascriptInterface
    public void getListByTime(String msg) {
        Log.e("getListByTime: ", msg);
        String time = "";
        setWeb(Urls.getInstance().FIT_H5+"/StepsHistory/"+time+"/"+MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }
}