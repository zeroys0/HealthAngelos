package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.BonusAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BonusBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class AlarmDetailActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    int count = 1;
    TextView tv_single_price,tv_count,tv_price;
    ImageView img_minus,img_add;
    Button btn_buy;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        init();
        context = this;
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_single_price = findViewById(R.id.tv_single_price);
        img_minus = findViewById(R.id.img_minus);
        img_minus.setOnClickListener(this);
        tv_count = findViewById(R.id.tv_count);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        tv_price = findViewById(R.id.tv_price);
        tv_single_price.setText(getIntent().getStringExtra("price"));
        tv_price.setText(getIntent().getStringExtra("price"));
        btn_buy = findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_minus:
                if(count <=1) {

                } else {
                    count--;
                    setTotal();
                }
                break;
            case R.id.img_add:
                if(count>=99) {

                } else  {
                    count ++;
                    setTotal();
                }
                break;
            case R.id.btn_buy:
                showpop();
                backgroundAlpha(0.5f);
                break;
        }
    }

    public void setTotal (){
        tv_count.setText(String.valueOf(count));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        float price = Float.valueOf(tv_single_price.getText().toString());
        float total = price * count;
        tv_price.setText(decimalFormat.format(total));

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
        pop.setOnDismissListener(new AlarmDetailActivity.poponDismissListener());
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

        pop.showAtLocation(AlarmDetailActivity.this.findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
    }

    public void pay_service(int payType){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("num",count);
            jsonObject.put("payType",payType);
            jsonObject.put("price",tv_price.getText().toString());
            jsonObject.put("productId",getIntent().getStringExtra("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        OkGo.<String>post(Urls.SERVICE)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject j = new JSONObject(response.body());
                            Log.d("报警支付: ", j.toString());
                            if (j.getInt("status") == 200) {


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
