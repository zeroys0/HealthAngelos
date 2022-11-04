package net.leelink.healthangelos.activity.Fit;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.fragment.ECGFragment;
import net.leelink.healthangelos.activity.Fit.fragment.FitSleepFragment;
import net.leelink.healthangelos.activity.Fit.fragment.FitStepFragment;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class DataMainActivity extends BaseActivity {
    private Context context;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.fit_tab_step,
            R.drawable.fit_tab_sleep,
            R.drawable.fit_tab_heart_rate,
            R.drawable.fit_tab_bloodpressure,
            R.drawable.fit_tab_bloodoxygen,
            R.drawable.fit_tab_heart,
    };
    private List<Fragment> fragments;
    private ViewPager2 view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_main);
        context = this;
        init();
    }

    public void init(){
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[0]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[1]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[2]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[3]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[4]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[5]));
        tabLayout.setSelectedTabIndicator(null);
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
        fragments.add(new FitStepFragment());
        fragments.add(new FitSleepFragment());
        fragments.add(new ECGFragment());
//        fragments.add(new FamilyFragment());
//        fragments.add(new ContactPersonFragment());
        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }

}