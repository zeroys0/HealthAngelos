package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
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

public class R60DetailSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private List<String> list_scene = new ArrayList<>();
    private EditText ed_smart_high, ed_stay_time, ed_fall_time;
    private String imei;
    public int SET_ANGLE = 1;
    private ImageView img_minus, img_plus;
    boolean check = false;
    private TextView tv_content;
    public static int PLUS = 0;
    public static int MINUS = 1;

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
        ed_smart_high = findViewById(R.id.ed_smart_high);
        ed_stay_time = findViewById(R.id.ed_stay_time);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(R.string.r60_content);
        ed_fall_time = findViewById(R.id.ed_fall_time);
        img_minus = findViewById(R.id.img_minus);
        img_minus.setOnClickListener(this);
        img_plus = findViewById(R.id.img_plus);
        img_plus.setOnClickListener(this);

        ed_smart_high.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    try {
                        Integer high = Integer.parseInt(ed_smart_high.getText().toString().trim());
                        if (high != null && high > 310) {
                            ed_smart_high.setText("310");
                        }
                        if (high != null && high < 170) {
                            ed_smart_high.setText("170");
                        }
                    } catch (Exception e) {

                    }
                    setHigh();
                }
                return false;
            }
        });

        ed_stay_time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    try {
                        Integer high = Integer.parseInt(ed_stay_time.getText().toString().trim());
                        if (high != null && high > 60) {
                            ed_stay_time.setText("60");
                        }
                        if (high != null && high < 1) {
                            ed_stay_time.setText("1");
                        }
                    } catch (Exception e) {

                    }
                    setStayTime();
                }
                return false;
            }
        });
        ed_fall_time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //跌倒时间设置
                    try {
                        Integer high = Integer.parseInt(ed_fall_time.getText().toString().trim());
                        if (high != null && high > 180) {
                            ed_fall_time.setText("180");
                        }
                        if (high != null && high < 5) {

                            ed_fall_time.setText("5");
                        }

                    } catch (Exception e) {

                    }
                    setFallTime();
                }
                return false;
            }
        });
    }


    public void initData() {
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
                                if (!json.isNull("sceneMode")) {
                                    switch (json.getInt("sceneMode")) {
                                        case 1:
                                            //客厅

                                            break;
                                        case 2:
                                            //卧室

                                            break;
                                        case 3:
                                            //卫生间

                                            break;
                                    }
                                }
                                if (!json.isNull("workMode")) {
                                    switch (json.getInt("workMode")) {
                                        case 1:
                                            //工作模式

                                            break;
                                        case 2:
                                            //待机模式

                                            break;
                                        case 3:
                                            //测试模式

                                            break;
                                    }
                                }
                                if (!json.isNull("installAngle")) {


                                }
                                if (!json.isNull("installHeight")) {
                                    ed_smart_high.setText(json.getString("installHeight"));
                                }
                                if (!json.isNull("movingTargetDetectionMaxDistance")) {

                                }
                                if (!json.isNull("staticTargetDetectionMaxDistance")) {

                                }
                                if (!json.isNull("fallSwitch")) {
                                    if (json.getInt("fallSwitch") == 0) {
                                        //   cb_fall.setChecked(false);
                                    } else {
                                        //   cb_fall.setChecked(true);
                                    }
                                }
                                if (!json.isNull("humanSwitch")) {
                                    if (json.getInt("humanSwitch") == 0) {
                                        //  cb_body.setChecked(false);
                                    } else {
                                        //   cb_body.setChecked(true);
                                    }
                                }
                                if (!json.isNull("residentWarningDurationSwitch")) {
                                    if (json.getInt("residentWarningDurationSwitch") == 0) {
                                        //   cb_static.setChecked(false);
                                    } else {
                                        //   cb_static.setChecked(true);
                                    }
                                }
                                if (!json.isNull("residentWarningDuration")) {
                                    ed_stay_time.setText(json.getString("residentWarningDuration"));
                                }
                                if (!json.isNull("fallDuration")) {
                                    ed_fall_time.setText(json.getString("fallDuration"));
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
            case R.id.img_minus:
                count(MINUS);
                break;
            case R.id.img_plus:
                count(PLUS);
                break;
        }
    }

    public void count(int type) {
        Integer high = Integer.parseInt(ed_smart_high.getText().toString().trim());
        if (type == PLUS) {
            high++;
            if (high != null && high > 310) {
                ed_smart_high.setText("310");
            } else {
                ed_smart_high.setText(high + "");
            }
        } else if (type == MINUS) {
            high--;
            if (high != null && high < 170) {
                ed_smart_high.setText("170");
            } else {
                ed_smart_high.setText(high + "");
            }
        }

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
        OkGo.<String>post(Urls.getInstance().INSTALLHEIGHT + "/" + imei + "/" + ed_smart_high.getText().toString())
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
                                Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show();
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
        OkGo.<String>post(Urls.getInstance().RESIDENTWARNINGDURATION + "/" + imei + "/" + ed_stay_time.getText().toString())
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

    public void setFallTime() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().FALLDURATION + "/" + imei + "/" + ed_fall_time.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置跌倒时长", json.toString());
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



//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null) {
//            if (requestCode == SET_ANGLE) {
//                String x = data.getStringExtra("x");
//                String y = data.getStringExtra("y");
//                String z = data.getStringExtra("z");
//              //  tv_angle.setText("(" + x + "," + y + "," + z + ")");
//            }
//        }
//    }
}