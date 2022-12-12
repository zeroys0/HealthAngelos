package net.leelink.healthangelos.activity.slaap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.widget.SwitchCompat;

public class SlaapSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_save,tv_start_time,tv_end_time;
    private EditText ed_max_breath,ed_min_breath,ed_max_heart,ed_min_heart,ed_alarm,ed_turn_time,ed_leave_time,ed_move_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private int type = 0;
    private SwitchCompat is_turn,is_leave,is_move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slaap_setting);
        context = this;
        init();
        initTime();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        ed_max_breath = findViewById(R.id.ed_max_breath);
        ed_min_breath = findViewById(R.id.ed_min_breath);
        ed_max_heart = findViewById(R.id.ed_max_heart);
        ed_min_heart = findViewById(R.id.ed_min_heart);
        ed_alarm = findViewById(R.id.ed_alarm);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
        is_turn = findViewById(R.id.is_turn);
        ed_turn_time = findViewById(R.id.ed_turn_time);
        is_leave = findViewById(R.id.is_leave);
        is_move = findViewById(R.id.is_move);
        ed_leave_time = findViewById(R.id.ed_leave_time);
        ed_move_time = findViewById(R.id.ed_move_time);

    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().SLAAP_DEVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("sn",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取床垫信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                ed_max_breath.setText(json.getString("breathupper"));
                                ed_min_breath.setText(json.getString("breathlower"));
                                ed_max_heart.setText(json.getString("heartupper"));
                                ed_min_heart.setText(json.getString("heartlower"));
                                ed_alarm.setText(json.getString("excCount"));
                                tv_start_time.setText(json.getString("startTime"));
                                tv_end_time.setText(json.getString("endTime"));
                                is_turn.setChecked(json.getBoolean("inbed"));
                                ed_turn_time.setText(json.getString("inBedTime"));
                                is_leave.setChecked(json.getBoolean("leaveBed"));
                                ed_leave_time.setText(json.getString("leaveBedTime"));
                                is_move.setChecked(json.getBoolean("move"));
                                ed_move_time.setText(json.getString("moveTime"));

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:  //保存设置
                save();
                break;
            case R.id.tv_start_time:    //报警开始时间
                type = 0;
                pvTime.show();
                break;
            case R.id.tv_end_time:      //报警截止时间
                type = 1;
                pvTime.show();
                break;
        }
    }

    public void save(){
        JSONObject json = new JSONObject();
        try {
            json.put("breathlower",ed_min_breath.getText().toString());
            json.put("breathupper",ed_max_breath.getText().toString());
            json.put("heartlower",ed_min_heart.getText().toString());
            json.put("heartupper",ed_max_heart.getText().toString());
            json.put("excCount",ed_alarm.getText().toString());
            json.put("startTime",tv_start_time.getText().toString());
            json.put("endTime",tv_end_time.getText().toString());
            json.put("inbed",is_turn.isChecked());
            json.put("inBedTime",ed_turn_time.getText().toString());
            json.put("leaveBed",is_leave.isChecked());
            json.put("leaveBedTime",ed_leave_time.getText().toString());
            json.put("move",is_move.isChecked());
            json.put("moveTime",ed_move_time.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d( "保存设置: ",json.toString());
        OkGo.<String>put(Urls.getInstance().SLAAP_DEVICE+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置床带", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置完成", Toast.LENGTH_SHORT).show();
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

    private void initTime() {
        boolean[] time_type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                switch (type) {
                    case 0:
                        tv_start_time.setText(sdf.format(date));
                        break;
                    case 1:
                        tv_end_time.setText(sdf.format(date));
                        break;

                }

            }
        }).setType(time_type).build();
    }
}