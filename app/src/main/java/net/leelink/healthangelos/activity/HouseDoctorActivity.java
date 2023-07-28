package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.im.ChatActivity;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static net.leelink.healthangelos.app.MyApplication.getContext;

public class HouseDoctorActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private CircleImageView img_head;
    private TextView tv_name, tv_duties, tv_department, tv_hospital, tv_score, tv_count, tv_collect_count, tv_follow, tv_detail;
    private Context context;
    private LinearLayout ll_image_message;
    private String clientId, doctorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_doctor);
        init();
        context = this;
        createProgressBar(context);
        initData();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_duties = findViewById(R.id.tv_duties);
        tv_department = findViewById(R.id.tv_department);
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_score = findViewById(R.id.tv_score);
        tv_count = findViewById(R.id.tv_count);
        tv_collect_count = findViewById(R.id.tv_collect_count);
        ll_image_message = findViewById(R.id.ll_image_message);
        ll_image_message.setOnClickListener(this);
        tv_follow = findViewById(R.id.tv_follow);
        tv_follow.setOnClickListener(this);
        tv_detail = findViewById(R.id.tv_detail);
        tv_detail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_image_message:
                goChat();

                break;
            case R.id.tv_follow:
                if (tv_follow.getText().equals("关注")) {
                    follow();
                } else {
                    notFollow();
                }
                break;
            case R.id.tv_detail:
                Intent intent = new Intent(this, DoctorDetailInfoActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().APPLYDOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取医生信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_count.setText(json.getString("consCount"));
                                tv_collect_count.setText(json.getString("followCount"));
                                clientId = json.getString("clientId");
                                json = json.getJSONObject("careDoctorRegedit");
                                Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("imgPath")).into(img_head);
                                doctorId = json.getString("id");
                                getFollow();
                                tv_name.setText(json.getString("name"));
                                tv_duties.setText(json.getString("duties"));
                                tv_department.setText(json.getString("department"));
                                tv_hospital.setText(json.getString("hospital"));
                                tv_score.setText(json.getString("totalScore"));
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
                        Toast.makeText(context, "信息错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goChat() {
        OkGo.<String>get(Urls.getInstance().CHAT_USERINFO + "/" + clientId + "/2")
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("消息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");

                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("clientId", clientId);
                                if (json.has("img_path")) {
                                    String img_head = jsonArray.getJSONObject(0).getString("img_path");
                                    intent.putExtra("receive_head", img_head);
                                }
                                intent.putExtra("name", tv_name.getText().toString());
                                startActivity(intent);
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
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("clientId", clientId);
                        intent.putExtra("receive_head", "");
                        intent.putExtra("name", tv_name.getText().toString());
                        startActivity(intent);
                    }
                });

    }

    public void getFollow() {
        OkGo.<String>get(Urls.getInstance().FOLLOW + "/" + doctorId)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("是否关注医生", json.toString());
                            if (json.getInt("status") == 200) {
                                if (json.getBoolean("data")) {

                                    tv_follow.setText("已关注");
                                } else {
                                    tv_follow.setText("关注");
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void follow() {
        OkGo.<String>post(Urls.getInstance().FOLLOW + "/" + doctorId)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("关注医生", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已关注", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("已关注");
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void notFollow() {
        OkGo.<String>delete(Urls.getInstance().FOLLOW + "/" + doctorId)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("取消关注", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已取关", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("关注");
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
