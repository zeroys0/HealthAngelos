package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class SkrUpdateActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private TextView tv_latest_version,tv_content,tv_data_size;
    private Button btn_update;
    private View popview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skr_update);
        context = this;
        init();
        getBin();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_latest_version = findViewById(R.id.tv_latest_version);
        tv_latest_version.setText("最新版本:V"+getIntent().getStringExtra("version_name"));
        tv_content = findViewById(R.id.tv_content);
        tv_data_size = findViewById(R.id.tv_data_size);
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_update:
                showUpdate();
                break;
        }
    }

    /**
     * 获取更新包信息
     */

    public void getBin(){
        OkGo.<String>get(Urls.getInstance().BIN)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取更新包信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_content.setText(json.getString("note"));
                                tv_data_size.setText(json.getString("size")+"KB");
                            }else if(json.getInt("status") == 505){
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 更新提示
     * @param
     */
    @SuppressLint("WrongConstant")
    public void showUpdate(){
        popview = LayoutInflater.from(SkrUpdateActivity.this).inflate(R.layout.pop_standard, null);
        PopupWindow popuPhoneW_update = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title = popview.findViewById(R.id.tv_title);
        tv_title.setText("更新注意事项");
        TextView tv_content = popview.findViewById(R.id.tv_content);
        tv_content.setText("升级可能持续较长时间,请确保设备处于电量充足状态.更新时设备不可使用.");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW_update.dismiss();
                Intent intent = new Intent(context,SkrUpActivity.class);
                intent.putExtra("version_name",getIntent().getStringExtra("version_name"));
                intent.putExtra("imei",getIntent().getStringExtra("imei"));
                intent.putExtra("auto",getIntent().getBooleanExtra("auto",false));
                startActivity(intent);
                finish();
            }
        });
        popuPhoneW_update.setFocusable(true);
        popuPhoneW_update.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW_update.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW_update.setOutsideTouchable(true);
        popuPhoneW_update.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW_update.setOnDismissListener(new SkrUpdateActivity.poponDismissListener());
        popuPhoneW_update.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
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

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }

}
