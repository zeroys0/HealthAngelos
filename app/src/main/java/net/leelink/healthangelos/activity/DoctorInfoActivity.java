package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import static net.leelink.healthangelos.app.MyApplication.getContext;

public class DoctorInfoActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private TextView tv_follow;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);
        init();
        context = this;
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_follow = findViewById(R.id.tv_follow);
        tv_follow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_follow:
                if(tv_follow.getText().equals("关注")) {
                    follow();
                }else {
                    notFollow();
                }
                break;
        }
    }

    public void follow(){
        OkGo.<String>post(Urls.getInstance().FOLLOW+"/"+getIntent().getStringExtra("doctorId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生列表", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已关注", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("已关注");
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void notFollow(){
        OkGo.<String>delete(Urls.getInstance().FOLLOW+"/"+getIntent().getStringExtra("doctorId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生关注", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已取关", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("关注");
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
