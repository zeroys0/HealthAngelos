package net.leelink.healthangelos.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ssk.SSKWebActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;


public class WebActivity extends BaseActivity {
    private WebView webview;
    private RelativeLayout rl_back, rl_top;
    LinearLayout ll1;
    AgentWeb agentweb;
    TextView text_title, tv_history;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        init();
        context = this;
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_top = findViewById(R.id.rl_top);
        ll1 = findViewById(R.id.ll_1);
        setWeb(getIntent().getStringExtra("url"));

        text_title = findViewById(R.id.text_title);
        text_title.setText(getIntent().getStringExtra("title"));
        tv_history = findViewById(R.id.tv_history);
        if (text_title.getText().toString().equals("睡眠趋势")) {
            tv_history.setVisibility(View.VISIBLE);
            tv_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SSKWebActivity.class);
                    intent.putExtra("title", "历史记录");
                    intent.putExtra("url", Urls.H5_IP + "/#/HistoryReport/index/" + MyApplication.userInfo.getOlderlyId() + "/" + MyApplication.token);
                    startActivity(intent);
                }
            });
        }
        if (getIntent().getIntExtra("his", 0) == 1) {
            if (text_title.getText().toString().equals("健康报告")) {
                tv_history.setVisibility(View.VISIBLE);
                tv_history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SSKWebActivity.class);
                        intent.putExtra("title", "健康报告");
                        intent.putExtra("url", Urls.H5_IP + "/#/HistoryReport/index/" + MyApplication.userInfo.getOlderlyId() + "/" + MyApplication.token);
                        startActivity(intent);
                    }
                });
            }
        }

    }

    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(WebActivity.this)
                    .setAgentWebParent(ll1, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App", this)
                    .createAgentWeb()
                    .ready()
                    .go(url);
            agentweb.clearWebCache();

        } else {
            ll1.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);
            ll1.setVisibility(View.VISIBLE);
        }


    }

    @JavascriptInterface
    public void getDataFormVue(String msg) {
        //做原生操作
        Log.e("getDataFormVue: ", msg);
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String number = jsonObject.getString("number");
            String type = jsonObject.getString("type");
            Message message = new Message();

            Bundle bundle = new Bundle();
            bundle.putString("url", Urls.getInstance().WEB + "/hsRecord/" + type + "/" + number + "/" + MyApplication.token);
            message.setData(bundle);
            handler.sendMessage(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            Intent intent = new Intent(context, SSKWebActivity.class);
            intent.putExtra("url", bundle.getString("url"));
            intent.putExtra("title", "一体机数据");
            startActivity(intent);
            //   setWeb(bundle.getString("url"));
            return false;
        }
    });


}
