package net.leelink.healthangelos.activity;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;


public class WebActivity extends BaseActivity {
    private WebView webview;
    private RelativeLayout rl_back,rl_top;
    LinearLayout ll1;
    AgentWeb agentweb;
    TextView text_title;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        init();
        context =this;
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

    }
    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(WebActivity.this)
                    .setAgentWebParent(ll1, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App",this)
                    .createAgentWeb()
                    .ready()
                    .go(url);

        } else {
            ll1.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);

            ll1.setVisibility(View.VISIBLE);
        }

    }

    @JavascriptInterface
    public void getDataFormVue(String msg) {
        //做原生操作
        Log.e( "getDataFormVue: ", msg);
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String number = jsonObject.getString("number");
            int type = jsonObject.getInt("type");
            setWeb(Urls.getInstance().WEB+"/hsRecord/"+type+"/"+number+"/"+ MyApplication.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "msg:"+msg, Toast.LENGTH_SHORT).show();
    }



}
