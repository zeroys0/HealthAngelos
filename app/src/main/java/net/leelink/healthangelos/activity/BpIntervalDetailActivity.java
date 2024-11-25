package net.leelink.healthangelos.activity;

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
import net.leelink.healthangelos.bean.BpIntervalBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.leelink.healthangelos.activity.SkrMainActivity.type;

public class BpIntervalDetailActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back,rl_start_time,rl_end_time;
    int time_type;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private TextView tv_start_time,tv_end_time,tv_delete;
    private Button btn_confirm;
    private EditText ed_freq;
    private JSONObject jsonObject = new JSONObject();
    public static int EDIT_INTERVAL = 1;
    public static int ADD_INTERVAL = 0;
    public int id = 0;
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bp_interval_detail);
        context = this;
        createProgressBar(context);
        init();
        initPickerView();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        data = getIntent().getStringExtra("data");
        type = getIntent().getIntExtra("type",0);

        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_delete = findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        ed_freq = findViewById(R.id.ed_freq);
        if(type == EDIT_INTERVAL){
            tv_delete.setVisibility(View.VISIBLE);
            BpIntervalBean bean = (BpIntervalBean) getIntent().getSerializableExtra("bean");
            tv_start_time.setText(bean.getStartTime());
            tv_end_time.setText(bean.getEndTime());
            ed_freq.setText(bean.getInterval().toString());

        }else if(type == ADD_INTERVAL){

        }
        // 设置 OnFocusChangeListener
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

        btn_confirm  = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);


    }
    private void checkInputRange() {
        try {
            int i = Integer.parseInt(ed_freq.getText().toString());
            if (i > 720) {
                ed_freq.setText("720");
            } else if (i < 30) {
                ed_freq.setText("30");
            }
            ed_freq.setSelection(ed_freq.getText().length());
        } catch (NumberFormatException e) {
            ed_freq.setText("");
            ed_freq.setSelection(0);
        }
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
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    jsonObject.put("startTime",tv_start_time.getText().toString());
                    jsonObject.put("endTime",tv_end_time.getText().toString());
                    jsonObject.put("interval",Integer.parseInt(ed_freq.getText().toString()));
                    if(type == EDIT_INTERVAL){
                        int position  = getIntent().getIntExtra("position",0);

                        jsonArray.put(position,jsonObject);
                    }else if(type == ADD_INTERVAL){
                        jsonArray.put(jsonObject);
                    }
                    save(jsonArray);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.tv_delete:
                int position  = getIntent().getIntExtra("position",0);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(data);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                jsonArray.remove(position);
                save(jsonArray);
                break;
        }
    }
    public void save(JSONArray jsonArray){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().BP_PERIOD + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonArray)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("添加/修改血压评率 ", json.toString());
                            if (json.getInt("status") == 200) {
                                finish();
                                Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
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