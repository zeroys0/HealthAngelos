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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import net.leelink.healthangelos.bean.VolCheckBean;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.adapter.CheckAdapter;
import net.leelink.healthangelos.volunteer.fragment.VolunteerHomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TeamMissionClockInActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back, rl_top, rl_clock_in_2;
    private FrameLayout fragment_view;
    TextView text_title, tv_time, tv_mission_manage, tv_num, tv_start_time, tv_end_time, tv_address, tv_people, tv_phone, tv_content, tv_title, tv_auditing, tv_reason, tv_reason_2, tv_name, tv_state, tv_sex, tv_vol_content, tv_name_2, tv_vol_phone_2, tv_sex_2, tv_vol_content_2;
    Context context;
    private ImageView img_head, img_1, img_2, img_3, img_head_2, sec_img_1, sec_img_2, sec_img_3;
    private LinearLayout ll_auiting_2;
    int state;
    private RecyclerView member_list;
    private CheckAdapter checkAdapter;
    private Button btn_submit;
    List<TeamMemberBean> list = new ArrayList<>();
    private TeamMissionBean volunteerEventBean;
    private List<VolCheckBean> vol_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_mission_clock_in);
        context = this;
        init();
        initData();
        getInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        member_list = findViewById(R.id.member_list);
        member_list.setNestedScrollingEnabled(false);
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
        tv_mission_manage = findViewById(R.id.tv_mission_manage);
        tv_mission_manage.setOnClickListener(this);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_reason = findViewById(R.id.tv_reason);
        tv_reason_2 = findViewById(R.id.tv_reason_2);
        tv_auditing = findViewById(R.id.tv_auditing);
        img_1 = findViewById(R.id.img_1);
        img_2 = findViewById(R.id.img_2);
        img_3 = findViewById(R.id.img_3);
        tv_vol_content = findViewById(R.id.tv_vol_content);
        ll_auiting_2 = findViewById(R.id.ll_auiting_2);
        tv_name_2 = findViewById(R.id.tv_name_2);
        tv_vol_phone_2 = findViewById(R.id.tv_vol_phone_2);
        tv_sex_2 = findViewById(R.id.tv_sex_2);
        sec_img_1 = findViewById(R.id.sec_img_1);
        sec_img_2 = findViewById(R.id.sec_img_2);
        sec_img_3 = findViewById(R.id.sec_img_3);
        tv_vol_content_2 = findViewById(R.id.tv_vol_content_2);
        rl_clock_in_2 = findViewById(R.id.rl_clock_in_2);
        tv_state = findViewById(R.id.tv_state);
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
        state = volunteerEventBean.getState();
        switch (state) {
            case 1:     //可领取
                tv_mission_manage.setText("领取任务");
                break;
            case 2:     //待开始打卡

                tv_state.setText("待开始打卡");
                break;
            case 3:     //待结束打卡

                tv_state.setText("任务进行中");
                break;
            case 4:     //等待审核
                tv_mission_manage.setVisibility(View.GONE);
                tv_auditing.setVisibility(View.VISIBLE);
                btn_submit.setVisibility(View.GONE);
                tv_state.setText("等待审核");
                break;
            case 5:     //审核已通过
                tv_mission_manage.setVisibility(View.GONE);
                btn_submit.setVisibility(View.GONE);
                tv_state.setText("任务完成");
                break;
            case 6:     //审核未通过
                tv_reason.setVisibility(View.VISIBLE);
                //   tv_reason.setText(volunteerEventBean.getCause());
                tv_mission_manage.setText("重新提交");
                tv_state.setText("审核未通过");
                btn_submit.setText("放弃申诉");
                break;
            case 7:     //二次审核中
                tv_reason.setVisibility(View.VISIBLE);
                //   tv_reason.setText(volunteerEventBean.getCause());
                tv_auditing.setVisibility(View.VISIBLE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                tv_state.setText("二次审核中");
                btn_submit.setVisibility(View.GONE);
                break;
            case 8:     //结束
                tv_reason.setVisibility(View.VISIBLE);
                tv_mission_manage.setVisibility(View.GONE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                tv_state.setText("任务完成");
                btn_submit.setVisibility(View.GONE);
                break;
            case 9:     //二次审核未通过
                tv_reason.setVisibility(View.VISIBLE);
                tv_reason_2.setVisibility(View.VISIBLE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                tv_reason_2.setVisibility(View.VISIBLE);
                tv_mission_manage.setVisibility(View.GONE);
                tv_state.setText("二次审核未通过");
                btn_submit.setVisibility(View.GONE);
                break;
        }
        if (volunteerEventBean.getMyState() == 1) {
            tv_mission_manage.setText("开始打卡");
        } else if (volunteerEventBean.getMyState() == 2) {
            tv_mission_manage.setText("结束打卡");
        } else {
            if (volunteerEventBean.getState() == 6) {
                tv_mission_manage.setText("重新提交");
            } else {
                tv_mission_manage.setVisibility(View.INVISIBLE);
            }
        }


        JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
        try {
            tv_name_2.setText(jsonObject.getString("volName"));
            if (jsonObject.getInt("volSex") == 0) {
                tv_sex_2.setText("男");
            } else {
                tv_sex_2.setText("女");
            }
            tv_vol_phone_2.setText(jsonObject.getString("volTelephone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getInfo() {
        //获取团队信息
        OkGo.<String>get(Urls.getInstance().TEAM_TASK + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询团队任务详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (!json.isNull("firstAuditPh1")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh1")).into(sec_img_1);
                                }
                                if (!json.isNull("firstAuditPh2")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh2")).into(sec_img_2);
                                }
                                if (!json.isNull("firstAuditPh3")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh3")).into(sec_img_3);
                                }
                                if (!json.isNull("firstAuditRemark")) {
                                    tv_vol_content_2.setText(json.getString("firstAuditRemark"));
                                }
                                if (!json.isNull("cause")) {
                                    tv_reason.setText(json.getString("cause"));
                                }
                                if (!json.isNull("causeTwo")) {
                                    tv_reason_2.setText(json.getString("causeIssue"));
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
        OkGo.<String>get(Urls.getInstance().TEAM_TASK_NUM + "/" + volunteerEventBean.getId())
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
                                JSONArray jsonArray = json.getJSONArray("modelList");
                                Gson gson = new Gson();
                                vol_list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolCheckBean>>() {
                                }.getType());
                                checkAdapter = new CheckAdapter(vol_list, context, TeamMissionClockInActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                member_list.setAdapter(checkAdapter);
                                member_list.setLayoutManager(layoutManager);
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
            case R.id.tv_mission_manage:        //服务打卡
                if (state == 2) {
                    startMission();
                } else if (state == 3) {
                    Intent intent = new Intent(context, ClockInActivity.class);
                    intent.putExtra("id", volunteerEventBean.getId());
                    intent.putExtra("type", 2);
                    startActivity(intent);
                } else if (state == 6) {
                    if (VolunteerHomeFragment.LEADER == 2) {
                        Intent intent = new Intent(context, ReClockInActivity.class);
                        intent.putExtra("id", volunteerEventBean.getId());
                        intent.putExtra("type", 2);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "您不是队长,不具备该权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_submit:       //提交任务|联系队长
                if (state == 6) {
                    if (VolunteerHomeFragment.LEADER == 2) {
                        backgroundAlpha(0.5f);
                        showpopu();
                    } else {
                        Toast.makeText(context, "您不是队长,不具备该权限", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    endMission();
                }
                break;
        }
    }

    public void startMission() {
        OkGo.<String>post(Urls.getInstance().TEAM_CARD_START + "/" + volunteerEventBean.getId())
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
                                Toast.makeText(context, "开始打卡成功", Toast.LENGTH_SHORT).show();
                                state = 3;
                                tv_mission_manage.setText("结束打卡");
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

        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_END + "/" + volunteerEventBean.getId())
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

    //放弃申诉
    public void confirmExit() {
        OkGo.<String>post(Urls.getInstance().TEAM_TASKRECHECK_REFUSE + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("放弃申诉", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "放弃了申诉", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        call(vol_list.get(position).getVolTelephone());
    }

    void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }

    public void showpopu() {
        View popView = getLayoutInflater().inflate(R.layout.popu_exit_team, null);
        TextView btn_cancel = popView.findViewById(R.id.btn_cancel);
        TextView btn_confirm = popView.findViewById(R.id.btn_confirm);
        TextView tv_title = popView.findViewById(R.id.tv_title);
        tv_title.setText("确定要取消申诉吗?");
        TextView tv_content = popView.findViewById(R.id.tv_content);
        tv_content.setText("放弃申诉后,任务将会被取消,无法再次申诉");
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new TeamMissionClockInActivity.poponDismissListener());
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        btn_confirm.setText("放弃申诉");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExit();
            }
        });
        pop.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
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
}
