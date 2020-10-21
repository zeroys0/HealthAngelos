package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Headers;
import okhttp3.internal.http2.Header;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.DetailItem;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

public class VitaMainActivity extends BaseActivity implements View.OnClickListener{
    RelativeLayout rl_back,rl_heart_rate,rl_blood_pressure;
    Button btn_unbind;
    Context context;
    TextView tv_connect;
    String mac;
    SharedPreferences sp;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vita_main);
        createProgressBar(this);
        context = this;
        init();
        connect();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_unbind = findViewById(R.id.btn_unbind);
        btn_unbind.setOnClickListener(this);
        tv_connect = findViewById(R.id.tv_connect);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        sp = getSharedPreferences("sp",0);
        login();
    }

    public void connect(){
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();
        mac = getIntent().getStringExtra("mac");
        MyApplication.mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == REQUEST_SUCCESS) {
                    setGattProfile(data);
                }else{
                    //检测是否需要连接设备（如果已经连接，则不需要再去连接）

                }
            }
        });
        MyApplication.mClient.registerConnectStatusListener(mac, new BleConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(String mac, int status) {
                if (status == STATUS_CONNECTED) {
                    tv_connect.setText("已连接");
                } else if (status == STATUS_DISCONNECTED) {
                    tv_connect.setText("未连接");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_unbind:
                unbind();
                break;
            case R.id.rl_heart_rate:
                Intent intent = new Intent(this,VitaHeartRateActivity.class);
                intent.putExtra("mac",mac);

                startActivity(intent);
                break;
            case R.id.rl_blood_pressure:
                Intent intent1 = new Intent(this,BloodPressureActivity.class);
                intent1.putExtra("mac",mac);
                intent1.putExtra("pid",pid);
                startActivity(intent1);
                break;
        }
    }

    public void login(){
        HttpParams httpParams= new HttpParams();
        httpParams.put("thridloginname",sp.getString("telephone",""));
        httpParams.put("appkey","v5XZpEVt4sGSzRud0I8piNPDcKvGqGzg");
        httpParams.put("password","000000");
        httpParams.put("surname","姓");
        httpParams.put("firstname","名");
        httpParams.put("sex",107);
        httpParams.put("height",178);
        httpParams.put("weight",66);
        httpParams.put("birthdate","524569475357");

        OkGo.<String>post(Urls.ENTERPRISELOGIN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            Headers headers = response.headers();
                            SharedPreferences sp = getSharedPreferences("sp",0);
                            SharedPreferences.Editor editor = sp.edit();
                            JSONObject json = new JSONObject(body);
                            Log.d("登录蓝牙", json.toString());
                            if (json.getInt("code") == 0) {
                                editor.putString("vtoken",headers.get("vtoken"));
                                editor.commit();
                                pid = json.getJSONObject("data").getString("pid");
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    public void unbind(){
        showProgressBar();
        OkGo.<String>delete(Urls.BIND+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取属性
    public void setGattProfile(BleGattProfile profile) {
        List<DetailItem> items = new ArrayList<DetailItem>();
        List<BleGattService> services = profile.getServices();

        for (BleGattService service : services) {
            service.getCharacters();
            items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), service.getUUID()));
            List<BleGattCharacter> characters = service.getCharacters();

            for (BleGattCharacter character : characters) {
                Log.e( "setGattProfile: ",  character.getUuid().toString());
                Log.e( "setGattProfile: ",  service.getUUID().toString());
                items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
            }
        }
        //将得到的items传递到需要发送信息的位置。DetailItem是service和character的实体。
        //上面我们就提到过，蓝牙间的消息传递获取是根据service的character来做到的。
    }

}
