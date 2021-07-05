package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.just.agentweb.AgentWeb;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

public class HealthDataActivity extends BaseActivity {
    private TabLayout tabLayout;
    AgentWeb agentweb;
    LinearLayout ll_data;
    RelativeLayout rl_back,img_add,rl_unusual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_data);
        init();
        initView();
    }

    public void init(){
        ll_data = findViewById(R.id.ll_data);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthDataActivity.this,WebActivity.class);
                intent.putExtra("url",Urls.getInstance().WEB+"/hsRecord/"+MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                startActivity(intent);
            }
        });
        rl_unusual = findViewById(R.id.rl_unusual);
        rl_unusual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthDataActivity.this,WebActivity.class);
                intent.putExtra("url", Urls.getInstance().WEB + "/dataAbort/" + MyApplication.userInfo.getOlderlyId() + "/" + MyApplication.token);
                intent.putExtra("title", "异常数据");
                startActivity(intent);
            }
        });
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("血压"));
        tabLayout.addTab(tabLayout.newTab().setText("心率"));
        tabLayout.addTab(tabLayout.newTab().setText("血氧"));
        tabLayout.addTab(tabLayout.newTab().setText("血糖"));
        tabLayout.addTab(tabLayout.newTab().setText("步数"));
        tabLayout.addTab(tabLayout.newTab().setText("血脂四项"));
        tabLayout.addTab(tabLayout.newTab().setText("血尿酸"));
        tabLayout.addTab(tabLayout.newTab().setText("体温"));
        tabLayout.addTab(tabLayout.newTab().setText("体脂"));
        tabLayout.addTab(tabLayout.newTab().setText("肌肉率"));
        tabLayout.addTab(tabLayout.newTab().setText("水分率"));
        tabLayout.addTab(tabLayout.newTab().setText("卡路里"));
        tabLayout.addTab(tabLayout.newTab().setText("睡眠指数"));
        tabLayout.addTab(tabLayout.newTab().setText("压力指数"));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        setWeb(Urls.getInstance().WEB+"/bloodPressureData/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 1:
                        setWeb(Urls.getInstance().WEB+"/heartRate/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 2:
                        setWeb(Urls.getInstance().WEB+"/bloodOxygen/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 3:
                        setWeb(Urls.getInstance().WEB+"/bloodSugar/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 4:
                        setWeb(Urls.getInstance().WEB+"/stepNumber/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 5:
                        setWeb(Urls.getInstance().WEB+"/bloodFat/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 6:
                        setWeb(Urls.getInstance().WEB+"/bloodUric/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 7:
                        setWeb(Urls.getInstance().WEB+"/temperature/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 8:
                        setWeb(Urls.getInstance().WEB+"/bodyFat/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 9:
                        setWeb(Urls.getInstance().WEB+"/muscleRatio/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 10:
                        setWeb(Urls.getInstance().WEB+"/waterRate/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 11:
                        setWeb(Urls.getInstance().WEB+"/calorie/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 12:
                        setWeb(Urls.getInstance().WEB+"/sleepRate/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    case 13:
                        setWeb(Urls.getInstance().WEB+"/pressureRate/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
                        break;
                    default:
                        break;
                }
//                initData(type,page);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setWeb(Urls.getInstance().WEB+"/bloodPressureData/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);


    }

    public void initView(){
        int type = getIntent().getIntExtra("type",0);
        //延时滑动过去
        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(type).select();
            }
        }, 100);
    }


    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(HealthDataActivity.this)
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
}
