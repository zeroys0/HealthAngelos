package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.fragment.TeamApplyFragment;
import net.leelink.healthangelos.volunteer.fragment.TeamMissionFragment;
import net.leelink.healthangelos.volunteer.fragment.TeamPartmentFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class MyTeamActivity extends BaseActivity   {
    TabLayout tabLayout;
    RelativeLayout rl_back;
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    Context context;
    TextView tv_title,tv_address,tv_service_type,tv_name,tv_phone,tv_time,tv_number,tv_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        init();
        context = this;
        createProgressBar(context);
        initData();
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
        tv_number = findViewById(R.id.tv_number);
        tv_exit = findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(0.5f);
                showpopu();
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("团队任务"));
        tabLayout.addTab(tabLayout.newTab().setText("团队成员"));
        if(type==1) {
            tabLayout.addTab(tabLayout.newTab().setText("入队申请"));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        view_pager = findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        fragments.add(new TeamMissionFragment());
        fragments.add(new TeamPartmentFragment());
        if(type ==1) {
            fragments.add(new TeamApplyFragment());
        }
        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
    }

    public void initData(){
        OkGo.<String>get(Urls.TEAM_TITLE)
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
                                tv_title.setText(json.getString("teamName"));
                                tv_address.setText(json.getString("teamAddress"));
                                tv_service_type.setText(json.getString("serviceRequair"));
                                tv_name.setText(json.getString("name"));
                                tv_phone.setText(json.getString("telephone"));
                                tv_time.setText(json.getString("updateTime"));
                                tv_number.setText(json.getString("teamNum"));

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
        pop.showAtLocation(view_pager, Gravity.CENTER,0,0);
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
        int type = getIntent().getIntExtra("type",0);
        String url = "";
        if(type ==1) {
            url  = Urls.CANCEL_TEAM;
        } else  {
            url = Urls.TEAM_EXIT;
        }


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
                                if(type==1) {
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
