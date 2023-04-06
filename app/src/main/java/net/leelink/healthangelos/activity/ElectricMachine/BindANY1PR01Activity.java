package net.leelink.healthangelos.activity.ElectricMachine;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.StreetBean;
import net.leelink.healthangelos.city.ScrollerNumberPicker;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BindANY1PR01Activity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, rl_birth;
    private Context context;
    private TextView tv_local, tv_town, tv_birth;
    private AlertDialog dialog;//城市选择
    String county_id_user; //个人住址区id
    String town_id_user; //个人街道id
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private Button btn_cancel, btn_confirm;
    private EditText ed_town_name, ed_town_address, ed_code,ed_name,ed_village_name,ed_village_address,ed_electric,ed_door_id;
    private RadioButton rb_man, rb_woman;
    String organ_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_any1_pr01);
        context = this;
        init();
        getOrganId();
        initPickerView();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_local = findViewById(R.id.tv_local);
        tv_local.setOnClickListener(this);
        tv_town = findViewById(R.id.tv_town);
        tv_town.setOnClickListener(this);
        rl_birth = findViewById(R.id.rl_birth);
        rl_birth.setOnClickListener(this);
        tv_birth = findViewById(R.id.tv_birth);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        ed_town_name = findViewById(R.id.ed_town_name);
        ed_town_address = findViewById(R.id.ed_town_address);
        ed_code = findViewById(R.id.ed_code);
        rb_man = findViewById(R.id.rb_man);
        rb_woman = findViewById(R.id.rb_woman);
        ed_name = findViewById(R.id.ed_name);
        ed_village_name = findViewById(R.id.ed_village_name);
        ed_village_address = findViewById(R.id.ed_village_address);
        ed_electric = findViewById(R.id.ed_electric);
        ed_door_id = findViewById(R.id.ed_door_id);
    }

    public void getOrganId(){
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人中心", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                json = json.getJSONObject("elderlyUserInfo");
                                organ_id = json.getString("organId");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_local:
                chooseLocal();
                break;
            case R.id.tv_town:
                street();
                break;
            case R.id.rl_birth:
                pvTime.show();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                bind();
                break;
        }
    }

    public void bind() {
        if (town_id_user == null) {
            Toast.makeText(context, "请选择所在街道", Toast.LENGTH_SHORT).show();
            return;
        }
        if(organ_id==null || organ_id.equals("")){
            Toast.makeText(context, "用户未选择机构,无法绑定该设备", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("area_id", town_id_user);
            jsonObject.put("area_name", tv_town.getText().toString());
            jsonObject.put("birthday", tv_birth.getText().toString());
            jsonObject.put("com_addr", ed_town_address.getText().toString());
            jsonObject.put("com_name", ed_town_name.getText().toString());
            jsonObject.put("device_id", ed_code.getText().toString());
            jsonObject.put("elderly_id", MyApplication.userInfo.getOlderlyId());
            if (rb_man.isChecked())
                jsonObject.put("gender", 1);
            if (rb_woman.isChecked())
                jsonObject.put("gender", 0);
            jsonObject.put("name", ed_name.getText().toString());
            jsonObject.put("neigh_addr", ed_village_address.getText().toString());
            jsonObject.put("neigh_name", ed_village_name.getText().toString());
            jsonObject.put("organ_id", organ_id);
            jsonObject.put("rated_current", ed_electric.getText().toString());
            jsonObject.put("room", ed_door_id.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().ANY1_REGISTER)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定电力脉象仪", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "绑定完成", Toast.LENGTH_SHORT).show();
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
                });
    }

    public void chooseLocal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_FS);
        View view = LayoutInflater.from(context).inflate(R.layout.addressdialog, null);
        builder.setView(view);
        TextView addressdialog_linearlayout = (TextView) view.findViewById(R.id.addressdialog_linearlayout);
        final ScrollerNumberPicker provincePicker = (ScrollerNumberPicker) view.findViewById(R.id.province);
        final ScrollerNumberPicker cityPicker = (ScrollerNumberPicker) view.findViewById(R.id.city);
        final ScrollerNumberPicker counyPicker = (ScrollerNumberPicker) view.findViewById(R.id.couny);
        dialog = builder.show();

        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
        addressdialog_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String province = provincePicker.getSelectedText();
                String city = cityPicker.getSelectedText();
                String couny = counyPicker.getSelectedText();
                county_id_user = counyPicker.getSelected();

                tv_local.setText(province + city + couny);
                dialog.dismiss();
            }
        });
    }

    //街道选择
    public void street() {
        OkGo.<String>get(Urls.getInstance().GETTOWN)
                .tag(this)
                .params("id", county_id_user)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询街道", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<StreetBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<StreetBean>>() {
                                }.getType());
                                showStreet(list);

                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //弹出街道列表

    public void showStreet(final List<StreetBean> list) {
        List<String> streetName = new ArrayList<>();
        for (StreetBean streetBean : list) {
            streetName.add(streetBean.getTown());

        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_town.setText(list.get(options1).getTown());
                    town_id_user = list.get(options1).getId();
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(streetName);
        pvOptions.show();
    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean[] type = {true, true, true, false, false, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_birth.setText(sdf.format(date));
                // setLocateTime();
            }
        }).setType(type).build();
    }

}