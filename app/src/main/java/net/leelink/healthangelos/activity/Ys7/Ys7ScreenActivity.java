package net.leelink.healthangelos.activity.Ys7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Ys7ScreenActivity extends BaseActivity {
    LinearLayout ll1;
    AgentWeb agentweb;
    private Context context;
    private ImageView img_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys7_screen);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        String imei = getIntent().getStringExtra("imei");
        img_setting = findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Ys7MainActivity.class);
                intent.putExtra("imei",imei);
                startActivity(intent);
            }
        });
        ll1 = findViewById(R.id.ll_1);
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().YS_ADDRESS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial",imei)
                .params("type","H5")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取视频地址", json.toString());
                            if (json.getInt("status") == 200) {
                                String url = json.getString("data");
                                setWeb(url);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context); 
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });

        OkGo.<String>get(Urls.getInstance().YS_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取隐私开关信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("enable") == 0) {
                                    //镜头遮蔽 关

                                } else {
                                    //镜头遮蔽 开
                                  Intent intent = new Intent(context,Ys7ClosedActivity.class);
                                  intent.putExtra("imei",getIntent().getStringExtra("imei"));
                                  startActivity(intent);
                                }

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(Ys7ScreenActivity.this)
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