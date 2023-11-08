package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.reform.adapter.ProjectListAdapter;
import net.leelink.healthangelos.reform.bean.ProductItemBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectAcceptanceActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private TextView tv_state,tv_civil,tv_organ,tv_name,tv_id_number,tv_content,tv_address,tv_telephone,tv_sign_time;
    SharedPreferences sp;
    private RecyclerView project_list;
    private ImageView img_sign1,img_sign2;
    List<ProductItemBean> list;
    private String applyId;
    private ProjectListAdapter projectListAdapter;
    private static final int REQUEST_SIGN = 101;
    String signImg = "";
    private Button btn_submit;
    private Boolean aBoolean;
    private JSONObject householdAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_acceptance);
        init();
        context = this;
        createProgressBar(context);
        initData();
    }

    public void init(){
        sp = getSharedPreferences("sp", 0);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
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
        project_list = findViewById(R.id.project_list);
        tv_content = findViewById(R.id.tv_content);
        img_sign1 = findViewById(R.id.img_sign1);
        img_sign2 = findViewById(R.id.img_sign2);
        img_sign2.setOnClickListener(this);
        tv_sign_time  = findViewById(R.id.tv_sign_time);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_address = findViewById(R.id.tv_address);
        tv_telephone = findViewById(R.id.tv_telephone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_state:
                Intent intent = new Intent(context,NeoReformProgressActivity.class);
                startActivity(intent);
                break;
            case R.id.img_sign2:
                Intent intent1 = new Intent(context, SignatureActivity.class);
                startActivityForResult(intent1, REQUEST_SIGN);
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUEST_SIGN) {
                String path = data.getStringExtra("img");
                Glide.with(context).load(path).into(img_sign2);
                upload(new File(path), REQUEST_SIGN);
            }
        }
    }

    public void initData() {
        showProgressBar();
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
                                Glide.with(context).load(Urls.getInstance().IMG_URL+json.getString("secondUserSign")).into(img_sign1);
                                tv_telephone.setText(json.getString("telephone"));
                                tv_sign_time.setText(json.getString("secondUserSignTime"));
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


    public void initList() {
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
                                projectListAdapter = new ProjectListAdapter(list,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                project_list.setAdapter(projectListAdapter);
                                project_list.setLayoutManager(layoutManager);

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

    public void submit(){
        if(aBoolean==null){
            Toast.makeText(context, "请确认是否同意方案", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressBar();
        JSONObject json = new JSONObject();
        try {
            json.put("applyId",applyId);
            json.put("confirm",aBoolean);
            json.put("signImg",signImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d( "submit: ",json.toString());
        OkGo.<String>post(Urls.getInstance().CIVILL_APPLY_DONE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("确认施工完成", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
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


    public void upload(File file, int type) {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().PHOTO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("multipartFile", file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取图片地址", json.toString());
                            if (json.getInt("status") == 200) {
                                switch (type) {
                                    case REQUEST_SIGN:
                                        signImg = json.getString("data");
                                        break;
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
                        stopProgressBar();
                    }
                });

    }


}
