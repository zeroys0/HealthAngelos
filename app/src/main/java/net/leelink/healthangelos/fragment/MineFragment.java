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

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.AlarmListActivity;
import net.leelink.healthangelos.activity.BalanceActivity;
import net.leelink.healthangelos.activity.BonusActivity;
import net.leelink.healthangelos.activity.CertificationActivity;
import net.leelink.healthangelos.activity.EquipmentActivity;
import net.leelink.healthangelos.activity.EstimateActivity;
import net.leelink.healthangelos.activity.MyActionActivity;
import net.leelink.healthangelos.activity.MyInfoActivty;
import net.leelink.healthangelos.activity.RepairActivity;
import net.leelink.healthangelos.activity.SetMealActivity;
import net.leelink.healthangelos.activity.SettingActivity;
import net.leelink.healthangelos.activity.WatchDemoActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;
import net.leelink.healthangelos.volunteer.VolunteerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView img_head;
    RelativeLayout rl_equipment, rl_community,rl_estimate,rl_mine,rl_repair,rl_set_meal,rl_balance,rl_alarm,rl_service,rl_old_pension,rl_my_action,rl_volunteer;
    Context context;
    TextView tv_name,tv_sao,tv_old_age_pension,tv_balance,tv_alarm_count,tv_my_cure,tv_my_package,tv_stepNumber,tv_sleepTime,tv_certifical;
    ImageView img_setting;

    public static final String PACK_NAME = "net.leelink.communityclient";//腕宝宝包名

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        context = getContext();
        initData();
        return view;
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
    }

    public void initData() {
        OkGo.<String>get(Urls.INFO)
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
                                Glide.with(context).load(Urls.IMG_URL + json.getString("headImgPath")).into(img_head);
                                StringBuilder sb = new StringBuilder();
                                tv_name.setText(json.getString("name"));
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
                                if(json.has("age")) {
                                    sb.append(json.getInt("age") + "|");
                                }
                                if(json.has("organName")) {
                                    sb.append(json.getString("organName"));
                                }
                                tv_sao.setText(sb);
                                tv_old_age_pension.setText(json.getString("oldProfit"));
                                tv_balance.setText(json.getString("wallet"));
                                tv_alarm_count.setText(json.getString("alermCount"));
                                tv_my_cure.setText(json.getString("queryCount"));
                                tv_my_package.setText(json.getString("mealCount"));
                                tv_stepNumber.setText(json.getString("stepNumber")+"步");
                                tv_sleepTime.setText(json.getString("sleepTime")+"h");


                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
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
            case R.id.rl_equipment:     //设备管理
                Intent intent = new Intent(getContext(), EquipmentActivity.class);
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
            case R.id.img_setting:  //设置
                Intent intent1 = new Intent(getContext(), SettingActivity.class);
                startActivity(intent1);
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
                startActivity(intent6);
                break;
            case R.id.rl_alarm: //报警服务
                Intent intent7 = new Intent(getContext(), AlarmListActivity.class);
                startActivity(intent7);
                break;
            case R.id.rl_service:   //联系客服
                Intent intent8 = new Intent(getContext(), WatchDemoActivity.class);
                startActivity(intent8);
                break;
            case R.id.rl_old_pension:   //养老积分
                Intent intent9 = new Intent(getContext(), BonusActivity.class);
                intent9.putExtra("profit",tv_old_age_pension.getText().toString());
                startActivity(intent9);
                break;
            case R.id.rl_my_action:     //我的活动
                Intent intent10 = new Intent(getContext(),MyActionActivity.class);
                startActivity(intent10);
                break;
            case R.id.tv_certifical:      //实名认证
                Intent intent11 = new Intent(getContext(), CertificationActivity.class);
                startActivity(intent11);
                break;
            case R.id.rl_volunteer:
                Intent intent12 = new Intent(getContext(), VolunteerActivity.class);
                startActivity(intent12);
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
