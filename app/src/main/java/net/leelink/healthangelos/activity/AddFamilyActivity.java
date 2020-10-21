package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFamilyActivity extends BaseActivity {
    RelativeLayout rl_back;
    EditText ed_name,ed_phone;
    Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ed_name = findViewById(R.id.ed_name);
        ed_phone = findViewById(R.id.ed_phone);

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ed_name.getText().toString().equals("")) {
                    if(!ed_phone.getText().toString().equals("")) {
                        add();
                    }else {
                        Toast.makeText(AddFamilyActivity.this, "请填写联系人电话", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddFamilyActivity.this, "请填写联系人姓名", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void add(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contact",ed_name.getText().toString().trim());
            jsonObject.put("imei",MyApplication.userInfo.getJwotchImei());
            jsonObject.put("phone",ed_phone.getText().toString().trim());
            jsonObject.put("position",getIntent().getIntExtra("position",0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.RELATIVE)
            .tag(this)
            .headers("token", MyApplication.token)
            .upJson(jsonObject)
            .execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        String body = response.body();
                        JSONObject json = new JSONObject(body);
                        Log.d("设置亲人号码", json.toString());
                        if (json.getInt("status") == 200) {
                            Toast.makeText(AddFamilyActivity.this, "成功", Toast.LENGTH_LONG).show();
                            setResult(0);
                            finish();
                        } else {
                            Toast.makeText(AddFamilyActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

    }
}
