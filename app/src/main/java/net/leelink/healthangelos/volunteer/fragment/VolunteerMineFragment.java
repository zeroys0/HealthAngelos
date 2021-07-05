package net.leelink.healthangelos.volunteer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.ExchangeListActivity;
import net.leelink.healthangelos.volunteer.MyTeamActivity;
import net.leelink.healthangelos.volunteer.TeamListActivity;
import net.leelink.healthangelos.volunteer.TimeBankActivity;
import net.leelink.healthangelos.volunteer.VolunteerApplyActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class VolunteerMineFragment extends BaseFragment implements View.OnClickListener
{
    Context context;
    Button btn_apply;
    RelativeLayout rl_back,rl_exchange_history,rl_time_bank,rl_my_party;
    TextView tv_name;
    int organ_id = 0;
    int roleState = 0;
    int teamState = 0;
    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_mine, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initData();
        return view;
    }

    public void init(View view){
        rl_back = view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_apply = view.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(this);
        rl_exchange_history = view.findViewById(R.id.rl_exchange_history);
        rl_exchange_history.setOnClickListener(this);
        rl_time_bank = view.findViewById(R.id.rl_time_bank);
        rl_time_bank.setOnClickListener(this);
        rl_my_party = view.findViewById(R.id.rl_my_party);
        rl_my_party.setOnClickListener(this);
        tv_name = view.findViewById(R.id.tv_name);

    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().MINE_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                btn_apply.setVisibility(View.INVISIBLE);
                                json = json.getJSONObject("data");
                                if (json.getInt("state")==0) {
                                    Toast.makeText(context, "该账号还在审核中", Toast.LENGTH_SHORT).show();
                                }
                                if(json.has("organId") && !json.getString("organId").equals("null")) {
                                    organ_id = json.getInt("organId");
                                }
                                roleState = json.getInt("roleState");
                                teamState = json.getInt("teamState");
                                tv_name.setText(json.getString("volName"));
                            } else if(json.getInt("status") == 201) {
                                btn_apply.setVisibility(View.VISIBLE);
                            }
                            else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                getActivity().finish();
                break;
            case R.id.btn_apply:
                Intent intent = new Intent(getContext(), VolunteerApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_exchange_history:
                Intent intent1 = new Intent(getContext(), ExchangeListActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_time_bank:
                Intent intent2=  new Intent(getContext(), TimeBankActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_my_party:
                if(roleState ==1 && teamState == 2) {     //志愿者
                    Intent intent4 = new Intent(getContext(), MyTeamActivity.class);
                    intent4.putExtra("type",0);
                    startActivity(intent4);
                }
                 else  if(roleState ==2 && teamState==2) {     //志愿者队长
                    Intent intent3 = new Intent(getContext(),MyTeamActivity.class);
                    intent3.putExtra("type",1);
                    startActivity(intent3);

                } else {
                    Intent intent3 = new Intent(getContext(), TeamListActivity.class);
                    intent3.putExtra("organ_id", organ_id + "");
                    startActivity(intent3);
                }
                break;
        }
    }
}
