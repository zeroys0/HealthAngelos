package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMissionBean;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.adapter.TeamMissionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class MyTeamActivity extends BaseActivity  implements OnOrderListener, View.OnClickListener {
    TabLayout tabLayout;
    RelativeLayout rl_back;
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    Context context;
    TextView tv_title,tv_address,tv_service_type,tv_name,tv_phone,tv_time,tv_number,tv_exit,tv_team_mission_list;
    private RecyclerView mission_list;
    private TeamMissionAdapter teamMissionAdapter;
    List<TeamMissionBean> list = new ArrayList<>();
    private Button btn_leader,btn_partment;
    public static int REMOVE_TEAM_MEMBER = 1;       //将志愿者移除团队
    public static int TEAM_MISSION_REFUSE = 2;      //拒绝志愿者参加团队任务
    public static int TEAM_MISSION = 3;     //队伍成员查看参加人员列表
    private String id;
    private int volId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        init();
        context = this;
        createProgressBar(context);
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initList();
    }

    public void init(){
        int type = getIntent().getIntExtra("type",0);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = findViewById(R.id.tv_title);
        tv_address = findViewById(R.id.tv_address);
        tv_service_type = findViewById(R.id.tv_service_type);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_time = findViewById(R.id.tv_time);
        btn_leader = findViewById(R.id.btn_leader);
        btn_leader.setOnClickListener(this);
        btn_partment = findViewById(R.id.btn_partment);
        btn_partment.setOnClickListener(this);
        tv_team_mission_list = findViewById(R.id.tv_team_mission_list);
        tv_team_mission_list.setOnClickListener(this);
        tv_number = findViewById(R.id.tv_number);
        tv_exit = findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(0.5f);
                showpopu();
            }
        });
        mission_list = findViewById(R.id.mission_list);


        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("团队任务"));
        tabLayout.addTab(tabLayout.newTab().setText("团队成员"));
        if(type==1) {
            tabLayout.addTab(tabLayout.newTab().setText("入队申请"));
        }
        if(type==1) {
            tv_exit.setText("解散团队");
        }
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().TEAM_TITLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("我的团队", json.toString());
                            if (json.getInt("status") == 200) {
                                json =  json.getJSONObject("data");
                                id = json.getString("id");
                                volId = json.getInt("volId");
                                tv_title.setText(json.getString("teamName"));
                                try {
                                    JSONObject jsonObject = new JSONObject(json.getString("teamAddress"));
                                    tv_address.setText(jsonObject.getString("fullAddress"));
                                } catch (JSONException e){
                                    tv_address.setText(json.getString("teamAddress"));
                                }

                                tv_service_type.setText(json.getString("serviceRequair"));
                                tv_name.setText(json.getString("name"));
                                tv_phone.setText(json.getString("telephone"));
                                tv_time.setText(json.getString("updateTime"));
                                tv_number.setText(json.getString("teamNum"));
                                initList();
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

    public void initList(){
        Log.e( "initList: ", id+"");
        OkGo.<String>get(Urls.getInstance().TEAM_TASK_NUM_UNFINISHED)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",1)
                .params("pageSize",5)
                .params("sendId",id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("已接团队任务列表", json.toString());
                            if (json.getInt("status") == 200) {
                                list.clear();
                                json =  json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                List<TeamMissionBean> eventBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMissionBean>>() {
                                }.getType());
                                list.addAll(eventBeans);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                teamMissionAdapter = new TeamMissionAdapter(list,context, MyTeamActivity.this);
                                mission_list.setLayoutManager(layoutManager);
                                mission_list.setAdapter(teamMissionAdapter);
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



    public void showpopu(){
        View popView = getLayoutInflater().inflate(R.layout.popu_exit_team, null);
        TextView btn_cancel = popView.findViewById(R.id.btn_cancel);
        TextView btn_confirm = popView.findViewById(R.id.btn_confirm);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new MyTeamActivity.poponDismissListener());
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExit();
            }
        });
        pop.showAtLocation(rl_back, Gravity.CENTER,0,0);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(View view) {
        int position = mission_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, TeamMissionActivity.class);
        intent.putExtra("mission",list.get(position));
        intent.putExtra("id",list.get(position).getId());
        intent.putExtra("state",list.get(position).getState());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_leader:
                call();
                break;
            case R.id.btn_partment:
                Intent intent = new Intent(context,TeamPartmentActivity.class);
                intent.putExtra("name",tv_title.getText().toString());
                intent.putExtra("volId",volId);
                startActivity(intent);
                break;
            case R.id.tv_team_mission_list:
                Intent intent1 = new Intent(context,TeamMissionListActivity.class);
                startActivity(intent1);
                break;
        }
    }

    void call(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + tv_phone.getText().toString().trim());
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    public void confirmExit(){
        showProgressBar();
        JSONObject json = Acache.get(context).getAsJSONObject("volunteer");
        int type = 0;
        try {
            type = json.getInt("roleState");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "";
        if(type ==2) {      //解散团队
            url  = Urls.getInstance().CANCEL_TEAM;
        } else  {       //退出团队
            url = Urls.getInstance().TEAM_EXIT;
        }

        int role = type;
        OkGo.<String>post(url)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("退出团队", json.toString());
                            if (json.getInt("status") == 200) {
                                if(role==2) {
                                    Toast.makeText(context, "团队已解散", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "已退出该团队", Toast.LENGTH_SHORT).show();
                                }

                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }
}
