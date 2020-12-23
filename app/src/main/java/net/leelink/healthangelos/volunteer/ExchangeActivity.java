package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.activity.EventDetailActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;

public class ExchangeActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_start_time,rl_end_time,rl_address,rl_content;
    private TimePickerView pvTime,pvTime1,pvTime2;
    private SimpleDateFormat sdf,sdf1;
    private TextView tv_start_time,tv_end_time,tv_address,tv_content;
    public static int ADDRESS = 2;
    public static int DETAIL = 3;
    EditText ed_title,ed_phone;
    Context context;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        init();
        initData();
        context = this;
        createProgressBar(context);
        initPickerView1();
        initPickerView2();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_address = findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        tv_address = findViewById(R.id.tv_address);
        rl_content = findViewById(R.id.rl_content);
        rl_content.setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
        ed_title = findViewById(R.id.ed_title);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        ed_phone  = findViewById(R.id.ed_phone);

    }

    public void initData(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_start_time:
                pvTime1.show();
                break;
            case R.id.rl_end_time:
                pvTime2.show();
                break;
            case R.id.rl_address:
                Intent intent = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(intent,ADDRESS);
                break;
            case R.id.rl_content:
                Intent intent1 = new Intent(this, EventDetailActivity.class);
                startActivityForResult(intent1,DETAIL);
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public void submit(){
        JSONObject jsonObject = new JSONObject();
        if(ed_title.getText().toString().equals("")){
            Toast.makeText(context, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_start_time.getText().toString().equals("")){
            Toast.makeText(context, "请选择开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_end_time.getText().toString().equals("")){
            Toast.makeText(context, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_address.getText().toString().equals("")){
            Toast.makeText(context, "请选择服务地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_phone.getText().toString().equals("")){
            Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_content.getText().toString().equals("")){
            Toast.makeText(context, "请描述服务内容", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            jsonObject.put("endTime",tv_end_time.getText().toString().trim()+":00");
            jsonObject.put("servAddress",tv_address.getText().toString().trim());
            jsonObject.put("tv_content",tv_content.getText().toString().trim());
            jsonObject.put("servName",getIntent().getStringExtra("servName"));
            jsonObject.put("servTelephone",ed_phone.getText().toString().trim());
            jsonObject.put( "startTime",tv_start_time.getText().toString().trim()+":00");
            jsonObject.put("servTitle",ed_title.getText().toString().trim());
            jsonObject.put("volUserNo",getIntent().getStringExtra("volUserNo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().VOL_TASK)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发布个人任务", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                            }
                            else if (json.getInt("status") == 505) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode ==2 ){
            tv_address.setText(data.getStringExtra("address"));
        }
        if(resultCode ==3) {
            tv_content.setText(data.getStringExtra("detail"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initPickerView1(){
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean[] type = {true, true, true, true, true, false};
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_start_time.setText(sdf1.format(date));

            }
        }).setType(type).build();
    }
    private void initPickerView2(){
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean[] type = {true, true, true, true, true, false};
        pvTime2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_end_time.setText(sdf1.format(date));

            }
        }).setType(type).build();
    }
}
