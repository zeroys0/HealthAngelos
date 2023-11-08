package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.fragment.ContactPersonFragment;
import net.leelink.healthangelos.fragment.FamilyFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ContactPersonActivity extends BaseActivity {
    RelativeLayout rl_back,img_add;
    TabLayout tabLayout;
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person);
        init();
    }

    public void init(){
        rl_back= findViewById(R.id.rl_back);
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
                Intent intent = new Intent(ContactPersonActivity.this,AddContactActivity.class);
                startActivity(intent);
            }
        });
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("亲人号码"));
        tabLayout.addTab(tabLayout.newTab().setText("联系人"));
        imei = getIntent().getStringExtra("imei");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        img_add.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        img_add.setVisibility(View.VISIBLE);
                        break;
                }
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
        fragments.add(new FamilyFragment());
        fragments.add(new ContactPersonFragment());
        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }

    public String getImei(){
        return  imei;
    }
}
