package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    private RelativeLayout rl_back;
    private TabLayout tabLayout;
    private TextView tv_state,tv_name;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
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
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
//        tv_name = findViewById(R.id.tv_name);
//        tv_name.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_state:
                Intent intent = new Intent(context, NeoReformProgressActivity.class);
                startActivity(intent);
                break;
        }
    }
}
