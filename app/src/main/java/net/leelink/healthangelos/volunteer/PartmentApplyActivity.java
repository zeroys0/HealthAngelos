package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMemberBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.adapter.OnApplyListener;
import net.leelink.healthangelos.volunteer.adapter.TeamApplyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PartmentApplyActivity extends BaseActivity implements View.OnClickListener, OnApplyListener {
    private RelativeLayout rl_back;
    Context context;
    private RecyclerView event_list;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    List<TeamMemberBean> list = new ArrayList<>();
    List<TeamMemberBean> apply_member_list = new ArrayList<>();
    TeamApplyAdapter teamApplyAdapter;
    private TextView tv_team_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partment_apply);
        context = this;
        init();
        initApplyList();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        event_list = findViewById(R.id.event_list);
        tv_team_name = findViewById(R.id.tv_team_name);
        tv_team_name.setText(getIntent().getStringExtra("name"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void initApplyList() {
        OkGo.<String>get(Urls.getInstance().USER_VERTIFY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", 1)
                .params("pageSize", 20)
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
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                teamApplyAdapter = new TeamApplyAdapter(apply_member_list, context, PartmentApplyActivity.this);
                                event_list.setLayoutManager(layoutManager);
                                event_list.setAdapter(teamApplyAdapter);
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
                                teamApplyAdapter.notifyDataSetChanged();
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
}
