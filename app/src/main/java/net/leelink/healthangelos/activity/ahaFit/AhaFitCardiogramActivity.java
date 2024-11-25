package net.leelink.healthangelos.activity.ahaFit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.WristbandConfig;
import com.htsmart.wristband2.bean.data.EcgData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.EcgDetailActivity;
import net.leelink.healthangelos.activity.Fit.adapter.FitStepAdapter;
import net.leelink.healthangelos.activity.Fit.bean.EcgBean;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.EcgView;
import net.leelink.healthangelos.view.HeartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class AhaFitCardiogramActivity extends BaseActivity implements OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private Button btn_start;
    private List<Float> allDatas = new ArrayList<>();
    private RecyclerView data_list;
    FitStepAdapter fitStepAdapter;

    private Disposable mTestingEcgDisposable;
    private EcgData mEcgData;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private WristbandConfig mWristbandConfig;
    private EcgView ecg_view;
    private List<EcgBean> list = new ArrayList<>();
    private HeartView heartView;
    JSONArray ja = new JSONArray();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_cardiogram);
        context = this;
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEcgTesting();
            }
        });
 //       ecg_view = findViewById(R.id.ecg_view);
        data_list = findViewById(R.id.data_list);
        heartView = findViewById(R.id.hv);
        heartView.setHeartSpeed(2f);
        heartView.setShowSeconds(5f);
        heartView.setBaseLine(200);
    }

    public void initList(){
        OkGo.<String>get(Urls.getInstance().LISTHISTORYECG)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取心电数据", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<EcgBean>>(){}.getType());

                                fitStepAdapter = new FitStepAdapter(list,context, AhaFitCardiogramActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                data_list.setAdapter(fitStepAdapter);
                                data_list.setLayoutManager(layoutManager);

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

    private void toggleEcgTesting() {
        if (mTestingEcgDisposable != null && !mTestingEcgDisposable.isDisposed()) {
            //结束测量
            mTestingEcgDisposable.dispose();
        } else {
            if (!mWristbandManager.isConnected()) {

                Toast.makeText(context, "设备失去连接", Toast.LENGTH_SHORT).show();
                return;
            } else if (mWristbandManager.isSyncingData()) {
                Toast.makeText(context, "同步中,请勿多次测量", Toast.LENGTH_SHORT).show();
                return;
            } else if (mWristbandConfig == null) {
                Toast.makeText(context, "mWristbandManager为空", Toast.LENGTH_SHORT).show();
                return;
            } else if (!mWristbandConfig.getWristbandVersion().isEcgEnabled()) {
                Toast.makeText(context, "设备没有此功能", Toast.LENGTH_SHORT).show();
                return;
            }
            mEcgData = null;
            //开始测量
            mTestingEcgDisposable = mWristbandManager
                    .openEcgRealTimeData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            //mBtnTestEcg.setText(R.string.real_time_data_stop);
                            Log.d( "ecg: ","开始测量");
                        }
                    })
                    .doOnTerminate(new Action() {
                        @Override
                        public void run() throws Exception {
                          //  mBtnTestEcg.setText(R.string.real_time_data_start);
                            Log.d( "ecg: ","停止测量");
                            upLoad();
                        }
                    })
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                          //  mBtnTestEcg.setText(R.string.real_time_data_start);
                        }
                    })
                    .subscribe(new Consumer<int[]>() {
                        @Override
                        public void accept(int[] ints) throws Exception {
                            if (mEcgData == null) {//This is the first packet
                                mEcgData = new EcgData();
                                mEcgData.setItems(new ArrayList<Integer>(1000));
                                if (ints.length == 1) {//Sample packet
                                    mEcgData.setSample(ints[0]);
                                //    mTvEcgSample.setText(getString(R.string.ecg_sample, mEcgData.getSample()));
                                    Log.d( "ecg_sample",getString(R.string.ecg_sample, mEcgData.getSample()));
                                } else {//Error packet, may be lost the sample packet.
                                    mEcgData.setSample(EcgData.DEFAULT_SAMPLE);//Set a default sample
                                    mEcgData.getItems().addAll(intsAsList(ints));//Add this ecg data
                                    Log.d( "ecg_sample",getString(R.string.ecg_sample, mEcgData.getSample()));
                                    Log.d( "ecg_value",getString(R.string.ecg_value, Arrays.toString(ints)));
                                }
                            } else {
                                mEcgData.getItems().addAll(intsAsList(ints));//Add this ecg data
//                                mTvEcgValue.setText(getString(R.string.ecg_value, Arrays.toString(ints)));
                                Log.d( "ecg_value_1",getString(R.string.ecg_value, Arrays.toString(ints)));
                                heartView.offer(ints);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.w("RealTimeData", "RealTimeData", throwable);
                        }
                    });
        }
    }

    private  List<Integer> intsAsList(int[] values) {
        List<Integer> list = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            list.add(values[i]);
            ja.put(values[i]);
        }
        return list;
    }

    public void upLoad(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject data = new JSONObject();
        try {


            data.put("items",ja);
            data.put("sample",0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            data.put("testTime",time);
            jsonArray.put(data);
            jsonObject.put("fitEcgList",jsonArray);
            jsonObject.put("elderlyId", MyApplication.userInfo.getOlderlyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
           Log.e( "upLoad: ",jsonObject.toString());
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
                            Log.d("上传心电数据", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "心电上传成功", Toast.LENGTH_SHORT).show();

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




    @Override
    public void onItemClick(View view) {
        int position = data_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, EcgDetailActivity.class);
        intent.putExtra("id",list.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}