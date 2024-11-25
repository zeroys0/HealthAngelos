package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class HealthManageActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView tv_name,tv_sex,tv_birth,tv_nation,tv_phone,tv_organ,tv_job,tv_number,tv_empty;

    private Context context;

    private LinearLayout ll_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_manage);
        context = this;
        init();
        initData();
    }

    public void init(){
        ll_info = findViewById(R.id.ll_info);

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(v -> finish());
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_birth = findViewById(R.id.tv_birth);
        tv_nation = findViewById(R.id.tv_nation);
        tv_phone = findViewById(R.id.tv_phone);
        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(tv_phone.getText().toString().trim());
            }
        });
        tv_organ = findViewById(R.id.tv_organ);
        tv_job = findViewById(R.id.tv_job);
        tv_number = findViewById(R.id.tv_number);
        tv_empty = findViewById(R.id.tv_empty);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().APP_USER+"/"+ MyApplication.userInfo.getOlderlyId())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取健康管理师信息", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.isNull("data")){
                                    ll_info.setVisibility(View.GONE);
                                    tv_empty.setVisibility(View.VISIBLE);
                                } else {
                                    json = json.getJSONObject("data");
                                    tv_name.setText(json.getString("name"));
                                    tv_sex.setText(json.getInt("sex") == 0 ? "男" : "女");
                                    tv_birth.setText(json.getString("birthday"));
                                    tv_nation.setText(json.getString("natureName"));
                                    tv_phone.setText(json.getString("telephone"));
                                    tv_organ.setText(json.getString("organName"));
                                    tv_job.setText("健康管理师");
                                    tv_number.setText(json.getString("userNo"));
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void call(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
        startActivity(intent);
    }
}