package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.pattonsoft.pattonutil1_0.util.Mytoast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelContentAddActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    TextView tv_name,tv_warn,text_title,btn_clear;
    EditText et_input;
    Button btn_go;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_content_add);
        context = this;
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        et_input = findViewById(R.id.et_input);
        btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        text_title = findViewById(R.id.text_title);
        if (getIntent().getIntExtra("type", 0) == 1) {
            text_title.setText("编辑模板内容");
            et_input.setText(getIntent().getStringExtra("content"));

        } else {
            text_title.setText("新增模板内容");
        }
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("typeName"));
        tv_warn = findViewById(R.id.tv_warn);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = et_input.getText().toString().trim();
                tv_warn.setText((150 - content.length()) + "/150");
            }
        });
        btn_go = findViewById(R.id.btn_go);
        btn_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_go:
                String content = et_input.getText().toString().trim();
                if (content.length() == 0) {
                    Mytoast.show(context, "请输入模版内容");
                    return;
                }
                if (getIntent().getIntExtra("type", 0) == 0) {
                    add();
                } else {
                    edit();
                }
                break;
            case R.id.btn_clear:
                et_input.setText("");
                break;

        }
    }

    public void add(){
        JSONObject json = new JSONObject();
        try {
            json.put("content",et_input.getText().toString().trim());
            json.put("templateId",getIntent().getIntExtra("templateId",0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit: ", json.toString());
        OkGo.<String>post(Urls.VOICECONTENT)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("添加语音播报内容", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void edit(){
        JSONObject json = new JSONObject();
        try {
            json.put("content",et_input.getText().toString().trim());
            json.put("templateId",getIntent().getIntExtra("templateId",0));
            json.put("id",getIntent().getIntExtra("id",0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit: ", json.toString());
        OkGo.<String>put(Urls.VOICECONTENT)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改语音播报内容", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
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
