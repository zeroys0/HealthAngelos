package net.leelink.healthangelos.volunteer;

import android.content.Context;
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
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.TeamBean;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class TeamDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private TextView tv_address,tv_type,tv_name,tv_title,tv_phone,tv_time,tv_num;
    private TeamBean teamBean;
    private Button btn_confirm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        teamBean = (TeamBean) getIntent().getSerializableExtra("team");
        tv_address = findViewById(R.id.tv_address);
        tv_address.setText(teamBean.getAreaAddress());
        tv_type = findViewById(R.id.tv_type);
        tv_type.setText(teamBean.getServiceRequair());
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(teamBean.getName());
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(teamBean.getTeamName());
        tv_phone = findViewById(R.id.tv_phone);
        tv_phone.setText(teamBean.getTelephone());
        tv_time = findViewById(R.id.tv_time);
        tv_time.setText(teamBean.getJoinTime());
        tv_num = findViewById(R.id.tv_num);
        tv_num.setText(teamBean.getTeamNum());
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                confirm();
                break;
        }
    }

    public void confirm(){
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().TEAM_JOIN+"/"+teamBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("加入团队", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "申请加入成功,请等待队长审核", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new TeamBean());
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
                        LoadDialog.stop();
                        Toast.makeText(context, "申请失败", Toast.LENGTH_SHORT).show();
                        super.onError(response);
                    }
                });
    }
}
