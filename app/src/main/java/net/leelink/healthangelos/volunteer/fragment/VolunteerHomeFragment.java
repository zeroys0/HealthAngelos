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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnItemClickListener;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.VolunteerAdapter;
import net.leelink.healthangelos.adapter.VolunteerNoticeAdapter;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.NoticeBean;
import net.leelink.healthangelos.bean.VolunteerEventBean;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.ExchangeActivity;
import net.leelink.healthangelos.volunteer.SingleVolunteerActivity;
import net.leelink.healthangelos.volunteer.TeamMissionListActivity;
import net.leelink.healthangelos.volunteer.WebMissionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VolunteerHomeFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, OnOrderListener {
    Context context;
    RelativeLayout rl_back,rl_personal,rl_exchange,rl_party;
    RecyclerView notice_list,action_list;
    VolunteerNoticeAdapter volunteerNoticeAdapter;
    List<NoticeBean> noticeBeans;
    List<VolunteerEventBean> list;
    VolunteerAdapter volunteerAdapter;

    @Override
    public void handleCallBack(Message msg) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initNotice();
        initList();
        return view;
    }

    public void init(View view) {
        rl_back = view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        notice_list = view.findViewById(R.id.notice_list);
        action_list = view.findViewById(R.id.action_list);
        rl_personal = view.findViewById(R.id.rl_personal);
        rl_personal.setOnClickListener(this);
        rl_exchange = view.findViewById(R.id.rl_exchange);
        rl_exchange.setOnClickListener(this);
        rl_party = view.findViewById(R.id.rl_party);
        rl_party.setOnClickListener(this);

    }

    public void initNotice() {

        OkGo.<String>get(Urls.VOL_NOTICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者公告", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                noticeBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<NoticeBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerNoticeAdapter = new VolunteerNoticeAdapter(noticeBeans,context,VolunteerHomeFragment.this);
                                notice_list.setLayoutManager(layoutManager);
                                notice_list.setAdapter(volunteerNoticeAdapter);
                            }else if(json.getInt("status") == 505){
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initList(){
        OkGo.<String>get(Urls.VOL_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("state",1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json =  json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolunteerEventBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerAdapter = new VolunteerAdapter(list,context,VolunteerHomeFragment.this);
                                action_list.setLayoutManager(layoutManager);
                                action_list.setAdapter(volunteerAdapter);
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                getActivity().finish();
                break;
            case R.id.rl_personal:
                Intent intent = new Intent(getContext(), SingleVolunteerActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_exchange:
                check();
                break;
            case R.id.rl_party:
                Intent intent2 = new Intent(getContext(), TeamMissionListActivity.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void OnItemClick(View view) {        //点击公告

    }

    @Override
    public void onItemClick(View view) {        //点击任务
        int position = action_list.getChildLayoutPosition(view);
        Intent intent = new Intent(getContext(), WebMissionActivity.class);
        intent.putExtra("id",list.get(position).getId());
        intent.putExtra("url",Urls.SINGLE_MISSION+list.get(position).getId()+"/"+MyApplication.token);
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    public void check(){
        OkGo.<String>get(Urls.MINE_INFO)
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
                                json = json.getJSONObject("data");
                                Intent intent = new Intent(getContext(),ExchangeActivity.class);
                                intent.putExtra("volUserNo",json.getString("volNo"));
                                intent.putExtra("servName",json.getString("volName"));
                                startActivity(intent);
                            } else if(json.getInt("status") == 201) {
                                Toast.makeText(context, "您还不是志愿者 无法兑换个人活动", Toast.LENGTH_SHORT).show();
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
}
