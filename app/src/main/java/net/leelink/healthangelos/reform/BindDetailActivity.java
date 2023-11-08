package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.BindBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class BindDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private TextView tv_civil, tv_name, tv_id_number, tv_telephone, tv_census_register, tv_address, tv_apply_time, tv_check_time, tv_state, tv_content;
    private Context context;
    private ImageView img_sign1;
    private Button btn_unbind;
    private LinearLayout ll_1;

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
        btn_unbind = findViewById(R.id.btn_unbind);
        ll_1 = findViewById(R.id.ll_1);

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
                    ll_1.setVisibility(View.GONE);
                    break;
                case 2:
                    tv_state.setText("已绑定");
                    btn_unbind.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_state.setText("审核未通过");
                    ll_1.setVisibility(View.GONE);
                    break;
                case 4:
                    tv_state.setText("已解绑");
                    break;
                case 5:
                    tv_state.setText("已撤销");
                    ll_1.setVisibility(View.GONE);
                    break;
            }
            if (bindBean.getVertifyContent() != null) {
                tv_content.setText(bindBean.getVertifyContent());
            }
            Glide.with(context).load(Urls.getInstance().IMG_URL+bindBean.getSysSign()).into(img_sign1);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_unbind:
                unbind();
                break;
        }
    }

    public void unbind(){
        OkGo.<String>post(Urls.getInstance().CIVILL_UNBIND)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑社区单位", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context,"解绑成功", Toast.LENGTH_LONG).show();
                                finish();
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
