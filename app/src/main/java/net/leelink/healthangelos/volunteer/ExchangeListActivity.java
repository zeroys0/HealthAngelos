package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.volunteer.fragment.GetMissionHisListFragment;
import net.leelink.healthangelos.volunteer.fragment.PushMissionHisListFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ExchangeListActivity extends BaseActivity  {
    Context context;
    RelativeLayout rl_back;


    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);
        init();
        context = this;


    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("领取任务"));
        tabLayout.addTab(tabLayout.newTab().setText("发布任务"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        view_pager = findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        fragments.add(new GetMissionHisListFragment());
        fragments.add(new PushMissionHisListFragment());

        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }



}
