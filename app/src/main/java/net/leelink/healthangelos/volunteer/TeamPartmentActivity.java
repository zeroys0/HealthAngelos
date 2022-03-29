package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import net.leelink.healthangelos.volunteer.adapter.OnApplyListener;
import net.leelink.healthangelos.volunteer.adapter.TeamApplyAdapter;
import net.leelink.healthangelos.volunteer.adapter.TeamPartmentAdapter;
import net.leelink.healthangelos.volunteer.fragment.VolunteerHomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TeamPartmentActivity extends BaseActivity implements View.OnClickListener, OnApplyListener, OnOrderListener {
    private RecyclerView apply_list, event_list;
    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_apply;
    TeamApplyAdapter teamApplyAdapter;
    List<TeamMemberBean> apply_member_list = new ArrayList<>();
    TeamPartmentAdapter teamPartmentAdapter;
    private TwinklingRefreshLayout refreshLayout;
    private TextView tv_exit, tv_team_name, tv_apply_list;

    int page = 1;
    boolean hasNextPage;
    List<TeamMemberBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_partment);
        context = this;
        init();
        initApplyList();
        initRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initList();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        apply_list = findViewById(R.id.apply_list);
        apply_list.setNestedScrollingEnabled(false);
        ll_apply = findViewById(R.id.ll_apply);
        event_list = findViewById(R.id.event_list);
        tv_exit = findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        tv_apply_list = findViewById(R.id.tv_apply_list);
        tv_apply_list.setOnClickListener(this);
        tv_team_name = findViewById(R.id.tv_team_name);
        tv_team_name.setText(getIntent().getStringExtra("name"));
        if(VolunteerHomeFragment.LEADER ==2) {
            tv_exit.setVisibility(View.VISIBLE);
        }
    }

    public void initApplyList() {
        OkGo.<String>get(Urls.getInstance().USER_VERTIFY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", 1)
                .params("pageSize", 2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("团队成员申请列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                apply_member_list = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMemberBean>>() {
                                }.getType());
                                if (apply_member_list.size() == 0) {
                                    ll_apply.setVisibility(View.GONE);
                                } else {
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                    teamApplyAdapter = new TeamApplyAdapter(apply_member_list, context, TeamPartmentActivity.this);
                                    apply_list.setLayoutManager(layoutManager);
                                    apply_list.setAdapter(teamApplyAdapter);
                                }
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

    public void initList() {


        OkGo.<String>get(Urls.getInstance().USER_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", page)
                .params("pageSize", 10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("团队成员列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                List<TeamMemberBean> eventBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMemberBean>>() {
                                }.getType());
                                list.addAll(eventBeans);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                teamPartmentAdapter = new TeamPartmentAdapter(list, context, TeamPartmentActivity.this);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(teamPartmentAdapter);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_exit:
                Intent intent = new Intent(context, RemovePartmentActivity.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("volId",getIntent().getIntExtra("volId",0));
                startActivity(intent);
                break;
            case R.id.tv_apply_list:
                Intent intent1 = new Intent(context, PartmentApplyActivity.class);
                intent1.putExtra("name", getIntent().getStringExtra("name"));
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onRefuse(View view, int position) {
        OkGo.<String>post(Urls.getInstance().VERTIFY_TRUE + "/" + apply_member_list.get(position).getVolId() + "/" + 2)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("成员审核", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "成员已拒绝", Toast.LENGTH_SHORT).show();
                                initApplyList();
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

    @Override
    public void onConfirm(View view, int position) {
        OkGo.<String>post(Urls.getInstance().VERTIFY_TRUE + "/" + apply_member_list.get(position).getVolId() + "/" + 1)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("成员审核", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "成员已通过", Toast.LENGTH_SHORT).show();
                                list.add(apply_member_list.get(position));
                                initApplyList();
                                teamPartmentAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + list.get(position).getTelephone());
        intent.setData(data);
        startActivity(intent);
    }

    public void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
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
