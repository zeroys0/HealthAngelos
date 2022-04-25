package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class DoctorCheckingActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private ImageView img_head;
    private Context context;
    private TextView tv_name, tv_profession, tv_sub, tv_hospital, tv_skill, tv_score, tv_total, tv_time;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_checking);
        context = this;
        init();
//        checkDoctor();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_profession = findViewById(R.id.tv_profession);
        tv_sub = findViewById(R.id.tv_sub);
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_skill = findViewById(R.id.tv_skill);
        tv_score = findViewById(R.id.tv_score);
        tv_total = findViewById(R.id.tv_total);
        tv_time = findViewById(R.id.tv_time);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public void initData() {
        String s = getIntent().getStringExtra("data");
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject json = jsonObject.getJSONObject("careDoctorRegedit");
            Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("imgPath")).into(img_head);
            tv_name.setText(json.getString("name"));
            tv_profession.setText(json.getString("duties"));
            tv_sub.setText(json.getString("department"));
            tv_hospital.setText(json.getString("hospital"));
            tv_skill.setText("擅长: " + json.getString("skill"));
            tv_score.setText(json.getString("totalScore"));
            tv_total.setText("月回答: " + json.getString("totalCount"));
            tv_time.setText(jsonObject.getString("signTime"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cancel() {
        OkGo.<String>post(Urls.getInstance().APPLYCANCEL)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("取消医生签约", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "取消签约成功", Toast.LENGTH_SHORT).show();
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




    public void checkDoctor() {

        OkGo.<String>get(Urls.getInstance().APPLYDOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取签约情况", json.toString());
                            if (json.getInt("status") == 200) {

                            } else if (json.getInt("status") == 201) {

                            } else if (json.getInt("status") == 202) {

                            } else if (json.getInt("status") == 203) {

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