package net.leelink.healthangelos.activity.Ys7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class Ys7RemindModeActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_alarm, rl_remind, rl_none;
    private ImageView tick0, tick1, tick2;
    List<ImageView> list = new ArrayList<>();
    int indicate = 0;
    private TextView tv_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys7_remind_mode);
        context = this;
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_alarm = findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        rl_remind = findViewById(R.id.rl_remind);
        rl_remind.setOnClickListener(this);
        rl_none = findViewById(R.id.rl_none);
        rl_none.setOnClickListener(this);
        tick0 = findViewById(R.id.tick0);
        tick1 = findViewById(R.id.tick1);
        tick2 = findViewById(R.id.tick2);
        list.add(tick0);
        list.add(tick1);
        list.add(tick2);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_alarm:
                setCheck(0);
                break;
            case R.id.rl_remind:
                setCheck(1);
                break;
            case R.id.rl_none:
                setCheck(2);
                break;
            case R.id.tv_save:
                setMode();
                break;
        }
    }

    public void setCheck(int position) {
        if (position != indicate) {
            list.get(indicate).setVisibility(View.GONE);
            list.get(position).setVisibility(View.VISIBLE);
            indicate = position;
        }
    }
    public void setMode(){
        OkGo.<String>put(Urls.getInstance().YS_SOUND)
                .tag(this)
                .headers("token",MyApplication.token)
                .params("deviceSerial", getIntent().getStringExtra("imei"))
                .params("type",indicate)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("设置警报模式", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent();
                                intent.putExtra("type",indicate);
                                setResult(1,intent);
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }



}