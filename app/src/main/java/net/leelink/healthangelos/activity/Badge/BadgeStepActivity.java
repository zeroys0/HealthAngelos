package net.leelink.healthangelos.activity.Badge;

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
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BadgeStepActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private Context context;
    private Button btn_before, btn_after;
    private String imei;
    private TextView tv_date,tv_step_number;
    Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_step);
        context = this;
        imei = getIntent().getStringExtra("imei");
        createProgressBar(context);
        init();
        getStepData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_before = findViewById(R.id.btn_before);
        btn_before.setOnClickListener(this);
        btn_after = findViewById(R.id.btn_after);
        btn_after.setOnClickListener(this);
        tv_date = findViewById(R.id.tv_date);
        long time = System.currentTimeMillis();
        date = new Date(time);
        tv_date.setText(simpleDateFormat.format(date));
        tv_step_number = findViewById(R.id.tv_step_number);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            //前一天
            case R.id.btn_before:
                try {
                    date = simpleDateFormat.parse(tv_date.getText().toString());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, -1);
                    String date2 = simpleDateFormat.format(calendar.getTime());
                    tv_date.setText(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getStepData();
                break;
            //后一天
            case R.id.btn_after:
                try {
                    String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    if (tv_date.getText().toString().equals(now)) {
                        return;
                    }
                    date = simpleDateFormat.parse(tv_date.getText().toString());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, +1);
                    String date2 = simpleDateFormat.format(calendar.getTime());
                    tv_date.setText(date2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getStepData();
                break;
        }
    }

    public void getStepData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BADGE_STEPS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("testTime", tv_date.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询计步数据", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.has("data")){
                                    json = json.getJSONObject("data");
                                    tv_step_number.setText(json.getString("steps"));
                                } else {
                                    tv_step_number.setText("0");
                                }
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