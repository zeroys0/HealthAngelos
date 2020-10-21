package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pattonsoft.pattonutil1_0.util.Mytoast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.RemindTimesManager;
import net.leelink.healthangelos.app.BaseActivity;

import java.util.Calendar;

public class SelectRemindDateActivity extends BaseActivity implements View.OnClickListener {
TextView tv_time,tv_1,tv_2,tv_3,tv_4,tv_5,tv_6,tv_7;
RelativeLayout rl_back;
Context context;
Button btn_sure;

    int type = 0;
    int sun = 0;
    int mon = 0;
    int tue = 0;
    int wed = 0;
    int thu = 0;
    int fri = 0;
    int sat = 0;

    int Year = 0;
    int Month = 0;
    int Day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_remind_date);
        context = this;
        init();
    }

    public void init(){
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_1 = findViewById(R.id.tv_1);
        tv_1.setOnClickListener(this);
        tv_2 = findViewById(R.id.tv_2);
        tv_2.setOnClickListener(this);
        tv_3 = findViewById(R.id.tv_3);
        tv_3.setOnClickListener(this);
        tv_4 = findViewById(R.id.tv_4);
        tv_4.setOnClickListener(this);
        tv_5 = findViewById(R.id.tv_5);
        tv_5.setOnClickListener(this);
        tv_6 = findViewById(R.id.tv_6);
        tv_6.setOnClickListener(this);
        tv_7 = findViewById(R.id.tv_7);
        tv_7.setOnClickListener(this);
        btn_sure = findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);


        Calendar calender = Calendar.getInstance();
        if (getIntent().getIntExtra("Year", -1) > 0) {
            Year = getIntent().getIntExtra("Year", 2018);
            Month = getIntent().getIntExtra("Month", 1);
            Day = getIntent().getIntExtra("Day", 1);
            tv_time.setText(Year + "-" + Month + "-" + Day);
        } else {
            tv_time.setText(calender.get(Calendar.YEAR) + "-" + (calender.get(Calendar.MONTH) + 1) + "-" + calender.get(Calendar.DAY_OF_MONTH));
        }
        type = getIntent().getIntExtra("type", 0);
        sun = RemindTimesManager.ifSundayHave(type) ? 1 : 0;
        mon = RemindTimesManager.ifMondayHave(type) ? 1 : 0;
        tue = RemindTimesManager.ifTuesdayHave(type) ? 1 : 0;
        wed = RemindTimesManager.ifWednesdayHave(type) ? 1 : 0;
        thu = RemindTimesManager.ifThursdayHave(type) ? 1 : 0;
        fri = RemindTimesManager.ifFridayHave(type) ? 1 : 0;
        sat = RemindTimesManager.ifSaturdayHave(type) ? 1 : 0;
        setSelect();
    }

    void setSelect() {
        tv_time.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_1.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_2.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_3.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_4.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_5.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_6.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        tv_7.setBackgroundColor(getResources().getColor(R.color.color_ccc));
        if (type == 0) {
            tv_time.setBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            if (sun > 0) tv_1.setBackgroundColor(getResources().getColor(R.color.blue));
            if (mon > 0) tv_2.setBackgroundColor(getResources().getColor(R.color.blue));
            if (tue > 0) tv_3.setBackgroundColor(getResources().getColor(R.color.blue));
            if (wed > 0) tv_4.setBackgroundColor(getResources().getColor(R.color.blue));
            if (thu > 0) tv_5.setBackgroundColor(getResources().getColor(R.color.blue));
            if (fri > 0) tv_6.setBackgroundColor(getResources().getColor(R.color.blue));
            if (sat > 0) tv_7.setBackgroundColor(getResources().getColor(R.color.blue));

        }
    }

    @Override
    public void onClick(View v) {
        Calendar calender = Calendar.getInstance();
        Year = calender.get(Calendar.YEAR);
        Month = calender.get(Calendar.MONTH) + 1;
        Day = calender.get(Calendar.DAY_OF_MONTH);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_time:
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Year = year;
                        Month = month + 1;
                        Day = dayOfMonth;
                        tv_time.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        type = 0;
                        setSelect();
                    }
                }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.tv_1:
                sun = sun == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_2:
                mon = mon == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_3:
                tue = tue == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_4:
                wed = wed == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_5:
                thu = thu == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_6:
                fri = fri == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.tv_7:
                sat = sat == 0 ? 1 : 0;
                type = getType();
                setSelect();
                break;
            case R.id.btn_sure:
                if (type == -1) {
                    Mytoast.show(context, "请选择提醒重复类型");
                }
                Intent intent = new Intent();
                intent.putExtra("Year", Year);
                intent.putExtra("Month", Month);
                intent.putExtra("Day", Day);
                intent.putExtra("type", type);
                setResult(-1, intent);
                finish();
                break;
        }
    }

    public int getType() {
        int type = 0;
        if (sun > 0) type += 1;
        if (mon > 0) type += 2;
        if (tue > 0) type += 4;
        if (wed > 0) type += 8;
        if (thu > 0) type += 16;
        if (fri > 0) type += 32;
        if (sat > 0) type += 64;

        return type;
    }
}
