package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

public class R60DetailSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private RelativeLayout rl_scene, rl_work_mode, rl_angle;
    private List<String> list_scene = new ArrayList<>();
    private TextView tv_scene, tv_work_mode, tv_angle;
    private EditText ed_high, ed_move_target, ed_static_target,ed_stay_time;
    private SwitchCompat cb_body,cb_fall,cb_static;
    private String imei;
    public int SET_ANGLE = 1;
    boolean check =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60_detail_setting);
        context = this;
        createProgressBar(context);
        init();
        imei = getIntent().getStringExtra("imei");
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_scene = findViewById(R.id.rl_scene);
        rl_scene.setOnClickListener(this);
        rl_work_mode = findViewById(R.id.rl_work_mode);
        rl_work_mode.setOnClickListener(this);
        rl_angle = findViewById(R.id.rl_angle);
        rl_angle.setOnClickListener(this);
        tv_work_mode = findViewById(R.id.tv_work_mode);
        tv_scene = findViewById(R.id.tv_scene);
        tv_scene.setOnClickListener(this);
        tv_angle = findViewById(R.id.tv_angle);
        ed_high = findViewById(R.id.ed_high);
        ed_move_target = findViewById(R.id.ed_move_target);
        ed_static_target = findViewById(R.id.ed_static_target);
        ed_stay_time = findViewById(R.id.ed_stay_time);
        cb_body = findViewById(R.id.cb_body);
        cb_body.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check) {
                    setBody(isChecked);
                }
            }
        });
        cb_fall = findViewById(R.id.cb_fall);
        cb_fall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check) {
                    setFall(isChecked);
                }
            }
        });
        cb_static = findViewById(R.id.cb_static);
        cb_static.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check) {
                    setStatic(isChecked);
                }
            }
        });

        ed_high.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Integer high = Integer.parseInt(s.toString());
                    if (high != null && high > 310) {
                        ed_high.setText("310");
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_high.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    setHigh();
                }
                return false;
            }
        });
        ed_move_target.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    setMoveTarget();
                }
                return false;
            }
        });
        ed_static_target.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    setStaticTarget();
                }
                return false;
            }
        });
        ed_stay_time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    setStayTime();
                }
                return false;
            }
        });
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().R60_PARAMS + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备属性", json.toString());
                            if (json.getInt("status") == 200) {
                               json = json.getJSONObject("data");
                               if(!json.isNull("sceneMode")) {
                                 switch (json.getInt("sceneMode")){
                                     case 1:
                                         tv_scene.setText("客厅");
                                         break;
                                     case 2:
                                         tv_scene.setText("卧室");
                                         break;
                                     case 3:
                                         tv_scene.setText("卫生间");
                                         break;
                                 }
                               }
                                if(!json.isNull("workMode")) {
                                    switch (json.getInt("workMode")){
                                        case 1:
                                            tv_work_mode.setText("工作模式");
                                            break;
                                        case 2:
                                            tv_work_mode.setText("待机模式");
                                            break;
                                        case 3:
                                            tv_work_mode.setText("测试模式");
                                            break;
                                    }
                                }
                                if(!json.isNull("installAngle")){
                                    JSONObject angle = json.getJSONObject("installAngle");
                                    tv_angle.setText("("+angle.getString("x")+","+angle.getString("y")+","+angle.getString("z")+")");
                                }
                                if(!json.isNull("installHeight")){
                                    ed_high.setText(json.getString("installHeight"));
                                }
                                if(!json.isNull("movingTargetDetectionMaxDistance")){
                                    ed_move_target.setText(json.getString("movingTargetDetectionMaxDistance"));
                                }
                                if(!json.isNull("staticTargetDetectionMaxDistance")){
                                    ed_static_target.setText(json.getString("staticTargetDetectionMaxDistance"));
                                }
                                if(!json.isNull("fallSwitch")){
                                    if(json.getInt("fallSwitch")==0){
                                        cb_fall.setChecked(false);
                                    } else {
                                        cb_fall.setChecked(true);
                                    }
                                }
                                if(!json.isNull("humanSwitch")){
                                    if(json.getInt("humanSwitch")==0){
                                        cb_body.setChecked(false);
                                    } else {
                                        cb_body.setChecked(true);
                                    }
                                }
                                if(!json.isNull("residentWarningDurationSwitch")){
                                    if(json.getInt("residentWarningDurationSwitch")==0){
                                        cb_static.setChecked(false);
                                    } else {
                                        cb_static.setChecked(true);
                                    }
                                }
                                if(!json.isNull("residentWarningDuration")){
                                    ed_stay_time.setText(json.getString("residentWarningDuration"));
                                }
                                check = true;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_scene:
                showScene();
                break;
            case R.id.rl_work_mode:
                showMode();
                break;
            case R.id.rl_angle:
                Intent intent = new Intent(this, R60SettingAngleActivity.class);
                intent.putExtra("imei", imei);
                startActivityForResult(intent, SET_ANGLE);
                break;
        }
    }

    /**
     * 选择场景模式
     */
    public void showScene() {
        list_scene.clear();
        list_scene.add("客厅");
        list_scene.add("卧室");
        list_scene.add("卫生间");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_scene.setText(list_scene.get(options1));
                int i = options1 + 1;
                setScene(i);
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_scene);
        pvOptions.show();
    }

    /**
     * 选择工作模式
     */
    public void showMode() {
        list_scene.clear();
        list_scene.add("工作模式");
        list_scene.add("待机模式");
        list_scene.add("测试模式");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_work_mode.setText(list_scene.get(options1));
                int i = options1 + 1;
                setWorkMode(i);
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_scene);
        pvOptions.show();
    }

    /**
     * 设置场景模式
     *
     * @param value
     */
    public void setScene(int value) {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().SCNENMODE + "/" + imei + "/" + value)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置场景模式", json.toString());
                            if (json.getInt("status") == 200) {
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


    public void setWorkMode(int value) {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().WORKMODE + "/" + imei + "/" + value)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置工作模式", json.toString());
                            if (json.getInt("status") == 200) {
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

    public void setHigh() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().INSTALLHEIGHT + "/" + imei + "/" + ed_high.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置安装高度", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setMoveTarget() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().MOVETARGET + "/" + imei + "/" + ed_move_target.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置运动目标最大距离", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setStaticTarget() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().STATICTARGET + "/" + imei + "/" + ed_static_target.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置静止目标最大距离", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setStayTime() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().RESIDENTWARNINGDURATION + "/" + imei + "/" + ed_static_target.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置驻留时长", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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


    public void setBody(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().HUMANSWITCH + "/" + imei + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置人体功能开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setFall(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().FALLSWITCH + "/" + imei + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置跌倒开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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

    public void setStatic(boolean b) {
        int i = 0;
        if (b)
            i = 1;

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().RESIDENTWARNING + "/" + imei + "/" + i)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置静止驻留开关", json.toString());
                            if (json.getInt("status") == 200) {
                                //  Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SET_ANGLE) {
                String x = data.getStringExtra("x");
                String y = data.getStringExtra("y");
                String z = data.getStringExtra("z");
                tv_angle.setText("(" + x + "," + y + "," + z + ")");
            }
        }
    }
}