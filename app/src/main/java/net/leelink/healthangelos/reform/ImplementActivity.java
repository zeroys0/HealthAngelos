package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.reform.adapter.ProjectAdapter;
import net.leelink.healthangelos.reform.bean.ProductItemBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImplementActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back;
    private RecyclerView project_list;
    private Context context;
    private ProjectAdapter projectAdapter;
    private TextView tv_civil,tv_organ,tv_name,tv_id_number,tv_total_price,tv_address;
    SharedPreferences sp;
    private String applyId;
    private JSONObject householdAddress;
    List<ProductItemBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implement);
        init();
        context = this;
        createProgressBar(context);
        initData();
    }

    public void init(){
        sp = getSharedPreferences("sp", 0);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        project_list = findViewById(R.id.project_list);
        tv_civil = findViewById(R.id.tv_civil);
        tv_civil.setText(sp.getString("committeeName", "社区名称"));
        tv_organ = findViewById(R.id.tv_organ);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(sp.getString("elderlyName", ""));
        tv_id_number = findViewById(R.id.tv_id_number);
        String id = sp.getString("idNumber", "");
        if (id != null && id.length() == 18) {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            sb.replace(2, 16, "**************");
            tv_id_number.setText(sb.toString());
        }
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_address = findViewById(R.id.tv_address);
    }

    public void initList(){
        OkGo.<String>get(Urls.getInstance().CIVILL_PRODUCT_LIST + "/" + applyId)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看改造方案项目列表", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<ProductItemBean>>() {
                                }.getType());

                                projectAdapter = new ProjectAdapter(context,ImplementActivity.this,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                project_list.setAdapter(projectAdapter);
                                project_list.setLayoutManager(layoutManager);
                                if (list.size() > 0) {
                                    calculatePrice();
                                } else {
                                    tv_total_price.setText("￥0");
                                }

                            } else if (json.getInt("status") == 505) {

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

                    }
                });



    }

    public void initData() {
        //查询最新一条适老化改造申请
        OkGo.<String>get(Urls.getInstance().CIVILL_LATEST)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询最新的改造申请", json.toString());
                            if (json.getInt("status") == 200) {
                                if (!json.has("data")) {
                                    return;
                                }
                                json = json.getJSONObject("data");
                                applyId = json.getString("applyId");
                                initList();
                                int state = json.getInt("state");
//                                if(state!=5 && state !=6 &&state !=7){
//                                    img_master.setClickable(false);
//                                    img_personal.setClickable(false);
//                                    img_house_certificate.setClickable(false);
//                                    img_five.setClickable(false);
//                                    img_low.setClickable(false);
//                                    img_sign1.setClickable(false);
//                                    rl_home_address.setClickable(false);
//                                    rl_house_type.setClickable(false);
//                                    rl_identity.setClickable(false);
//                                    ed_content.setClickable(false);
//                                    ed_telephone.setClickable(false);
//                                    ed_contact_person.setClickable(false);
//                                    ed_family_size.setClickable(false);
//                                    btn_submit.setClickable(false);
//                                    btn_submit.setBackground(getResources().getDrawable(R.drawable.bg_gray_radius));
//                                }

                                householdAddress = new JSONObject(json.getString("currentAddress"));
                                tv_address.setText(householdAddress.getString("fullAddress"));

                            } else if (json.getInt("status") == 505) {

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:  
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        Intent intent = new Intent(context,ProjectDetailActivity.class);
        intent.putExtra("productId",list.get(position).getId());
        startActivity(intent);
    }


    public void calculatePrice() {
        Integer price = 0;
        for (ProductItemBean productItemBean : list) {
            Integer i = Integer.parseInt(productItemBean.getAmount());
            price = price + i;
        }
        tv_total_price.setText("¥"+price);

    }

}
