package net.leelink.healthangelos.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sinocare.multicriteriasdk.entity.SNDevice;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.AddEquipmentActivity;
import net.leelink.healthangelos.activity.Badge.BadgeMainActivity;
import net.leelink.healthangelos.activity.BioRadar.BioRadarMainActivity;
import net.leelink.healthangelos.activity.DeviceManageActivity;
import net.leelink.healthangelos.activity.ElectricMachine.ANY1PR01MainActivity;
import net.leelink.healthangelos.activity.Fit.FitMainActivity;
import net.leelink.healthangelos.activity.JWatchB.JWatchBMainActivity;
import net.leelink.healthangelos.activity.R60flRadar.R60flRadarMainActivity;
import net.leelink.healthangelos.activity.SinoMainActivity;
import net.leelink.healthangelos.activity.SinoUgActivity;
import net.leelink.healthangelos.activity.SkrMainActivity;
import net.leelink.healthangelos.activity.Ys7.Ys7ScreenActivity;
import net.leelink.healthangelos.activity.ZW011.ZW011MainActivity;
import net.leelink.healthangelos.activity.a666g.A666gMainActivity;
import net.leelink.healthangelos.activity.a666g.G777gMainActivity;
import net.leelink.healthangelos.activity.hck.HCKMainActivity;
import net.leelink.healthangelos.activity.slaap.SlaapMainActivity;
import net.leelink.healthangelos.activity.ssk.SSKMainActivity;
import net.leelink.healthangelos.adapter.MyDeviceAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceFragment extends BaseFragment implements View.OnClickListener, OnOrderListener {
    RecyclerView device_list;
    ProgressBar mProgressBar;
    MyDeviceAdapter myDeviceAdapter;
    RelativeLayout img_add;
    JSONArray jsonArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        createProgressBar(getContext());
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    public void init(View view) {
        device_list = view.findViewById(R.id.device_list);
        img_add = view.findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
    }

    public void initList() {
        OkGo.<String>get(Urls.getInstance().MYBIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取绑定的设备", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = json.getJSONArray("data");
                                myDeviceAdapter = new MyDeviceAdapter(jsonArray, DeviceFragment.this, getContext());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                device_list.setLayoutManager(layoutManager);
                                device_list.setAdapter(myDeviceAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void handleCallBack(Message msg) {

    }

    public void createProgressBar(Context context) {

        //整个Activity布局的最终父布局,参见参考资料
        FrameLayout rootFrameLayout = (FrameLayout) getActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProgressBar = new ProgressBar(context);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(mProgressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
                Intent intent = new Intent(getContext(), AddEquipmentActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        if(!Utils.isFastClick()){
            return;
        }
        int position = device_list.getChildLayoutPosition(view);
        try {
            if (jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_GLUCOSE_SANNUO")) {      //三诺血糖仪
                SNDevice snDevice = new SNDevice(SNDevice.DEVICE_WL_ONE, jsonArray.getJSONObject(position).getString("imei"));
                Intent intent = new Intent(getContext(), SinoMainActivity.class);
                ArrayList<SNDevice> snDevices = new ArrayList<>();
                snDevices.add(snDevice);
                intent.putExtra("snDevices", snDevices);
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_UA_SANNUO")) { //三诺血糖仪
                SNDevice snDevice = new SNDevice(SNDevice.DEVICE_UG_11, jsonArray.getJSONObject(position).getString("imei"));
                Intent intent = new Intent(getContext(), SinoUgActivity.class);
                ArrayList<SNDevice> snDevices = new ArrayList<>();
                snDevices.add(snDevice);
                intent.putExtra("snDevices", snDevices);
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            } else if (jsonArray.getJSONObject(position).getString("buildVersion").equals("SKR_W204")) { //w204安防设备
                checkOnline(jsonArray.getJSONObject(position).getString("imei"),jsonArray.getJSONObject(position).getString("telephone"),jsonArray.getJSONObject(position).getString("imgPath"));
            }  else if (jsonArray.getJSONObject(position).getString("buildVersion").equals("HCK")) { //hck安防设备
                checkHckOnline(jsonArray.getJSONObject(position).getString("imei"),jsonArray.getJSONObject(position).getString("telephone"),jsonArray.getJSONObject(position).getString("imgPath"));
            }  else if (jsonArray.getJSONObject(position).getString("buildVersion").equals("SSK")) { //SSK智能床垫
                Intent intent = new Intent(getContext(), SSKMainActivity.class);
                intent.putExtra("imei",jsonArray.getJSONObject(position).getString("imei"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("JWOTCH_B")){  //Saas防跌倒腕表
                Intent intent = new Intent(getContext(), JWatchBMainActivity.class);
                /**
                 * 传 腕表名称, 原6041 现改为6031B
                 */
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name",jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("model",jsonArray.getJSONObject(position).getString("modelName"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("MJ_501")){  //AI生物雷达
                Intent intent = new Intent(getContext(), BioRadarMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                startActivity(intent);
            }  else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("GPRS_A")) {        //A666g 臂式电子血压计
                Intent intent  = new Intent(getContext(), A666gMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("GPRS_G")) {        //G777g 爱奥乐电子血糖仪
                Intent intent  = new Intent(getContext(), G777gMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("FIT_CLOUD_PRO")) {        //Fit 蓝牙腕表
              //  Intent intent  = new Intent(getContext(), BindFitWatchActivity.class);
                Intent intent  = new Intent(getContext(), FitMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }
            else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("SLAAP")) {        //SLAAP无感床带
                Intent intent  = new Intent(getContext(), SlaapMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("JWOTCH") && jsonArray.getJSONObject(position).getString("modelName").equals("ZW011")){
                Intent intent = new Intent(getContext(), ZW011MainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("R60A")){
                Intent intent = new Intent(getContext(), R60flRadarMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("OVIPHONE_G")){
                Intent intent = new Intent(getContext(), BadgeMainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                intent.putExtra("model",jsonArray.getJSONObject(position).getString("modelName"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("HOME_INSIGHT")){
                Intent intent = new Intent(getContext(), ANY1PR01MainActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                intent.putExtra("model",jsonArray.getJSONObject(position).getString("modelName"));
                startActivity(intent);
            }else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("YS7_EZVIZ")){
                //Intent intent = new Intent(getContext(), Ys7MainActivity.class);
                Intent intent = new Intent(getContext(), Ys7ScreenActivity.class);
                intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                intent.putExtra("model",jsonArray.getJSONObject(position).getString("modelName"));
                startActivity(intent);
            }

            else {
                // Intent intent = new Intent(getContext(), UnbindEquipmentActivity.class);
                Intent intent = new Intent(getContext(), DeviceManageActivity.class);     //设备管理
//                Intent intent = new Intent(getContext(), SafeDeviceActivity.class);     //安防设备
//                Intent intent = new Intent(getContext(), New4gWotchActivity.class);     //新4g腕表
                try {
                    intent.putExtra("imei", jsonArray.getJSONObject(position).getString("imei"));
                    intent.putExtra("name", jsonArray.getJSONObject(position).getString("name"));
                    intent.putExtra("img", jsonArray.getJSONObject(position).getString("imgPath"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    /**
     * 查看设备是否在线
     * @param imei 设备cid
     * @param telephone 设备电话
     */
    public void checkOnline(String imei,String telephone,String path) {
        OkGo.<String>get(Urls.getInstance().ONLINE+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            Log.d("获取设备在线状态", body);
                            JSONObject json = new JSONObject(body);
                            if (json.getInt("status") == 200) {     //在线跳转到主页
                                Intent intent = new Intent(getContext(), SkrMainActivity.class);
                                intent.putExtra("imei", imei);
                                startActivity(intent);

                            } else if (json.getInt("status") == 204) {  //否则绑定 发送短信
                                Intent intent = new Intent(getContext(), SkrMainActivity.class);
//                                Intent intent = new Intent(getContext(), SkrMain2Activity.class);
                                intent.putExtra("telephone",telephone);
                                intent.putExtra("path",path);
                                intent.putExtra("imei", imei);
                                startActivity(intent);

                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        Toast.makeText(getContext(), "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    /**
     * 查看hck设备是否在线
     * @param imei 设备cid
     * @param telephone 设备电话
     */
    public void checkHckOnline(String imei,String telephone,String path) {
        OkGo.<String>get(Urls.getInstance().HCK_ONLINE+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            Log.d("获取HCK设备在线状态", body);
                            JSONObject json = new JSONObject(body);
                            if (json.getInt("status") == 200) {     //在线跳转到主页
                                Intent intent = new Intent(getContext(), HCKMainActivity.class);
                                intent.putExtra("imei", imei);
                                startActivity(intent);
                                
                            } else if (json.getInt("status") == 204) {  //否则绑定 发送短信
                                Intent intent = new Intent(getContext(), HCKMainActivity.class);
                                intent.putExtra("telephone",telephone);
                                intent.putExtra("path",path);
                                intent.putExtra("imei", imei);
                                startActivity(intent);

                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        Toast.makeText(getContext(), "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
