package net.leelink.healthangelos.reform;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.reform.fragment.ReformApplyFragment;
import net.leelink.healthangelos.reform.fragment.ReformProgressFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ReformProgressActivity extends BaseActivity {
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    private RelativeLayout rl_back;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reform_progress);
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
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("申请审批表"));
        tabLayout.addTab(tabLayout.newTab().setText("申请审批进度"));

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
        fragments.add(new ReformApplyFragment());
        fragments.add(new ReformProgressFragment());
        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }
}
