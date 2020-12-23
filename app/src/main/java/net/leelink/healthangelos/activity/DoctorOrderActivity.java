package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.fragment.HomeOrderFragment;
import net.leelink.healthangelos.fragment.HospitaOrderlFragment;
import net.leelink.healthangelos.fragment.PhoneOrderFragment;
import net.leelink.healthangelos.fragment.PictureOrderFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class DoctorOrderActivity extends BaseActivity {
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    TabLayout tabLayout;
    Context context;
    private RelativeLayout rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_order);
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
        tabLayout.addTab(tabLayout.newTab().setText("图文问诊"));
        tabLayout.addTab(tabLayout.newTab().setText("电话问诊"));
        tabLayout.addTab(tabLayout.newTab().setText("上门就诊"));
        tabLayout.addTab(tabLayout.newTab().setText("医院就诊"));
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
        fragments.add(new PictureOrderFragment());
        fragments.add(new PhoneOrderFragment());
        fragments.add(new HomeOrderFragment());
        fragments.add(new HospitaOrderlFragment());

        view_pager.setAdapter(new Pager2Adapter(DoctorOrderActivity.this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }
}
