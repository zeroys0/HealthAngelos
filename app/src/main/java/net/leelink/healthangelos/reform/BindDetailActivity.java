package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.BindBean;

import org.json.JSONException;
import org.json.JSONObject;

public class BindDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private TextView tv_civil, tv_name, tv_id_number, tv_telephone, tv_census_register, tv_address, tv_apply_time, tv_check_time, tv_state, tv_content;
    private Context context;
    private ImageView img_sign1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_detail);
        context = this;
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_civil = findViewById(R.id.tv_civil);
        tv_name = findViewById(R.id.tv_name);
        tv_id_number = findViewById(R.id.tv_id_number);
        tv_telephone = findViewById(R.id.tv_telephone);
        tv_census_register = findViewById(R.id.tv_census_register);
        tv_address = findViewById(R.id.tv_address);
        tv_apply_time = findViewById(R.id.tv_apply_time);
        tv_check_time = findViewById(R.id.tv_check_time);
        tv_state = findViewById(R.id.tv_state);
        tv_content = findViewById(R.id.tv_content);
        img_sign1 = findViewById(R.id.img_sign1);

        BindBean bindBean = (BindBean) getIntent().getSerializableExtra("bean");
        if (bindBean != null) {
            tv_civil.setText(bindBean.getCommitteeName());
            tv_name.setText(bindBean.getElderlyName());
            tv_id_number.setText(bindBean.getIdNumber());
            tv_telephone.setText(bindBean.getTelephone());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(bindBean.getHouseholdAddress());
                tv_census_register.setText(jsonObject.getString("fullAddress"));
                jsonObject = new JSONObject(bindBean.getCurrentAddress());
                tv_address.setText(jsonObject.getString("fullAddress"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            tv_apply_time.setText(bindBean.getApplyTime());
            if (bindBean.getAuthTime() != null) {
                tv_check_time.setText(bindBean.getAuthTime());
            }
            switch (bindBean.getAuthState()) {
                case 1:
                    tv_state.setText("审核中");
                    break;
                case 2:
                    tv_state.setText("已通过");
                    break;
                case 3:
                    tv_state.setText("审核失败");
                    break;
                case 4:
                    tv_state.setText("已撤销");
                    break;
            }
            if (bindBean.getVertifyContent() != null) {
                tv_content.setText(bindBean.getVertifyContent());
            }
            //Glide.with(context).load(Urls.getInstance().IMG_URL+bindBean.get)
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
