package net.leelink.healthangelos.activity.T6LS;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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

public class UploadT6LSActivity extends BaseActivity {
    private RelativeLayout rl_back, rl_interval,rl_radio;
    private Context context;
    private TextView text_title, tv_tips;
    private ImageView img_t6ls_minus, img_t6ls_plus;
    private RadioButton rb_close, rb_single, rb_continuous;
    String type = "";
    private EditText ed_time;
    private Button btn_cancel, btn_confirm;
    private Integer period = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_t6_lsactivity);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        text_title = findViewById(R.id.text_title);
        img_t6ls_minus = findViewById(R.id.img_minus);
        img_t6ls_minus.setOnClickListener(this);
        img_t6ls_plus = findViewById(R.id.img_plus);
        img_t6ls_plus.setOnClickListener(this);
        rb_close = findViewById(R.id.rb_close);
        rb_single = findViewById(R.id.rb_single);
        rb_continuous = findViewById(R.id.rb_continuous);
        rl_interval = findViewById(R.id.rl_interval);
        rl_radio = findViewById(R.id.rl_radio);
        ed_time = findViewById(R.id.ed_time);
        ed_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0) {
                    Integer integer = Integer.parseInt(s.toString());
                    if (integer > 720) {
                        ed_time.setText("720");
                    }
                    if (integer < 5) {
                        ed_time.setText("5");
                    }
                }
            }
        });
        tv_tips = findViewById(R.id.tv_tips);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        rb_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rl_interval.setVisibility(View.GONE);
                    tv_tips.setText(type + "上传功能关闭");
                    period = 0;
                }
            }
        });
        rb_single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rl_interval.setVisibility(View.GONE);
                    tv_tips.setText("立即开启" + type + "单次上传，上传完后自动关闭。");
                    period = 1;
                }
            }
        });
        rb_continuous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rl_interval.setVisibility(View.VISIBLE);
                    tv_tips.setText("连续上传" + type + "数据，每隔" + ed_time.getText().toString() + "分钟上传一次。");
                    period = Integer.parseInt(ed_time.getText().toString());
                }
            }
        });


        switch (getIntent().getIntExtra("type", 0)) {
            case 1:
                text_title.setText("位置上报频率");
                type = "位置";
                rl_radio.setVisibility(View.GONE);
                rl_interval.setVisibility(View.VISIBLE);
                period = Integer.parseInt(ed_time.getText().toString());
                break;
            case 3:
                text_title.setText("心率上报频率");
                type = "心率";
                break;
            case 5:
                text_title.setText("体温上报频率");
                type = "体温";
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Integer number = Integer.parseInt(ed_time.getText().toString());
        switch (v.getId()) {
            case R.id.rl_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.img_minus:
                number = number - 5;
                if (number < 5) {
                    number = 5;
                }
                ed_time.setText(String.valueOf(number));
                tv_tips.setText("连续上传" + type + "数据，每隔" + ed_time.getText().toString() + "分钟上传一次。");
                break;
            case R.id.img_plus:
                number = number + 5;
                if (number > 720) {
                    number = 720;
                }
                ed_time.setText(String.valueOf(number));
                tv_tips.setText("连续上传" + type + "数据，每隔" + ed_time.getText().toString() + "分钟上传一次。");
                break;
            case R.id.btn_confirm:
                upload();
                break;
        }
    }

    public void upload() {
        String url = "";
        if(period==-1){
            Toast.makeText(context, "请选择上传模式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("心率")) {
            url = Urls.getInstance().T6LS_HRSTART;
        } else if (type.equals("位置")) {
            url = Urls.getInstance().T6LS_UPLOAD;
            period = Integer.parseInt(ed_time.getText().toString());
        } else if (type.equals("体温")) {
            url = Urls.getInstance().T6LS_TEMPSTART;
        }
        if(rb_continuous.isChecked()){
            period = Integer.parseInt(ed_time.getText().toString());
        }
        JSONObject json  = new JSONObject();
        try {
            json.put("period",period);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d( "upload: ",period+"");
        showProgressBar();
        OkGo.<String>post(url+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置上传频率", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "指令发送成功", Toast.LENGTH_LONG).show();
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