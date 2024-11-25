package net.leelink.healthangelos.activity.ahaFit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.ConnectionState;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.SearchFitWatchActivity;
import net.leelink.healthangelos.activity.Fit.mock.User;
import net.leelink.healthangelos.activity.Fit.mock.UserMock;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.ActivityCompat;
import io.reactivex.disposables.Disposable;

import static net.leelink.healthangelos.app.MyApplication.fit_connect;

public class BindAhaFitWatchActivity extends BaseActivity {

    private Context context;
    private BluetoothDevice mBluetoothDevice;
    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private User mUser = UserMock.getLoginUser();
    TextView tv_tips;
    private ConnectionState mState = ConnectionState.DISCONNECTED;
    private RelativeLayout rl_back;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_fit_watch);
        context = this;
        sp = getSharedPreferences("sp", 0);
            SharedPreferences sp = getSharedPreferences("sp", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("fit_device");
        editor.apply();
        tv_tips = findViewById(R.id.tv_tips);

        connect();
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mUser.setId(Integer.parseInt(MyApplication.userInfo.getOlderlyId()));

    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

             bindDevice(mBluetoothDevice.getAddress());

            }

        }
    };

    public void bindDevice(String mac) {

        OkGo.<String>post(Urls.getInstance().FIT_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("imei", mac)
                .params("model", "FitWatch")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定fit腕表", json.toString());
                            if (json.getInt("status") == 200) {
//                                SharedPreferences.Editor editor = sp.edit();
//                                Gson gson = new Gson();
//                                String bluedevice = gson.toJson(mBluetoothDevice);
//                                editor.putString("fit_device", bluedevice);
//                                editor.apply();
                                Toast.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, AhaFitMainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    BluetoothGatt mBluetoothGatt;

    private void connect() {
        mBluetoothDevice = getIntent().getParcelableExtra(SearchFitWatchActivity.EXTRA_DEVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mCallback);
        }


//        boolean isBind = DbMock.isUserBind(this, mBluetoothDevice, mUser);
//
//        //If previously bind, use login mode
//        //If haven't  bind before, use bind mode
//        Log.d(TAG, "Connect device:" + mBluetoothDevice.getAddress() + " with user:" + mUser.getId()
//                + " use " + (isBind ? "Login" : "Bind") + " mode");
//
//        mWristbandManager.connect(mBluetoothDevice, String.valueOf(mUser.getId()), true
//                , mUser.isSex(), mUser.getAge(), mUser.getHeight(), mUser.getWeight());
    }

    BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("BluetoothGatt", status + "\n" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (fit_connect) {
                    BleManager.getInstance().setConnectedDevice(mBluetoothDevice);
                    //连接成功进行绑定
                    myHandler.sendEmptyMessageDelayed(1,0);
                    fit_connect = false;
                }
                finish();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                tv_tips.setText(R.string.state_connect_failed);
            }
        }
    };

}