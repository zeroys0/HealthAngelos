package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMemberBean;
import net.leelink.healthangelos.bean.TeamMissionBean;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.adapter.TeamPartmentAdapter;
import net.leelink.healthangelos.volunteer.fragment.TeamMissionCheckFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.volunteer.MyTeamActivity.TEAM_MISSION;
import static net.leelink.healthangelos.volunteer.MyTeamActivity.TEAM_MISSION_REFUSE;
import static net.leelink.healthangelos.volunteer.fragment.VolunteerHomeFragment.VOL_ID;

public class TeamMissionActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back, rl_top;
    private FrameLayout fragment_view;
    TextView text_title, tv_time, tv_mission_manage, tv_num, tv_start_time, tv_end_time, tv_address, tv_people, tv_phone, tv_content, tv_title;
    Context context;
    int state;
    private RecyclerView member_list;
    private ImageView img_member;
    private TeamMissionBean volunteerEventBean;
    private Button btn_confirm, btn_cancel;
    private TeamPartmentAdapter teamPartmentAdapter;
    List<TeamMemberBean> list = new ArrayList<>();
    private LinearLayout ll_1;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_mission);
        init();
        context = this;
        initData();
        searchData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        member_list = findViewById(R.id.member_list);
        rl_top = findViewById(R.id.rl_top);
        text_title = findViewById(R.id.text_title);
        tv_phone = findViewById(R.id.tv_phone);
        tv_people = findViewById(R.id.tv_people);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_address = findViewById(R.id.tv_address);
        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_num = findViewById(R.id.tv_num);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        ll_1 = findViewById(R.id.ll_1);
        fragment_view = findViewById(R.id.fragment_view);


        tv_time = findViewById(R.id.tv_time);
        tv_mission_manage = findViewById(R.id.tv_mission_manage);
        img_member = findViewById(R.id.img_member);
        img_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initList();
            }
        });
        state = getIntent().getIntExtra("state", 2);
        switch (state) {
            case 2:
                tv_mission_manage.setText("报名任务");
                tv_mission_manage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getMission();
                    }
                });
                break;
            case 3:
                tv_mission_manage.setText("打卡签到");
                tv_mission_manage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        endMission();
                    }
                });
                break;
            case 4:
            case 5:
                tv_mission_manage.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void initData() {
        volunteerEventBean = (TeamMissionBean) getIntent().getSerializableExtra("mission");

        tv_title.setText(volunteerEventBean.getServTitle());
        tv_start_time.setText(volunteerEventBean.getStartTime());
        tv_end_time.setText(volunteerEventBean.getEndTime());
        String s = volunteerEventBean.getServAddress();
        try {
            JSONObject jsonObject = new JSONObject(s);
            tv_address.setText(jsonObject.getString("fullAddress"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_people.setText(volunteerEventBean.getServName());
        tv_phone.setText(volunteerEventBean.getServTelephone());
        tv_content.setText(volunteerEventBean.getContent());

        try {
            JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
            if (jsonObject.getInt("roleState") != 2) {
                btn_cancel.setVisibility(View.INVISIBLE);
                btn_confirm.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {

        }
    }


    public void searchData() {
        OkGo.<String>get(Urls.getInstance().TEAM_TASK_NUM + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询团队人数以及时间", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
//                                String startTime = Utils.getStandardTime(json.getString("time"));
                                JSONArray jsonArray = json.getJSONArray("modelList");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMemberBean>>() {
                                }.getType());
                                String startTime = json.getString("time");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = sdf.parse(startTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.e("onSuccess: ", startTime);
                                long c = System.currentTimeMillis() - date.getTime();     //计算打卡时间和现在的时间差
                                int s = (int) c / 1000;
                                if (s < 10800) {       //判断是否超过三个小时
                                    long millisInFuture = (10800 - s) * 1000;
                                    new CountDownTimer(millisInFuture, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            int t = (int) millisUntilFinished / 1000;
                                            int h = t / 3600;
                                            int m = t % 3600 / 60;
                                            int s = t % 3600 % 60;
                                            tv_time.setText(h + ":" + m + ":" + s);
                                        }

                                        @Override
                                        public void onFinish() {
                                            tv_time.setText("报名已结束");
                                        }
                                    }.start();
                                } else {
                                    tv_time.setText("报名已结束");
                                }
                                tv_num.setText(json.getInt("num") + "/" + json.getInt("nums") + "人");
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

    public void getMission() {
        OkGo.<String>post(Urls.getInstance().USER_SIGN + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("接受团队任务任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务接取成功,记得完成哦", Toast.LENGTH_SHORT).show();
                                refreshData();
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


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        if (volunteerEventBean.getVolId()== VOL_ID) {
            teamPartmentAdapter = new TeamPartmentAdapter(list, context, TeamMissionActivity.this,TEAM_MISSION_REFUSE,VOL_ID);
        } else {
            teamPartmentAdapter = new TeamPartmentAdapter(list, context, TeamMissionActivity.this,TEAM_MISSION);
        }
        member_list.setLayoutManager(layoutManager);
        member_list.setAdapter(teamPartmentAdapter);

        if (member_list.getVisibility() == View.VISIBLE) {
            member_list.setVisibility(View.GONE);
        } else if (member_list.getVisibility() == View.GONE) {
            member_list.setVisibility(View.VISIBLE);
        }
    }

    public void cancel() {
        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_CANCEL + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("取消团队任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "活动已取消参加", Toast.LENGTH_SHORT).show();
                                finish();
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

    public void startMission() {

        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_SIGN + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("队长确认结束报名,开始打卡", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "活动开始", Toast.LENGTH_SHORT).show();
                                TeamMissionCheckFragment teamMissionCheckFragment = new TeamMissionCheckFragment();
                                fragmentTransaction.add(R.id.fragment_view, teamMissionCheckFragment);
                                fragmentTransaction.commit();
                                finish();
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

    //队长确认活动完成 提交任务
    public void endMission() {

        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_END + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("队长确认活动完成,不在打卡", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "活动结束了", Toast.LENGTH_SHORT).show();
                                finish();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:      //确认开始任务
                startMission();
                break;
            case R.id.btn_cancel:       //取消任务
                cancel();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        if(!list.get(position).getId().equals(String.valueOf(VOL_ID))) {
            //拒绝此人参加
            OkGo.<String>post(Urls.getInstance().TEAM_TASK_REJECT + "/" + list.get(position).getId() + "/" + volunteerEventBean.getId())
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
                                    Toast.makeText(context, "拒绝成员成功", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    teamPartmentAdapter.notifyDataSetChanged();
                                    refreshData();
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
        } else {
            //取消报名
            OkGo.<String>post(Urls.getInstance().USER_CONFIRM_CANCEL + "/" + volunteerEventBean.getId())
                    .tag(this)
                    .headers("token", MyApplication.token)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                String body = response.body();
                                JSONObject json = new JSONObject(body);
                                Log.d("取消报名", json.toString());
                                if (json.getInt("status") == 200) {
                                    Toast.makeText(context, "取消报名成功", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    teamPartmentAdapter.notifyDataSetChanged();
                                    refreshData();
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

    public void refreshData() {
        OkGo.<String>get(Urls.getInstance().TEAM_TASK_NUM + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询团队人数以及时间", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
//                                String startTime = Utils.getStandardTime(json.getString("time"));
                                JSONArray jsonArray = json.getJSONArray("modelList");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<TeamMemberBean>>() {
                                }.getType());
                                tv_num.setText(json.getInt("num") + "/" + json.getInt("nums") + "人");
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


}
