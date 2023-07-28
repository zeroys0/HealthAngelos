package net.leelink.healthangelos.activity.sleepace;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SleepaceSettingBActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_thickness,tv_material,tv_save;
    List<String> organName = new ArrayList<>();
    private int type = 1;
    private int thickness,material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepace_setting_bactivity);
        context = this;
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_thickness = findViewById(R.id.tv_thickness);
        tv_thickness.setOnClickListener(this);
        tv_material = findViewById(R.id.tv_material);
        tv_material.setOnClickListener(this);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().SLEEPACE_SETTING)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceId",getIntent().getStringExtra("deviceId"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取床垫参数", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                thickness = json.getInt("thickness");
                                material = json.getInt("material");
                                List<String> list =Arrays.asList(getResources().getStringArray(R.array.sleepace_thickness));
                                tv_thickness.setText(list.get(thickness-1));
                                List<String> list1 =Arrays.asList(getResources().getStringArray(R.array.sleepace_material));
                                tv_material.setText(list1.get(material-1));

                            } else if (json.getInt("status") == 505) {
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
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_thickness:
                type = 1;
                showPop();
                break;
            case R.id.tv_material:
                type = 2;
                showPop();
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }

    public void save(){
        OkGo.<String>post(Urls.getInstance().SLEEPACE_SETTING)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceId",getIntent().getStringExtra("deviceId"))
                .params("material",material)
                .params("thickness",thickness)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("床垫参数设置", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
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
                });

    }

    public void showPop(){
        if (organName.size() > 0) {
            organName.clear();
        }
        if (type == 1) {
            organName = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sleepace_thickness)));
        }
        if (type == 2) {
            organName =new ArrayList<>( Arrays.asList(getResources().getStringArray(R.array.sleepace_material)));
        }

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (type ==1){
                    tv_thickness.setText(organName.get(options1));
                    thickness = options1+1;
                }
                if (type == 2) {
                    tv_material.setText(organName.get(options1));
                    material = options1+1;
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }
}