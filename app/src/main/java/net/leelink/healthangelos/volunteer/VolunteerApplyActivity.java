package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;

public class VolunteerApplyActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back, rl_organ, rl_sex, rl_nation, rl_address;
    private int organ_id;
    TextView tv_organ, tv_sex, tv_nation, tv_address, tv_reason, tv_auditing;
    EditText ed_name, ed_card, ed_phone;
    private List<String> list_sex = new ArrayList<>();
    private List<String> list_nation = new ArrayList<>();
    int sex, nature;
    Context context;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_apply);
        context = this;
        createProgressBar(this);
        init();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_organ = findViewById(R.id.rl_organ);
        rl_organ.setOnClickListener(this);
        tv_organ = findViewById(R.id.tv_organ);
        ed_name = findViewById(R.id.ed_name);
        rl_sex = findViewById(R.id.rl_sex);
        rl_sex.setOnClickListener(this);
        tv_sex = findViewById(R.id.tv_sex);
        rl_nation = findViewById(R.id.rl_nation);
        rl_nation.setOnClickListener(this);
        tv_nation = findViewById(R.id.tv_nation);
        ed_card = findViewById(R.id.ed_card);
        ed_phone = findViewById(R.id.ed_phone);
        rl_address = findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        tv_address = findViewById(R.id.tv_address);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_reason = findViewById(R.id.tv_reason);
        tv_auditing = findViewById(R.id.tv_auditing);
        JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
        if (jsonObject!=null) {
            try {
                tv_organ.setText(jsonObject.getString("organName"));
                ed_name.setText(jsonObject.getString("volName"));
                if (jsonObject.getInt("volSex") == 0) {
                    tv_sex.setText("男");
                } else {
                    tv_sex.setText("女");
                }
                tv_nation.setText(jsonObject.getString("volNation"));
                ed_card.setText(jsonObject.getString("volCard"));
                ed_phone.setText(jsonObject.getString("volTelephone"));
                try {
                    String s = jsonObject.getString("volAddress");
                    JSONObject j = new JSONObject(s);
                    tv_address.setText(j.getString("fullAddress"));
                } catch (Exception e){
                    tv_address.setText(jsonObject.getString("servAddress"));
                }
                if (jsonObject.getInt("state") == 0) {
                    tv_auditing.setVisibility(View.VISIBLE);
                    btn_submit.setVisibility(View.GONE);
                }
                if (jsonObject.getInt("state") == 2) {
                    tv_reason.setVisibility(View.VISIBLE);
                    tv_reason.setText(jsonObject.getString("cause"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            initData();
        }
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                ed_name.setText(json.getString("name"));
                                if (json.has("sex")) {
                                    sex = json.getInt("sex");
                                    switch (json.getInt("sex")) {
                                        case 0:
                                            tv_sex.setText("男");
                                            break;
                                        case 1:
                                            tv_sex.setText("女");
                                            break;
                                    }
                                }

                                tv_organ.setText(json.getString("organName"));
                                JSONObject jsonObject = json.getJSONObject("elderlyUserInfo");
                                organ_id = jsonObject.getInt("organId");
                                tv_nation.setText(jsonObject.getString("nation"));
                                ed_card.setText(jsonObject.getString("idCard"));
                                if(!jsonObject.isNull("telephone")) {
                                    ed_phone.setText(jsonObject.getString("telephone"));
                                }
                                try {
                                    String s = jsonObject.getString("address");
                                    JSONObject address = new JSONObject(s);
                                    tv_address.setText(address.getString("fullAddress"));
                                } catch (JSONException e) {

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
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_organ:
                Intent intent = new Intent(this, ChooseOrganActivity.class);
                startActivityForResult(intent, 7);
                break;
            case R.id.rl_sex:
                showSex();
                break;
            case R.id.rl_nation:
                showNation();
                break;
            case R.id.rl_address:
                Intent intent1 = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(intent1, 2);
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public void submit() {
        if (tv_organ.getText().toString().equals("")) {
            Toast.makeText(context, "请选择所属机构", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ed_name.getText().toString().equals("")) {
            Toast.makeText(context, "请填写姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_sex.getText().toString().equals("")) {
            Toast.makeText(context, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_nation.getText().toString().equals("")) {
            Toast.makeText(context, "请选择民族", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ed_card.getText().toString().equals("")) {
            Toast.makeText(context, "请填写身份证号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_address.getText().toString().equals("")) {
            Toast.makeText(context, "请选择详细地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_phone.getText().toString().equals("")) {
            Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", tv_address.getText().toString().trim());
            jsonObject.put("idCard", ed_card.getText().toString().trim());
            jsonObject.put("organId", organ_id);
            jsonObject.put("sex", sex);
            jsonObject.put("telephone", ed_phone.getText().toString().trim());
            jsonObject.put("volName", ed_name.getText().toString().trim());
            jsonObject.put("volNation", tv_nation.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        showProgressBar();
        OkGo.<String>post(Urls.getInstance().VOL_SIGN)
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
                            Log.d("申请成为志愿者", json.toString());
                            if (json.getInt("status") == 200) {
//                                JSONObject json_volunteer = Acache.get(context).getAsJSONObject("volunteer");
//                                json_volunteer.getJSONObject("data").put("state", 0);
//                                Acache.get(context).put("volunteer", json_volunteer);
                                btn_submit.setVisibility(View.INVISIBLE);
                                tv_auditing.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(context, ExamineVolunteerActivity.class);
                                startActivity(intent);
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
                        stopProgressBar();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 7) {
            organ_id = data.getIntExtra("organ", 0);
            String organ_name = data.getStringExtra("organ_name");
            tv_organ.setText(organ_name);
        }
        if (resultCode == 2) {
            tv_address.setText(data.getStringExtra("address"));
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
