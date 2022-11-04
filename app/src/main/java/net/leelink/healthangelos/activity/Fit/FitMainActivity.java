package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.ConnectionError;
import com.htsmart.wristband2.bean.ConnectionState;
import com.htsmart.wristband2.bean.SyncDataRaw;
import com.htsmart.wristband2.bean.data.BloodPressureData;
import com.htsmart.wristband2.bean.data.EcgData;
import com.htsmart.wristband2.bean.data.HeartRateData;
import com.htsmart.wristband2.bean.data.OxygenData;
import com.htsmart.wristband2.bean.data.RespiratoryRateData;
import com.htsmart.wristband2.bean.data.SleepData;
import com.htsmart.wristband2.bean.data.SportData;
import com.htsmart.wristband2.bean.data.StepData;
import com.htsmart.wristband2.bean.data.TodayTotalData;
import com.htsmart.wristband2.packet.SyncDataParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.mock.User;
import net.leelink.healthangelos.activity.Fit.mock.UserMock;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static net.leelink.healthangelos.app.MyApplication.fit_connect;

public class FitMainActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,rl_cardiogram,rl_setting,rl_step,rl_blood_oxygen,rl_sleep_data,rl_heart_rate,rl_blood_pressure;
    private Context context;
    private ImageView img_head;
    private TextView tv_name,tv_state,tv_charge,tv_step_number,tv_unbind,tv_sleep_time;
    private BluetoothDevice mBluetoothDevice;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mStateDisposable;
    private Disposable mErrorDisposable;
    private ConnectionState mState = ConnectionState.DISCONNECTED;
    private User mUser = UserMock.getLoginUser();
    private static final String TAG = "ConnectActivity";
    private Disposable mSyncDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_main);
        context = this;
        init();
        mStateDisposable = mWristbandManager.observerConnectionState()
                .subscribe(new Consumer<ConnectionState>() {
                    @Override
                    public void accept(ConnectionState connectionState) throws Exception {
                        if (connectionState == ConnectionState.DISCONNECTED) {
                            if (mWristbandManager.getRxBleDevice() == null) {

                                tv_state.setText("主动断开连接");
                            } else {
                                if (mState == ConnectionState.CONNECTED) {

                                    tv_state.setText("被动断开连接");
                                } else {
                                    tv_state.setText("连接断开");
                                }
                            }
                        } else if (connectionState == ConnectionState.CONNECTED) {
                            tv_state.setText(R.string.state_connect_success);
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
        mErrorDisposable = mWristbandManager.observerConnectionError()
                .subscribe(new Consumer<ConnectionError>() {
                    @Override
                    public void accept(ConnectionError connectionError) throws Exception {
                        Log.w(TAG, "Connect Error occur and retry:" + connectionError.isRetry(), connectionError.getThrowable());
                    }
                });

    }

    public void init(){
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
        tv_unbind  = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_sleep_time = findViewById(R.id.tv_sleep_time);
        img_head = findViewById(R.id.img_head);
        img_head.setOnClickListener(this);

   //     mBluetoothDevice = getIntent().getParcelableExtra(SearchFitWatchActivity.EXTRA_DEVICE);


        tv_charge.setText("当前电量: "+mWristbandManager.requestBattery().blockingGet().getPercentage()+"%");


    }

    public void syncData(){
        if (mSyncDisposable != null && !mSyncDisposable.isDisposed()) {
            //Syncing
            return;
        }

        int d = mWristbandManager.requestLatestHealthy().blockingGet().getDiastolicPressure();
        int s = mWristbandManager.requestLatestHealthy().blockingGet().getSystolicPressure();
        Log.d("获取最新血压数据: ", d + " " + s);
        mSyncDisposable = mWristbandManager
                .syncData()
                .observeOn(Schedulers.io(), true)
                .flatMapCompletable(new Function<SyncDataRaw, CompletableSource>() {
                    @Override
                    public CompletableSource apply(SyncDataRaw syncDataRaw) throws Exception {
                        //parser sync data and save to database
                        if (syncDataRaw.getDataType() == SyncDataParser.TYPE_HEART_RATE) {
                            Log.d("同步心率数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<HeartRateData> datas = SyncDataParser.parserHeartRateData(syncDataRaw.getDatas());
                            if(datas!=null) {
                                for (HeartRateData data : datas) {
                                    Log.d("同步心率数据: ", data.getHeartRate() + "");
                                }
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_BLOOD_PRESSURE) {
                            Log.d("同步血压数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<BloodPressureData> datas = SyncDataParser.parserBloodPressureData(syncDataRaw.getDatas());
                            if(datas!=null) {
                                for (BloodPressureData data : datas) {
                                    Log.d("同步血压数据: ", data.getDbp() + " " + data.getSbp());
                                }
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_OXYGEN) {
                            Log.d("同步血氧数据条目: ", syncDataRaw.getDatas().size() + "");
                            List<OxygenData> datas = SyncDataParser.parserOxygenData(syncDataRaw.getDatas());
                            if(datas!=null) {
                                for (OxygenData data : datas) {
                                    Log.d("同步血氧数据: ", data.getOxygen() + "%");
                                }
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_RESPIRATORY_RATE) {
                              List<RespiratoryRateData> datas = SyncDataParser.parserRespiratoryRateData(syncDataRaw.getDatas());

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SLEEP) {
                            List<SleepData> datas = SyncDataParser.parserSleepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            if(datas!=null) {
                                for (SleepData data : datas) {
                                    Log.d("同步睡眠数据: ", data.getItems().get(0).getStatus() + "%");
                                }
                            }

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_SPORT) {
                            List<SportData> datas = SyncDataParser.parserSportData(syncDataRaw.getDatas(), syncDataRaw.getConfig());

                        } else if (syncDataRaw.getDataType() == SyncDataParser.TYPE_STEP) {
                            List<StepData> datas = SyncDataParser.parserStepData(syncDataRaw.getDatas(), syncDataRaw.getConfig());
                            Log.d("同步步数数据条目: ", syncDataRaw.getDatas().size() + "");
                            if (datas != null && datas.size() > 0) {
                                if (syncDataRaw.getConfig().getWristbandVersion().isExtStepExtra()) {
                                    //The wristband supports automatic calculation of distance and calorie data
                                    for (StepData data : datas) {
                                        Log.d("同步步数数据: ", data.getStep() + "%");
                                    }
                                } else {
                                    //you need to calculate distance and calorie yourself.
                                    User user = UserMock.getLoginUser();
                                    float stepLength = Utils2.getStepLength(user.getHeight(), user.isSex());
                                    for (StepData data : datas) {
                                        data.setDistance(Utils2.step2Km(data.getStep(), stepLength));
                                        data.setCalories(Utils2.km2Calories(data.getDistance(), user.getWeight()));
                                        Log.d("同步步数数据: ", data.getStep() + "%");
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
                            Log.d("同步所有步数数据条目: ", data.getStep() + "");
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
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_cardiogram:
                //心电图
                Intent intent = new Intent(context,FitCardiogramActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting:
                Intent intent1 = new Intent(context,FitSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_step:  //步数选项
                Intent intent2 = new Intent(context,FitStepActivity.class);
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
                Intent intent5= new Intent(context,FitHeartRateActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_blood_pressure:    //血压数据
                Intent intent6 = new Intent(context, FitBloodPressureActivity.class);
                startActivity(intent6);
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

    @SuppressLint("WrongConstant")
    public void showPopup(){

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

    public void unbind(){
        OkGo.<String>delete(Urls.getInstance().FIT_UNBIND+"/"+mWristbandManager.getConnectedDevice().getAddress())
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWristbandManager.close();
        fit_connect = true;
    }
}