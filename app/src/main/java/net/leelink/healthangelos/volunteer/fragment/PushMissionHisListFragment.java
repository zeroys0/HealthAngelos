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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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
import net.leelink.healthangelos.adapter.VolunteerEventAdapter;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMissionBean;
import net.leelink.healthangelos.bean.VolunteerEventBean;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;
import net.leelink.healthangelos.volunteer.MissionDetailActivity;
import net.leelink.healthangelos.volunteer.PushTeamMissionActivity;
import net.leelink.healthangelos.volunteer.adapter.TeamMissonClockInAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PushMissionHisListFragment extends BaseFragment implements OnOrderListener,View.OnClickListener {
    private Context context;
    RecyclerView event_list;
    VolunteerEventAdapter volunteerEventAdapter;
    private TwinklingRefreshLayout refreshLayout;
    TeamMissonClockInAdapter teamMissonClockInAdapter;
    int page = 1;
    boolean hasNextPage;
    List<VolunteerEventBean> list = new ArrayList<>();
    List<TeamMissionBean> team_list = new ArrayList<>();
    private TextView tv_single, tv_team, tv_time;
    private CheckBox cb_single, cb_team;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private int mission_type = 3;


    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_mission_his, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initList();
        initPickerView();
        initRefreshLayout(view);

        return view;
    }

    public void init(View view) {
        event_list = view.findViewById(R.id.event_list);
        tv_single = view.findViewById(R.id.tv_single);
        tv_single.setOnClickListener(this);
        tv_time = view.findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        tv_time.setText(Utils.getYear()+"-"+Utils.getMonth());
        tv_team = view.findViewById(R.id.tv_team);
        tv_team.setOnClickListener(this);
        cb_single = view.findViewById(R.id.cb_single);
        cb_single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_team.setChecked(false);
                    mission_type = 3;
                    initList();
                }
            }
        });
        cb_team = view.findViewById(R.id.cb_team);

        cb_team.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_single.setChecked(false);
                    mission_type = 4;
                    initTeamList();
                }
            }
        });
        tv_time = view.findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
    }


    void initList() {
        OkGo.<String>get(Urls.getInstance().MINE_HISTORY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date", tv_time.getText().toString().trim())
                .params("type", mission_type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询领取的个人任务", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
//                                List<VolunteerEventBean> eventBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolunteerEventBean>>() {
//                                }.getType());
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolunteerEventBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                volunteerEventAdapter = new VolunteerEventAdapter(list, PushMissionHisListFragment.this, 2);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(volunteerEventAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    void initTeamList() {
        OkGo.<String>get(Urls.getInstance().MINE_HISTORY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date", tv_time.getText().toString().trim())
                .params("type", mission_type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询领取的团队任务", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                team_list = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMissionBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                teamMissonClockInAdapter = new TeamMissonClockInAdapter(team_list, getContext(), PushMissionHisListFragment.this);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(teamMissonClockInAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
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
                        page++;
                        initList();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_single:
                if (cb_single.isChecked()) {
                    cb_single.setChecked(false);
                } else {
                    cb_single.setChecked(true);
                    cb_team.setChecked(false);
                }
                break;
            case R.id.tv_team:
                if (cb_team.isChecked()) {
                    cb_team.setChecked(false);
                } else {
                    cb_team.setChecked(true);
                    cb_single.setChecked(false);
                }
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        int position = event_list.getChildLayoutPosition(view);
        if(mission_type ==3) {
            //发布的个人任务
            Intent intent = new Intent(getContext(), MissionDetailActivity.class);
            intent.putExtra("id", list.get(position).getId());
            intent.putExtra("mission", list.get(position));
            startActivity(intent);
        }else if(mission_type ==4){
            //发布的团队任务
            Intent intent = new Intent(getContext(), PushTeamMissionActivity.class);
            intent.putExtra("id", team_list.get(position).getId());
            intent.putExtra("mission", team_list.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("yyyy-MM");
        boolean[] type = {true, true, false, false, false, false};
        pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
                myDate = sdf.format(date);
                if(mission_type== 1) {
                    initList();
                }else {
                    initTeamList();
                }
            }
        }).setType(type).build();
    }

    String myDate = "";


}
