package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.ConnectionError;
import com.htsmart.wristband2.bean.ConnectionState;
import com.htsmart.wristband2.bean.SyncDataRaw;
import com.htsmart.wristband2.bean.data.BloodPressureData;
import com.htsmart.wristband2.bean.data.BloodPressureMeasureData;
import com.htsmart.wristband2.bean.data.EcgData;
import com.htsmart.wristband2.bean.data.HeartRateData;
import com.htsmart.wristband2.bean.data.OxygenData;
import com.htsmart.wristband2.bean.data.RespiratoryRateData;
import com.htsmart.wristband2.bean.data.SleepData;
import com.htsmart.wristband2.bean.data.SleepItemData;
import com.htsmart.wristband2.bean.data.SportData;
import com.htsmart.wristband2.bean.data.StepData;
import com.htsmart.wristband2.bean.data.TemperatureData;
import com.htsmart.wristband2.bean.data.TodayTotalData;
import com.htsmart.wristband2.packet.SyncDataParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.mock.DbMock;
import net.leelink.healthangelos.activity.Fit.mock.User;
import net.leelink.healthangelos.activity.Fit.mock.UserMock;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.AndPermissionHelper;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_BLOOD_PRESSURE;
import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_BLOOD_PRESSURE_MEASURE;
import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_HEART_RATE;
import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_OXYGEN;
import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_OXYGEN_MEASURE;
import static com.htsmart.wristband2.packet.SyncDataParser.TYPE_TEMPERATURE_MEASURE;
import static net.leelink.healthangelos.app.MyApplication.fit_connect;

public class FitMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_cardiogram, rl_setting, rl_step, rl_blood_oxygen, rl_sleep_data, rl_heart_rate, rl_blood_pressure,rl_temperature;
    private Context context;
    private ImageView img_head;
    private TextView tv_name, tv_state, tv_charge, tv_step_number, tv_unbind, tv_sleep_time, tv_blood_pressure, tv_blood_oxygen, tv_heart_rate,tv_temperature;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private ConnectionState mState = ConnectionState.DISCONNECTED;
    private User mUser = UserMock.getLoginUser();
    private static final String TAG = "ConnectActivity";
    private Disposable mSyncDisposable;
    private Disposable mScanDisposable;
    private RxBleClient mRxBleClient;
    JSONObject jsonObject = new JSONObject();
    boolean a = true;
    int recon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_main);
        mRxBleClient = WristbandApplication.getRxBleClient();
        mUser.setId(Integer.parseInt(MyApplication.userInfo.getOlderlyId()));
        context = this;
        init();
        getNewestData();
        if (mWristbandManager.isConnected()) {
            tv_state.setText(R.string.state_connect_success);
            tv_charge.setText("当前电量: " + mWristbandManager.requestBattery().blockingGet().getPercentage() + "%");
            syncData();
        }
        if (getIntent().getStringExtra("imei") != null) {
//                connect(getIntent().getStringExtra("imei"));
                String img = getIntent().getStringExtra("img");
                Glide.with(context).load(Urls.getInstance().IMG_URL + img).into(img_head);
            if (!mWristbandManager.isConnected()) {
                reScan(1);
            }
        }
        if(mStateDisposable == null) {
            mStateDisposable = mWristbandManager.observerConnectionState()
                    .subscribe(new Consumer<ConnectionState>() {
                        @Override
                            public void accept(ConnectionState connectionState) throws Exception {
                            if (connectionState == ConnectionState.DISCONNECTED) {
                                if (mWristbandManager.getRxBleDevice() == null) {
                                    tv_state.setText("主动断开连接");
                                    if (getIntent().getStringExtra("imei") != null && a) {
//                                        if(recon == 0) {
//                                            mWristbandManager.close();
//                                            mWristbandManager = WristbandApplication.getWristbandManager();
//                                            reScan(0);
//                                            recon++;
//                                        }
                                    }
                                } else {
                                    if (mState == ConnectionState.CONNECTED) {
                                        tv_state.setText("被动断开连接");
                                    } else {
                                        tv_state.setText("连接断开");
                                    }
                                }
                            } else if (connectionState == ConnectionState.CONNECTED) {
                                tv_state.setText(R.string.state_connect_success);
                                if (mWristbandManager.isConnected()) {
                                    tv_charge.setText("当前电量: " + mWristbandManager.requestBattery().blockingGet().getPercentage() + "%");
                                    syncData();
                                }
                                if (mWristbandManager.isBindOrLogin()) {
                                    //If connect with bind mode, clear Today Step Data
//                                toast(R.string.toast_connect_bind_tips);
//                                mSyncDataDao.clearTodayStep();

                                } else {
//                                toast(R.string.toast_connect_login_tips);
                                }
                            } else {
                                tv_state.setText(R.string.state_connecting);

                            }
                            mState = connectionState;
                        }

                    });
        }
        mErrorDisposable = mWristbandManager.observerConnectionError()
                .subscribe(new Consumer<ConnectionError>() {
                    @Override
                    public void accept(ConnectionError connectionError) throws Exception {
                        Log.w(TAG, "Connect Error occur and retry:" + connectionError.isRetry(), connectionError.getThrowable());
                    }
                });

    }

    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int number = Integer.parseInt(tv_step_number.getText().toString());
            //总步数相加计算 >>暂时不需要
