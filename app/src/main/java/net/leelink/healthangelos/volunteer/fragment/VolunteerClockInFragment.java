package net.leelink.healthangelos.volunteer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.adapter.VolunteerEventAdapter;
import net.leelink.healthangelos.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class VolunteerClockInFragment extends BaseFragment  {
    Context context;
    RelativeLayout rl_back;
    VolunteerEventAdapter volunteerEventAdapter;
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    TabLayout tabLayout;
    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_clockin, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        return view;
    }

    public void init(View view){
        rl_back = view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("个人任务"));
        tabLayout.addTab(tabLayout.newTab().setText("团队任务"));
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
        view_pager = view.findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        fragments.add(new SingleMissionClockInFragment());
        fragments.add(new TeamMissionClockInFragment());

        view_pager.setAdapter(new Pager2Adapter(getActivity(),fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }




}
