package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import net.leelink.healthangelos.bean.PayResult;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvestActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    EditText ed_balance;
    ImageView img_clear,img_ali_check,img_wx_check;
    int position = -1;
    private boolean choose =true;
    int type = 1;
    TextView tv_1,tv_2,tv_3,tv_4,tv_5,tv_6;
    List<TextView> list = new ArrayList<>();
    LinearLayout ll_alipay,ll_wxpay;
    Button btn_pay;
    private IWXAPI api;
    String orderInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);
        init();
        api = WXAPIFactory.createWXAPI(this, "wxe21caf2c821161fb");
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_balance = findViewById(R.id.ed_balance);
        img_clear = findViewById(R.id.img_clear);
        img_clear.setOnClickListener(this);
        tv_1 = findViewById(R.id.tv_1);
        tv_1.setOnClickListener(this);
        list.add(tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_2.setOnClickListener(this);
        list.add(tv_2);
        tv_3 = findViewById(R.id.tv_3);
        tv_3.setOnClickListener(this);
        list.add(tv_3);
        tv_4 = findViewById(R.id.tv_4);
        tv_4.setOnClickListener(this);
        list.add(tv_4);
        tv_5 = findViewById(R.id.tv_5);
        tv_5.setOnClickListener(this);
        list.add(tv_5);
        tv_6 = findViewById(R.id.tv_6);
        tv_6.setOnClickListener(this);
        list.add(tv_6);
        ll_alipay = findViewById(R.id.ll_alipay);
        ll_alipay.setOnClickListener(this);
        ll_wxpay = findViewById(R.id.ll_wxpay);
        ll_wxpay.setOnClickListener(this);
        img_ali_check = findViewById(R.id.img_ali_check);
        img_wx_check = findViewById(R.id.img_wx_check);
        btn_pay = findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_clear:
                ed_balance.setText("");
                break;
            case R.id.tv_1:
                setCheck(0);
                ed_balance.setText("10");
                break;
            case R.id.tv_2:
                setCheck(1);
                ed_balance.setText("30");
                break;
            case R.id.tv_3:
                setCheck(2);
                ed_balance.setText("50");
                break;
            case R.id.tv_4:
                setCheck(3);
                ed_balance.setText("300");
                break;
            case R.id.tv_5:
                setCheck(4);
                ed_balance.setText("500");
                break;
            case R.id.tv_6:
                setCheck(5);
                ed_balance.setText("100");
                break;
            case R.id.ll_alipay:
                img_ali_check.setImageResource(R.drawable.img_choose);
                img_wx_check.setImageResource(R.drawable.img_unchoose);
                type = 1;
                break;
            case R.id.ll_wxpay:
                img_ali_check.setImageResource(R.drawable.img_unchoose);
                img_wx_check.setImageResource(R.drawable.img_choose);
                type = 2;
                break;
            case R.id.btn_pay:
                pay();
                break;
        }
    }

    public void pay(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put( "decimal",ed_balance.getText().toString().trim());
            if(type ==1) {
                jsonObject.put( "payType",1);
            } else if(type == 2) {
                jsonObject.put( "payType",2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.ICPAY)
                .upJson(jsonObject)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d( "调起支付: ",j.toString());
                            if(j.getInt("status") == 200) {
                                //调起微信支付
                                if (type==2) {
                                    String s = j.getString("data");
                                    s = s.replaceAll("\\\\","");
                                    j = new JSONObject(s);
                                    if (api != null) {
                                        PayReq req = new PayReq();
                                        req.appId = j.getString("appid");
                                        req.partnerId = j.getString("partnerid");
                                        req.prepayId = j.getString("prepayid");
                                        req.packageValue = j.getString("package");
                                        req.nonceStr = j.getString("noncestr");
                                        req.timeStamp = j.getString("timestamp");
                                        req.sign = j.getString("sign");
                                        Log.d("调起微信支付 ", "\nappid:" + req.appId + "\n" + "partnerid:" + req.partnerId + "\n" + "prepayid:" + req.prepayId + "\n" + "package:" + req.packageValue + "\n" + "noncestr:" + req.nonceStr + "\n" + "timestamp:" + req.timeStamp + "\n" + "sign:" + req.sign + "\n");
                                        api.sendReq(req);
                                    }
                                }
                                //调起支付宝支付
                                else if (type == 1) {
                                    orderInfo = j.getString("data");
                                    Runnable payRunnable = new Runnable() {

                                        @Override
                                        public void run() {
                                            PayTask alipay = new PayTask(InvestActivity.this);
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

                                }

                            } else {
                                Toast.makeText(InvestActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    Handler handler=new Handler(new Handler.Callback() {
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
                        Toast.makeText(InvestActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(InvestActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            return false;
        }
    });

    public void setCheck(int target){
        if(position == target) {
            return;
        }
        list.get(target).setBackgroundResource(R.drawable.blue_bg_stroke);
        list.get(target).setTextColor(getResources().getColor(R.color.blue));
        if(choose) {
            choose = false;
        } else {
            list.get(position).setBackgroundResource(R.drawable.grey_bg_stroke);
            list.get(position).setTextColor(getResources().getColor(R.color.text_black));
        }
        position = target;
    }
}
