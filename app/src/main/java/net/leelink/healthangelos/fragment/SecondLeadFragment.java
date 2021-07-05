package net.leelink.healthangelos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.BenefitActivity;
import net.leelink.healthangelos.activity.ContactPersonActivity;
import net.leelink.healthangelos.activity.FoodRecordActivity;
import net.leelink.healthangelos.activity.HealthKnowledgeActivity;
import net.leelink.healthangelos.activity.VoiceBroadcastActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.reform.ReformMainActivity;
import net.leelink.healthangelos.reform.TransFormApplyAvtivity;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;


public class SecondLeadFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout rl_voice,rl_food_record,rl_contact,rl_knowladge,rl_benefit,rl_transform;

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

    @Override
    public void onResume() {
        super.onResume();

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
        rl_transform = view.findViewById(R.id.rl_transform);
        rl_transform.setOnClickListener(this);
        rl_transform.setVisibility(View.INVISIBLE);
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
            case R.id.rl_transform:     //适老化改造
                //绑定民政
                checkBind();

                break;
        }
    }
    public void checkBind(){
        OkGo.<String>get(Urls.getInstance().CIVILL_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人中心", json.toString());
                            if (json.getInt("status") == 200) {
                                int state = json.getInt("data");
                                if(state ==0 || state ==5) {
                                    Intent intent = new Intent(getContext(), TransFormApplyAvtivity.class);
                                    startActivity(intent);
                                } else if(state ==1) {
                                    Intent intent= new Intent(getContext(),TransFormApplyAvtivity.class);
                                    intent.putExtra("state",1);
                                    startActivity(intent);
                                } else if(state ==2){
                                    Intent intent= new Intent(getContext(), ReformMainActivity.class);
                                    startActivity(intent);
                                }

                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
