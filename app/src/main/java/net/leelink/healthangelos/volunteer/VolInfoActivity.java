package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class VolInfoActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_organ,ed_name,tv_sex,tv_nation,ed_card,ed_phone,tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_info);
        context = this;
        init();
        initData();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_organ = findViewById(R.id.tv_organ);
        ed_name = findViewById(R.id.ed_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_nation = findViewById(R.id.tv_nation);
        ed_card = findViewById(R.id.ed_card);
        ed_phone = findViewById(R.id.ed_phone);
        tv_address = findViewById(R.id.tv_address);

    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().MINE_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者个人信息", json.toString());
                            Acache.get(context).put("is_vol", "true");
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_organ.setText(json.getString("organName"));
                                ed_name.setText(json.getString("volName"));
                                if(json.getInt("volSex")==0) {
                                    tv_sex.setText("男");
                                }else {
                                    tv_sex.setText("女");
                                }
                                tv_nation.setText(json.getString("volNation"));
                                ed_phone.setText(json.getString("volTelephone"));
                                ed_card.setText(json.getString("volCard"));
                                String address= json.getString("volAddress");
                                try {
                                    JSONObject jsonObject = new JSONObject(address);
                                    tv_address.setText(jsonObject.getString("fullAddress"));
                                } catch (JSONException e) {
                                    tv_address.setText(json.getString("volAddress"));
                                    throw new RuntimeException(e);
                                }


                            } else if (json.getInt("status") == 201) {
                                Acache.get(context).remove("volunteer");

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
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}