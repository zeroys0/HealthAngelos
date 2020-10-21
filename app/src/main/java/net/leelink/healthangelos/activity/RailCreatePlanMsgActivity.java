package net.leelink.healthangelos.activity;

/**
 * 新增任务计划-报警方式
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pattonsoft.pattonutil1_0.util.Mytoast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.AlarmWayManager;


public class RailCreatePlanMsgActivity extends Activity implements View.OnClickListener {
RelativeLayout rl_back;


    CheckBox cb_1,cb_2;
    EditText ed_phone,ed_email;
    int AlarmWay;
    String cellphoneNumber;
    String emailAddress;
    Context mContext;
    TextView tv_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_create_plan_msg);
        mContext = this;
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);

        ed_phone = findViewById(R.id.ed_phone);
        ed_email = findViewById(R.id.ed_email);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }



    void initData() {

        ed_email.setVisibility(View.GONE);
        ed_phone.setVisibility(View.GONE);
        cb_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed_phone.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                ed_phone.setText("");
            }
        });
        cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ed_email.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                ed_email.setText("");

            }
        });

        AlarmWay = getIntent().getIntExtra("AlarmWay", 0);
        cellphoneNumber = getIntent().getStringExtra("cellphoneNumber");
        emailAddress = getIntent().getStringExtra("emailAddress");
        //显示
        if (AlarmWayManager.ifMessageHave(AlarmWay)) cb_1.setChecked(true);
        if (AlarmWayManager.ifEmailHave(AlarmWay)) cb_2.setChecked(true);
        ed_phone.setText(cellphoneNumber);
        ed_email.setText(emailAddress);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                AlarmWay = 0;
                if (cb_1.isChecked()) AlarmWay += 1;
                if (cb_2.isChecked()) AlarmWay += 2;
                cellphoneNumber = ed_phone.getText().toString().trim();
                emailAddress = ed_email.getText().toString().trim();
                if (cb_1.isChecked() && cellphoneNumber.length() < 1) {
                    Mytoast.show(mContext, "请输入手机号");
                    return;
                }
                if (cb_2.isChecked() && emailAddress.length() < 1) {
                    Mytoast.show(mContext, "请输入邮箱");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("AlarmWay", AlarmWay);
                intent.putExtra("cellphoneNumber", cellphoneNumber);
                intent.putExtra("emailAddress", emailAddress);
                setResult(-1, intent);
                finish();
                break;
        }
    }
}
