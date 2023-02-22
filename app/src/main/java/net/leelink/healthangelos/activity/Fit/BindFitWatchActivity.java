package net.leelink.healthangelos.activity.Fit;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.ConnectionError;
import com.htsmart.wristband2.bean.ConnectionState;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.mock.DbMock;
import net.leelink.healthangelos.activity.Fit.mock.User;
import net.leelink.healthangelos.activity.Fit.mock.UserMock;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static net.leelink.healthangelos.app.MyApplication.fit_connect;

public class BindFitWatchActivity extends BaseActivity {
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
        tv_tips = findViewById(R.id.tv_tips);
//        if (getIntent().getStringExtra("imei") != null) {
//
//            if (!mWristbandManager.isConnected())
//                connect(getIntent().getStringExtra("imei"));
//        } else {
            mBluetoothDevice = getIntent().getParcelableExtra(SearchFitWatchActivity.EXTRA_DEVICE);
            connect();
//        }
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mStateDisposable = mWristbandManager.observerConnectionState()
                .subscribe(new Consumer<ConnectionState>() {
                    @Override
                    public void accept(ConnectionState connectionState) throws Exception {
                        if (connectionState == ConnectionState.DISCONNECTED) {
                            if (mWristbandManager.getRxBleDevice() == null) {
                                tv_tips.setText(R.string.state_active_disconnect);
                            } else {
                                if (mState == ConnectionState.CONNECTED) {
                                    tv_tips.setText(R.string.state_passive_disconnect);
                                } else {
                                    tv_tips.setText(R.string.state_connect_failed);
                                }
                            }

                        } else if (connectionState == ConnectionState.CONNECTED) {
                            tv_tips.setText(R.string.state_connect_success);
                            if (getIntent().getStringExtra("imei") != null) {
                                if (fit_connect) {
                                    Intent intent = new Intent(context, FitMainActivity.class);
                                    startActivity(intent);
                                    fit_connect = false;
                                }
                                finish();
                            } else {
                                //根据存储的设备地址 判断设备是否绑定
                                if(sp.getString("fit_device","").equals("")) {
                                    bindDevice(mBluetoothDevice.getAddress());

                                }
                                DbMock.setUserBind(BindFitWatchActivity.this, mBluetoothDevice, mUser);
                            }
                            if (mWristbandManager.isBindOrLogin()) {
                                //If connect with bind mode, clear Today Step Data
//                                toast(R.string.toast_connect_bind_tips);
//                                mSyncDataDao.clearTodayStep();
                            } else {
//                                toast(R.string.toast_connect_login_tips);
                            }
                        } else {
                            tv_tips.setText(R.string.state_connecting);

                        }
                        mState = connectionState;
                    }
                });
        mErrorDisposable = mWristbandManager.observerConnectionError()
                .subscribe(new Consumer<ConnectionError>() {
                    @Override
                    public void accept(ConnectionError connectionError) throws Exception {
                        Log.w(TAG, "Connect Error occur and retry:" + connectionError.isRetry(), connectionError.getThrowable());
                    }
                });


    }

    private static final String TAG = "ConnectActivity";

    public void bindDevice(String mac) {

        OkGo.<String>post(Urls.getInstance().FIT_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("imei", mac)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定fit腕表", json.toString());
                            if (json.getInt("status") == 200) {
                                SharedPreferences.Editor editor = sp.edit();
                                Gson gson = new Gson();
                                String bluedevice = gson.toJson(mBluetoothDevice);
                                editor.putString("fit_device", bluedevice);
                                editor.apply();
                                Intent intent = new Intent(context, BindFitSuccessActivity.class);
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

    private void connect() {
        boolean isBind = DbMock.isUserBind(this, mBluetoothDevice, mUser);

        //If previously bind, use login mode
        //If haven't  bind before, use bind mode
        Log.d(TAG, "Connect device:" + mBluetoothDevice.getAddress() + " with user:" + mUser.getId()
                + " use " + (isBind ? "Login" : "Bind") + " mode");

        mWristbandManager.connect(mBluetoothDevice, String.valueOf(mUser.getId()), true
                , mUser.isSex(), mUser.getAge(), mUser.getHeight(), mUser.getWeight());
    }


    private void connect(String imei) {


        boolean isBind = false;


        //If previously bind, use login mode
        //If haven't  bind before, use bind mode
        Log.e("connect", "Connect device:" + imei + " with user:" + mUser.getId()
                + " use " + (isBind ? "Login" : "Bind") + " mode");

        mWristbandManager.connect(imei, String.valueOf(mUser.getId()), false
                , mUser.isSex(), mUser.getAge(), mUser.getHeight(), mUser.getWeight());
    }

    private static String getKey(String address) {
        address = address.replaceAll(":", "");
        String key = "device_" + address;
        return key;
    }

    public static boolean isUserBind(Context context, String address, User user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int bindUserId = sharedPreferences.getInt(getKey(address), 0);
        return bindUserId == user.getId();
    }

}