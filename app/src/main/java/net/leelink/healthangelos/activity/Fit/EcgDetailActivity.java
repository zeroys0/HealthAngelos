package net.leelink.healthangelos.activity.Fit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.HeartView;

import org.json.JSONException;
import org.json.JSONObject;

public class EcgDetailActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private Button btn_start;
    private boolean start = false;
    private HeartView heartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detail);
        context = this;
        init();
        createProgressBar(context);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        heartView = findViewById(R.id.hv);
        heartView.setHeartSpeed(2f);
        heartView.setShowSeconds(5f);
        heartView.setBaseLine(200);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_start:

                if(!start){
                    showProgressBar();
                    heartView.clear();
                    getData();
                    start = true;
                } else {
                    heartView.stopNestedScroll();
                    start = false;
                }
                break;
        }
    }

    public void getData(){
        int id = getIntent().getIntExtra("id",0);
        OkGo.<String>get(Urls.getInstance().LISTHISTORYECGBYID+"/"+id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取心电图数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                String data = json.getString("items");
                                data =  data.substring(1,data.length()-1);
                                String[] s = data.split(",");
                                for(int i =0;i<s.length;i++){
                                    if(start) {
                                        heartView.offer(Integer.parseInt(s[i]));
                                    }else {
                                        break;
                                    }
                                }


                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}