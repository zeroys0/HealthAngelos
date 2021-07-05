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
import net.leelink.healthangelos.reform.adapter.ReformProgressAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReformProgressFragment extends BaseFragment {
    private Context context;
    ReformProgressAdapter reformProgressAdapter;
    private RecyclerView progress_list;
    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reform_progress, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        context = getContext();
        initList();
        return view;
    }

    public void init(View view){
        progress_list = view.findViewById(R.id.progress_list);
    }

    public void initList(){
        reformProgressAdapter = new ReformProgressAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,true);
        progress_list.setAdapter(reformProgressAdapter);
        progress_list.setLayoutManager(layoutManager);
    }

}
