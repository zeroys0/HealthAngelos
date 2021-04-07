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
import net.leelink.healthangelos.activity.DeviceManageActivity;
import net.leelink.healthangelos.activity.SinoMainActivity;
import net.leelink.healthangelos.activity.SinoUgActivity;
import net.leelink.healthangelos.adapter.MyDeviceAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceFragment extends BaseFragment implements View.OnClickListener, OnOrderListener {
    RecyclerView device_list;
    ProgressBar mProgressBar;
    MyDeviceAdapter myDeviceAdapter;
    RelativeLayout img_add;
    JSONArray jsonArray;

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

    public void initList(){
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
                                myDeviceAdapter = new MyDeviceAdapter(jsonArray,DeviceFragment.this,getContext());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
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
        switch (v.getId()){
            case R.id.img_add:
                Intent intent = new Intent(getContext(), AddEquipmentActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        int position = device_list.getChildLayoutPosition(view);
        try {
            if(jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_GLUCOSE_SANNUO")) {
                SNDevice snDevice = new SNDevice(SNDevice.DEVICE_WL_ONE, jsonArray.getJSONObject(position).getString("imei"));
                Intent intent = new Intent(getContext(), SinoMainActivity.class);
                ArrayList<SNDevice> snDevices = new ArrayList<>();
                snDevices.add(snDevice);
                intent.putExtra("snDevices", snDevices);
                startActivity(intent);
            } else if(jsonArray.getJSONObject(position).getString("buildVersion").equals("BT_UA_SANNUO")){
                SNDevice snDevice = new SNDevice(SNDevice.DEVICE_UG_11, jsonArray.getJSONObject(position).getString("imei"));
                Intent intent = new Intent(getContext(), SinoUgActivity.class);
                ArrayList<SNDevice> snDevices = new ArrayList<>();
                snDevices.add(snDevice);
                intent.putExtra("snDevices", snDevices);
                startActivity(intent);
            }
            else  {
               // Intent intent = new Intent(getContext(), UnbindEquipmentActivity.class);
                Intent intent = new Intent(getContext(), DeviceManageActivity.class);
                try {
                    intent.putExtra("imei",jsonArray.getJSONObject(position).getString("imei"));
                    intent.putExtra("name",jsonArray.getJSONObject(position).getString("name"));
                    intent.putExtra("img",jsonArray.getJSONObject(position).getString("imgPath"));
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
}
