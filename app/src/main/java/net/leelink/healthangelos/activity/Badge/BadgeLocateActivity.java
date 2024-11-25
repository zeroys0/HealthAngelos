package net.leelink.healthangelos.activity.Badge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BadgeLocateActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_start_time,rl_end_time;
    int time_type;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private TextView tv_start_time,tv_end_time;
    private Button btn_confirm;
    private EditText ed_freq;
    private JSONObject jsonObject;
    public int type = 0;
    public int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_locate);
        context = this;
        createProgressBar(context);
        init();
        initPickerView();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        btn_confirm  = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        ed_freq = findViewById(R.id.ed_freq);
        ed_freq.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText 失去焦点时进行范围检查
                    checkInputRange();
                }
            }
        });
        // 设置点击监听器，点击布局的任意地方时让 EditText 失去焦点
        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_freq.isFocused()) {
                    ed_freq.clearFocus();
                }
            }
        });

        // 防止点击 EditText 时触发点击事件
        ed_freq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 不做任何操作
            }
        });
        // 处理触摸事件，防止点击 EditText 时触发点击事件
        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ed_freq.isFocused()) {
                        ed_freq.clearFocus();
                    }
                }
                return false;
            }
        });
    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BADGE_POSITION+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据imei获取定位设置", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                if(jsonArray.length()>0){
                                    type = 1;
                                    id = jsonArray.getJSONObject(0).getInt("id");
                                    tv_start_time.setText(jsonArray.getJSONObject(0).getString("beginTime"));
                                    tv_end_time.setText(jsonArray.getJSONObject(0).getString("endTime"));
                                    ed_freq.setText(jsonArray.getJSONObject(0).getString("freq"));
                                } else {
                                    type = 0;
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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
        if (ed_freq.isFocused()) {
            ed_freq.clearFocus();
        }
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_start_time:
                pvTime.show();
                time_type = 1;
                break;
            case R.id.rl_end_time:
                pvTime.show();
                time_type = 2;
                break;
            case R.id.btn_confirm:
                if(type == 1){
                    setLocate(id);
                }else {
                    addLocate();
                }

                break;
        }
    }
    private void checkInputRange() {
        try {
            int i = Integer.parseInt(ed_freq.getText().toString());
            if (i > 1440) {
                ed_freq.setText("1440");
            } else if (i < 1) {
                ed_freq.setText("1");
            }
            ed_freq.setSelection(ed_freq.getText().length());
        } catch (NumberFormatException e) {
            ed_freq.setText("");
            ed_freq.setSelection(0);
        }
    }

    public void setLocate(int id){
        showProgressBar();
        OkGo.<String>put(Urls.getInstance().BADGE_POSITION)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("beginTime",tv_start_time.getText().toString().trim())
                .params("endTime",tv_end_time.getText().toString().trim())
                .params("freq",ed_freq.getText().toString().trim())
                .params("id",id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改定时频率", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                synchronization();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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


    public void addLocate(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BADGE_POSITION)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("beginTime",tv_start_time.getText().toString().trim())
                .params("endTime",tv_end_time.getText().toString().trim())
                .params("freq",ed_freq.getText().toString().trim())
                .params("imei",getIntent().getStringExtra("imei"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("新增定时频率", json.toString());
                            if (json.getInt("status") == 200) {
                                synchronization();
                                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                                type = 1;
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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

    public void synchronization(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BADGE_POSITION+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("同步腕表定位设置", json.toString());
                            if (json.getInt("status") == 200) {

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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

    private void initPickerView() {
        sdf = new SimpleDateFormat("HH:mm");
        boolean[] type = {false, false, false, true, true, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_start_time.setText(sdf.format(date));

                }
                if (time_type == 2) {
                    tv_end_time.setText(sdf.format(date));
                }
               // setLocateTime();
            }
        }).setType(type).build();
    }

}