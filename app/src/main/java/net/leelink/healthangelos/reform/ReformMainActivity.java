package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ReformMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    Context context;
    private ImageView img_apply,img_conirm,img_implement,img_accpet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFontSize();
        setContentView(R.layout.activity_reform_main);
        init();
        context = this;
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_apply = findViewById(R.id.img_apply);
        img_apply.setOnClickListener(this);
        img_conirm = findViewById(R.id.img_conirm);
        img_conirm.setOnClickListener(this);
        img_implement = findViewById(R.id.img_implement);
        img_implement.setOnClickListener(this);
        img_accpet = findViewById(R.id.img_accpet);
        img_accpet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_apply:        //项目申请
                Intent intent = new Intent(context,ReformApplyActivity.class);
//                Intent intent = new Intent(context,ReformProgressActivity.class);
                startActivity(intent);
                break;
            case R.id.img_conirm:   //方案确认
                Intent intent1= new Intent(context,ProjectConfirmActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_implement:    //改造实施
                Intent intent2 = new Intent(context,ImplementActivity.class);
                startActivity(intent2);
                break;
            case R.id.img_accpet:       //验收
                Intent intent3 = new Intent(context,ProjectAcceptanceActivity.class);
                startActivity(intent3);
                break;
        }
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人中心", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                Acache.get(context).put("userBean",json);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
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
