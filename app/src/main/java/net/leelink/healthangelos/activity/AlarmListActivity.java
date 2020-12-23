package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.AlarmAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.AlarmBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmListActivity extends BaseActivity implements OnOrderListener
{
    RecyclerView alarm_list;
    RelativeLayout rl_back;
    AlarmAdapter alarmAdapter;
    Context context;
    List<AlarmBean> list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        init();
        context = this;
        initList();
    }

    public void init(){
        alarm_list = findViewById(R.id.alarm_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initList(){

        OkGo.<String>get(Urls.getInstance().SERVICE)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("获取所有可以订阅的服务: ", j.toString());
                            if (j.getInt("status") == 200) {
                                Gson gson = new Gson();
                                JSONArray jsonArray = j.getJSONArray("data");
                                list= gson.fromJson(jsonArray.toString(),new TypeToken<List<AlarmBean>>(){}.getType());
                                alarmAdapter = new AlarmAdapter(list,context,AlarmListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                alarm_list.setAdapter(alarmAdapter);
                                alarm_list.setLayoutManager(layoutManager);

                            }else if(j.getInt("status") == 505){
                                reLogin(context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        Intent intent = new Intent(this,AlarmDetailActivity.class);
        intent.putExtra("price",list.get(position).getPrice());
        intent.putExtra("id",list.get(position).getId());
        startActivity(intent);
    }
}
