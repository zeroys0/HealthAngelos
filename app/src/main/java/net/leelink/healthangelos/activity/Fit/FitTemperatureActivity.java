package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class FitTemperatureActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private LinearLayout ll_data;
    AgentWeb agentweb;
    private WristbandManager mWristbandManager = WristbandApplication.getWristbandManager();
    private Disposable mTestingHealthyDisposable;
    private PopupWindow popuPhoneW;
    private View popview;
    private float temp_body,temp_wrist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_temperature);
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
        setWeb(Urls.getInstance().FIT_H5+"/Temperature/index/"+ MyApplication.userInfo.getOlderlyId()+"/"+MyApplication.token);
    }

    void setWeb(String url) {
        if (agentweb == null) {
            agentweb = AgentWeb.with(FitTemperatureActivity.this)
                    .setAgentWebParent(ll_data, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .addJavascriptInterface("$App", this)
                    .createAgentWeb()
                    .ready()
                    .go(url);
            agentweb.clearWebCache();
        } else {
            agentweb.clearWebCache();
            Log.e("getDataFormVue: ", "刷新");
            ll_data.setVisibility(View.GONE);
            agentweb.getWebCreator().getWebView().loadUrl(url);
            ll_data.setVisibility(View.VISIBLE);
        }

    }

    @JavascriptInterface
    public void startMeasureWatch(String msg) {
        //做原生操作
        Log.e("getDataFormVue: ", msg);
        if(!mWristbandManager.isConnected()){
            Toast.makeText(context, "设备未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //开始测量
        mTestingHealthyDisposable = mWristbandManager
                .openHealthyRealTimeData(mWristbandManager.HEALTHY_TYPE_TEMPERATURE)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        //  mBtnTestHealthy.setText(R.string.real_time_data_stop);
                        Log.e( "体温测量: ", "开始");
                        myHandler.sendEmptyMessageDelayed(0, 0);
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
//                          mBtnTestHealthy.setText(R.string.real_time_data_start);
                        Log.e( "体温测量: ", "停止");
                        myHandler.sendEmptyMessageDelayed(1, 0);
                        upLoad();
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
                     //   Log.d("体温数据: ", getString(R.string.heart_rate_value, result.getTemperatureBody()));
                        temp_body = result.getTemperatureBody();
                        temp_wrist = result.getTemperatureWrist();
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
    public void getListByTimeTemper(String msg,String id) {
        Log.e("getListByTime: ", msg);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("time",msg);
        bundle.putString("id",id);
        message.setData(bundle);
        message.what = 3;
        myHandler.sendMessage(message);
    }
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if(msg.what==1){
                popuPhoneW.dismiss();
            } else if(msg.what ==3){
                String time = msg.getData().getString("time");
                String id = msg.getData().getString("id");
                setWeb(Urls.getInstance().FIT_H5+"/TemperatureHistory/"+time+"/"+id+"/"+MyApplication.token);
            }
            else {
                popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
            }

        }
    };

    public void upLoad(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject data = new JSONObject();
        try {
            data.put("body",temp_body);
            data.put("wrist",temp_wrist);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            data.put("testTime",time);
            jsonArray.put(data);
            jsonObject.put("fitTemperatureList",jsonArray);
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
                            Log.d("上传体温数据", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "体温上传成功", Toast.LENGTH_SHORT).show();
                                agentweb.getWebCreator().getWebView().reload();
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



    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(FitTemperatureActivity.this).inflate(R.layout.popu_fit_health, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new FitTemperatureActivity.poponDismissListener());
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