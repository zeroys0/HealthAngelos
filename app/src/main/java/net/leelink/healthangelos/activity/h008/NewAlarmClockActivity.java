package net.leelink.healthangelos.activity.h008;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.SelectRemindDateActivity;
import net.leelink.healthangelos.activity.home.RemindTimesManager;
import net.leelink.healthangelos.app.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewAlarmClockActivity extends BaseActivity {
    Context context;
    RelativeLayout rl_back,img_add;
    private TextView tv_type,tv_time,tv_days;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int Type = -1;
    int type = -1;
    private CheckBox cb_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm_clock);
        context = this;
        init();
        initPickerView();
    }

    public void init(){
        tv_type = findViewById(R.id.tv_type);
        tv_type.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        tv_days = findViewById(R.id.tv_days);
        tv_days.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        cb_1 = findViewById(R.id.cb_1);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_type:
                showType();
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
            case R.id.tv_days:
                Intent intent = new Intent(context, SelectRemindDateActivity.class);
                intent.putExtra("Year", 1);
                intent.putExtra("Month", 1);
                intent.putExtra("Day", 1);
                intent.putExtra("type", Type);
                intent.putExtra("hide",1);
                startActivityForResult(intent, 1000);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_add:
                Intent intent1 = new Intent();
                String time = tv_time.getText().toString();
                time = time.replace(":","");
                intent1.putExtra("type",type);
                intent1.putExtra("time",time);
                intent1.putExtra("repeat",Type);
                intent1.putExtra("state",cb_1.isChecked());
                setResult(6,intent1);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == -1) {
            Type = data.getIntExtra("type", -1);
            if (Type == 0) {
                tv_days.setText("重复一次");
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

    public void showType() {
        List<String> typeList = new ArrayList<>();
        typeList.add("吃药提醒");
        typeList.add("喝水提醒");
        typeList.add("运动提醒");
        typeList.add("其他");


        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (typeList.size() != 0) {
                    tv_type.setText(typeList.get(options1));
                    type = options1;
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(typeList);
        pvOptions.show();
    }


    private void initPickerView() {
        boolean[] type = {false, false, false, true, true, false};
        sdf = new SimpleDateFormat("HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));

            }
        }).setType(type).build();
    }

}