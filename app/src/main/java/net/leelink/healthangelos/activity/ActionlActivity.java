package net.leelink.healthangelos.activity;

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

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.HtmlUtil;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionlActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_time,tv_address,tv_main,tv_phone,tv_detail,text_title;
    private String id;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actionl);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_address = findViewById(R.id.tv_address);
        tv_main = findViewById(R.id.tv_main);
        tv_phone = findViewById(R.id.tv_phone);
        id = getIntent().getStringExtra("activity_id");
        text_title = findViewById(R.id.text_title);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_detail = findViewById(R.id.tv_detail);
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ACTION+"/"+id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_time.setText(json.getString("startTime")+"至"+json.getString("endTime"));
                                JSONObject json_address = new JSONObject(json.getString("actAddress"));
                                tv_address.setText(json_address.getString("fullAddress"));
                                tv_main.setText(json.getString("orgName"));
                                tv_phone.setText(json.getString("orgTelephone"));
                                text_title.setText(json.getString("actName"));
                                String detail = json.getString("actContent");
                                tv_detail.setText(HtmlUtil.delHTMLTag(detail));
                            }else if(json.getInt("status") == 505){
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                join_action();
                break;
        }
    }

    public void join_action(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().ACTION+"/"+id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动详情", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "活动报名完成~", Toast.LENGTH_SHORT).show();
                                btn_confirm.setBackground(getResources().getDrawable(R.drawable.bg_gray_radius));
                            }else if(json.getInt("status") == 505){
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
