package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelAddActivity extends BaseActivity implements View.OnClickListener {
    Context context;
    RelativeLayout rl_back;
    EditText ed_name;
    Button btn_save;
    TextView text_title;
    ImageView im_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_add);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_name = findViewById(R.id.ed_name);
        btn_save = findViewById(R.id.btn_save);
        text_title = findViewById(R.id.text_title);
        btn_save.setOnClickListener(this);
        int type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            text_title.setText("编辑模版分类名称");
            ed_name.setText(getIntent().getStringExtra("typeName"));
        } else {
            text_title.setText("新增模版分类");

        }
        im_cancel = findViewById(R.id.im_cancel);
        im_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_save:
                String title = ed_name.getText().toString();
                if (title.length() == 0) {
                    Mytoast.show(context, "请输入模版类别名称");
                    return;
                }
                if(getIntent().getIntExtra("type", 0) == 0) {
                    add();
                }else {
                    edit();
                }
                break;
            case R.id.im_cancel:
                ed_name.setText("");
                break;
        }
    }

    public void add(){
        JSONObject json = new JSONObject();
        try {
            json.put("typeName",ed_name.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.VOICETEMPLATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("添加模板", json.toString());
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
            json.put("id",getIntent().getIntExtra("templateId",0));
            json.put("typeName",ed_name.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>put(Urls.VOICETEMPLATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改模板", json.toString());
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
