package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInfoActivty extends BaseActivity {
    RelativeLayout rl_back;
    ImageView img_edit,img_head;
    Context context;
    private TextView tv_name,tv_sex,tv_age,tv_organ,tv_organize,tv_info_name,tv_info_sex,tv_nation,tv_phone,tv_card,tv_educate,tv_province,tv_city,tv_local,tv_address,tv_tall,tv_weight,tv_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_activty);
        context = this;
        createProgressBar(this);
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_edit = findViewById(R.id.img_edit);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditInfoActivity.class);
                startActivity(intent);
            }
        });
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_age = findViewById(R.id.tv_age);
        tv_organ = findViewById(R.id.tv_organ);
        tv_organize = findViewById(R.id.tv_organize);
        tv_info_name = findViewById(R.id.tv_info_name);
        tv_info_sex = findViewById(R.id.tv_info_sex);
        tv_nation = findViewById(R.id.tv_nation);
        tv_phone = findViewById(R.id.tv_phone);
        tv_card = findViewById(R.id.tv_card);
        tv_educate = findViewById(R.id.tv_educate);
        tv_province = findViewById(R.id.tv_province);
        tv_city = findViewById(R.id.tv_city);
        tv_local = findViewById(R.id.tv_local);
        tv_address = findViewById(R.id.tv_address);
        tv_tall = findViewById(R.id.tv_tall);
        tv_weight = findViewById(R.id.tv_weight);
        tv_contact = findViewById(R.id.tv_contact);

    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.INFO)
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
                                Glide.with(context).load(Urls.IMG_URL + json.getString("headImgPath")).into(img_head);
                                tv_name.setText(json.getString("name"));
                                tv_info_name.setText(json.getString("name"));
                                if (json.has("sex")) {
                                    switch (json.getInt("sex")) {
                                        case 0:
                                            tv_sex.setText("男");
                                            tv_info_sex.setText("男");
                                            break;
                                        case 1:
                                            tv_sex.setText("女");
                                            tv_info_sex.setText("女");
                                            break;
                                    }
                                }
                                tv_age.setText(json.getInt("age")+"岁");
                                tv_organ.setText(json.getString("organName"));
                                tv_organize.setText(json.getString("organName"));
                                JSONObject jsonObject = json.getJSONObject("elderlyUserInfo");
                                tv_nation.setText(jsonObject.getString("nation"));
                                tv_card.setText(jsonObject.getString("idCard"));
                                tv_phone.setText(jsonObject.getString("telephone"));
                                String[] ed = new String[]{"文盲", "半文盲", "小学", "初中", "高中", "技工学校", "中专/中技", "大专", "本科", "硕士", "博士"};
                                if (!jsonObject.getString("education").equals("null")) {
                                    tv_educate.setText(ed[jsonObject.getInt("education")-1]);
                                }
                                tv_province.setText(jsonObject.getString("provinceName"));
                                tv_city.setText(jsonObject.getString("cityName"));
                                tv_local.setText(jsonObject.getString("areaName"));
                                tv_address.setText(jsonObject.getString("address"));
                                tv_tall.setText(jsonObject.getString("height")+"cm");
                                tv_weight.setText(jsonObject.getString("weight")+"kg");
                                tv_contact.setText(jsonObject.getString("urgentPhone"));
                            }  else if (json.getInt("status") == 505) {
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
}
