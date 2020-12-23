package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class WebMissionActivity extends BaseActivity {
    private WebView webview;
    private RelativeLayout rl_back,rl_top;
    RelativeLayout ll1;
    AgentWeb agentweb;
    TextView text_title,tv_cancel;
    Context context;
    Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_mission);
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
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = getIntent().getIntExtra("type",1);
                if(type ==1) {
                    getMission();
                } else {
                    getTeamMission();
                }
            }
        });
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = getIntent().getIntExtra("type",1);
                if(type ==1) {
                    MissionCancel();
                } else {
                    TeamMissionCancel();
                }
            }
        });

    }
    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(WebMissionActivity.this)
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

    public void getMission(){
        OkGo.<String>post(Urls.getInstance().VOL_ACCEPT+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("接受任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务接取成功,记得完成哦", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getTeamMission(){
        OkGo.<String>post(Urls.getInstance().TEAM_ACCEPT+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("接受团队任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务接取成功,记得完成哦", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    public void MissionCancel(){
        OkGo.<String>post(Urls.getInstance().VOL_CANCEL+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人任务取消报名", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务已放弃", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void TeamMissionCancel(){
        OkGo.<String>post(Urls.getInstance().USER_CONFIRM_CANCEL+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("团队成员取消报名", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务已放弃", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @JavascriptInterface
    public void getDataFormVue(String msg) {
        //做原生操作
        Log.e( "getDataFormVue: ", msg);
        Toast.makeText(context, "msg:"+msg, Toast.LENGTH_SHORT).show();
    }
}
