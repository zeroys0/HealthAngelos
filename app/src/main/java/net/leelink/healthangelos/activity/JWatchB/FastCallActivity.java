package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class FastCallActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_phone;
    private Button btn_save,btn_fast_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_call);
        context = this;
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_phone = findViewById(R.id.ed_phone);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_fast_call = findViewById(R.id.btn_fast_call);
        btn_fast_call.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_save: //设置快捷拨号
                save();
                break;
            case R.id.btn_fast_call:    //快捷呼叫
                fastCall();
                break;

        }
    }

    public void save() {
        String uid = getSharedPreferences("sp",0).getString("uid","");
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().GUARDER)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("guarder",ed_phone.getText().toString().trim())
                .params("uId", uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("保存电话号码", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置快捷拨号成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }

    public void fastCall(){
        String uid = getSharedPreferences("sp",0).getString("uid","");
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().LISTEN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("uId", uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("快捷呼叫", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "拨号成功", Toast.LENGTH_SHORT).show();
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
                        LoadDialog.stop();
                    }
                });
    }
}