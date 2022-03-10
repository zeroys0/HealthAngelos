package net.leelink.healthangelos.volunteer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.volunteer.adapter.CheckAdapter;

import androidx.recyclerview.widget.RecyclerView;

public class TeamMissionCheckFragment extends BaseFragment {
    private Context context;
    private Button btn_submit;
    private RecyclerView member_list;
    private CheckAdapter checkAdapter;
    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_mission_check, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initList();
        return view;
    }

    public void init(View view){
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        member_list = view.findViewById(R.id.member_list);
    }

    public void initList(){

    }
}
