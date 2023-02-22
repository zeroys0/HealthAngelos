package net.leelink.healthangelos.activity.ZW011;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ChooseAdapter;
import net.leelink.healthangelos.adapter.OnDeviceChooseListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UploadFreqActivity extends BaseActivity implements OnDeviceChooseListener, View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_frequency;
    private TextView text_title,tv_title,tv_confirm,tv_frequency;
    private SwitchCompat cb;
    private RecyclerView user_list;
    PopupWindow pop;
    private ChooseAdapter chooseAdapter;
    private int position;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_freq);
        context = this;
        createProgressBar(this);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_title = findViewById(R.id.text_title);
        tv_title = findViewById(R.id.tv_title);
        tv_frequency = findViewById(R.id.tv_frequency);
        cb = findViewById(R.id.cb);
        switch (getIntent().getIntExtra("type",0)){
            case 1:
                text_title.setText("位置上报频率");
                tv_title.setText("位置上报");
                break;
            case 2:
                text_title.setText("计步上报频率");
                tv_title.setText("计步上报");
                break;
            case 3:
                text_title.setText("心率上报频率");
                tv_title.setText("心率上报");
                break;
            case 4:
                text_title.setText("血氧上报频率");
                tv_title.setText("血氧上报");
                break;
            default:
                break;
        }
        if(getIntent().getIntExtra("cb",0)==0){
            cb.setChecked(false);
        }else {
            cb.setChecked(true);
        }
        tv_frequency.setText(getIntent().getStringExtra("fre")+"分钟/次");
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setStepTarget(isChecked);
            }
        });
        rl_frequency = findViewById(R.id.rl_frequency);
        rl_frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(0.5f);
                showRun();
            }
        });
    }

    public void setFrequency() {
        String code = "";
        if(getIntent().getIntExtra("type",0)==1){
            code = "ZW106_LOCATION";
        } else if(getIntent().getIntExtra("type",0)==2){
            code = "ZW107_STEPS";
        } else if(getIntent().getIntExtra("type",0)==3){
            code = "ZW143_HR";
        } else if(getIntent().getIntExtra("type",0)==4){
            code = "ZW118_SPO";
        }
        int freq = 0;
        switch (position){
            case 0:
                freq = 5;
                break;
            case 1:
                freq = 15;
                break;
            case 2:
                freq = 30;
                break;
            case 3:
                freq = 60;
                break;
            case 4:
                freq = 120;
                break;
            case 5:
                freq = 180;
                break;
        }
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().FREQUENCY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .params("freq",freq)
                .params("code",code)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置上报频率", json.toString());
                            if (json.getInt("status") == 200) {
                                String freq = list.get(position)+"/次";
                                tv_frequency.setText(freq);
                                EventBus.getDefault().post(new SettingBean(cb.isChecked(),list.get(position),getIntent().getIntExtra("type",0)));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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
                    }
                });
    }

    /**
     * 设置各项开关
     */
    public void setStepTarget(boolean b) {
        String on =  "";
        if(b){
            on = "ON";
        }else {
            on = "OFF";
        }
        String code = "";
        if(getIntent().getIntExtra("type",0)==1){
            code = "ZW109_TRACK";
        } else if(getIntent().getIntExtra("type",0)==2){
            code = "ZW110_STEP";
        } else if(getIntent().getIntExtra("type",0)==2){
            code = "ZW112_HR";
        } else if(getIntent().getIntExtra("type",0)==2){
            code = "ZW114_SPO";
        }
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().ZW_SWITCH)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",getIntent().getStringExtra("imei"))
                .params("code",code)
                .params("onOff",on)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置开关", json.toString());
                            if (json.getInt("status") == 200) {
                                EventBus.getDefault().post(new SettingBean(cb.isChecked(),tv_frequency.getText().toString(),getIntent().getIntExtra("type",0)));
                                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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
                    }
                });
    }

    public void showRun() {
        View popView = getLayoutInflater().inflate(R.layout.popu_target, null);

        user_list = popView.findViewById(R.id.user_list);
        TextView tv_title = popView.findViewById(R.id.tv_title);
        tv_title.setText("设置频率");
        String[] run = {};
        run = getResources().getStringArray(R.array.freq);
        TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        TextView tv_confirm = popView.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        list = Arrays.asList(run);
        chooseAdapter = new ChooseAdapter(list, context, UploadFreqActivity.this, 0);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        user_list.setAdapter(chooseAdapter);
        user_list.setLayoutManager(layoutManager);
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, 600, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new UploadFreqActivity.poponDismissListener());

        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 100);
    }

    @Override
    public void onItemClick(View view, int type) {
        int position = user_list.getChildLayoutPosition(view);
        chooseAdapter.setChecked(position);
        this.position = position;
    }

    @Override
    public void onChooseClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                setFrequency();
                pop.dismiss();
                break;
        }
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

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