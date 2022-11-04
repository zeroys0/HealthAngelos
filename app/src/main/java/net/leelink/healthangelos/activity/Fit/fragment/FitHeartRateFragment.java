package net.leelink.healthangelos.activity.Fit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.fragment.BaseFragment;

import androidx.recyclerview.widget.RecyclerView;

public class FitHeartRateFragment extends BaseFragment {
    private Context context;
    private TabLayout tabLayout;
    private RecyclerView data_list;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fit_sleep, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        return view;
    }

    public void init(View view){
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("今天"));
        tabLayout.addTab(tabLayout.newTab().setText("每周"));
        tabLayout.addTab(tabLayout.newTab().setText("每月"));
        data_list = view.findViewById(R.id.data_list);
    }
}
