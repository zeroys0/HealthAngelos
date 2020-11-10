package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;

public class CreatePartyActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_address;
    TextView tv_address,tv_service_type,tv_leader;
    Button btn_submit;
    EditText ed_title,ed_phone;
    Context context;
    String local_address;
    String local_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);
        init();
        context = this;
        createProgressBar(context);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_address = findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        tv_address = findViewById(R.id.tv_address);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        ed_title = findViewById(R.id.ed_title);
        tv_service_type = findViewById(R.id.tv_service_type);
        ed_phone = findViewById(R.id.ed_phone);
        tv_leader = findViewById(R.id.tv_leader);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_address:
                Intent intent = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(intent,2);
                break;
            case R.id.btn_submit:
                createParty();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode ==2 ){
            tv_address.setText(data.getStringExtra("address"));
            local_address = data.getStringExtra("local_address");
            local_id = data.getStringExtra("local_id");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createParty(){
        if(ed_title.getText().toString().equals("")){
            Toast.makeText(this, "请输入团队名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_address.getText().toString().equals("")){
            Toast.makeText(this, "请选择地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_service_type.getText().toString().equals("")){
            Toast.makeText(this, "输入服务类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_leader.getText().toString().equals("")){
            Toast.makeText(this, "请填写负责人", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ed_phone.getText().toString().equals("")){
            Toast.makeText(this, "请填写电话", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("areaAddress",local_address);
            jsonObject.put("areaId",local_id);
            jsonObject.put("name",tv_leader.getText().toString());
            jsonObject.put("organId",getIntent().getStringExtra("organ_id"));
            jsonObject.put("serviceRequair",tv_service_type.getText().toString());
            jsonObject.put("teamAddress",tv_address.getText().toString());
            jsonObject.put("teamName",ed_title.getText().toString());
            jsonObject.put("telephone",ed_phone.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(Urls.VOL_TEAM)
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
                            Log.d("创建志愿者团队", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "申请成功,请等待审核", Toast.LENGTH_SHORT).show();
                                finish();
                            }  else if (json.getInt("status") == 505) {
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
}
