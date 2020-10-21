package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.RemindTimesManager;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class EditRemindActivity extends BaseActivity implements View.OnClickListener {
private RelativeLayout rl_back,img_add;
EditText ed_name;
TextView tv_time,tv_days,text_title;
Context mContext;
CheckBox cb_1;

    int Year = 2018;
    int Month = 1;
    int Day = 1;
    int Hour = -1;
    int Minute = -1;
    int Type = -1;

    int editType = 0;//0.新增,1编辑
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remind);
        mContext = this;
        init();
        createProgressBar(this);
    }

    public void init(){
        text_title = findViewById(R.id.text_title);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        ed_name = findViewById(R.id.ed_name);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        tv_days = findViewById(R.id.tv_days);
        tv_days.setOnClickListener(this);
        cb_1 = findViewById(R.id.cb_1);


        editType = getIntent().getIntExtra("editType", 0);
        if (editType == 1) {
            //修改
            text_title.setText("编辑");
            ed_name.setText(getIntent().getStringExtra("Title"));
            Type = getIntent().getIntExtra("Type", -1);
            Year = getIntent().getIntExtra("Year", 2018);
            Month = getIntent().getIntExtra("Month", 1);
            Day = getIntent().getIntExtra("Day", 1);
            if (Type == 0) {
                tv_days.setText("重复一次(" + Year + "-" + Month + "-" + Day + ")");
            } else if (Type == 127) {
                tv_days.setText("每天重复");
            } else {
                String text = "(每周 ";
                if (RemindTimesManager.ifSundayHave(Type)) text += "日 ";
                if (RemindTimesManager.ifMondayHave(Type)) text += "一 ";
                if (RemindTimesManager.ifTuesdayHave(Type)) text += "二 ";
                if (RemindTimesManager.ifWednesdayHave(Type)) text += "三 ";
                if (RemindTimesManager.ifThursdayHave(Type)) text += "四 ";
                if (RemindTimesManager.ifFridayHave(Type)) text += "五 ";
                if (RemindTimesManager.ifSaturdayHave(Type)) text += "六 ";
                text += ")重复";
                tv_days.setText(text);
            }
            Year = getIntent().getIntExtra("Year", 2018);
            Month = getIntent().getIntExtra("Month", 1);
            Day = getIntent().getIntExtra("Day", 1);

            Minute = getIntent().getIntExtra("Minute", 0);
            Hour = getIntent().getIntExtra("Hour", Hour);
            String time = Hour + " : " + Minute + "";
            tv_time.setText(time);
            boolean State = getIntent().getBooleanExtra("State", false);
            cb_1.setChecked(State);
        } else {

            text_title.setText("新增");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_add:
                if(editType == 0) {
                    add();
                } else if(editType==1) {
                    edit();
                }
                break;
            case R.id.tv_time:
                //弹出对话框选择时间
                Calendar calender = Calendar.getInstance();
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + " : " + minute + "";
                        Hour = hourOfDay;
                        Minute = minute;
                        tv_time.setText(time);
                    }
                }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show();
                break;
            case R.id.tv_days:
                Intent intent = new Intent(mContext, SelectRemindDateActivity.class);
                intent.putExtra("Year", Year);
                intent.putExtra("Month", Month);
                intent.putExtra("Day", Day);
                intent.putExtra("type", Type);
                startActivityForResult(intent, 1000);

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == -1) {
            Year = data.getIntExtra("Year", 2018);
            Month = data.getIntExtra("Month", 1);
            Day = data.getIntExtra("Day", 1);
            Type = data.getIntExtra("type", -1);
            if (Type == 0) {
                tv_days.setText("重复一次(" + Year + "-" + Month + "" + Day + ")");
            } else if (Type == 127) {
                tv_days.setText("每天重复");
            } else {
                String text = "(每周 ";
                if (RemindTimesManager.ifSundayHave(Type)) text += "日 ";
                if (RemindTimesManager.ifMondayHave(Type)) text += "一 ";
                if (RemindTimesManager.ifTuesdayHave(Type)) text += "二 ";
                if (RemindTimesManager.ifWednesdayHave(Type)) text += "三 ";
                if (RemindTimesManager.ifThursdayHave(Type)) text += "四 ";
                if (RemindTimesManager.ifFridayHave(Type)) text += "五 ";
                if (RemindTimesManager.ifSaturdayHave(Type)) text += "六 ";
                text += ")重复";
                tv_days.setText(text);
            }


        }
    }

    public void add(){
        showProgressBar();
        int state = 0;
        if(cb_1.isChecked()){
            state = 1;
        } else {
            state = 0;
        }


        OkGo.<String>post(Urls.ADDREMIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("day",Day)
                .params("hour",Hour)
                .params("imei",MyApplication.userInfo.getJwotchImei())
                .params("minute",Minute)
                .params("month",Month)
                .params("state",state)
                .params("title",ed_name.getText().toString().trim())
                .params("type",Integer.toBinaryString(Type))
                .params("year",Year)
                .params("titleId",0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("新增提醒", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(mContext, "添加成功", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //修改定时提醒
    public  void edit(){
        showProgressBar();
        int state = 0;
        if(cb_1.isChecked()){
            state = 1;
        } else {
            state = 0;
        }
        OkGo.<String>post(Urls.UPDATEREMIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("day",Day)
                .params("hour",Hour)
                .params("imei",MyApplication.userInfo.getJwotchImei())
                .params("minute",Minute)
                .params("month",Month)
                .params("state",state)
                .params("title",ed_name.getText().toString().trim())
                .params("type",Type)
                .params("year",Year)
                .params("titleId",getIntent().getIntExtra("Id",0))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("编辑提醒", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(mContext, "编辑完成", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
