package net.leelink.healthangelos.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import net.leelink.healthangelos.activity.home.AlarmWayManager;
import net.leelink.healthangelos.activity.home.CycleTypeManager;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.FencePlanBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static net.leelink.healthangelos.activity.ElectFenceActivity.ADD_PLAN;
import static net.leelink.healthangelos.activity.ElectFenceActivity.EDIT_PLAN;

public class RailCreatePlanActivity extends BaseActivity  implements View.OnClickListener
{

    RelativeLayout rl_type,rl_back,rl_circle_type,rl_start_time,rl_end_time,rl_limits,rl_alarm;
    Context mContext;
    TextView tv_type,tv_time,tv_start_time,tv_end_time,tv_limits,tv_create,tv_warning;
    public static final int ONCE_TIME = 1;  //仅一次
    public static final int WEEK_CIRCLE = 2;    //每周循环
    public int type;
    private TimePickerView pvTime, pvTime1,pvTime2;
    private SimpleDateFormat sdf, sdf1,sdf2;
    CheckBox cb_monday,cb_tuesday,cb_wednesday,cb_thursday,cb_friday,cb_saturday,cb_sunday;
    String[] weekDays = new String[7];
    HashMap<Integer,String> map = new HashMap();
    private String  limit_id = "";
    int AlarmWay = 0;
    Button btn_login;
    String cellphoneNumber,emailAddress;
    EditText ed_divide;
    private  String plan_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_create_plan);
        mContext = this;
        init();
        initData();
        initTime();
        initStartTime();
        initEndTime();
    }

    public void init(){
        rl_type = findViewById(R.id.rl_type);
        rl_type.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_type = findViewById(R.id.tv_type);
        rl_circle_type = findViewById(R.id.rl_circle_type);
        rl_circle_type.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        rl_start_time = findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_limits = findViewById(R.id.rl_limits);
        rl_limits.setOnClickListener(this);
        rl_alarm = findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        tv_limits = findViewById(R.id.tv_limits);
        tv_create = findViewById(R.id.tv_create);
        tv_create.setOnClickListener(this);
        ed_divide = findViewById(R.id.ed_divide);
        tv_warning = findViewById(R.id.tv_warning);

    }

    public void initData(){
        if(getIntent().getIntExtra("type",0)==ADD_PLAN) {
            return;
        } else if(getIntent().getIntExtra("type",0)==EDIT_PLAN) {
            FencePlanBean fencePlanBean = getIntent().getParcelableExtra("data");
            if(fencePlanBean.getCycleType() ==1) {
                tv_type.setText("仅一次");
                type =ONCE_TIME;
                tv_time.setText(fencePlanBean.getMonitorDate());
            } else if(fencePlanBean.getCycleType()==2) {
                tv_type.setText("周提醒");
                type = WEEK_CIRCLE;
                tv_time.setText(fencePlanBean.getWeeks());
            }
            tv_start_time.setText(fencePlanBean.getStartTime());
            tv_end_time.setText(fencePlanBean.getStopTime());
            ed_divide.setText(fencePlanBean.getTimeInterval());
            tv_limits.setText(fencePlanBean.getScopeName());
            limit_id = fencePlanBean.getScopeId();
            AlarmWay = fencePlanBean.getAlarmWay();
            switch (AlarmWay) {
                case 1:
                    tv_warning.setText("短信");

                    break;
                case 2:
                    tv_warning.setText("邮箱");

                    break;
                case 3:
                    tv_warning.setText("短信,邮箱");
                    break;
            }
            cellphoneNumber = fencePlanBean.getCellphoneNumber();
            emailAddress = fencePlanBean.getMailAddress();
            plan_id = fencePlanBean.getId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_type:
                chooseType();
                break;
            case R.id.rl_circle_type:
                if(type == ONCE_TIME) {

                    pvTime.show();

                } else if(type == WEEK_CIRCLE) {
                    chooseWeek();
                }
                break;
            case R.id.rl_start_time:
                pvTime1.show();
                break;
            case R.id.rl_end_time:
                pvTime2.show();
                break;
            case R.id.rl_limits:
                Intent intent =new Intent(this,LimitListActivity.class);
                startActivityForResult(intent,1000);
                break;
            case R.id.rl_alarm: //报警方式
                Intent intent2 = new Intent(mContext, RailCreatePlanMsgActivity.class);
//                intent2.putExtra("AlarmWay", AlarmWay);
//                intent2.putExtra("cellphoneNumber", cellphoneNumber);
//                intent2.putExtra("emailAddress", emailAddress);
                startActivityForResult(intent2, 2000);
                break;
            case R.id.tv_create:    //新增 |编辑
                if(AlarmWay != 0) {
                    if(getIntent().getIntExtra("type",0) == ADD_PLAN) {
                        add();
                    } else if(getIntent().getIntExtra("type",0) == EDIT_PLAN) {
                        edit();
                    }
                } else {
                    Toast.makeText(mContext, "请选择报警方式", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void add(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("alarmWay", AlarmWay);
            jsonObject.put("cellphoneNumber",cellphoneNumber);
            jsonObject.put("cycleType", type);
            jsonObject.put("mailAddress", emailAddress);
            jsonObject.put("scopeId", limit_id);
            jsonObject.put("startTime", tv_start_time.getText().toString() );
            jsonObject.put("stopTime", tv_end_time.getText().toString());
            jsonObject.put("timeInterval",ed_divide.getText().toString());
            if(type == ONCE_TIME) {
                jsonObject.put("monitorDate", tv_time.getText().toString());
            } else if(type == WEEK_CIRCLE) {
                jsonObject.put("weeks", tv_time.getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit:alarmWay ",AlarmWay+"" );
        Log.e( "edit:cellphoneNumber ", cellphoneNumber);
        Log.e( "edit:cycleType ",type+"" );
        Log.e( "edit:mailAddress ",emailAddress);
        Log.e( "edit:scopeId ",limit_id);
        Log.e( "edit:startTime ",tv_start_time.getText().toString());
        Log.e( "edit:stopTime ",tv_end_time.getText().toString());
        Log.e( "edit:timeInterval ", ed_divide.getText().toString());
        Log.e( "edit:monitorDate ", tv_time.getText().toString());
        OkGo.<String>post(Urls.ELECTRPLAN)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("新增监控计划", json.toString());
                            if (json.getInt("status") == 200) {

                                finish();

                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void edit(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("alarmWay", AlarmWay);
            jsonObject.put("cellphoneNumber",cellphoneNumber);
            jsonObject.put("cycleType", type);
            jsonObject.put("mailAddress", emailAddress);
            jsonObject.put("scopeId", limit_id);
            jsonObject.put("startTime", tv_start_time.getText().toString() );
            jsonObject.put("stopTime", tv_end_time.getText().toString());
            jsonObject.put("timeInterval",ed_divide.getText().toString());
            jsonObject.put("id",plan_id);
            if(type == ONCE_TIME) {
                jsonObject.put("monitorDate", tv_time.getText().toString());
            } else if(type == WEEK_CIRCLE) {
                jsonObject.put("weeks", tv_time.getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit:alarmWay ",AlarmWay+"" );
        Log.e( "edit:cellphoneNumber ", cellphoneNumber);
        Log.e( "edit:cycleType ",type+"" );
        Log.e( "edit:mailAddress ",emailAddress);
        Log.e( "edit:scopeId ",limit_id);
        Log.e( "edit:startTime ",tv_start_time.getText().toString());
        Log.e( "edit:stopTime ",tv_end_time.getText().toString());
        Log.e( "edit:timeInterval ", ed_divide.getText().toString());
        Log.e( "edit:monitorDate ", tv_time.getText().toString());
        OkGo.<String>put(Urls.ELECTRPLAN)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("编辑监控计划", json.toString());
                            if (json.getInt("status") == 200) {

                                finish();

                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 选择重复方式
     */
    public void chooseType(){
        tv_time.setText("");
        View popView = getLayoutInflater().inflate(R.layout.pop_circle_type, null);
        TextView tv_once  = popView.findViewById(R.id.tv_once);
        TextView tv_week_circle = popView.findViewById(R.id.tv_week_circle);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        tv_once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  仅一次
                tv_type.setText("仅一次");
                if(type != ONCE_TIME) {
                    type = ONCE_TIME;
                    tv_time.setText("");
                    map.clear();
                }

                   pop.dismiss();
            }
        });
        tv_week_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每周都要提醒
                tv_type.setText("周提醒");
                if(type!= WEEK_CIRCLE) {
                    type = WEEK_CIRCLE;
                    tv_time.setText("");
                }

                pop.dismiss();

            }
        });
        pop.showAtLocation(rl_back, Gravity.BOTTOM,0,50);
    }

    String time = "";
    /**
     * 设置每周的某几天循环
     */
    public void chooseWeek(){
        final View popView = getLayoutInflater().inflate(R.layout.pop_week_circle, null);
        cb_monday = popView.findViewById(R.id.cb_monday);
        cb_monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(0,cb_monday.getText().toString());
                } else {
                    map.remove(0);
                }
                showTime();
            }
        });
        cb_tuesday = popView.findViewById(R.id.cb_tuesday);
        cb_tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(1,cb_tuesday.getText().toString());
                } else {
                    map.remove(1);
                }
                showTime();
            }
        });
        cb_wednesday = popView.findViewById(R.id.cb_wednesday);
        cb_wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(2,cb_wednesday.getText().toString());
                } else {
                    map.remove(2);
                }
                showTime();
            }
        });

        cb_thursday = popView.findViewById(R.id.cb_thursday);
        cb_thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(3,cb_thursday.getText().toString());
                } else {
                    map.remove(3);
                }
                showTime();
            }
        });
        cb_friday = popView.findViewById(R.id.cb_friday);
        cb_friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(4,cb_friday.getText().toString());
                } else {
                    map.remove(4);
                }
                showTime();
            }
        });
        cb_saturday = popView.findViewById(R.id.cb_saturday);
        cb_saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(5,cb_saturday.getText().toString());
                } else {
                    map.remove(5);
                }
                showTime();
            }
        });
        cb_sunday = popView.findViewById(R.id.cb_sunday);
        cb_sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(6,cb_sunday.getText().toString());
                } else {
                    map.remove(6);
                }
                showTime();
            }
        });
        btn_login = popView.findViewById(R.id.btn_login);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAtLocation(rl_back, Gravity.BOTTOM,0,0);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == -1) {
            limit_id =   data.getStringExtra("id");
            tv_limits.setText(data.getStringExtra("alias"));

        } else if (requestCode == 2000 && resultCode == -1) {
            AlarmWay = data.getIntExtra("AlarmWay", 0);
            switch (AlarmWay) {
                case 1:
                    tv_warning.setText("短信");
                    break;
                case 2:
                    tv_warning.setText("邮箱");
                    break;
                case 3:
                    tv_warning.setText("短信,邮箱");
                    break;
            }

            cellphoneNumber = data.getStringExtra("cellphoneNumber");
            emailAddress = data.getStringExtra("emailAddress");
        }
    }

    private void initTime() {
        boolean[] type = {true, true, true, false, false, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
            }
        }).setType(type).build();
    }
    private void initStartTime(){
        boolean[] type = {false, false, false, true, true, false};
        sdf1 = new SimpleDateFormat("HH:mm");
        pvTime1 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_start_time.setText(sdf1.format(date));
            }
        }).setType(type).build();
    }
    private void initEndTime(){
        boolean[] type = {false, false, false, true, true, false};
        sdf2 = new SimpleDateFormat("HH:mm");
        pvTime2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_end_time.setText(sdf2.format(date));
            }
        }).setType(type).build();
    }

    public void showTime() {
        time = "";
        for (String value : map.values()) {
            time = time+value+",";
        }
        if(map.size() >0) {
            time = time.substring(0, time.length() - 1);
        } else {
            time = "";
        }
        tv_time.setText(time);
    }
}
