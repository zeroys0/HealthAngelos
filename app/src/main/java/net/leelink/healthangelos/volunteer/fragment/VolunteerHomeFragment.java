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
import android.widget.TextView;
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
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.MarqueeTextView;
import net.leelink.healthangelos.volunteer.ExchangeActivity;
import net.leelink.healthangelos.volunteer.MissionDetailActivity;
import net.leelink.healthangelos.volunteer.MyTeamActivity;
import net.leelink.healthangelos.volunteer.NoticeActivity;
import net.leelink.healthangelos.volunteer.SingleVolunteerActivity;
import net.leelink.healthangelos.volunteer.TeamListActivity;
import net.leelink.healthangelos.volunteer.TeamMissionDetailActivity;
import net.leelink.healthangelos.volunteer.TeamMissionListActivity;
import net.leelink.healthangelos.volunteer.VolunteerApplyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VolunteerHomeFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, OnOrderListener {
    Context context;
    RelativeLayout rl_back, rl_personal, rl_exchange, rl_party, rl_left, rl_right, rl_left_1, rl_right_1;
    RecyclerView notice_list, action_list;
    VolunteerNoticeAdapter volunteerNoticeAdapter;
    List<NoticeBean> noticeBeans;
    List<VolunteerEventBean> list = new ArrayList<>();
    VolunteerAdapter volunteerAdapter;
    private TextView tv_organ, tv_team;
    private String id;
    private MarqueeTextView text_scroll;

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initList();
        check();
    }

    public void init(View view) {
        rl_back = view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_left = view.findViewById(R.id.rl_left);
        rl_left.setOnClickListener(this);
        rl_left_1 = view.findViewById(R.id.rl_left_1);
        tv_team = view.findViewById(R.id.tv_team);


        rl_right = view.findViewById(R.id.rl_right);
        rl_right.setOnClickListener(this);
        rl_right_1 = view.findViewById(R.id.rl_right_1);
        rl_right_1.setOnClickListener(this);
        action_list = view.findViewById(R.id.action_list);
        rl_personal = view.findViewById(R.id.rl_personal);
        rl_personal.setOnClickListener(this);
        rl_exchange = view.findViewById(R.id.rl_exchange);
        rl_exchange.setOnClickListener(this);
        rl_party = view.findViewById(R.id.rl_party);
        rl_party.setOnClickListener(this);

        tv_organ = view.findViewById(R.id.tv_organ);
        text_scroll = view.findViewById(R.id.text_scroll);

    }

    public void initNotice() {

        OkGo.<String>get(Urls.getInstance().VOL_NOTICE)
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
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i =0;i<noticeBeans.size();i++){
                                    stringBuilder.append(noticeBeans.get(i).getTitle()+"    ");
                                }
                                text_scroll.initScrollTextView(getActivity().getWindowManager(), stringBuilder.toString(), 1);
                                text_scroll.setText("");
                                text_scroll.starScroll();
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerNoticeAdapter = new VolunteerNoticeAdapter(noticeBeans, context, VolunteerHomeFragment.this);
                            } else if (json.getInt("status") == 505) {
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

    public void initList() {
        OkGo.<String>get(Urls.getInstance().TEAMS_MINE_QB)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("state", 1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolunteerEventBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerAdapter = new VolunteerAdapter(list, context, VolunteerHomeFragment.this);
                                action_list.setLayoutManager(layoutManager);
                                action_list.setAdapter(volunteerAdapter);
                            } else if (json.getInt("status") == 505) {
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
                Intent intent4 = new Intent(getContext(), ExchangeActivity.class);
                if(id!=null){
                    intent4.putExtra("id",id);
                }
                startActivity(intent4);
                break;
            case R.id.rl_party:
                Intent intent2 = new Intent(getContext(), TeamMissionListActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_left:
                Intent intent1 = new Intent(getContext(), VolunteerApplyActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_right:
                Intent intent3 = new Intent(getContext(), TeamListActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_right_1:       //查看所属团队
                if (roleState == 1 && teamState == 2) {     //志愿者
                    Intent intent5 = new Intent(getContext(), MyTeamActivity.class);
                    intent5.putExtra("type", 0);
                    startActivity(intent5);
                } else if (roleState == 2) {     //志愿者队长
                    Intent intent5 = new Intent(getContext(), MyTeamActivity.class);
                    intent5.putExtra("type", 1);
                    startActivity(intent5);

                } else {
                    Intent intent5 = new Intent(getContext(), TeamListActivity.class);
                    //intent5.putExtra("organ_id", organ_id + "");
                    startActivity(intent5);
                }
                break;
        }
    }

    @Override
    public void OnItemClick(View view) {        //点击公告
        int position = notice_list.getChildLayoutPosition(view);
        Intent intent = new Intent(getContext(), NoticeActivity.class);
        intent.putExtra("title", noticeBeans.get(position).getTitle());
        intent.putExtra("content", noticeBeans.get(position).getContent());
        intent.putExtra("time", noticeBeans.get(position).getCreateTime());
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view) {        //点击任务
        int position = action_list.getChildLayoutPosition(view);
        if(list.get(position).getType()==1){
            //个人任务详情
            Intent intent = new Intent(getContext(), MissionDetailActivity.class);
            intent.putExtra("mission",list.get(position));
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
        } else if(list.get(position).getType()==2){
            //团队任务详情
            Intent intent = new Intent(getContext(), TeamMissionDetailActivity.class);
            intent.putExtra("mission",list.get(position));
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    private static int roleState =-1,teamState = -1;
    public static int LEADER = 0;
    public static int VOL_ID = -1;
    public void check() {
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
                            Acache.get(getContext()).put("is_vol", "true");
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                roleState = json.getInt("roleState");
                                LEADER = json.getInt("roleState");
                                teamState = json.getInt("teamState");
                                if(!json.isNull("id")) {
                                    id = json.getString("id");
                                    VOL_ID = json.getInt("id");
                                }
                                Acache.get(getContext()).put("volunteer", json);
                                if (json.getInt("state") == 1) {
                                    rl_left_1.setVisibility(View.VISIBLE);
                                    rl_left.setVisibility(View.INVISIBLE);
                                    tv_organ.setText(json.getString("organName"));
                                }
                                if (json.getInt("teamState") == 2) {
                                    rl_right_1.setVisibility(View.VISIBLE);
                                    rl_right.setVisibility(View.INVISIBLE);
                                    tv_team.setText(json.getString("volTeam"));
                                } else {
                                    rl_right_1.setVisibility(View.INVISIBLE);
                                    rl_right.setVisibility(View.VISIBLE);
                                }
                            } else if (json.getInt("status") == 201) {
                                Acache.get(getContext()).put("is_vol", "false");
                            } else if (json.getInt("status") == 505) {
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
