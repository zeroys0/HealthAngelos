package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SubmitSubsidyActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_sex,rl_back,rl_birth,rl_nation,rl_estimate_date,rl_married;
    EditText ed_name,ed_card,ed_house_register,ed_phone,ed_address,ed_married,ed_relation,ed_contact_name,ed_contact_phone,ed_total_income,ed_month_income,ed_m_income,ed_other_income,ed_estimate_address,ed_estimate_phone;
    Button btn_confirm;
    TextView tv_sex,tv_birth,tv_nation,tv_estimate_date,tv_married;
    Context context;
    int sex,nature,relationType,maritalStatus ;
    private List<String> list_sex = new ArrayList<>();
    private List<String> list_marry = new ArrayList<>();
    private TimePickerView pvTime,pvTime1;
    private SimpleDateFormat sdf,sdf1;
    private List<String> list_nation = new ArrayList<>();
    RadioGroup rg_1,rg_2;
    RadioButton rb_1,rb_2,rb_3,rb_4,rb_5,rb_6,rb_7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_submit_subsidy);
        context = this;
        createProgressBar(this);
        init();
        initPickerView();
        initPickerView2();
    }

    public void init(){

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_name = findViewById(R.id.ed_name);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        rl_sex = findViewById(R.id.rl_sex);
        rl_sex.setOnClickListener(this);
        tv_sex = findViewById(R.id.tv_sex);
        rl_birth = findViewById(R.id.rl_birth);
        rl_birth.setOnClickListener(this);
        tv_birth = findViewById(R.id.tv_birth);
        rl_nation = findViewById(R.id.rl_nation);
        rl_nation.setOnClickListener(this);
        tv_nation = findViewById(R.id.tv_nation);
        ed_card = findViewById(R.id.ed_card);
        ed_house_register = findViewById(R.id.ed_house_register);
        ed_phone = findViewById(R.id.ed_phone);
        ed_address = findViewById(R.id.ed_address);
        rl_married = findViewById(R.id.rl_married);
        rl_married.setOnClickListener(this);
        tv_married = findViewById(R.id.tv_married);
        ed_relation = findViewById(R.id.ed_relation);
        ed_contact_name = findViewById(R.id.ed_contact_name);
        ed_contact_phone = findViewById(R.id.ed_contact_phone);
        ed_total_income = findViewById(R.id.ed_total_income);
        ed_month_income = findViewById(R.id.ed_month_income);
        ed_m_income = findViewById(R.id.ed_m_income);
        ed_other_income = findViewById(R.id.ed_other_income);
        ed_estimate_address = findViewById(R.id.ed_estimate_address);
        ed_estimate_phone = findViewById(R.id.ed_estimate_phone);
        rl_estimate_date = findViewById(R.id.rl_estimate_date);
        rl_estimate_date.setOnClickListener(this);
        tv_estimate_date = findViewById(R.id.tv_estimate_date);

        rg_1 = findViewById(R.id.rg_1);
        rg_2 = findViewById(R.id.rg_2);
        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rb_4 = findViewById(R.id.rb_4);
        rb_5 = findViewById(R.id.rb_5);
        rb_6 = findViewById(R.id.rb_6);
        rb_7 = findViewById(R.id.rb_7);
        rg_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_1:
                        if(rb_1.isChecked()) {
                            relationType = 1;
                            rg_2.clearCheck();
                        }
                        break;
                    case R.id.rb_2:
                        if(rb_2.isChecked()) {
                            relationType = 2;
                            rg_2.clearCheck();
                        }
                        break;
                    case R.id.rb_3:
                        if(rb_3.isChecked()) {
                            relationType = 3;
                            rg_2.clearCheck();
                        }
                        break;
                    case R.id.rb_4:
                        if(rb_4.isChecked()) {
                            relationType = 4;
                            rg_2.clearCheck();
                        }
                        break;

                }
            }
        });
        rg_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_5:
                        if(rb_5.isChecked()) {
                            relationType = 5;
                            rg_1.clearCheck();
                        }
                        break;
                    case R.id.rb_6:
                        if(rb_6.isChecked()) {
                            relationType = 6;
                            rg_1.clearCheck();
                        }
                        break;
                    case R.id.rb_7:
                        if(rb_7.isChecked()) {
                            relationType = 7;
                            rg_1.clearCheck();
                        }
                        break;
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
            case R.id.btn_confirm:  //提交
                check();
                break;
            case R.id.rl_sex:   //选择性别
                showSex();
                break;
            case R.id.rl_birth: //出生日期
                pvTime.show();
                break;
            case R.id.rl_nation:    //民族
                showNation();
                break;
            case R.id.rl_estimate_date:     //评估日期
                pvTime1.show();
                break;
            case R.id.rl_married:       //选择婚姻情况
                showMarry();
                break;

        }
    }

    public void check(){
        if(ed_name.getText().toString().equals("")){
            Toast.makeText(context, "请填写姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_sex.getText().toString().trim().equals("")){
            Toast.makeText(context, "请选择您的性别", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_birth.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写出生年月日", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_nation.getText().toString().trim().equals("")){
            Toast.makeText(context, "请选择民族", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_card.getText().toString().trim().equals("")){
            Toast.makeText(context, "填写身份证号", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_house_register.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写户籍地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_phone.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_address.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写您的地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_married.getText().toString().trim().equals("")) {
            Toast.makeText(context, "请填写您的婚姻状况", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_relation.getText().toString().trim().equals("")) {
            Toast.makeText(context, "请填写联系人关系", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_contact_name.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写联系人姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_contact_phone.getText().toString().trim().equals("")) {
            Toast.makeText(context, "请填写联系人电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_total_income.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写总收入", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_month_income.getText().toString().trim().equals("")) {
            Toast.makeText(context, "请填写月收入", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_m_income.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填配偶收入", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_other_income.getText().toString().trim().equals("")){
            Toast.makeText(context, "其他人收入", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_estimate_address.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写上门评估地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_estimate_phone.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写上门评估联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_estimate_date.getText().toString().trim().equals("")){
            Toast.makeText(context, "请选择评估日期", Toast.LENGTH_SHORT).show();
            return;
        }

        submit();
    }

    public void submit(){
        JSONObject jsonObject=  new JSONObject();
        try {
            jsonObject.put("address",ed_address.getText().toString().trim());
            jsonObject.put("appointTime",tv_estimate_date.getText().toString().trim()+":00");
            jsonObject.put("birthday",tv_birth.getText().toString().trim());
            jsonObject.put("deliveryPhone",ed_estimate_phone.getText().toString().trim());
            jsonObject.put("domicile",ed_house_register.getText().toString().trim());
            jsonObject.put("elderlyId", MyApplication.userInfo.getOlderlyId());
            jsonObject.put("idNumber",ed_card.getText().toString().trim());
            jsonObject.put("maritalStatus",maritalStatus);
            jsonObject.put("monSale",ed_month_income.getText().toString().trim());
            jsonObject.put("name",ed_name.getText().toString().trim());
            jsonObject.put("nation",tv_nation.getText().toString().trim());
            jsonObject.put("otherSale",ed_other_income.getText().toString().trim());
            jsonObject.put("receivingAddress",ed_estimate_address.getText().toString().trim());
            jsonObject.put("relation",ed_relation.getText().toString().trim());
            jsonObject.put("relationName",ed_contact_name.getText().toString().trim());
            jsonObject.put("relationPhone",ed_contact_phone.getText().toString().trim());
            jsonObject.put("relationType",relationType);
            jsonObject.put("sex",sex);
            jsonObject.put("spouseSale",ed_m_income.getText().toString().trim());
            jsonObject.put("telephone",ed_phone.getText().toString().trim());
            jsonObject.put("totalSale",ed_total_income.getText().toString().trim());
            showProgressBar();
            OkGo.<String>post(Urls.getInstance().EVALUTION)
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
                                Log.d("申请高龄补贴", json.toString());
                                if (json.getInt("status") == 200) {

                                    Toast.makeText(context, "申请成功", Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (json.getInt("status") == 505) {
                                    reLogin(context);
                                }  else {
                                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //性别选择器
    public void showSex() {
        list_sex.clear();
        list_sex.add("男");
        list_sex.add("女");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_sex.setText(list_sex.get(options1));
                if (list_sex.get(options1).equals("男")) {
                    sex = 0;
                } else if (list_sex.get(options1).equals("女")) {
                    sex = 1;
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_sex);
        pvOptions.show();
    }

    public void showMarry(){
        list_marry.clear();
        list_marry.add("未婚");
        list_marry.add("已婚");
        list_marry.add("离婚");
        list_marry.add("丧偶");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_married.setText(list_marry.get(options1));
                maritalStatus = options1;
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_marry);
        pvOptions.show();
    }

    //民族选择
    public void showNation() {
        list_nation = Arrays.asList(getResources().getStringArray(R.array.nation_list));
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_nation.setText(list_nation.get(options1));
                nature = options1 + 1;
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();

        pvOptions.setPicker(list_nation);
        pvOptions.show();
    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_birth.setText(sdf.format(date));

            }
        }).build();

    }
    private void initPickerView2(){
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean[] type = {true, true, true, true, true, false};
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_estimate_date.setText(sdf1.format(date));

            }
        }).setType(type).build();
    }
}
