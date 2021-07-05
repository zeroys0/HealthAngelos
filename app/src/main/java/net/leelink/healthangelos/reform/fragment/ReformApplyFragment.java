package net.leelink.healthangelos.reform.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.fragment.BaseFragment;

public class ReformApplyFragment extends BaseFragment {
    private Context context;
    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reform_apply, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        context = getContext();

        return view;
    }


    public void init(View view){

    }


}
