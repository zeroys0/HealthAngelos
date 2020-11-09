package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import net.leelink.healthangelos.adapter.ContentAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.PayResult;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MealDetailActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    TextView tv_name,tv_price,tv_time,tv_detail,tv_content;
    Context context;
    RecyclerView content_list;
    ContentAdapter contentAdapter;
    Button btn_buy;
    private IWXAPI api;
    String orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);
        init();
        initData();
        context = this;
        api = WXAPIFactory.createWXAPI(this, "wxe21caf2c821161fb");
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_price = findViewById(R.id.tv_price);
        tv_price.setText(getIntent().getStringExtra("price"));
        tv_time = findViewById(R.id.tv_time);
        tv_time.setText(getIntent().getStringExtra("time"));

        tv_detail = findViewById(R.id.tv_detail);
        tv_detail.setText(getIntent().getStringExtra("detail"));
        tv_content = findViewById(R.id.tv_content);
        content_list = findViewById(R.id.content_list);
        btn_buy = findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(this);

    }

    public void initData(){
        OkGo.<String>get(Urls.MEAL+"/"+getIntent().getStringExtra("id"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("套餐详情", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                contentAdapter = new ContentAdapter(jsonArray,context);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                content_list.setLayoutManager(layoutManager);
                                content_list.setAdapter(contentAdapter);
                            }else if(json.getInt("status") == 505){
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_buy:
                showpop();
                break;
        }
    }

    public void showpop(){
        View popView = getLayoutInflater().inflate(R.layout.popu_pay_function, null);
        LinearLayout ll_alipay = popView.findViewById(R.id.ll_alipay);
        LinearLayout ll_wxpay = popView.findViewById(R.id.ll_wxpay);
        LinearLayout ll_extra = popView.findViewById(R.id.ll_extra);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new MealDetailActivity.poponDismissListener());
        ll_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay_service(1);

            }
        });
        ll_wxpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay_service(2);
            }
        });
        ll_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay_service(3);
            }
        });

        pop.showAtLocation(MealDetailActivity.this.findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
    }

    public void pay_service(int payType){
        JSONObject jsonObject = new JSONObject();
        try {

            if(payType ==2) {       //微信支付*100
                float price = Float.valueOf(tv_price.getText().toString());
                price = price*100;
                jsonObject.put("price",price);
            } else {
                jsonObject.put("price",tv_price.getText().toString());
            }
            jsonObject.put("num",getIntent().getStringExtra("time"));
            jsonObject.put("payType",payType);
            jsonObject.put("productId",getIntent().getStringExtra("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        OkGo.<String>post(Urls.MEAL)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("套餐支付: ", j.toString());
                            if (j.getInt("status") == 200) {
                                //调起微信支付
                                if (payType==2) {
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
                                else if (payType == 1) {
                                    orderInfo = j.getString("data");
                                    Runnable payRunnable = new Runnable() {

                                        @Override
                                        public void run() {
                                            PayTask alipay = new PayTask(MealDetailActivity.this);
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

                                } else if(payType == 3){
                                    Toast.makeText(context, "缴费成功", Toast.LENGTH_SHORT).show();
                                }

                            }else if(j.getInt("status") == 505){
                                reLogin(context);
                            }
                            else {
                                Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
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
}
