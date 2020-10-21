package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.pattonsoft.pattonutil1_0.views.MyLine;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceBroadcastActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,img_add;
    private ImageView iv_no;
    LinearLayout ll_tips;
    MyLine ml;
    int TemplateId = 0;
    private EditText ed_note;
    Button tv_waitsend,send;
    TextView tv_num,tv_delete;
    String title = "";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_broadcast);
        context = this;
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        iv_no = findViewById(R.id.iv_no);
        iv_no.setOnClickListener(this);
        tv_delete = findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);

        ll_tips = findViewById(R.id.ll_tips);
        ml = findViewById(R.id.ml);
        ml.setOnClickListener(this);
        tv_waitsend  = findViewById(R.id.tv_waitsend);
        tv_waitsend.setOnClickListener(this);
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
                    tv_waitsend.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));
                    send.setBackground(getResources().getDrawable(R.drawable.bg_blue_radius));
                } else {
                    tv_waitsend.setBackground(getResources().getDrawable(R.drawable.expire_button_black));
                    send.setBackground(getResources().getDrawable(R.drawable.expire_button_black));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_no:
                ll_tips.setVisibility(View.GONE);
                break;
            case R.id.ml:
                Intent intent = new Intent(this, ChooseModelActivity.class);
                intent.putExtra("TemplateId", TemplateId);
                startActivityForResult(intent, 10000);
                break;
            case R.id.tv_delete:
                ed_note.setText("");
                break;
            case R.id.tv_waitsend:  //定时发送
                String string = ed_note.getText().toString().trim();
                if (string.length() == 0) {
                    Mytoast.show(context, "请输入需要发送的文本");
                    return;
                }
                Intent intent2 = new Intent(this,SendByTimeActivity.class);
                intent2.putExtra("content",string);

                startActivity(intent2);

                break;
            case R.id.send: //立即发送
                String str = ed_note.getText().toString().trim();
                if (title.length() == 0) {
                    Mytoast.show(context, "请选择模版");
                    return;
                }
                if (str.length() == 0) {
                    Mytoast.show(context, "请输入需要发送的文本");
                    return;
                }
                send(str);
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
            ml.setText2_text(title);
        }
    }

    public void send(String string){
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String t = sdf.format(date);
        try {
            json.put("imei",MyApplication.userInfo.getJwotchImei());
            json.put("msg",ed_note.getText().toString().trim());
            json.put("sendTime",t);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.SENDMESSAGE)
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
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void sendBytime(){

    }

}
