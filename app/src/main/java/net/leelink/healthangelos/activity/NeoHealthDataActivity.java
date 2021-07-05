package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DataLeadAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.HealthDataSort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NeoHealthDataActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back,rl_title;
    private ImageView img_edit;
    private Context context;
    private RecyclerView lead_list;
    private DataLeadAdapter dataLeadAdapter;
    private List<String> list = new ArrayList<>();
    private View title_hearteat,title_pressure,title_oxygen,title_sugar,title_bushu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_health_data);
        context = this;
        EventBus.getDefault().register(this);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_edit = findViewById(R.id.img_edit);
        img_edit.setOnClickListener(this);
        lead_list = findViewById(R.id.lead_list);
        rl_title = findViewById(R.id.rl_title);
        title_hearteat = findViewById(R.id.title_hearteat);
        title_pressure = findViewById(R.id.title_pressure);
        title_oxygen = findViewById(R.id.title_oxygen);
        title_sugar = findViewById(R.id.title_sugar);
        title_bushu = findViewById(R.id.title_bushu);
        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HealthDataSort healthDataSort) {
       initView();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_edit:
                Intent intent = new Intent(context,EditLeapActivity.class);
                startActivity(intent);
                break;

        }
    }

    public void initView(){
        String s = (String) SPUtils.get(context,"sort","");
        Log.e( "onClick: ",s );
        if(s.equals("")) {
            s = "步 数,血 压,血 氧,血 糖,心 率";
        }
        String[] strings = s.split(",");

        list = Arrays.asList(strings);

        if(list.get(0).equals("步 数")) {
            title_bushu.setVisibility(View.VISIBLE);
            title_pressure.setVisibility(View.INVISIBLE);
            title_oxygen.setVisibility(View.INVISIBLE);
            title_sugar.setVisibility(View.INVISIBLE);
            title_hearteat.setVisibility(View.INVISIBLE);
        } else if(list.get(0).equals("血 压")){
            title_bushu.setVisibility(View.INVISIBLE);
            title_pressure.setVisibility(View.VISIBLE);
            title_oxygen.setVisibility(View.INVISIBLE);
            title_sugar.setVisibility(View.INVISIBLE);
            title_hearteat.setVisibility(View.INVISIBLE);
        } else if(list.get(0).equals("血 氧")) {
            title_bushu.setVisibility(View.INVISIBLE);
            title_pressure.setVisibility(View.INVISIBLE);
            title_oxygen.setVisibility(View.VISIBLE);
            title_sugar.setVisibility(View.INVISIBLE);
            title_hearteat.setVisibility(View.INVISIBLE);
        } else if(list.get(0).equals("血 糖")) {
            title_bushu.setVisibility(View.INVISIBLE);
            title_pressure.setVisibility(View.INVISIBLE);
            title_oxygen.setVisibility(View.INVISIBLE);
            title_sugar.setVisibility(View.VISIBLE);
            title_hearteat.setVisibility(View.INVISIBLE);
        } else {
            title_bushu.setVisibility(View.INVISIBLE);
            title_pressure.setVisibility(View.INVISIBLE);
            title_oxygen.setVisibility(View.INVISIBLE);
            title_sugar.setVisibility(View.INVISIBLE);
            title_hearteat.setVisibility(View.VISIBLE);
        }

        initList();
    }

    public void initList(){

        dataLeadAdapter = new DataLeadAdapter(context,list,NeoHealthDataActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
        lead_list.setAdapter(dataLeadAdapter);
        lead_list.setLayoutManager(layoutManager);
        lead_list.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(View view) {
        Intent intent = new Intent(this,HealthDataDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
