package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sinocare.multicriteriasdk.blebooth.DeviceAdapter;
import com.sinocare.multicriteriasdk.entity.SNDevice;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BluetoothListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BlueToothBean;
import net.leelink.healthangelos.bean.DetailItem;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

public class WatchDemoActivity extends BaseActivity implements OnOrderListener {
    RecyclerView list;
    BluetoothListAdapter bluetoothListAdapter;
    List<BlueToothBean> blueToothBeans = new ArrayList<>();
    RelativeLayout rl_back;
    Button btn_startscan;
    String mac;

    String s = "b102830b000144eac34b1f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_demo);
        init();
        initList();
    }

    public void init() {
        list = findViewById(R.id.list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_startscan = findViewById(R.id.btn_startscan);

        btn_startscan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        MyApplication.mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                if (!device.getName().equals("NULL") && !device.getName().equals("")) {
                    if (blueToothBeans.size() > 0) {
                        if (checkDevice(device.getName())) {

                        } else {

                            blueToothBeans.add(new BlueToothBean(device.getName(), device.getAddress()));
                            bluetoothListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        blueToothBeans.add(new BlueToothBean(device.getName(), device.getAddress()));
                        bluetoothListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }

    public void initList() {
        bluetoothListAdapter = new BluetoothListAdapter(blueToothBeans, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        list.setAdapter(bluetoothListAdapter);
        list.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildLayoutPosition(view);
        mac = blueToothBeans.get(position).getAddress();
        new AlertDialog.Builder(WatchDemoActivity.this, R.style.common_dialog).setTitle("提示").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bindDevice(mac);


            }
        }).setMessage("您确定绑定该设备吗？\n" + "  mac:" + mac)
                .setNegativeButton(android.R.string.no, null).show();

    }

    private List<DetailItem> mDeviceDetailDataList = new ArrayList<>();


    @Override
    public void onButtonClick(View view, int position) {

    }

    public void bindDevice(String mac) {
        Intent intent = new Intent(WatchDemoActivity.this,VitaMainActivity.class);
        intent.putExtra("mac", mac);
        startActivity(intent);
        finish();
//        JSONObject json = new JSONObject();
//        try {
//            json.put("deviceId",getIntent().getIntExtra("deviceId",0));
//            json.put("modelId",getIntent().getIntExtra("modelId",0));
//            json.put("imei",mac);
//            json.put("isBluetooth",1);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.e( "bindDevice: ", json.toString());
//        OkGo.<String>post(Urls.BIND)
//                .tag(this)
//                .headers("token", MyApplication.token)
//                .upJson(json)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        try {
//                            String body = response.body();
//                            JSONObject json = new JSONObject(body);
//                            Log.d("绑定设备", json.toString());
//                            if (json.getInt("status") == 200) {
//                                Toast.makeText(WatchDemoActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
//
//                            } else {
//                                Toast.makeText(WatchDemoActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

    public boolean checkDevice(String name) {
        for (int i = 0; i < blueToothBeans.size(); i++) {
            if (blueToothBeans.get(i).getName().equals(name)) {
                return true;
            }
        }
        if(name.contains("vita")) {
            return false;
        }else {
            return true;
        }
//        return false;
    }

}
