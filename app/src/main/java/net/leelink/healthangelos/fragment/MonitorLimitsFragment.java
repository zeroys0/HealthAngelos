package net.leelink.healthangelos.fragment;

import android.app.Activity;
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
import net.leelink.healthangelos.activity.LimitListActivity;
import net.leelink.healthangelos.adapter.MonitorLimitsAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonitorLimitsFragment extends BaseFragment implements OnOrderListener {

    private RecyclerView monitor_limits_list;
    private MonitorLimitsAdapter monitorLimitsAdapter;
    Context context;
    private List<LimitBean> list = new ArrayList<>();
    public static final int EDIT_LIMIT = 1;
    public static final int ADD_LIMIT = 0;
    public static final int CHECK_LIMIT = 2;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_limits, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initData();
        return view;
    }

    public void init(View view){
        monitor_limits_list = view.findViewById(R.id.monitor_limits_list);

    }

    public void initData(){
        OkGo.<String>get(Urls.ELECTRADDRESS)
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
                            Log.d("获取监控范围", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<LimitBean> limitBeans = gson.fromJson(jsonArray.toString(),new TypeToken<List<LimitBean>>(){}.getType());
                                list.addAll(limitBeans);
                                monitorLimitsAdapter = new MonitorLimitsAdapter(list,context, MonitorLimitsFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                monitor_limits_list.setLayoutManager(layoutManager);
                                monitor_limits_list.setAdapter(monitorLimitsAdapter);

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
        View popView = getLayoutInflater().inflate(R.layout.view_rail_scope, null);
        LinearLayout ll_edit = (LinearLayout) popView.findViewById(R.id.ll_edit);
        LinearLayout ll_delete = (LinearLayout) popView.findViewById(R.id.ll_delete);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  编辑监控范围
                pop.dismiss();
             //   editScope(map);
                edit(position);
            }
        });
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除监控范围
                pop.dismiss();
             //   scopeInUse(Id);
                delete(position);
            }
        });

        pop.showAsDropDown(view);
    }

    /**
     *
     *编辑范围
     */
    public void edit(int position) {
        Intent intent = new Intent(context, ActivitySetMapPoint.class);
        intent.putExtra("bean",list.get(position));
        intent.putExtra("type",EDIT_LIMIT);
        startActivity(intent);
    }


    /**
     *
     *删除范围
     * @param position
     */
    public void delete(final int position) {

        OkGo.<String>delete(Urls.ELECTRADDRESS+"/"+list.get(position).getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除范围", json.toString());
                            if (json.getInt("status") == 200) {
                              list.remove(position);
                              monitorLimitsAdapter.notifyDataSetChanged();

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
