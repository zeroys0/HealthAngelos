package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    RelativeLayout rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_data);
        init();
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
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        setWeb(Urls.WEB+"/bloodPressureData/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 1:
                        setWeb(Urls.WEB+"/heartRate/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 2:
                        setWeb(Urls.WEB+"/bloodOxygen/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 3:
                        setWeb(Urls.WEB+"/bloodSugar/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 4:
                        setWeb(Urls.WEB+"/stepNumber/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 5:
                        setWeb(Urls.WEB+"/bloodFat/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 6:
                        setWeb(Urls.WEB+"/bloodUric/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 7:
                        setWeb(Urls.WEB+"/temperature/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 8:
                        setWeb(Urls.WEB+"/bodyFat/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 9:
                        setWeb(Urls.WEB+"/muscleRatio/"+ MyApplication.userInfo.getOlderlyId());
                        break;
                    case 10:
                        setWeb(Urls.WEB+"/waterRate/"+ MyApplication.userInfo.getOlderlyId());
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
        setWeb(Urls.WEB+"/bloodPressureData/"+ MyApplication.userInfo.getOlderlyId());
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
