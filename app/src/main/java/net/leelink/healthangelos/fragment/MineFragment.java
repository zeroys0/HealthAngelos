package net.leelink.healthangelos.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.AlarmListActivity;
import net.leelink.healthangelos.activity.BalanceActivity;
import net.leelink.healthangelos.activity.BonusActivity;
import net.leelink.healthangelos.activity.CertificationActivity;
import net.leelink.healthangelos.activity.ContactServiceActivity;
import net.leelink.healthangelos.activity.DoctorOrderActivity;
import net.leelink.healthangelos.activity.EstimateActivity;
import net.leelink.healthangelos.activity.FocusDoctorActivity;
import net.leelink.healthangelos.activity.HealthDataActivity;
import net.leelink.healthangelos.activity.MyActionActivity;
import net.leelink.healthangelos.activity.MyInfoActivty;
import net.leelink.healthangelos.activity.MyWorksActivity;
import net.leelink.healthangelos.activity.RepairActivity;
import net.leelink.healthangelos.activity.SetMealActivity;
import net.leelink.healthangelos.activity.SettingActivity;
import net.leelink.healthangelos.activity.SuggestActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;
import net.leelink.healthangelos.volunteer.VolunteerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView img_head;
    RelativeLayout rl_equipment, rl_community, rl_estimate, rl_mine, rl_repair, rl_set_meal, rl_balance, rl_alarm, rl_service, rl_old_pension, rl_my_action, rl_volunteer, rl_setting, rl_my_order, rl_suggest, rl_step_number, rl_health_data,rl_my_works;
    Context context;
    TextView tv_name, tv_sao, tv_old_age_pension, tv_balance, tv_alarm_count, tv_my_cure, tv_my_package, tv_stepNumber, tv_sleepTime, tv_certifical;
    ImageView img_setting;

    public static final String PACK_NAME = "net.leelink.communityclient";//乐聆社区助手

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        String fontSize = (String) SPUtils.get(context, "font", "");
        if (fontSize.equals("1.3")) {
            getContext().setTheme(R.style.theme_large);
        } else {
            getContext().setTheme(R.style.theme_standard);
        }
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    public void init(View view) {
        img_head = view.findViewById(R.id.img_head);
        tv_name = view.findViewById(R.id.tv_name);
        rl_equipment = view.findViewById(R.id.rl_equipment);
        rl_equipment.setOnClickListener(this);
        rl_community = view.findViewById(R.id.rl_community);
        rl_community.setOnClickListener(this);
        img_head = view.findViewById(R.id.img_head);
        tv_sao = view.findViewById(R.id.tv_sao);
        tv_old_age_pension = view.findViewById(R.id.tv_old_age_pension);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_alarm_count = view.findViewById(R.id.tv_alarm_count);
        tv_my_cure = view.findViewById(R.id.tv_my_cure);
        tv_my_package = view.findViewById(R.id.tv_my_package);
        tv_stepNumber = view.findViewById(R.id.tv_stepNumber);
        tv_sleepTime = view.findViewById(R.id.tv_sleepTime);
        img_setting = view.findViewById(R.id.img_setting);
        img_setting.setOnClickListener(this);
        rl_estimate = view.findViewById(R.id.rl_estimate);
        rl_estimate.setOnClickListener(this);
        rl_mine = view.findViewById(R.id.rl_mine);
        rl_mine.setOnClickListener(this);
        rl_repair = view.findViewById(R.id.rl_repair);
        rl_repair.setOnClickListener(this);
        rl_set_meal = view.findViewById(R.id.rl_set_meal);
        rl_set_meal.setOnClickListener(this);
        rl_balance = view.findViewById(R.id.rl_balance);
        rl_balance.setOnClickListener(this);
        rl_alarm = view.findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        rl_service = view.findViewById(R.id.rl_service);
        rl_service.setOnClickListener(this);
        rl_old_pension = view.findViewById(R.id.rl_old_pension);
        rl_old_pension.setOnClickListener(this);
        rl_my_action = view.findViewById(R.id.rl_my_action);
        rl_my_action.setOnClickListener(this);
        tv_certifical = view.findViewById(R.id.tv_certifical);
        tv_certifical.setOnClickListener(this);
        rl_volunteer = view.findViewById(R.id.rl_volunteer);
        rl_volunteer.setOnClickListener(this);
        rl_setting = view.findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(this);
        rl_my_order = view.findViewById(R.id.rl_my_order);
        rl_my_order.setOnClickListener(this);
        rl_suggest = view.findViewById(R.id.rl_suggest);
        rl_suggest.setOnClickListener(this);
        rl_step_number = view.findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        rl_health_data = view.findViewById(R.id.rl_health_data);
        rl_health_data.setOnClickListener(this);
        rl_my_works = view.findViewById(R.id.rl_my_works);
        rl_my_works.setOnClickListener(this);
    }

    public void initData() {
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
                                if (!json.isNull("headImgPath")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("headImgPath")).into(img_head);
                                }

                                StringBuilder sb = new StringBuilder();
                                if (!json.isNull("name")) {
                                    tv_name.setText(json.getString("name"));
                                }
                                if (json.has("sex")) {
                                    switch (json.getInt("sex")) {
                                        case 0:
                                            sb.append("男|");
                                            break;
                                        case 1:
                                            sb.append("女|");
                                            break;
                                    }
                                }
                                if (json.has("age") || !json.isNull("age")) {
                                    sb.append(json.getInt("age") + "|");
                                }
                                if (json.has("organName")) {
                                    sb.append(json.getString("organName"));
                                }
                                tv_sao.setText(sb);
                                tv_old_age_pension.setText(json.getString("oldProfit"));
                                tv_balance.setText(json.getString("wallet"));
                                tv_alarm_count.setText(json.getString("alermCount"));
                                tv_my_cure.setText(json.getString("queryCount"));
                                tv_my_package.setText(json.getString("mealCount"));
                                tv_stepNumber.setText(json.getString("stepNumber") + "步");
                                tv_sleepTime.setText(json.getString("sleepTime") + "h");
                                json = json.getJSONObject("elderlyUserInfo");
                                if (json.getInt("face") == 0) {
                                    tv_certifical.setText("未认证");
                                } else {
                                    tv_certifical.setText("已认证");
                                }

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
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
            case R.id.rl_equipment:     //关注医生
                Intent intent = new Intent(getContext(), FocusDoctorActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_community:     //跳转到社区应用
                if (isInstallApp(context)) {
                    Intent intent1 = new Intent(Intent.ACTION_MAIN);
                    ComponentName componentName = new ComponentName("net.leelink.communityclient", "net.leelink.communityclient.activity.SplashActivity");
                    intent1.setComponent(componentName);
                    startActivity(intent1);
                } else {
                    Mytoast.show(context, "您没有安装乐聆社区助手,请下载");
                }
                break;
            case R.id.rl_estimate:      //问卷调查
                Intent intent2 = new Intent(getContext(), EstimateActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_mine:      //个人信息
                Intent intent3 = new Intent(getContext(), MyInfoActivty.class);
                startActivity(intent3);
                break;
            case R.id.rl_repair:       //售后维修
                Intent intent4 = new Intent(getContext(), RepairActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_set_meal:      //套餐
                Intent intent5 = new Intent(getContext(), SetMealActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_balance:   //账户余额
                Intent intent6 = new Intent(getContext(), BalanceActivity.class);
                intent6.putExtra("balance", tv_balance.getText().toString());
                startActivity(intent6);
                break;
            case R.id.rl_alarm: //报警服务
                Intent intent7 = new Intent(getContext(), AlarmListActivity.class);
                startActivity(intent7);
                break;
            case R.id.rl_service:   //联系客服
                Intent intent8 = new Intent(getContext(), ContactServiceActivity.class);
                startActivity(intent8);
                break;
            case R.id.rl_old_pension:   //养老积分
                Intent intent9 = new Intent(getContext(), BonusActivity.class);
                intent9.putExtra("profit", tv_old_age_pension.getText().toString());
                startActivity(intent9);
                break;
            case R.id.rl_my_action:     //我的活动
                Intent intent10 = new Intent(getContext(), MyActionActivity.class);

                startActivity(intent10);
                break;
            case R.id.tv_certifical:      //实名认证
                if(tv_certifical.getText().equals("未认证")) {
                    Intent intent11 = new Intent(getContext(), CertificationActivity.class);
                    startActivity(intent11);
                }else {
                    Toast.makeText(context, "您已经认证过了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_volunteer:     //志愿者模块
                Intent intent12 = new Intent(getContext(), VolunteerActivity.class);
                startActivity(intent12);
                break;
            case R.id.rl_setting:       //设置
                Intent intent13 = new Intent(getContext(), SettingActivity.class);
                startActivity(intent13);
                break;
            case R.id.rl_my_order:      //我的医单
                Intent intent14 = new Intent(getContext(), DoctorOrderActivity.class);
                startActivity(intent14);
                break;
            case R.id.rl_suggest:      //意见反馈
                Intent intent15 = new Intent(getContext(), SuggestActivity.class);
                startActivity(intent15);
                break;
            case R.id.rl_step_number:   //运动步数
                Intent intent16 = new Intent(getContext(), HealthDataActivity.class);
                intent16.putExtra("type", 4);
                startActivity(intent16);
                break;
            case R.id.rl_health_data:   //睡眠质量
                Intent intent1 = new Intent(getContext(), HealthDataActivity.class);
                intent1.putExtra("type", 12);
                startActivity(intent1);
                break;
            case R.id.rl_my_works:      //我的作品
                Intent intent11 = new Intent(getContext(), MyWorksActivity.class);
                startActivity(intent11);
                break;
        }
    }

    public static boolean isInstallApp(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals(PACK_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

}
