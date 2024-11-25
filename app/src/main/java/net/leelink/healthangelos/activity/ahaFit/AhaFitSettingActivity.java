package net.leelink.healthangelos.activity.ahaFit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.config.FunctionConfig;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.DrindRemindActivity;
import net.leelink.healthangelos.activity.Fit.FitAlarmClockActivity;
import net.leelink.healthangelos.activity.Fit.FitOverTurnActivity;
import net.leelink.healthangelos.activity.Fit.SitRemindActivity;
import net.leelink.healthangelos.activity.Fit.bean.DrinkRemind;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class AhaFitSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_alarm_clock, rl_drink_remind, rl_sit_remind, rl_overturn, rl_sport_target, rl_hand, rl_time_style, rl_search;
    private LinearLayout ll_step_target;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private TextView tv_step_number, tv_interval, tv_apparel, tv_time_form, tv_sit_interval;
    private SwitchCompat cb_weather;
    private boolean open_weather;
    private int step = 2;

    private Integer stepValue = 0;
    private BluetoothViewModel bluetoothViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_setting);
        context = this;
        init();
        mBluetoothGatt = BleManager.getInstance().getBluetoothGatt();
        mWritableCharacteristic = BleManager.getInstance().getWritableCharacteristic();
        EventBus.getDefault().register(this);
       // getTarget();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            byte bytetype = data[0];
            int type = bytetype & 0xFF;
            Log.d( "TAG: ",type + "");
            if (type == 58) {
                //目标步数
                byte byteValue5 = data[5];
                byte byteValue6 = data[6];
                byte[] bytes = {byteValue5, byteValue6};
                int stepValue =(bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
                Log.d( "TAG: ",stepValue + "步");

                Message message = new Message();
                message.what = 58;
                Bundle bundle = new Bundle();
                bundle.putInt("step", stepValue);
                message.setData(bundle);
                myHandler.sendMessage(message);
            } else {
                Log.e( "TAG: ", "error");
            }
        }
    };
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DrinkRemind drinkRemind) {
        tv_interval.setText(drinkRemind.getInterval() + "分钟提醒一次");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("com.ble.stepNumber"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        byte[] command = new byte[]{0x3A};
        sendCommand(command);

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ll_step_target = findViewById(R.id.ll_step_target);
        ll_step_target.setOnClickListener(this);
        rl_alarm_clock = findViewById(R.id.rl_alarm_clock);
        rl_alarm_clock.setOnClickListener(this);
        rl_drink_remind = findViewById(R.id.rl_drink_remind);
        rl_drink_remind.setOnClickListener(this);
        rl_sit_remind = findViewById(R.id.rl_sit_remind);
        rl_sit_remind.setOnClickListener(this);
        tv_apparel = findViewById(R.id.tv_apparel);
        rl_hand = findViewById(R.id.rl_hand);
        rl_hand.setOnClickListener(this);
        rl_overturn = findViewById(R.id.rl_overturn);
        rl_overturn.setOnClickListener(this);
        tv_step_number = findViewById(R.id.tv_step_number);
        tv_interval = findViewById(R.id.tv_interval);
        rl_time_style = findViewById(R.id.rl_time_style);
        rl_time_style.setOnClickListener(this);
        tv_time_form = findViewById(R.id.tv_time_form);
        cb_weather = findViewById(R.id.cb_weather);
        cb_weather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SetConfig(FunctionConfig.FLAG_WEATHER_SWITCH, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_WEATHER_SWITCH, false);
                }
            }
        });
        tv_sit_interval = findViewById(R.id.tv_sit_interval);
        rl_search = findViewById(R.id.rl_search);
        rl_search.setOnClickListener(this);
//        synData();

    }
    private void updateUIWithData(byte[] data) {
        // 更新 UI 逻辑

    }

    BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic mWritableCharacteristic;

    public void synData() {
        BluetoothDevice bluetoothDevice = BleManager.getInstance().getConnectedDevice();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mCallback);

        }
        if (mBluetoothGatt == null) {
            Toast.makeText(context, "gatt空", Toast.LENGTH_SHORT).show();
        }
