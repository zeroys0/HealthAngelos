package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
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
import net.leelink.healthangelos.adapter.EquipmentAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EquipmentActivity extends BaseActivity implements OnOrderListener {
    RecyclerView equipment_list;
    RelativeLayout rl_back;
    Context context;
    EquipmentAdapter equipmentAdapter;
    JSONArray jsonArray;
    Button btn_add_equipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        context = this;
        createProgressBar(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    public void init(){
        equipment_list = findViewById(R.id.equipment_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_add_equipment = findViewById(R.id.btn_add_equipment);
        btn_add_equipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,AddEquipmentActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().MYBIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询绑定设备", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = json.getJSONArray("data");
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                equipmentAdapter = new EquipmentAdapter(jsonArray,EquipmentActivity.this,context);
                                equipment_list.setLayoutManager(layoutManager);
                                equipment_list.setAdapter(equipmentAdapter);
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

    @Override
    public void onItemClick(View view) {
        int position = equipment_list.getChildLayoutPosition(view);
        Intent intent = new Intent(this,UnbindEquipmentActivity.class);
        try {
            intent.putExtra("imei",jsonArray.getJSONObject(position).getString("imei"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
