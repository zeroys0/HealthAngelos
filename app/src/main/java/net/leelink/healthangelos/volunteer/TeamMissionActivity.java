package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TeamMissionActivity extends BaseActivity {
    private RelativeLayout rl_back,rl_top;
    RelativeLayout ll1,rl_bottom;
    AgentWeb agentweb;
    TextView text_title,tv_confirm,tv_number,tv_time,tv_mission_manage;
    Context context;
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_mission);
        init();
        context =this;
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_top = findViewById(R.id.rl_top);
        ll1 = findViewById(R.id.ll_1);
        setWeb(getIntent().getStringExtra("url"));
        text_title = findViewById(R.id.text_title);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMission();
            }
        });
        tv_number = findViewById(R.id.tv_number);
        tv_time = findViewById(R.id.tv_time);
        tv_mission_manage = findViewById(R.id.tv_mission_manage);
        tv_mission_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopu();
            }
        });
        state = getIntent().getIntExtra("state",2);
        rl_bottom = findViewById(R.id.rl_bottom);
        switch (state){
            case 2:
                rl_bottom.setVisibility(View.VISIBLE);
                tv_mission_manage.setText("管理选项");
                tv_mission_manage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showVolList();
                    }
                });
                break;
            case 3:
                rl_bottom.setVisibility(View.INVISIBLE);
                tv_mission_manage.setText("确认完成");
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

    public void initData(){
        OkGo.<String>get(Urls.getInstance().TEAM_TASK_NUM+"/"+getIntent().getStringExtra("id"))
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
                                String startTime = Utils.getStandardTime(json.getString("time"));
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = sdf.parse(startTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.e( "onSuccess: ",startTime );
                                long c = System.currentTimeMillis()-date.getTime();     //计算打卡时间和现在的时间差
                                int s =(int) c/1000;
                                if(s<10800 ){       //判断是否超过三个小时
                                    long millisInFuture = (10800-s)*1000;
                                    new CountDownTimer(millisInFuture, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            int t =(int) millisUntilFinished / 1000;
                                            int h = t/3600;
                                            int m = t%3600/60;
                                            int s = t%3600%60;
                                            tv_time.setText(h+":"+m+":"+s);
                                        }

                                        @Override
                                        public void onFinish() {
                                            tv_time.setText("报名已结束");
                                        }
                                    }.start();
                                } else {
                                    tv_time.setText("报名已结束");
                                }
                                tv_number.setText(json.getInt("num")+"");
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

    public void getMission(){
        OkGo.<String>post(Urls.getInstance().USER_SIGN+"/"+getIntent().getStringExtra("id"))
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
                                tv_confirm.setText("取消");
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

    void setWeb(String url) {


        if (agentweb == null) {

            agentweb = AgentWeb.with(TeamMissionActivity.this)
                    .setAgentWebParent(ll1, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App",this)
                    .createAgentWeb()
                    .ready()
                    .go(url);

        } else {
            ll1.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);

            ll1.setVisibility(View.VISIBLE);
        }

    }

    @JavascriptInterface
    public void getDataFormVue(String msg) {
        //做原生操作
        Log.e( "getDataFormVue: ", msg);
        Toast.makeText(context, "msg:"+msg, Toast.LENGTH_SHORT).show();
    }

    public void showPopu(){
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = popView.findViewById(R.id.ll_create_plan);
        TextView tv_party = popView.findViewById(R.id.tv_party);
        tv_party.setText("取消任务");
        LinearLayout ll_create_scope = popView.findViewById(R.id.ll_create_scope);
        TextView tv_person = popView.findViewById(R.id.tv_person);
        tv_person.setText("确认开始");
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new TeamMissionActivity.poponDismissListener());
        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                pop.dismiss();
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMission();
                pop.dismiss();
            }
        });
        pop.showAsDropDown(tv_mission_manage);
    }

    public void showVolList(){
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = popView.findViewById(R.id.ll_create_plan);
        TextView tv_party = popView.findViewById(R.id.tv_party);
        tv_party.setText("取消任务");
        LinearLayout ll_create_scope = popView.findViewById(R.id.ll_create_scope);
        TextView tv_person = popView.findViewById(R.id.tv_person);
        tv_person.setText("确认开始");
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new TeamMissionActivity.poponDismissListener());
        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                pop.dismiss();
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMission();
                pop.dismiss();
            }
        });
        pop.showAsDropDown(tv_mission_manage);
    }

    public void cancel(){
        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_CANCEL+"/"+getIntent().getStringExtra("id"))
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

    public void startMission(){

        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_SIGN+"/"+getIntent().getStringExtra("id"))
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
                });
    }

    public void endMission(){

        OkGo.<String>post(Urls.getInstance().TEAM_CONFIRM_END+"/"+getIntent().getStringExtra("id"))
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


}
