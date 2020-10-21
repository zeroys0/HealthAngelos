package net.leelink.healthangelos.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ActivitySetMapPoint;
import net.leelink.healthangelos.activity.ChooseClassActivity;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.RailCreatePlanActivity;
import net.leelink.healthangelos.adapter.MonitorLimitsAdapter;
import net.leelink.healthangelos.adapter.MonitorPlanAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.FencePlanBean;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.activity.ElectFenceActivity.EDIT_PLAN;
import static net.leelink.healthangelos.fragment.MonitorLimitsFragment.CHECK_LIMIT;

public class MonitorPlanFragment extends BaseFragment implements OnOrderListener {

    private RecyclerView monitor_plan_list;
    private MonitorPlanAdapter monitorPlanAdapter;
    Context context;
    List<FencePlanBean> list = new ArrayList<>();
    @Override
    public void handleCallBack(Message msg) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_plan, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initData();
        return view;
    }

    public void init(View view){
        monitor_plan_list = view.findViewById(R.id.monitor_plan_list);
//        List<String> list = new ArrayList<>();
//        list.add("测试");
//        list.add("测试2");


    }

    public void initData(){
        OkGo.<String>get(Urls.ELECTRPLAN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",1)
                .params("pageSize",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取监控计划", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<FencePlanBean> planBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<FencePlanBean>>(){}.getType());
                                list.addAll(planBeans);
                                monitorPlanAdapter = new MonitorPlanAdapter(list, getContext(), MonitorPlanFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                monitor_plan_list.setLayoutManager(layoutManager);
                                monitor_plan_list.setAdapter(monitorPlanAdapter);

                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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
    public void onButtonClick(View view, final int position) {
        View popView = getLayoutInflater().inflate(R.layout.view_rail_plan, null);
        LinearLayout ll_info = (LinearLayout) popView.findViewById(R.id.ll_info);
        LinearLayout ll_edit = (LinearLayout) popView.findViewById(R.id.ll_edit);
        LinearLayout ll_delete = (LinearLayout) popView.findViewById(R.id.ll_delete);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());


        ll_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  监控计划详情
              //  startActivity(new Intent(mContext, ActivityShowMap.class).putExtra("data", new Gson().toJson(map)));
                Intent intent = new Intent(context, ActivitySetMapPoint.class);
                intent.putExtra("type",CHECK_LIMIT);
                intent.putExtra("data",list.get(position));
                startActivity(intent);
                pop.dismiss();
            }
        });
        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(mContext, RailCreatePlanActivity.class).putExtra("type", 1).putExtra("info", new Gson().toJson(map)));
                //  编辑监控计划
                Intent intent = new Intent(context, RailCreatePlanActivity.class);
                intent.putExtra("type",EDIT_PLAN);
                intent.putExtra("data",list.get(position));
                startActivity(intent);
                pop.dismiss();
            }
        });
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除监控计划
                pop.dismiss();
                delete(position);
            }
        });
        pop.showAsDropDown(view);
    }

    void delete(final int position){


        OkGo.<String>delete(Urls.ELECTRPLAN+"/"+list.get(position).getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除计划", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                list.remove(position);
                                monitorPlanAdapter.notifyDataSetChanged();
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
