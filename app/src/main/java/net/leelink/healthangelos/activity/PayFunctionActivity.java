package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.PayResult;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PayFunctionActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Button btn_cancel,btn_confirm;
    private ImageView img_alipay,img_wxpay,img_balance;
    private int payfunction = 4;
    private IWXAPI api;
    Context context;
    String orderInfo;
    TextView tv_price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_function);
        init();
        context = this;
        api = WXAPIFactory.createWXAPI(this, "wxe21caf2c821161fb");
        createProgressBar(this);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        img_alipay = findViewById(R.id.img_alipay);
        img_alipay.setOnClickListener(this);
        img_wxpay = findViewById(R.id.img_wxpay);
        img_wxpay.setOnClickListener(this);
        img_balance = findViewById(R.id.img_balance);
        img_balance.setOnClickListener(this);
        tv_price = findViewById(R.id.tv_price);
        tv_price.setText(getIntent().getStringExtra("actPayPrice"));
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            return false;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_confirm:
                pay();
                break;
            case R.id.img_alipay:
                switchImg(img_alipay);
                break;
            case R.id.img_wxpay:
                switchImg(img_wxpay);
                break;
            case R.id.img_balance:
                switchImg(img_balance);
                break;
        }
    }

    public void pay(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId",getIntent().getStringExtra("orderId"));
            jsonObject.put("payType",payfunction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "pay: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().DOCTOR_PAY)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("确认支付", json.toString());
                            if (json.getInt("status") == 200) {
//调起微信支付
                                if (payfunction == 9) {
                                    String s = json.getString("data");
                                    s = s.replaceAll("\\\\", "");
                                    json = new JSONObject(s);
                                    if (api != null) {
                                        PayReq req = new PayReq();
                                        req.appId = json.getString("appid");
                                        req.partnerId = json.getString("partnerid");
                                        req.prepayId = json.getString("prepayid");
                                        req.packageValue = json.getString("package");
                                        req.nonceStr = json.getString("noncestr");
                                        req.timeStamp = json.getString("timestamp");
                                        req.sign = json.getString("sign");
                                        Log.d("调起微信支付 ", "\nappid:" + req.appId + "\n" + "partnerid:" + req.partnerId + "\n" + "prepayid:" + req.prepayId + "\n" + "package:" + req.packageValue + "\n" + "noncestr:" + req.nonceStr + "\n" + "timestamp:" + req.timeStamp + "\n" + "sign:" + req.sign + "\n");
                                        api.sendReq(req);
                                    }
                                }
                                //调起支付宝支付
                                else if (payfunction == 10) {
                                    orderInfo = json.getString("data");
                                    Runnable payRunnable = new Runnable() {

                                        @Override
                                        public void run() {
                                            PayTask alipay = new PayTask(PayFunctionActivity.this);
                                            Map<String, String> result = alipay.payV2(orderInfo, true);
                                            Message msg = new Message();
                                            msg.what = 0;
                                            msg.obj = result;
                                            handler.sendMessage(msg);
                                        }
                                    };
                                    // 必须异步调用
                                    Thread payThread = new Thread(payRunnable);
                                    payThread.start();

                                } else if (payfunction == 4) {
                                    Toast.makeText(context, "缴费成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancel(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().DOCTOR_CANCEL+"/"+getIntent().getStringExtra("orderId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("取消订单", json.toString());
                            if (json.getInt("status") == 200) {
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void switchImg(ImageView imageView){
        imageView.setImageResource(R.drawable.tick_choose);
        if(imageView == img_alipay){
            img_wxpay.setImageResource(R.drawable.tick);
            img_balance.setImageResource(R.drawable.tick);
            payfunction = 10;
        } else if(imageView == img_wxpay){
            img_alipay.setImageResource(R.drawable.tick);
            img_balance.setImageResource(R.drawable.tick);
            payfunction = 9;
        } else {
            img_alipay.setImageResource(R.drawable.tick);
            img_wxpay.setImageResource(R.drawable.tick);
            payfunction = 4;
        }
    }
}
