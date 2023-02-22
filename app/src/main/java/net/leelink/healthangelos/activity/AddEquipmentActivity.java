package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.BioRadar.BindBioRadarActivity;
import net.leelink.healthangelos.activity.Fit.SearchFitWatchActivity;
import net.leelink.healthangelos.activity.R60flRadar.R60flRadarMainActivity;
import net.leelink.healthangelos.activity.a666g.A666gActivity;
import net.leelink.healthangelos.activity.a666g.G777gActivity;
import net.leelink.healthangelos.activity.hck.BindHCKActivity;
import net.leelink.healthangelos.activity.slaap.BindSlaapActivity;
import net.leelink.healthangelos.activity.ssk.BindSSKActivity;
import net.leelink.healthangelos.adapter.DeviceListAdapter;
import net.leelink.healthangelos.adapter.EpAdapter;
import net.leelink.healthangelos.adapter.EquiementTypeAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddEquipmentActivity extends BaseActivity implements OnOrderListener {
    RecyclerView type_list,equipment_list;
    RelativeLayout rl_back;
    Context context;
    JSONArray typeArray,jsonArray;
    EquiementTypeAdapter equiementTypeAdapter;
    EpAdapter epAdapter;
    DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);
        createProgressBar(this);
        context = this;


        init();
        initList();
    }



    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        type_list = findViewById(R.id.type_list);
        equipment_list = findViewById(R.id.equipment_list);
    }

    public void initList(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("可绑定设备列表", json.toString());
                            if (json.getInt("status") == 200) {
//                                typeArray = json.getJSONArray("data");
//                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
//                                equiementTypeAdapter = new EquiementTypeAdapter(typeArray,context,AddEquipmentActivity.this);
//                                type_list.setLayoutManager(layoutManager);
//                                type_list.setAdapter(equiementTypeAdapter);
//                                if(typeArray.length()>0) {
//                                    jsonArray = typeArray.getJSONObject(0).getJSONArray("deviceList");
//                                    epAdapter = new EpAdapter(jsonArray,context,AddEquipmentActivity.this);
//                                    RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
//                                    equipment_list.setLayoutManager(layoutManager1);
//                                    equipment_list.setAdapter(epAdapter);
//                                }
                                jsonArray = json.getJSONArray("data");
                                deviceListAdapter = new DeviceListAdapter(jsonArray,AddEquipmentActivity.this,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                equipment_list.setLayoutManager(layoutManager);
                                equipment_list.setAdapter(deviceListAdapter);

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

//    @Override
//    public void onItemClick(View view) {
//        int position = type_list.getChildLayoutPosition(view);
//        equiementTypeAdapter.setChecked(position);
//
//        try {
//            jsonArray = typeArray.getJSONObject(position).getJSONArray("deviceList");
//            epAdapter.update(jsonArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onItemClick(View view) {
        int position = equipment_list.getChildLayoutPosition(view);
        try {
            if(jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_GLUCOSE_SANNUO")) {
                Intent intent  = new Intent(this,BindSinoActivity.class);
                intent.putExtra("deviceId",jsonArray.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId",jsonArray.getJSONObject(position).getInt("modelId"));
                intent.putExtra("snDeviceType",8);
                startActivity(intent);

            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_WATCH_AILE")) {
                Intent intent  = new Intent(this,WatchDemoActivity.class);
                intent.putExtra("deviceId",jsonArray.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId",jsonArray.getJSONObject(position).getInt("modelId"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_UA_SANNUO")) {
                Intent intent  = new Intent(this,BindSinoActivity.class);
                intent.putExtra("deviceId",jsonArray.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId",jsonArray.getJSONObject(position).getInt("modelId"));
                intent.putExtra("snDeviceType",26);
                startActivity(intent);
            } //JWOTCH
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("SKR_W204")) {
                Intent intent  = new Intent(this,BindSkrActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("HCK")) {      //hck安防设备
                Intent intent  = new Intent(this, BindHCKActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("SSK")) {        //睡睡康智能床垫
                Intent intent  = new Intent(this, BindSSKActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("JWOTCH_B")) {        //6041B腕表
                Intent intent  = new Intent(this, BindSaasActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("GPRS_A")) {        //A666g 臂式电子血压计
                Intent intent  = new Intent(this, A666gActivity.class);
//                Intent intent  = new Intent(this, A666gMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("GPRS_G")) {        //G777g 爱奥乐电子血糖仪
                Intent intent  = new Intent(this, G777gActivity.class);
//                Intent intent  = new Intent(this, G777gMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("MJ_501")) {        //MJ501 生物雷达
                Intent intent  = new Intent(this, BindBioRadarActivity.class);
//                Intent intent  = new Intent(this, BioRadarMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("FIT_CLOUD_PRO")) {        //FIT_CLOUD腕表
                Intent intent  = new Intent(this, SearchFitWatchActivity.class);
//                Intent intent  = new Intent(this, FitMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("SLAAP")) {        //无感睡眠监护仪
                Intent intent  = new Intent(this, BindSlaapActivity.class);
//                Intent intent  = new Intent(this, FitMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("R60A")) {        //防跌倒雷达
//                Intent intent  = new Intent(this, Bind60flRadarActivity.class);
                Intent intent  = new Intent(this, R60flRadarMainActivity.class);
                intent.putExtra("deviceModel", jsonArray.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }

            else {
                Intent intent  = new Intent(this,BindEquipmentActivity.class);
                intent.putExtra("deviceId",jsonArray.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId",jsonArray.getJSONObject(position).getInt("modelId"));
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

}
