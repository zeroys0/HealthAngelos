package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMemberBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.adapter.TeamPartmentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.volunteer.MyTeamActivity.REMOVE_TEAM_MEMBER;

public class RemovePartmentActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back;
    Context context;
    private RecyclerView event_list;
    TeamPartmentAdapter teamPartmentAdapter;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    List<TeamMemberBean> list = new ArrayList<>();
    private TextView tv_team_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_partment);
        context = this;
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        event_list = findViewById(R.id.event_list);
        tv_team_name = findViewById(R.id.tv_team_name);
        tv_team_name.setText(getIntent().getStringExtra("name"));
    }

    public void initList(){
        OkGo.<String>get(Urls.getInstance().USER_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",page)
                .params("pageSize",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("团队成员列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json =  json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                List<TeamMemberBean> eventBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMemberBean>>() {
                                }.getType());
                                list.addAll(eventBeans);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                int volId = getIntent().getIntExtra("volId",0);
                                teamPartmentAdapter = new TeamPartmentAdapter(list,context, RemovePartmentActivity.this,REMOVE_TEAM_MEMBER,volId);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(teamPartmentAdapter);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {

        OkGo.<String>post(Urls.getInstance().CANCEL_TEAM_USER+"/"+list.get(position).getVolId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("移除成员", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "成员移除成功", Toast.LENGTH_SHORT).show();
                                list.remove(position);
                                teamPartmentAdapter.notifyDataSetChanged();
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
}
