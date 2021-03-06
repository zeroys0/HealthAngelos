package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MissionListAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.VolBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeBankActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    TextView tv_mission_type,tv_time,tv_ex_time,tv_num,tv_order_count;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    RecyclerView mission_list;
    MissionListAdapter missionListAdapter;
    int page = 1;
    boolean hasNextPage = false;
    Context context;
    List<VolBean> list = new ArrayList<>();
    String myDate = "";
    int missionType = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_bank);
        init();
        context = this;
        createProgressBar(context);
        initList();
        initPickerView();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_mission_type = findViewById(R.id.tv_mission_type);
        tv_mission_type.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        mission_list = findViewById(R.id.mission_list);
        tv_ex_time = findViewById(R.id.tv_ex_time);
        tv_num = findViewById(R.id.tv_num);
        tv_order_count = findViewById(R.id.tv_order_count);

    }

    public void initList(){

       showProgressBar();
        OkGo.<String>get(Urls.getInstance().VOL_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("type",missionType)
                .params("date",myDate)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询时间银行个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json =  json.getJSONObject("data");
                                tv_ex_time.setText(json.getString("workTime"));
                                tv_num.setText(json.getString("serviceNum"));
                                tv_order_count.setText(json.getString("cumulativeTime"));
                                JSONArray jsonArray = json.getJSONArray("serviceList");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolBean>>() {}.getType());
                                missionListAdapter = new MissionListAdapter(list,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                mission_list.setLayoutManager(layoutManager);
                                mission_list.setAdapter(missionListAdapter);
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
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
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_mission_type:

                showpopu();
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
        }
    }

    private void initPickerView(){
        sdf = new SimpleDateFormat("yyyy-MM");
        boolean[] type = {true, true, false, false, false, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
                myDate = sdf.format(date);
                initList();
            }
        }).setType(type).build();
    }

    public void showpopu(){
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = (LinearLayout) popView.findViewById(R.id.ll_create_plan);
        LinearLayout ll_create_scope = (LinearLayout) popView.findViewById(R.id.ll_create_scope);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_mission_type.setText("团队任务");
                missionType =2;
                initList();
                pop.dismiss();
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_mission_type.setText("个人任务");
                missionType = 1;
                initList();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_mission_type);
    }
}
