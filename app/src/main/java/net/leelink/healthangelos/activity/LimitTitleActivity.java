package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.MainActivity;
import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class LimitTitleActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Button btn_save, btn_add_plan;
    private EditText ed_title;
    public static final boolean ADD_PLAN = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_title);
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_add_plan = findViewById(R.id.btn_add_plan);
        btn_add_plan.setOnClickListener(this);
        ed_title = findViewById(R.id.ed_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_add_plan:
                if (getIntent().getIntExtra("action", 0) == 0) {
                    save(ADD_PLAN);

                } else if(getIntent().getIntExtra("action", 0) == 1){
                    edit(ADD_PLAN);
                }
                break;
            case R.id.btn_save:
                if (getIntent().getIntExtra("action", 0) == 0) {
                    save(false);

                } else if(getIntent().getIntExtra("action", 0) == 1){
                    edit(false);
                }

                break;
        }
    }

    public void save(final boolean ADD) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("aaddress", getIntent().getStringExtra("a_address"));
            jsonObject.put("baddress", getIntent().getStringExtra("b_address"));
            jsonObject.put("alias", ed_title.getText().toString().trim());
            jsonObject.put("la1", getIntent().getDoubleExtra("start_lat", 0) + "");
            jsonObject.put("lo1", getIntent().getDoubleExtra("start_lon", 0) + "");
            jsonObject.put("la2", getIntent().getDoubleExtra("end_lat", 0) + "");
            jsonObject.put("lo2", getIntent().getDoubleExtra("end_lon", 0) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.ELECTRADDRESS)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传监控范围", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(LimitTitleActivity.this, "成功", Toast.LENGTH_LONG).show();
                                finish();
                                if (ADD) {
                                    Intent intent = new Intent(LimitTitleActivity.this, RailCreatePlanActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(LimitTitleActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void edit(final boolean ADD) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("aaddress", getIntent().getStringExtra("a_address"));
            jsonObject.put("baddress", getIntent().getStringExtra("b_address"));
            jsonObject.put("alias", ed_title.getText().toString().trim());
            jsonObject.put("la1", getIntent().getDoubleExtra("start_lat", 0) + "");
            jsonObject.put("lo1", getIntent().getDoubleExtra("start_lon", 0) + "");
            jsonObject.put("la2", getIntent().getDoubleExtra("end_lat", 0) + "");
            jsonObject.put("lo2", getIntent().getDoubleExtra("end_lon", 0) + "");
            jsonObject.put("id", getIntent().getStringExtra("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit:aaddress ",getIntent().getStringExtra("a_address") );
        Log.e( "edit:baddress ", getIntent().getStringExtra("b_address"));
        Log.e( "edit:alias ",ed_title.getText().toString().trim() );
        Log.e( "edit:la1 ",getIntent().getDoubleExtra("start_lat", 0) + "");
        Log.e( "edit:lo1 ",getIntent().getDoubleExtra("start_lon", 0) + "");
        Log.e( "edit:la2 ",getIntent().getDoubleExtra("end_lat", 0) + "" );
        Log.e( "edit:lo2 ",getIntent().getDoubleExtra("end_lon", 0) + "");
        Log.e( "edit:id ", getIntent().getStringExtra("id") );
        OkGo.<String>put(Urls.ELECTRADDRESS)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("编辑监控范围", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(LimitTitleActivity.this, "成功", Toast.LENGTH_LONG).show();
                                finish();
                                if (ADD) {
                                    Intent intent = new Intent(LimitTitleActivity.this, RailCreatePlanActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(LimitTitleActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
