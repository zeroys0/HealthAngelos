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
import net.leelink.healthangelos.activity.Badge.BindBadgeActivity;
import net.leelink.healthangelos.activity.BioRadar.BindBioRadarActivity;
import net.leelink.healthangelos.activity.ElectricMachine.BindANY1PR01Activity;
import net.leelink.healthangelos.activity.Fit.SearchFitWatchActivity;
import net.leelink.healthangelos.activity.R60flRadar.Bind60flRadarActivity;
import net.leelink.healthangelos.activity.Ys7.BindYs7Activity;
import net.leelink.healthangelos.activity.a666g.A666gActivity;
import net.leelink.healthangelos.activity.a666g.G777gActivity;
import net.leelink.healthangelos.activity.hck.BindHCKActivity;
import net.leelink.healthangelos.activity.slaap.BindSlaapActivity;
import net.leelink.healthangelos.activity.sleepace.BindSleepaceActivity;
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

public class AddEquipmentActivity extends BaseActivity implements OnOrderListener, View.OnClickListener {
    RecyclerView type_list, equipment_list;
    RelativeLayout rl_back, rl_chuandai, rl_jiankong, rl_jiance;
    Context context;
    JSONArray typeArray, jsonArray, ja;
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
        //  initList();
        initList2();
    }


    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        type_list = findViewById(R.id.type_list);
        equipment_list = findViewById(R.id.equipment_list);
        rl_chuandai = findViewById(R.id.rl_chuandai);
        rl_chuandai.setOnClickListener(this);
        rl_jiankong = findViewById(R.id.rl_jiankong);
        rl_jiankong.setOnClickListener(this);
        rl_jiance = findViewById(R.id.rl_jiance);
        rl_jiance.setOnClickListener(this);
    }

    public void initList() {
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
                                deviceListAdapter = new DeviceListAdapter(jsonArray, AddEquipmentActivity.this, context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                equipment_list.setLayoutManager(layoutManager);
                                equipment_list.setAdapter(deviceListAdapter);

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
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initList2() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BIND2)
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
                                jsonArray = json.getJSONArray("data");
                                ja = jsonArray.getJSONObject(0).getJSONArray("deviceList");
                                setList(ja);
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
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chuandai:
                try {
                    ja = jsonArray.getJSONObject(0).getJSONArray("deviceList");
                    setList(ja);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_jiankong:

                try {
                    ja = jsonArray.getJSONObject(1).getJSONArray("deviceList");
                    setList(ja);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_jiance:
                try {
                    ja = jsonArray.getJSONObject(2).getJSONArray("deviceList");
                    setList(ja);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setList(JSONArray jsonArray) {
        deviceListAdapter = new DeviceListAdapter(jsonArray, AddEquipmentActivity.this, context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        equipment_list.setLayoutManager(layoutManager);
        equipment_list.setAdapter(deviceListAdapter);
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
            if (ja.getJSONObject(position).getString("buildVersion").equals("BT_GLUCOSE_SANNUO")) {
                Intent intent = new Intent(this, BindSinoActivity.class);
                intent.putExtra("deviceId", ja.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId", ja.getJSONObject(position).getInt("modelId"));
                intent.putExtra("snDeviceType", 8);
                startActivity(intent);

            } else if (ja.getJSONObject(position).getString("buildVersion").equals("BT_WATCH_AILE")) {
                Intent intent = new Intent(this, WatchDemoActivity.class);
                intent.putExtra("deviceId", ja.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId", ja.getJSONObject(position).getInt("modelId"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("BT_UA_SANNUO")) {
                Intent intent = new Intent(this, BindSinoActivity.class);
                intent.putExtra("deviceId", ja.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId", ja.getJSONObject(position).getInt("modelId"));
                intent.putExtra("snDeviceType", 26);
                startActivity(intent);
            } //JWOTCH
            else if (ja.getJSONObject(position).getString("buildVersion").equals("SKR_W204")) {
                Intent intent = new Intent(this, BindSkrActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("HCK")) {      //hck安防设备
                Intent intent = new Intent(this, BindHCKActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("SSK")) {        //睡睡康智能床垫
                Intent intent = new Intent(this, BindSSKActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("JWOTCH_B")) {        //6041B腕表
                Intent intent = new Intent(this, BindSaasActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("GPRS_A")) {        //A666g 臂式电子血压计
                Intent intent = new Intent(this, A666gActivity.class);
//                Intent intent  = new Intent(this, A666gMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("GPRS_G")) {        //G777g 爱奥乐电子血糖仪
                Intent intent = new Intent(this, G777gActivity.class);
//                Intent intent  = new Intent(this, G777gMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("MJ_501")) {        //MJ501 生物雷达
                Intent intent = new Intent(this, BindBioRadarActivity.class);
//                Intent intent  = new Intent(this, BioRadarMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("FIT_CLOUD_PRO")) {        //FIT_CLOUD腕表
                Intent intent = new Intent(this, SearchFitWatchActivity.class);
//                Intent intent  = new Intent(this, FitMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("SLAAP")) {        //无感睡眠监护仪
                Intent intent = new Intent(this, BindSlaapActivity.class);
//                Intent intent  = new Intent(this, FitMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("R60A")) {        //防跌倒雷达
                Intent intent = new Intent(this, Bind60flRadarActivity.class);
//                Intent intent  = new Intent(this, R60flRadarMainActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("OVIPHONE_G")) {        //工牌胸卡
                Intent intent = new Intent(this, BindBadgeActivity.class);
//                Intent intent  = new Intent(this, BadgeMainActivity.class);
                intent.putExtra("deviceId", ja.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId", ja.getJSONObject(position).getInt("modelId"));
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("HOME_INSIGHT")) {        //电力脉象仪
                Intent intent = new Intent(this, BindANY1PR01Activity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (ja.getJSONObject(position).getString("buildVersion").equals("YS7_EZVIZ")) {        //萤石摄像头
                Intent intent = new Intent(this, BindYs7Activity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }else if (ja.getJSONObject(position).getString("buildVersion").equals("SLEEPACE")) {        //萤石摄像头
                Intent intent = new Intent(this, BindSleepaceActivity.class);
                intent.putExtra("deviceModel", ja.getJSONObject(position).getString("deviceModel"));
                intent.putExtra("path", ja.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, BindEquipmentActivity.class);
                intent.putExtra("deviceId", ja.getJSONObject(position).getInt("deviceId"));
                intent.putExtra("modelId", ja.getJSONObject(position).getInt("modelId"));
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