//            number += msg.getData().getInt("step");
//            tv_step_number.setText(String.valueOf(number));
            int step  =msg.getData().getInt("step");
            tv_step_number.setText(String.valueOf(step));
            if (msg.what == TYPE_BLOOD_PRESSURE_MEASURE) {
                tv_blood_pressure.setText(msg.getData().getInt("sbp") + "/" + msg.getData().getInt("dbp") + "mmHg");
            } else if (msg.what == TYPE_OXYGEN) {
                tv_blood_oxygen.setText(msg.getData().getInt("bloodoxygen") + "%");
            } else if (msg.what == TYPE_HEART_RATE) {
                tv_heart_rate.setText(msg.getData().getInt("heartRate") + "次/分钟");
            }else if (msg.what == TYPE_TEMPERATURE_MEASURE) {
                tv_temperature.setText(msg.getData().getFloat("temperature_wrist") + "℃/"+msg.getData().getFloat("temperature_body")+ "℃");
            }

        }
    };

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        rl_cardiogram = findViewById(R.id.rl_cardiogram);
        rl_cardiogram.setOnClickListener(this);
        rl_setting = findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(this);
        rl_step = findViewById(R.id.rl_step);
        rl_step.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        tv_blood_oxygen = findViewById(R.id.tv_blood_oxygen);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_name = findViewById(R.id.tv_name);
        tv_state = findViewById(R.id.tv_state);
        tv_charge = findViewById(R.id.tv_charge);
        tv_step_number = findViewById(R.id.tv_step_number);
        rl_blood_oxygen = findViewById(R.id.rl_blood_oxygen);
        rl_blood_oxygen.setOnClickListener(this);
        rl_sleep_data = findViewById(R.id.rl_sleep_data);
        rl_sleep_data.setOnClickListener(this);
        rl_heart_rate = findViewById(R.id.rl_heart_rate);
        rl_heart_rate.setOnClickListener(this);
        rl_temperature = findViewById(R.id.rl_temperature);
        rl_temperature.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_sleep_time = findViewById(R.id.tv_sleep_time);
        img_head.setOnClickListener(this);
        tv_blood_pressure = findViewById(R.id.tv_blood_pressure);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //进入页面后 自动同步数据
        syncData();
    }



    public void syncData() {
        if (mSyncDisposable != null && !mSyncDisposable.isDisposed()) {
            //Syncing
            return;
        }
        mSyncDisposable = mWristbandManager
                .syncData()
                .observeOn(Schedulers.io(), true)
                .flatMapCompletable(new Function<SyncDataRaw, CompletableSource>() {
                    @Override
                    public CompletableSource apply(SyncDataRaw syncDataRaw) throws Exception {
                        //parser sync data and save to database
                        //获取心率数据
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_HEART_RATE) {
                            Log.d("同步心率数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<HeartRateData> datas = SyncDataParser.parserHeartRateData(syncDataRaw.getDatas());
                            if (datas != null) {
                                for (HeartRateData data : datas) {
                                    Log.d("同步心率数据: ", data.getHeartRate() + "");
                                }
                            }

                        } else if (syncDataRaw.getDataType() == TYPE_BLOOD_PRESSURE) {
                            Log.d("同步血压数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<BloodPressureData> datas = SyncDataParser.parserBloodPressureData(syncDataRaw.getDatas());
                            if (datas != null) {
                                for (BloodPressureData data : datas) {
                                    Log.d("同步血压数据: ", data.getDbp() + " " + data.getSbp());
                                }
                            }
                        }  else if (syncDataRaw.getDataType() == TYPE_OXYGEN) {
                            Log.d("同步血氧数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<OxygenData> datas = SyncDataParser.parserOxygenData(syncDataRaw.getDatas());
                            if (datas != null) {
                                JSONArray jsonArray = new JSONArray();
                                for (OxygenData data : datas) {
                                    Log.d("同步血氧数据: ", data.getOxygen() + "%");
                                    JSONObject bloodOxygen = new JSONObject();
                                    bloodOxygen.put("oxygen", data.getOxygen());
                                    bloodOxygen.put("testTime", getTestTime(data.getTimeStamp()));
                                    jsonArray.put(bloodOxygen);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("bloodoxygen", datas.get(datas.size() - 1).getOxygen());
                                message.what = TYPE_OXYGEN;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitBloodOxygenList", jsonArray);
                            }
                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_HEART_RATE_MEASURE) {
                            List<HeartRateData> datas = SyncDataParser.parserHeartRateMeasure(syncDataRaw);
                            if (datas != null && datas.size()>0) {
                                JSONArray jsonArray = new JSONArray();
                                for (HeartRateData data : datas) {
                                    Log.d("同步手动测量心率数据: ", data.getHeartRate() + "");
                                    JSONObject heart_rate = new JSONObject();
                                    heart_rate.put("heartRate", data.getHeartRate());
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = new Date(System.currentTimeMillis());
                                    String time = sdf.format(date);
                                    heart_rate.put("testTime", time);
                                    jsonArray.put(heart_rate);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("heartRate", datas.get(datas.size() - 1).getHeartRate());
                                message.what = TYPE_HEART_RATE;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitHeartRateList", jsonArray);
                            }
                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_BLOOD_PRESSURE_MEASURE) {
                            List<BloodPressureMeasureData> datas = SyncDataParser.parserBloodPressureMeasure(syncDataRaw);
                            if (datas != null) {
                                JSONArray jsonArray = new JSONArray();
                                for (BloodPressureData data : datas) {
                                    Log.d("同步手动测量血压数据: ", data.getDbp() + " " + data.getSbp());
                                    JSONObject bloodpressure = new JSONObject();
                                    bloodpressure.put("dbp", data.getDbp());
                                    bloodpressure.put("sbp", data.getSbp());
                                    bloodpressure.put("testTime", getTestTime(data.getTimeStamp()));
                                    jsonArray.put(bloodpressure);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("sbp", datas.get(0).getSbp());
                                bundle.putInt("dbp", datas.get(0).getDbp());
                                message.what = TYPE_BLOOD_PRESSURE_MEASURE;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitBloodPressureList", jsonArray);
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_OXYGEN_MEASURE) {
                            List<OxygenData> datas = SyncDataParser.parserOxygenMeasure(syncDataRaw);
                            if (datas != null) {
                                JSONArray jsonArray = new JSONArray();
                                for (OxygenData data : datas) {
                                    Log.d("同步手动测量血氧数据: ", data.getOxygen() + "%");
                                    JSONObject bloodOxygen = new JSONObject();
                                    bloodOxygen.put("oxygen", data.getOxygen());
                                    bloodOxygen.put("testTime", getTestTime(data.getTimeStamp()));
                                    jsonArray.put(bloodOxygen);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("bloodoxygen", datas.get(datas.size() - 1).getOxygen());
                                message.what = TYPE_OXYGEN_MEASURE;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitBloodOxygenList", jsonArray);
                            }

                        }else if (syncDataRaw.getDataType() == TYPE_TEMPERATURE_MEASURE) {
                            List<TemperatureData> datas = SyncDataParser.parserTemperatureMeasure(syncDataRaw);
                            if (datas != null) {
                                JSONArray jsonArray = new JSONArray();
                                for (TemperatureData data : datas) {
                                    Log.d("同步手动测量温度数据: ", data.getBody()+"°C/"+data.getWrist()+"°C");
                                    JSONObject temperature = new JSONObject();
                                    temperature.put("body", data.getBody());
                                    temperature.put("wrist", data.getWrist());
                                    temperature.put("testTime", getTestTime(data.getTimeStamp()));
                                    jsonArray.put(temperature);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putFloat("temperature_body", datas.get(datas.size() - 1).getBody());
                                bundle.putFloat("temperature_wrist", datas.get(datas.size() - 1).getWrist());
                                message.what = TYPE_TEMPERATURE_MEASURE;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitTemperatureList", jsonArray);
                            }
                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_RESPIRATORY_RATE) {
                            List<RespiratoryRateData> datas = SyncDataParser.parserRespiratoryRateData(syncDataRaw.getDatas());

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SLEEP) {
                            List<SleepData> datas = SyncDataParser.parserSleepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            if (datas != null) {
                                JSONArray jsonArray = new JSONArray();
                                for (SleepData data : datas) {
                                    Log.d("同步睡眠数据: ", data.getItems().get(0).getStatus() + "%");
                                    JSONArray ja = new JSONArray();
                                    JSONObject sleepData = new JSONObject();
                                    JSONObject item = new JSONObject();
                                    for (SleepItemData sleepItemData : data.getItems()) {
                                        item.put("startTime", getTestTime(sleepItemData.getStartTime()));
                                        item.put("endTime", getTestTime(sleepItemData.getEndTime()));
                                        item.put("status", sleepItemData.getStatus());
                                        ja.put(item);
                                    }
                                    sleepData.put("items", ja);
                                    sleepData.put("testTime", getTestTime(data.getTimeStamp()));
                                    jsonArray.put(sleepData);
                                }
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("fitSleep", getTestTime(datas.get(0).getItems().get(0).getStartTime()));
                                message.what = TYPE_OXYGEN_MEASURE;
                                message.setData(bundle);
                                handler.sendMessage(message);
                                jsonObject.put("fitSleepList", jsonArray);
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SPORT) {
                            List<SportData> datas = SyncDataParser.parserSportData(syncDataRaw.getDatas(), syncDataRaw.getConfig());

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_STEP) {
                            List<StepData> datas = SyncDataParser.parserStepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.d("同步步数数据: ", syncDataRaw.getDatas().size() + "");
                            if (datas != null && datas.size() > 0) {
                                if (syncDataRaw.getConfig().getWristbandVersion().isExtStepExtra()) {
                                    //The wristband supports automatic calculation of distance and calorie data
                                    JSONArray step = new JSONArray();
                                    for (StepData data : datas) {
                                        Log.d("同步步数数据详情: ", data.getStep() + "步");
                                        JSONObject json = new JSONObject();
                                        json.put("calories", 0);
                                        json.put("distance", 0);
                                        json.put("step", data.getStep());
                                        json.put("testTime", getTestTime(data.getTimeStamp()));
                                        step.put(json);
                                    }
                                    jsonObject.put("fitStepList", step);
                                } else {
                                    //you need to calculate distance and calorie yourself.
                                    User user = UserMock.getLoginUser();
                                    float stepLength = Utils2.getStepLength(user.getHeight(), user.isSex());
                                    for (StepData data : datas) {
                                        data.setDistance(Utils2.step2Km(data.getStep(), stepLength));
                                        data.setCalories(Utils2.km2Calories(data.getDistance(), user.getWeight()));

                                    }
                                }
                                //Only the step data is saved here. If you need distance and calorie data, you can choose according to the actual situation.
                                //   mSyncDataDao.saveStep(datas);
                            }
                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_ECG) {
                            EcgData ecgData = SyncDataParser.parserEcgData(syncDataRaw.getDatas());
                            //   mSyncDataDao.saveEcg(ecgData);
                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_TOTAL_DATA) {
                            Log.d("同步所有数据条目: ", syncDataRaw.getDatas().size() + "");
                            TodayTotalData data = SyncDataParser.parserTotalData(syncDataRaw.getDatas());
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("step", data.getStep());
                            message.setData(bundle);
                            handler.sendMessage(message);
                            Log.d("同步步数数据: ", data.getStep() + "");

                            Log.d("同步所有睡眠数据条目: ", data.getLightSleep() + "");

                            //   mSyncDataDao.saveTodayTotalData(data);
                        }
                        return Completable.complete();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d("Sync", "Sync Data Success");
                        upLoad();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Sync", "Sync Data Failed", throwable);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_cardiogram:
                //心电图
                Intent intent = new Intent(context, FitCardiogramActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting:
                if (!mWristbandManager.isConnected()) {
                    Toast.makeText(context, "设备未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(context, FitSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_step:  //步数选项
                Intent intent2 = new Intent(context, FitStepActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_blood_oxygen:  //血氧数据
                Intent intent3 = new Intent(context, BloodOxygenActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_sleep_data:    //睡眠数据
                Intent intent4 = new Intent(context, FitSleepDataActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_heart_rate:    //心率数据
                Intent intent5 = new Intent(context, FitHeartRateActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_blood_pressure:    //血压数据
                Intent intent6 = new Intent(context, FitBloodPressureActivity.class);
                startActivity(intent6);
                break;
            case R.id.rl_temperature:   //温度数据
                Intent intent7 = new Intent(context, FitTemperatureActivity.class);
                startActivity(intent7);
                break;
            case R.id.tv_unbind:       //解除绑定
                showPopup();
                break;
            case R.id.img_head:
                syncData();

                break;
            default:
                break;

        }
    }

    public void getNewestData() {
        OkGo.<String>get(Urls.getInstance().FIT_OXYGEN_NEWEST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新血氧数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_blood_oxygen.setText(json.get("oxygen") + "%");
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
        OkGo.<String>get(Urls.getInstance().FIT_BP_NEWEST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新血压数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_blood_pressure.setText(json.get("sbp") + "/" + json.getString("dbp"));
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

        OkGo.<String>get(Urls.getInstance().FIT_HEARTRATE_NEWEST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新心率数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_heart_rate.setText(json.get("heartRate") + "次/分钟");
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
        OkGo.<String>get(Urls.getInstance().FIT_STEP)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("testTime", getTestTime(System.currentTimeMillis()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新步数数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                // tv_heart_rate.setText(json.get("heartRate")+"次/分钟");
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

        OkGo.<String>get(Urls.getInstance().FIT_SLEEP_DAY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId", MyApplication.userInfo.getOlderlyId())
                .params("testTime", getTestTime(System.currentTimeMillis()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取当日睡眠数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.isNull("totalSleep")) {
                                    tv_sleep_time.setText("暂无");
                                } else {
                                    int second = json.getInt("totalSleep");
                                    int i = second / 3600;
                                    tv_sleep_time.setText(i + "小时");
                                }
                                // tv_heart_rate.setText(json.get("heartRate")+"次/分钟");
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

    @SuppressLint("WrongConstant")
    public void showPopup() {

        // TODO Auto-generated method stub
        View popview = LayoutInflater.from(FitMainActivity.this).inflate(R.layout.popu_fit_unbind, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbind();
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new FitMainActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);

    }

    public String getTestTime(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeStamp);
        String time = sdf.format(date);
        return time;
    }


    //上传同步数据
    public void upLoad() {
        try {
            jsonObject.put("elderlyId", MyApplication.userInfo.getOlderlyId());
            jsonObject.put("imei",getIntent().getStringExtra("imei"));
            //     JSONArray bloodPressure = jsonObject.getJSONArray("fitBloodPressureList");
            //     tv_blood_pressure.setText(bloodPressure.getJSONObject(bloodPressure.length()-1).getInt("dbp")+"/"+bloodPressure.getJSONObject(bloodPressure.length()-1).getInt("sbp")+"mmHg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("上传同步数据: ", jsonObject.toString());

        OkGo.<String>post(Urls.getInstance().FIT_UPLOAD)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("同步腕表数据", json.toString());
                            if (json.getInt("status") == 200) {
                                // Toast.makeText(context, "数据同步完成", Toast.LENGTH_SHORT).show();
                                jsonObject = new JSONObject();
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

    private void connect(String imei) {


        boolean isBind = isUserBind(this, imei, mUser);


        //If previously bind, use login mode
        //If haven't  bind before, use bind mode
        Log.e("connect", "Connect device:" + imei + " with user:" + MyApplication.userInfo.getOlderlyId()
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


    //设备解绑
    public void unbind() {
        String imei;
        if (mWristbandManager.isConnected()) {
            imei = mWristbandManager.getConnectedDevice().getAddress();
        } else {
            imei = getIntent().getStringExtra("imei");
        }

        OkGo.<String>delete(Urls.getInstance().FIT_UNBIND + "/" + imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解绑设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设备解绑成功", Toast.LENGTH_SHORT).show();
                                //解绑设备 清空设备地址
                                SharedPreferences sp = getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("fit_device");
                                editor.apply();
                                mWristbandManager.userUnBind();
                                mWristbandManager.close();
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

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    private void reScan(int i) {

        if (Utils2.checkLocationForBle(this)) {
            AndPermissionHelper.blePermissionRequest(this, new AndPermissionHelper.AndPermissionHelperListener1() {
                @Override
                public void onSuccess() {
                    ScanSettings scanSettings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build();

                    // mSwipeRefreshLayout.setRefreshing(true);
                    invalidateOptionsMenu();

                    mScanDisposable = mRxBleClient.scanBleDevices(scanSettings)
                            .subscribe(new Consumer<ScanResult>() {
                                @Override
                                public void accept(ScanResult scanResult) throws Exception {
                                    if (scanResult.getBleDevice().getBluetoothDevice().getAddress().equals(getIntent().getStringExtra("imei"))) {
                                        if(i==0) {
                                            connect(scanResult.getBleDevice().getBluetoothDevice());
                                        }else if(i ==1){
                                            connectb(scanResult.getBleDevice().getBluetoothDevice());
                                        }
                                        stopScanning();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    stopScanning();
                                }
                            });
                }
            });
        }
    }

    /**
     * Stop scan
     */
    private void stopScanning() {
        if (mScanDisposable != null)
            mScanDisposable.dispose();
        //    mSwipeRefreshLayout.setRefreshing(false);
        invalidateOptionsMenu();
    }

    private void connect(BluetoothDevice mBluetoothDevice) {
        boolean isBind = DbMock.isUserBind(this, mBluetoothDevice, mUser);

        //If previously bind, use login mode
        //If haven't  bind before, use bind mode
        Log.d(TAG, "Connect device:" + mBluetoothDevice.getAddress() + " with user:" + mUser.getId()
                + " use " + (isBind ? "Login" : "Bind") + " mode");

        mWristbandManager.connect(mBluetoothDevice, String.valueOf(mUser.getId()), false
                , mUser.isSex(), mUser.getAge(), mUser.getHeight(), mUser.getWeight());
        stopScanning();
    }

    private void connectb(BluetoothDevice mBluetoothDevice) {

        mWristbandManager.connect(mBluetoothDevice, String.valueOf(mUser.getId()), false
                , mUser.isSex(), mUser.getAge(), mUser.getHeight(), mUser.getWeight());
        stopScanning();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        a = false;

    //    mWristbandManager.close();
        fit_connect = true;
    }
}