package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.pattonsoft.pattonutil1_0.util.Mytoast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.widget.SwitchCompat;

public class VoiceBroadcastActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,img_add;
//    private ImageView iv_no;
//    LinearLayout ll_tips;
    TextView ml;
    int TemplateId = 0;
    private EditText ed_note;
//    tv_waitsend
    Button send;
    TextView tv_num,tv_delete,tv_time;
    String title = "";
    Context context;
    private SwitchCompat cb_send_time,cb_save;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_broadcast);
        context = this;
        init();
        initTime();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_delete = findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        ml = findViewById(R.id.ml);
        ml.setOnClickListener(this);
//        tv_waitsend  = findViewById(R.id.tv_waitsend);
//        tv_waitsend.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        cb_send_time = findViewById(R.id.cb_send_time);
        cb_send_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pvTime.show();
                }else {
                    tv_time.setVisibility(View.INVISIBLE);
                }
            }
        });
        imei = getIntent().getStringExtra("imei");
        cb_save = findViewById(R.id.cb_save);
        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        tv_num = findViewById(R.id.tv_num);
        ed_note =  findViewById(R.id.ed_note);
        ed_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int a = ed_note.getText().toString().trim().length();
                tv_num.setText((150 - a) + "/150");
                if (a > 0) {
//                    tv_waitsend.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));
                    send.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));

                } else {
//                    tv_waitsend.setBackground(getResources().getDrawable(R.drawable.expire_button_black));
                    send.setBackground(getResources().getDrawable(R.drawable.expire_button_black));
                }
            }
        });
        imei = getIntent().getStringExtra("imei");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ml:
                Intent intent = new Intent(this, ChooseModelActivity.class);
                intent.putExtra("TemplateId", TemplateId);
                startActivityForResult(intent, 10000);
                break;
            case R.id.tv_delete:
                ed_note.setText("");
                break;
//            case R.id.tv_waitsend:  //定时发送
//                String string = ed_note.getText().toString().trim();
//                if (string.length() == 0) {
//                    Mytoast.show(context, "请输入需要发送的文本");
//                    return;
//                }
//                Intent intent2 = new Intent(this,SendByTimeActivity.class);
//                intent2.putExtra("content",string);
//
//                startActivity(intent2);
//
//                break;
            case R.id.send: //发送
                String str = ed_note.getText().toString().trim();
//                if (title.length() == 0) {
//                    Mytoast.show(context, "请选择模版");
//                    return;
//                }
                if (str.length() == 0) {
                    Mytoast.show(context, "请输入需要发送的文本");
                    return;
                }


                if(cb_send_time.isChecked()) {  //定时发送
                    if(tv_time.getText().toString().equals("")){    //未选择时间
                        Toast.makeText(context, "请选择发送时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendByTime();   //选择了时间 定时发送
                    return;
                }  else {
                    send(str);
                }
                break;
            case R.id.img_add:
                Intent intent1 = new Intent(this,BroadcastRecordActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == -1) {
            String content = data.getStringExtra("content");
            ed_note.setText(content);
            TemplateId = data.getIntExtra("TemplateId", 0);
            title = data.getStringExtra("title");
        //    ml.setText(title);
        }
    }

    public void send(String string){
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String t = sdf.format(date);
        try {
            if(imei ==null) {
                imei =  MyApplication.userInfo.getJwotchImei();
            }
            json.put("imei", imei);
            json.put("msg",ed_note.getText().toString().trim());
            json.put("sendTime",t);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().SENDMESSAGE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送即时消息", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                if(cb_save.isChecked()){
                                    Intent intent = new Intent(context,SaveTemplateActivity.class);
                                    startActivity(intent);
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
                });
    }

    private void initTime() {
        boolean[] type = {true, true, true, true, true, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
                tv_time.setVisibility(View.VISIBLE);
            }
        }).setType(type).build();
    }
    public void sendByTime(){
        JSONObject json = new JSONObject();

        try {
            if(imei ==null) {
               imei =  MyApplication.userInfo.getJwotchImei();
            }
            json.put("imei", imei);
            json.put("msg",ed_note.getText().toString().trim());
            json.put("sendTime",tv_time.getText().toString().trim()+":00");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().SENDMESSAGEBYTIME)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送定时消息", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                if(cb_save.isChecked()){
                                    Intent intent = new Intent(context,SaveTemplateActivity.class);
                                    startActivity(intent);
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
                });
    }

}