//        FunctionConfig functionConfig = mWristbandManager.getWristbandConfig().getFunctionConfig();
//        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_WEATHER_SWITCH)) {        //判断天气选项开关
//            cb_weather.setChecked(true);
//        } else {
//            cb_weather.setChecked(false);
//        }
//        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_WEAR_WAY)) {        //获取佩戴习惯
//            tv_apparel.setText("右手");
//        } else {
//            tv_apparel.setText("左手");
//        }
//        if (functionConfig.isFlagEnable(FunctionConfig.FLAG_HOUR_STYLE)) {        //获取时间格式
//            tv_time_form.setText("12小时制");
//        } else {
//            tv_time_form.setText("24小时制");
//        }
//        DrinkWaterConfig drinkWaterConfig = mWristbandManager.getWristbandConfig().getDrinkWaterConfig();
//        tv_interval.setText(drinkWaterConfig.getInterval() + "分钟提醒一次");

    }

    BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("BluetoothGatt", status + "\n" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.d("Bluetooth", "Device connected. Discovering services...");
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("Bluetooth", "Device disconnected.");
                gatt.close();
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 服务发现成功
                if (mBluetoothGatt != null) {
                    BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E"));
                    if (service != null) {
                        Log.i("Bluetooth", "Service found: " + service.getUuid());
                        // 进一步处理服务
                        mWritableCharacteristic = service.getCharacteristic(UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E"));


                        //延迟后注册通知回调 避免直接注册出现错误
                        try {
                            Thread.sleep(1000); // 等待1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //注册回调 接收设备通知
                        BluetoothGattCharacteristic notifyChar = service.getCharacteristic(UUID.fromString("6E40FC21-B5A3-F393-E0A9-E50E24DCCA9E"));
                        boolean success = mBluetoothGatt.setCharacteristicNotification(notifyChar, true); // 启用通知
                        if (success) {
                            // 可能还需要注册回调监听通知事件
                            mBluetoothGatt.readCharacteristic(notifyChar);
                        }

                    } else {
                        Log.e("Bluetooth", "未找到服务: 6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E");
                    }
                } else {
                    Log.e("Bluetooth", "mBluetoothGatt is null");
                }
            } else {
                // 服务发现失败
                Log.e("Bluetooth", "Service discovery failed with status: " + status);
            }
        }

        //接收数据回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
//            byte bytetype = data[0];
//            int type = bytetype & 0xFF;
//            Log.d( "TAG: ",type + "");
//            if (type == 58) {
//
//                byte byteValue5 = data[5];
//                byte byteValue6 = data[6];
//                byte[] bytes = {byteValue5, byteValue6};
//                stepValue =(bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
//                Log.d( "TAG: ",stepValue + "步");
//                myHandler.sendEmptyMessageDelayed(58, 0);
//            } else {
//                Log.e( "TAG: ", "error");
//            }

//            for (byte datum : data) {
//                int intValue = datum & 0xFF;
//                Log.e("TAG", "onCharacteristicChanged: intValue2 = " + intValue);
//            }

            myHandler.sendEmptyMessageDelayed(1, 0);


        }
    };

    private void sendCommand(byte[] command) {

        if (mWritableCharacteristic != null && mBluetoothGatt != null) {
            mWritableCharacteristic.setValue(command);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                return;
            }
            boolean status = mBluetoothGatt.writeCharacteristic(mWritableCharacteristic);
            myHandler.sendEmptyMessageDelayed(0, 0);
            if (!status) {
                Log.e("TAG", "Failed to write to characteristic.");
            }
        } else {
            Log.e("TAG", "Characteristic or GATT not initialized.");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 58) {
                Bundle data = msg.getData();
                if (data != null){
                    int step = data.getInt("step");
                    tv_step_number.setText(step + "步");
                }


            } else if (msg.what == 3) {
                String time = msg.getData().getString("time");
                String id = msg.getData().getString("id");

            } else {

            }

        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_step_target:   //运动目标
                byte[] command = new byte[]{0x3A};
                sendCommand(command);
                break;
            case R.id.rl_alarm_clock:   //闹钟
                Intent intent = new Intent(context, FitAlarmClockActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_drink_remind:  //喝水提醒
                Intent intent1 = new Intent(context, DrindRemindActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_sit_remind:    //久坐提醒
                Intent intent2 = new Intent(context, SitRemindActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_overturn:      //翻腕亮屏
                Intent intent3 = new Intent(context, FitOverTurnActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_hand:      //佩戴方式
                showHand();
                break;
            case R.id.rl_time_style:        //时间格式
                chooseTimeStyle();
                break;
            case R.id.rl_search:       //查找腕表
                findWatch();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void showStep() {
        List<Integer> integerList = new ArrayList<>();
        List<String> stepList = new ArrayList<>();
        for (int i = 2000; i < 30000; i += 1000) {
            integerList.add(i);
            stepList.add(i + "步");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_step_number.setText(stepList.get(options1));
                step = integerList.get(options1) / 1000;
                mWristbandManager.setExerciseTarget(integerList.get(options1), 0, 0);
                changeTarget();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(stepList);
        pvOptions.show();
    }

    public void showHand() {
        List<String> hand_list = new ArrayList<>();
        hand_list.add("左手");
        hand_list.add("右手");

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_apparel.setText(hand_list.get(options1));
                if (tv_apparel.getText().toString().equals("右手")) {
                    SetConfig(FunctionConfig.FLAG_WEAR_WAY, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_WEAR_WAY, false);
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(hand_list);
        pvOptions.show();
    }

    public void chooseTimeStyle() {
        List<String> hand_list = new ArrayList<>();
        hand_list.add("12小时制");
        hand_list.add("24小时制");

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_time_form.setText(hand_list.get(options1));
                if (tv_time_form.getText().toString().equals("12小时制")) {
                    SetConfig(FunctionConfig.FLAG_HOUR_STYLE, true);
                } else {
                    SetConfig(FunctionConfig.FLAG_HOUR_STYLE, false);
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();


        pvOptions.setPicker(hand_list);
        pvOptions.show();
    }

    @SuppressLint("CheckResult")
    public void SetConfig(int FLAG, boolean state) {
        if (mWristbandManager.isConnected()) {
            FunctionConfig config = mWristbandManager.getWristbandConfig().getFunctionConfig();
            config.setFlagEnable(FLAG, state);//Wear right hand
            mWristbandManager.setFunctionConfig(config)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
//                            Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                            Log.d("run: ", "成功");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }

    public void findWatch() {
        mWristbandManager.findWristband().blockingGet();
        Log.e("onClick: ", "查找腕表");
    }

    public void changeTarget() {
        OkGo.<String>put(Urls.getInstance().RUNTARGET + "/" + MyApplication.userInfo.getOlderlyId() + "/" + step)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置运动目标", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置目标成功", Toast.LENGTH_SHORT).show();

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

    public void getTarget() {
        OkGo.<String>get(Urls.getInstance().RUNTARGET + "/" + MyApplication.userInfo.getOlderlyId())
                .tag(this)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取运动目标", json.toString());
                            if (json.getInt("status") == 200) {
                                step = json.getInt("data");
                                tv_step_number.setText(step * 1000 + "步");

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

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}