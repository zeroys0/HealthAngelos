package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.PayResult;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DoctorPrepayActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private Button btn_confirm;
    Context context;
    private TextView tv_price;
    private IWXAPI api;
    String orderInfo;
    TextView tv_name,tv_duties,tv_department,tv_hospital;
    CircleImageView img_head;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_prepay);
        context = this;
        init();
        createProgressBar(this);
        api = WXAPIFactory.createWXAPI(this, "wxe21caf2c821161fb");
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        tv_price = findViewById(R.id.tv_price);
        tv_price.setText(getIntent().getStringExtra("price"));
        img_head = findViewById(R.id.img_head);
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("img_head")).into(img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_duties = findViewById(R.id.tv_duties);
        tv_duties.setText(getIntent().getStringExtra("dutis"));
        tv_department = findViewById(R.id.tv_department);
        tv_department.setText(getIntent().getStringExtra("department"));
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_hospital.setText(getIntent().getStringExtra("hospital"));
    }

    public void submit() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("actPayPrice", tv_price.getText().toString());

            int size = getIntent().getIntExtra("size", 0);

            if (size > 0) {
                jsonObject.put("imgFirstPath", getIntent().getStringExtra("imgFirstPath"));
            }
            if (size > 1) {
                jsonObject.put("imgSecondPath", getIntent().getStringExtra("imgSecondPath"));
            }
            if (size > 2) {
                jsonObject.put("imgThirdPath", getIntent().getStringExtra("imgThirdPath"));
            }
            if (size > 3) {
                jsonObject.put("imgForthPath", getIntent().getStringExtra("imgForthPath"));
            }
            jsonObject.put("doctorId", getIntent().getStringExtra("doctorId"));
            jsonObject.put("elderlyId", MyApplication.userInfo.getOlderlyId());
            jsonObject.put("orderType", 2);
            jsonObject.put("remark", getIntent().getStringExtra("remark"));
//            jsonObject.put("payMethodId", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("submit: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().DOCTORORDER)
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
                            Log.d("新增医单", json.toString());

                            if (json.getInt("status") == 200) {
                                String orderId = json.getString("data");
                                Intent intent = new Intent(context,PayFunctionActivity.class);
                                intent.putExtra("orderId",orderId);
                                intent.putExtra("actPayPrice", tv_price.getText().toString());
                                intent.putExtra("json",jsonObject.toString());
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                    }
                });
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
