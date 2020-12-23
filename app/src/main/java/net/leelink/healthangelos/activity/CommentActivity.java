package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import net.leelink.healthangelos.bean.OrderBean;
import net.leelink.healthangelos.util.RatingBar;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private CircleImageView img_head;
    private TextView tv_name,tv_duties,tv_department,tv_hospital,tv_content;
    Context context;
    private RatingBar rb_doctor;
    private EditText ed_comment;
    private Button btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();

        context = this;
        createProgressBar(this);
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

    }

    public void initData(){
        img_head = findViewById(R.id.img_head);
        OrderBean orderBean = getIntent().getParcelableExtra("orderBean");
        Glide.with(context).load(Urls.getInstance().IMG_URL+orderBean.getImgPath()).into(img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(orderBean.getName());
        tv_duties = findViewById(R.id.tv_duties);
        tv_duties.setText(orderBean.getDuties());
        tv_department = findViewById(R.id.tv_department);
        tv_department.setText(orderBean.getDepartment());
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_hospital.setText(orderBean.getHospital());
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText("病情描述:"+orderBean.getRemark());
        rb_doctor = findViewById(R.id.rb_doctor);
        rb_doctor.setSelectedNumber(5);
        ed_comment = findViewById(R.id.ed_comment);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(orderBean.getOrderId());
            }
        });
    }

    public void submit(String orderId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content",ed_comment.getText().toString());
            jsonObject.put("orderId",orderId);
            jsonObject.put("star",rb_doctor.getSelectedNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().APPRAISE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医单评价", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "评价成功", Toast.LENGTH_SHORT).show();
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                        stopProgressBar();
                    }
                });
    }
}
