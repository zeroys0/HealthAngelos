package net.leelink.healthangelos.activity.yasee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class YaseeBpMoreActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {

    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView data_list;

    private YaseeDataAdapter yaseeDataAdapter;
    private List<YaseeBean> list = new ArrayList<>();
    private  int  type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yasee_bp_more);
        context = this;
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        data_list = findViewById(R.id.data_list);
        type = getIntent().getIntExtra("type",0);
    }

    public void initList(){

        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().YASEE_DATE_LIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("type",getIntent().getIntExtra("type",0))
                .params("pageNum",1)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询日期数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                for(int i = 0;i<jsonArray.length();i++){
                                    list.add(new YaseeBean(jsonArray.getJSONObject(i).getString("date")));
                                }
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                yaseeDataAdapter = new YaseeDataAdapter(context,list, YaseeBpMoreActivity.this,type);
                                data_list.setAdapter(yaseeDataAdapter);
                                data_list.setLayoutManager(layoutManager);
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
                        LoadDialog.stop();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onButtonClick(View view, int position) {
        if(list.get(position).getIs_show()){
            list.get(position).setIs_show(false);
            yaseeDataAdapter.notifyDataSetChanged();
        } else {
            list.get(position).setIs_show(true);
            getDetailData(position);
        }
    }

    public void getDetailData(int position){
        String url =  "";
        if(type==1){
            url = Urls.getInstance().YASEE_BS;
        } else if (type==2){
            url = Urls.getInstance().YASEE_BP;
        } else if (type==4){
            url = Urls.getInstance().YASEE_ACID;
        }
        LoadDialog.start(context);
        OkGo.<String>get(url)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .params("date",list.get(position).getName())
                .execute(new StringCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据日期查询详细数据", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                if(type ==2) {
                                    List<YaseeBpBean> beans = gson.fromJson(jsonArray.toString(), new TypeToken<List<YaseeBpBean>>() {
                                    }.getType());
                                    list.get(position).setList(beans);
                                }else {
                                    List<YaseeBsBean> beans = gson.fromJson(jsonArray.toString(), new TypeToken<List<YaseeBsBean>>() {
                                    }.getType());
                                    list.get(position).setList2(beans);
                                }
//                                Log.e( "BBB: ",list.get(position).getList().size()+"" );
                                yaseeDataAdapter.notifyDataSetChanged();
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
                        LoadDialog.stop();
                    }
                });
    }
}