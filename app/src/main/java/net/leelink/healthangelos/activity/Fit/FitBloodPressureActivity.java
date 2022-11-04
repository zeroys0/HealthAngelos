package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.HealthyDataResult;
import com.just.agentweb.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class FitBloodPressureActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_data;
    AgentWeb agentweb;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mTestingHealthyDisposable;
    private PopupWindow popuPhoneW;
    private View popview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_blood_pressure);
        context = this;
        init();
        popu_head();
    }

    public void init(){
        ll_data = findViewById(R.id.ll_data);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setWeb(Urls.getInstance().FIT_H5+"/Blood/index/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }

    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(FitBloodPressureActivity.this)
                    .setAgentWebParent(ll_data, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App", this)
                    .createAgentWeb()
                    .ready()
                    .go(url);
        } else {
            ll_data.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);

            ll_data.setVisibility(View.VISIBLE);
        }

    }

    @JavascriptInterface
    public void startMeasureWatch(String msg) {
        //做原生操作
        Log.e("getDataFormVue: ", msg);

        //开始测量
        mTestingHealthyDisposable = mWristbandManager
                .openHealthyRealTimeData(mWristbandManager.HEALTHY_TYPE_BLOOD_PRESSURE)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //  mBtnTestHealthy.setText(R.string.real_time_data_stop);
                        Log.e( "血压测量: ", "开始");
                        myHandler.sendEmptyMessageDelayed(0, 0);

                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        //  mBtnTestHealthy.setText(R.string.real_time_data_start);
                        Log.e( "血压测量: ", "停止");

                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        //   mBtnTestHealthy.setText(R.string.real_time_data_start);
                    }
                })
                .subscribe(new Consumer<HealthyDataResult>() {
                    @Override
                    public void accept(HealthyDataResult result) throws Exception {
                        //  mTvHeartRate.setText(getString(R.string.heart_rate_value, result.getHeartRate()));
                        Log.d("血压数据: ", getString(R.string.blood_pressure_value, result.getDiastolicPressure(),result.getSystolicPressure()));
                        if(result.getDiastolicPressure() >0){
                            upLoad(result);
                            myHandler.sendEmptyMessageDelayed(1, 0);
                        }

//                            mTvBloodPressure.setText(getString(R.string.blood_pressure_value, result.getDiastolicPressure(), result.getSystolicPressure()));
//                            mTvRespiratoryRate.setText(getString(R.string.respiratory_rate_value, result.getRespiratoryRate()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.w("RealTimeData", "RealTimeData", throwable);
                    }
                });

    }

    //点击历史数据
    @JavascriptInterface
    public void getListByTime(String msg) {
        Log.e("getListByTime: ", msg);
        String time = "";
        setWeb(Urls.getInstance().FIT_H5+"/BloodHistory/"+time+"/"+MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }

    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(FitBloodPressureActivity.this).inflate(R.layout.popu_fit_health, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new FitBloodPressureActivity.poponDismissListener());
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if(msg.what==1){
                popuPhoneW.dismiss();
            } else {
                popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
            }

        }
    };

    public void upLoad(HealthyDataResult result){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject bloodpressure = new JSONObject();
        try {
            bloodpressure.put("dbp",result.getDiastolicPressure());
            bloodpressure.put("sbp",result.getSystolicPressure());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            bloodpressure.put("testTime",time);
            jsonArray.put(bloodpressure);
            jsonObject.put("fitBloodPressureList",jsonArray);
            jsonObject.put("elderlyId",MyApplication.userInfo.getOlderlyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            Log.d("上传血压数据", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();

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
}