package net.leelink.healthangelos.activity.Badge;

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

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BadgeAffectionNumberActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_number_1, ed_number_2, ed_number_3;
    private Button btn_save;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_affection_number);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_number_1 = findViewById(R.id.ed_number_1);
        ed_number_2 = findViewById(R.id.ed_number_2);
        ed_number_3 = findViewById(R.id.ed_number_3);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BADGE_FAMILY + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据imei获取亲情号码", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = json.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    ed_number_1.setText(jsonArray.getJSONObject(0).getString("sosPhone"));
                                }
                                if (jsonArray.length() > 1) {
                                    ed_number_2.setText(jsonArray.getJSONObject(1).getString("sosPhone"));
                                }
                                if (jsonArray.length() > 2) {
                                    ed_number_3.setText(jsonArray.getJSONObject(2).getString("sosPhone"));
                                }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_save:
               // upload();
                addPhone();

                break;
        }
    }

    public void upload() {

    }

    public void addPhone() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BADGE_FAMILY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", getIntent().getStringExtra("imei"))
                .params("sosPhone1", ed_number_1.getText().toString().trim())
                .params("sosPhone2", ed_number_2.getText().toString().trim())
                .params("sosPhone3", ed_number_3.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("新增腕表号码", json.toString());
                            if (json.getInt("status") == 200) {
                                synchronization();
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

    public void editPhone(String phone, int id) {
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().BADGE_FAMILY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("id", id)
                .params("sosName", "name")
                .params("sosPhone", phone)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改腕表号码", json.toString());
                            if (json.getInt("status") == 200) {

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

    public void deletePhone(int id) {
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().BADGE_FAMILY + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除腕表号码", json.toString());
                            if (json.getInt("status") == 200) {

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

    public void synchronization(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BADGE_FAMILY+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("同步腕表亲情号码", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context,"同步成功", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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