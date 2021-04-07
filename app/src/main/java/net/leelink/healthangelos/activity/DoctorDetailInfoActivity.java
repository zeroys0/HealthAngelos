package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import net.leelink.healthangelos.bean.HomeDoctorBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class DoctorDetailInfoActivity extends BaseActivity {
    RelativeLayout rl_back;
    Context context;
    CircleImageView img_head;
    TextView tv_name, tv_duties, tv_department, tv_hospital, tv_honor, tv_skill, tv_work_exp, tv_educate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail_info);
        context = this;
        createProgressBar(this);
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_duties = findViewById(R.id.tv_duties);
        tv_department = findViewById(R.id.tv_department);
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_honor = findViewById(R.id.tv_honor);
        tv_skill = findViewById(R.id.tv_skill);
        tv_work_exp = findViewById(R.id.tv_work_exp);
        tv_educate = findViewById(R.id.tv_educate);
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            initData(); //家庭
        } else {
            initView(); //极速
        }
    }

    public void initView() {
        HomeDoctorBean.CareDoctorRegeditBean doctor = getIntent().getParcelableExtra("doctor");
        assert doctor != null;
        Glide.with(context).load(Urls.getInstance().IMG_URL + doctor.getImgPath()).into(img_head);

        tv_name.setText(doctor.getName());

        tv_duties.setText(doctor.getDuties());

        tv_department.setText(doctor.getDepartment());

        tv_hospital.setText(doctor.getHospital());

        tv_honor.setText(doctor.getHonor());

        tv_skill.setText(doctor.getSkill());

        tv_work_exp.setText(doctor.getWorkHistory());

        tv_educate.setText(doctor.getEducation());

    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().APPLYDOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取医生信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                json = json.getJSONObject("careDoctorRegedit");
                                Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("imgPath")).into(img_head);

                                tv_name.setText(json.getString("name"));
                                tv_duties.setText(json.getString("duties"));
                                tv_department.setText(json.getString("department"));
                                tv_hospital.setText(json.getString("hospital"));
                                tv_honor.setText(json.getString("honor"));

                                tv_skill.setText(json.getString("skill"));

                                tv_work_exp.setText(json.getString("workHistory"));

                                tv_educate.setText(json.getString("education"));

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

                        stopProgressBar();
                    }
                });
    }
}
