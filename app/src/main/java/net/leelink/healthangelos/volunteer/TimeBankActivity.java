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
import net.leelink.healthangelos.util.Utils;

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
    TextView tv_mission_type,tv_time,tv_ex_time,tv_total_count,tv_total_time,tv_cost_time,tv_exchange_count,tv_type;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    RecyclerView mission_list;
    MissionListAdapter missionListAdapter;
    int page = 1;
    boolean hasNextPage = false;
    Context context;
    List<VolBean> list = new ArrayList<>();
    String myDate = "";
    int missionType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_bank);
        init();
        context = this;
        createProgressBar(context);
        initData();
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
        tv_time.setText(Utils.getYear()+"-"+Utils.getMonth());
        mission_list = findViewById(R.id.mission_list);
        tv_cost_time = findViewById(R.id.tv_cost_time);
        tv_ex_time = findViewById(R.id.tv_ex_time);
        tv_total_count = findViewById(R.id.tv_total_count);
        tv_total_time = findViewById(R.id.tv_total_time);
        tv_exchange_count = findViewById(R.id.tv_exchange_count);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setOnClickListener(this);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().VOL_INFOS_COUNT)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询志愿者时间信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_total_count.setText(json.getString("serviceNum"));
                                tv_exchange_count.setText(json.getString("conversionNum"));
                                tv_total_time.setText(json.getString("cumulativeTime"));
                                tv_cost_time.setText(json.getString("conversionTime"));

                            } else if(json.getInt("status") == 201) {

                            }
                            else if (json.getInt("status") == 505) {
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
//                                json =  json.getJSONObject("data");
//                                tv_ex_time.setText(json.getString("workTime"));
//                                tv_total_count.setText(json.getString("serviceNum"));
//                                tv_total_time.setText(json.getString("cumulativeTime"));
                                JSONArray jsonArray = json.getJSONArray("data");
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
            case R.id.tv_type:      //任务类型
                showpopu1();
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
        LinearLayout ll_all_type = popView.findViewById(R.id.ll_all_type);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        ll_all_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全部
                tv_mission_type.setText("全部任务");
                missionType = 0;
                initList();
                pop.dismiss();
            }
        });

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
    private int types = 0;
    public void showpopu1(){
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = (LinearLayout) popView.findViewById(R.id.ll_create_plan);
        LinearLayout ll_create_scope = (LinearLayout) popView.findViewById(R.id.ll_create_scope);
        LinearLayout ll_all_type = popView.findViewById(R.id.ll_all_type);
        TextView tv_all = popView.findViewById(R.id.tv_all);
        TextView tv_party = popView.findViewById(R.id.tv_party);
        TextView tv_person = popView.findViewById(R.id.tv_person);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        tv_party.setText("完成任务");
        tv_person.setText("兑换任务");
        tv_all.setText("总览任务");
        ll_all_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  全部
                tv_type.setText("总览任务");
                types =0;
                initType();
                pop.dismiss();
            }
        });
        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_type.setText("完成任务");
                types =1;
                initType();
                pop.dismiss();
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_type.setText("兑换任务");
                types = 2;
                initType();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_type);
    }

    public void initType(){

        showProgressBar();
        OkGo.<String>get(Urls.getInstance().VOL_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("types",types)
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
//                                json =  json.getJSONObject("data");
//                                tv_ex_time.setText(json.getString("workTime"));
//                                tv_total_count.setText(json.getString("serviceNum"));
//                                tv_total_time.setText(json.getString("cumulativeTime"));
                                JSONArray jsonArray = json.getJSONArray("data");
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


}
