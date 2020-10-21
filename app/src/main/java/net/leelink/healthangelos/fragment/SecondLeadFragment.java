package net.leelink.healthangelos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ContactPersonActivity;
import net.leelink.healthangelos.activity.FoodRecordActivity;
import net.leelink.healthangelos.activity.HealthKnowledgeActivity;
import net.leelink.healthangelos.activity.BenefitActivity;
import net.leelink.healthangelos.activity.VoiceBroadcastActivity;


public class SecondLeadFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout rl_voice,rl_food_record,rl_contact,rl_knowladge,rl_benefit;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);

        return view;
    }

    public void init(View view) {
        rl_voice = view.findViewById(R.id.rl_voice);
        rl_voice.setOnClickListener(this);
        rl_food_record = view.findViewById(R.id.rl_food_record);
        rl_food_record.setOnClickListener(this);
        rl_contact = view.findViewById(R.id.rl_contact);
        rl_contact.setOnClickListener(this);
        rl_knowladge = view.findViewById(R.id.rl_knowladge);
        rl_knowladge.setOnClickListener(this);
        rl_benefit = view.findViewById(R.id.rl_benefit);
        rl_benefit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_voice:     //语音播报
                Intent intent = new Intent(getContext(), VoiceBroadcastActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_food_record:   //饮食记录
                Intent intent1 = new Intent(getContext(), FoodRecordActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_contact:   //联系人
                Intent intent2 = new Intent(getContext(), ContactPersonActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_knowladge:     //健康知识
                Intent intent3 = new Intent(getContext(), HealthKnowledgeActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_benefit:       //惠民政策
                Intent intent4 = new Intent(getContext(), BenefitActivity.class);
                startActivity(intent4);
                break;
        }
    }
}
