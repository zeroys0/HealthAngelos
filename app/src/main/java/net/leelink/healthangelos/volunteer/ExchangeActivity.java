package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

public class ExchangeActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    private LinearLayout rl_start_time, rl_end_time, rl_address, ll_num;
    private TimePickerView pvTime, pvTime1, pvTime2;
    private SimpleDateFormat sdf, sdf1;
    private TextView tv_start_time, tv_end_time, tv_address, tv_type, tv_auditing, tv_count, tv_minute, tv_reason;
    private EditText ed_content;
    public static int ADDRESS = 2;
    public static int DETAIL = 3;
    EditText ed_phone, ed_name, ed_title;
    Context context;
    Button btn_submit;
    private boolean isSingle = true;
    private String id;
    private boolean dhstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        init();
        initData();

        context = this;
        createProgressBar(context);
        initPickerView1();
        initPickerView2();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_address = findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        tv_address = findViewById(R.id.tv_address);
        ed_content = findViewById(R.id.ed_content);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setOnClickListener(this);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_auditing = findViewById(R.id.tv_auditing);
        tv_auditing.setOnClickListener(this);
        tv_count = findViewById(R.id.tv_count);
        tv_count.setOnClickListener(this);
        ed_phone = findViewById(R.id.ed_phone);
        ll_num = findViewById(R.id.ll_num);
        tv_minute = findViewById(R.id.tv_minute);
        ed_name = findViewById(R.id.ed_name);
        ed_title = findViewById(R.id.ed_title);
        tv_reason = findViewById(R.id.tv_reason);


    }

    public void initData() {
        OkGo.<String>get(Urls.getInstance().VOL_INFOS_COUNT)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询志愿者时间信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_minute.setText(json.getString("workTime"));

                            } else if (json.getInt("status") == 201) {

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
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });


        OkGo.<String>get(Urls.getInstance().VOL_LIST_COIN)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询申请中的志愿者任务", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("dhstate") == 0) {      //未申请兑换

                                } else {
                                    if (json.getInt("dhstate") == 1) {     //申请兑换个人任务
                                        tv_type.setText("个人任务");
                                        ll_num.setVisibility(View.GONE);
                                        isSingle = true;
                                        dhstate = true;

                                    }
                                    if (json.getInt("dhstate") == 2) {     //申请兑换团队任务
                                        tv_type.setText("团队任务");
                                        ll_num.setVisibility(View.VISIBLE);
                                        isSingle = false;
                                        dhstate = false;

                                    }
                                    json = json.getJSONObject("jgVolSend");
                                    tv_start_time.setText(json.getString("servStartTime"));
                                    tv_end_time.setText(json.getString("servEndTime"));
                                    tv_address.setText(json.getString("servAddress"));
                                    ed_name.setText(json.getString("servName"));
                                    ed_phone.setText(json.getString("servTelephone"));
                                    ed_title.setText(json.getString("servTitle"));
                                    ed_content.setText(json.getString("servContent"));
                                    if (json.getInt("state") == 0) {
                                        btn_submit.setVisibility(View.GONE);
                                        tv_auditing.setVisibility(View.VISIBLE);
                                    } else if (json.getInt("state") == -1) {
                                        btn_submit.setVisibility(View.VISIBLE);
                                        tv_auditing.setVisibility(View.GONE);
                                        tv_reason.setVisibility(View.VISIBLE);
                                        tv_reason.setText(json.getString("causeIssue"));
                                        id = json.getString("id");
                                    } else {
                                        btn_submit.setVisibility(View.GONE);
                                        tv_auditing.setVisibility(View.VISIBLE);
                                        tv_auditing.setText("任务进行中...");
                                    }
                                }


                            } else if (json.getInt("status") == 201) {

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
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_start_time:
                pvTime1.show();
                break;
            case R.id.rl_end_time:
                pvTime2.show();
                break;
            case R.id.rl_address:
                Intent intent = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(intent, ADDRESS);
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.tv_type:      //选择任务类型
                showpopu();
                break;
            case R.id.tv_count:     //选择参与人数
                showNum();
                break;
        }
    }

    public void submit() {
        JSONObject jsonObject = new JSONObject();
        if (tv_type.getText().toString().equals("")) {
            Toast.makeText(context, "请选择类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_start_time.getText().toString().equals("")) {
            Toast.makeText(context, "请选择开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_end_time.getText().toString().equals("")) {
            Toast.makeText(context, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_address.getText().toString().equals("")) {
            Toast.makeText(context, "请选择服务地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ed_phone.getText().toString().equals("")) {
            Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ed_content.getText().toString().equals("")) {
            Toast.makeText(context, "请描述服务内容", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            jsonObject.put("servAddress", tv_address.getText().toString().trim());

            jsonObject.put("servName", ed_name.getText().toString().trim());
            jsonObject.put("servTelephone", ed_phone.getText().toString().trim());
            jsonObject.put("servTitle", ed_title.getText().toString().trim());
            if (getIntent().getStringExtra("id") != null) {     //发布人id
                jsonObject.put("senderId", getIntent().getStringExtra("id"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url;
        if (isSingle) {
            //发布个人任务
            try {
                jsonObject.put("servEndTime", tv_end_time.getText().toString().trim() + ":00");
                jsonObject.put("servStartTime", tv_start_time.getText().toString().trim() + ":00");
                jsonObject.put("servContent", ed_content.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            url = Urls.getInstance().VOL_TASK;
        } else {
            //发布团队任务
            url = Urls.getInstance().VOL_TEAM_USER;
            try {
                //团队任务添加需要人数
                String count = tv_count.getText().toString().trim();
                count = count.substring(0, count.length() - 1);
                jsonObject.put("num", count);
                jsonObject.put("endTime", tv_end_time.getText().toString().trim() + ":00");
                jsonObject.put("startTime", tv_start_time.getText().toString().trim() + ":00");
                jsonObject.put("content", ed_content.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("submit: ", jsonObject.toString());
        LoadDialog.start(context);
        OkGo.<String>post(url)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发布任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发布完成,请等待审核", Toast.LENGTH_SHORT).show();
                                btn_submit.setVisibility(View.GONE);
                                tv_auditing.setVisibility(View.VISIBLE);
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
                        LoadDialog.stop();
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 2) {
            tv_address.setText(data.getStringExtra("address"));
        }
        if (resultCode == 3) {
            ed_content.setText(data.getStringExtra("detail"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initPickerView1() {
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean[] type = {true, true, true, true, true, false};
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_start_time.setText(sdf1.format(date));

            }
        }).setType(type).build();
    }

    private void initPickerView2() {
        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean[] type = {true, true, true, true, true, false};
        pvTime2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_end_time.setText(sdf1.format(date));

            }
        }).setType(type).build();
    }

    public void showpopu() {
        View popView = getLayoutInflater().inflate(R.layout.view_mission_type, null);
        LinearLayout ll_create_plan = (LinearLayout) popView.findViewById(R.id.ll_create_plan);
        LinearLayout ll_create_scope = (LinearLayout) popView.findViewById(R.id.ll_create_scope);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_create_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_type.setText("团队任务");
                ll_num.setVisibility(View.VISIBLE);
                pop.dismiss();
                isSingle = false;
            }
        });
        ll_create_scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_type.setText("个人任务");
                ll_num.setVisibility(View.GONE);
                pop.dismiss();
                isSingle = true;
            }
        });

        pop.showAsDropDown(tv_type);
    }

    //性别选择器
    public void showNum() {
        List<String> list_num = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list_num.add(String.valueOf(i));
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_count.setText(list_num.get(options1) + "人");
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_num);
        pvOptions.show();
    }

}
