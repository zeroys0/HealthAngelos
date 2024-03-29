package net.leelink.healthangelos.volunteer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMissionBean;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.ClockInActivity;
import net.leelink.healthangelos.volunteer.TeamMissionClockInActivity;
import net.leelink.healthangelos.volunteer.adapter.TeamMissonClockInAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.volunteer.fragment.VolunteerHomeFragment.LEADER;

public class TeamMissionClockInFragment extends BaseFragment implements OnOrderListener {

    Context context;
    RecyclerView event_list;
    TeamMissonClockInAdapter teamMissonClockInAdapter;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    List<TeamMissionBean> list= new ArrayList<>();
    @Override
    public void handleCallBack(Message msg) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clockin_list, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initRefreshLayout(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initList();
    }

    public void init(View view){
        event_list = view.findViewById(R.id.event_list);
    }

    public void initList(){
        //查询进行中的团队任务
        String url;
        String state;
        if(LEADER == 2 ) {      //队长
            url = Urls.getInstance().TEAMS_MINE_TOP;
            state = "3,4,6,7";

        } else {        //队员
            url = Urls.getInstance().TEAMS_MINE;
            state = "0,1,2";
        }

        OkGo.<String>get(url)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",page)
                .params("pageSize",10)
                .params("state",state)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("团队任务签到列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json =  json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                List<TeamMissionBean> eventBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMissionBean>>() {
                                }.getType());
                                list.addAll(eventBeans);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                teamMissonClockInAdapter = new TeamMissonClockInAdapter(list,context,TeamMissionClockInFragment.this);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(teamMissonClockInAdapter);
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initRefreshLayout(View view) {
        refreshLayout = view.findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(context);
        headerView.setTextColor(0xff745D5C);
//        refreshLayout.setHeaderView((new ProgressLayout(getActivity())));
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setBottomView(new LoadingView(context));
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        list.clear();
                        page = 1;
                        initList();

                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                        if (hasNextPage) {
                            page++;
                            initList();
                        }
                    }
                }, 1000);
            }

        });
        // 是否允许开启越界回弹模式
        refreshLayout.setEnableOverScroll(false);
        //禁用掉加载更多效果，即上拉加载更多
        refreshLayout.setEnableLoadmore(true);
        // 是否允许越界时显示刷新控件
        refreshLayout.setOverScrollRefreshShow(true);


    }

    @Override
    public void onItemClick(View view) {
        int position = event_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, TeamMissionClockInActivity.class);
        intent.putExtra("mission",list.get(position));
        startActivity(intent);

    }

    @Override
    public void onButtonClick(View view, int position) {
        if(list.get(position).getMyState()==1) {
            startMission(position);
        }
        if(list.get(position).getMyState()==2) {
            Intent intent = new Intent(getContext(), ClockInActivity.class);
            intent.putExtra("id",list.get(position).getId());
            intent.putExtra("type",2);
            startActivity(intent);
        }
    }

    public void startMission(int position){
        OkGo.<String>post(Urls.getInstance().TEAM_CARD_START+"/"+list.get(position).getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("开始打卡", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "开始打卡完成", Toast.LENGTH_SHORT).show();
                                list.get(position).setMyState(2);
                                teamMissonClockInAdapter.notifyDataSetChanged();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
