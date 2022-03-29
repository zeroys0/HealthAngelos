package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamMissionBean;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class TeamMissionDetailActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    private Context context;
    private TextView tv_call;
    private Button btn_confirm;
    private TeamMissionBean volunteerEventBean;
    private TextView tv_type,tv_start_time,tv_end_time,tv_address,tv_people,tv_phone,tv_content,tv_title,tv_count,tv_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_mission_detail);
        context = this;
        init();
        try {
            initData();
        } catch (Exception e){
            getDataFromHost();
        }
        check();

    }
    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_call = findViewById(R.id.tv_call);
        tv_call.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setText("团队任务");
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_address = findViewById(R.id.tv_address);
        tv_people = findViewById(R.id.tv_people);
        tv_phone = findViewById(R.id.tv_phone);
        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);

    }
    public void initData(){
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
        tv_count.setText(volunteerEventBean.getNum()+"人");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_call:      //拨打电话
                call();
                break;
            case R.id.btn_confirm:
                getMission();
                break;
            case R.id.tv_cancel:
                if(state!=1){
                    Intent intent = new Intent(context,VolunteerApplyActivity.class);
                    startActivity(intent);
                    return;
                }
                if (roleState == 1 && teamState == 2) {     //志愿者
                    Intent intent5 = new Intent(context, MyTeamActivity.class);
                    intent5.putExtra("type", 0);
                    startActivity(intent5);
                } else if (roleState == 2) {     //志愿者队长
                    Intent intent5 = new Intent(context, MyTeamActivity.class);
                    intent5.putExtra("type", 1);
                    startActivity(intent5);

                } else {
                    Intent intent5 = new Intent(context, TeamListActivity.class);
                    //intent5.putExtra("organ_id", organ_id + "");
                    startActivity(intent5);
                }
                break;
        }
    }

    void call(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + volunteerEventBean.getServTelephone());
        intent.setData(data);
        startActivity(intent);
    }

    public void getDataFromHost(){
        OkGo.<String>get(Urls.getInstance().TEAM_TASK + "/" + getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取团队任务详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");

                                tv_title.setText(json.getString("servTitle"));
                                tv_start_time.setText(json.getString("startTime"));
                                tv_end_time.setText(json.getString("endTime"));
                                String s = json.getString("servAddress");
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    tv_address.setText(jsonObject.getString("fullAddress"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                tv_people.setText(json.getString("servName"));
                                tv_phone.setText(json.getString("servTelephone"));
                                tv_content.setText(json.getString("content"));
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
    public void getMission(){
        JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
        if(jsonObject!=null){
            try {
                if(jsonObject.getInt("team_state")!=2) {
                    Toast.makeText(context, "团队审核未成功,无法接取任务", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        OkGo.<String>post(Urls.getInstance().TEAM_ACCEPT+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("接受团队任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务接取成功,记得完成哦", Toast.LENGTH_SHORT).show();
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

    private int roleState,teamState,state;
    public void check() {
        OkGo.<String>get(Urls.getInstance().MINE_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者个人信息", json.toString());
                            Acache.get(context).put("is_vol", "true");
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                roleState = json.getInt("roleState");
                                teamState = json.getInt("teamState");
                                state = json.getInt("state");
                                if(roleState!=2){
                                    btn_confirm.setVisibility(View.INVISIBLE);
                                }
                                Acache.get(context).put("volunteer", json);
                            } else if (json.getInt("status") == 201) {
                                Acache.get(context).put("is_vol", "false");
                                tv_cancel.setVisibility(View.INVISIBLE);
                                btn_confirm.setVisibility(View.INVISIBLE);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
