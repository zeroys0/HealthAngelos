package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ActionAdapter;
import net.leelink.healthangelos.adapter.NewsAdapter;
import net.leelink.healthangelos.app.BaseActivity;

public class CommunityActionActivity extends BaseActivity {

    TabLayout tabLayout;
    RelativeLayout rl_back;
    RecyclerView action_list;
    ActionAdapter actionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_action);
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        action_list = findViewById(R.id.action_list);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("社区活动"));
        tabLayout.addTab(tabLayout.newTab().setText("志愿者活动"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void initList(){
        actionAdapter = new ActionAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        action_list.setAdapter(actionAdapter);
        action_list.setLayoutManager(layoutManager);
    }
}
