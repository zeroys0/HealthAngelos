package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class R60SettingAngleActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_x,ed_y,ed_z;
    private Button btn_cancel,btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60_setting_angle);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_x = findViewById(R.id.ed_x);
        ed_y = findViewById(R.id.ed_y);
        ed_z = findViewById(R.id.ed_z);
        ed_x.addTextChangedListener(new mTextWatcher(ed_x));
        ed_y.addTextChangedListener(new mTextWatcher(ed_y));
        ed_z.addTextChangedListener(new mTextWatcher(ed_z));
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    class mTextWatcher implements TextWatcher{
        EditText editText;

        public mTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                Integer high = Integer.parseInt(s.toString());
                if(high!=null && high>45){
                    editText.setText("45");
                }
            } catch (Exception e){

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                setAngle();
                break;
        }
    }

    public void setAngle(){
        if(ed_x.getText().toString().trim().equals("") || ed_y.getText().toString().trim().equals("") || ed_z.getText().toString().trim().equals("")){
            Toast.makeText(context, "请填写完整数据", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().INSTALLANGLE+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .params("x",ed_x.getText().toString().trim())
                .params("y",ed_y.getText().toString().trim())
                .params("z",ed_z.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置安装角度", json.toString());
                            if (json.getInt("status") == 200) {

                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
                                setComplete();
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

    public void setComplete(){
        Intent intent = new Intent();
        intent.putExtra("x",ed_x.getText().toString());
        intent.putExtra("y",ed_y.getText().toString());
        intent.putExtra("z",ed_z.getText().toString());
        setResult(0,intent);
        finish();
    }
}