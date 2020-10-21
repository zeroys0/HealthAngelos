package net.leelink.healthangelos.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import net.leelink.healthangelos.R;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView tv_return,  tv_check, tv_sucess, tv_tips;
    private ImageView img_sucess, img_fail;
    private IWXAPI api;
    private String order_id;//订单ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucess);
        api = WXAPIFactory.createWXAPI(this, "");
        api.handleIntent(getIntent(), this);
        init();
    }

    public void init() {
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            tv_sucess = (TextView) findViewById(R.id.tv_sucess);
            tv_tips = (TextView) findViewById(R.id.tv_tips);
            img_sucess = (ImageView) findViewById(R.id.img_sucess);
            tv_sucess.setText(resp.errCode+"");
            switch (resp.errCode) {
                case 0://成功
               //     tv_sucess.setText("订单支付成功!");
                    img_sucess.setVisibility(View.VISIBLE);
                    img_fail.setVisibility(View.GONE);
                    tv_tips.setVisibility(View.VISIBLE);
                    SharedPreferences sp = getSharedPreferences("sp",0);
                    break;
                case -1://失败
               //     tv_sucess.setText("支付失败");
                    img_sucess.setVisibility(View.GONE);
                    img_fail.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    Toast.makeText(this, "-1", Toast.LENGTH_SHORT).show();
                    break;
                case -2://取消
                //    tv_sucess.setText("支付已取消");
                    img_sucess.setVisibility(View.GONE);
                    img_fail.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    Toast.makeText(this, "-2", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;


            default:
                break;
        }
    }


}
