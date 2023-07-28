package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class R60SceneAettingActivity extends BaseActivity{
    private RelativeLayout rl_back,rl_scene;
    private List<String> list_scene = new ArrayList<>();
    private Context context;
    private TextView tv_scene;
    private String imei;
    private EditText ed_move_target,ed_static_target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r60_scene_aetting);
        context = this;
        createProgressBar(context);
        init();
        imei = getIntent().getStringExtra("imei");
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_scene = findViewById(R.id.rl_scene);
        rl_scene.setOnClickListener(this);
        tv_scene = findViewById(R.id.tv_scene);
        ed_move_target = findViewById(R.id.ed_move_target);
        ed_static_target = findViewById(R.id.ed_static_target);

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_scene:
                showScene();
                break;
        }
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
                                            tv_scene.setText("客厅");
                                            break;
                                        case 2:
                                            //卧室
                                            tv_scene.setText("卧室");
                                            break;
                                        case 3:
                                            //卫生间
                                            tv_scene.setText("卫生间");
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

                                }
                                if (!json.isNull("movingTargetDetectionMaxDistance")) {
                                    ed_move_target.setText(json.getString("movingTargetDetectionMaxDistance"));
                                }
                                if (!json.isNull("staticTargetDetectionMaxDistance")) {
                                    ed_static_target.setText(json.getString("staticTargetDetectionMaxDistance"));
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
}