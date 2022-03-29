package net.leelink.healthangelos.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseClassActivity;
import net.leelink.healthangelos.activity.CommunityActionActivity;
import net.leelink.healthangelos.activity.ElectFenceActivity;
import net.leelink.healthangelos.activity.HomeDoctorListActivity;
import net.leelink.healthangelos.activity.HouseDoctorActivity;
import net.leelink.healthangelos.activity.LocationActivity;
import net.leelink.healthangelos.activity.OrganActivity;
import net.leelink.healthangelos.activity.PromptActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.VolunteerActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstLeadFragment extends  BaseFragment implements View.OnClickListener {
    private RelativeLayout rl_fast_cure,rl_elect_fence,rl_house_doctor,rl_location,rl_remind,rl_organ,rl_community_act,rl_subsidy;
    private PopupWindow popuPhoneW;
    private View popview;
    private TextView btn_cancel, btn_confirm;
    Context context;


    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkFontSize();
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        context = getContext();
        popu_head();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void init(View view){
        rl_fast_cure = view.findViewById(R.id.rl_fast_cure);
        rl_fast_cure.setOnClickListener(this);
        rl_elect_fence = view.findViewById(R.id.rl_elect_fence);
        rl_elect_fence.setOnClickListener(this);
        rl_house_doctor = view.findViewById(R.id.rl_house_doctor);
        rl_house_doctor.setOnClickListener(this);
        rl_location = view.findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        rl_remind = view.findViewById(R.id.rl_remind);
        rl_remind.setOnClickListener(this);
        rl_organ = view.findViewById(R.id.rl_organ);
        rl_organ.setOnClickListener(this);
        rl_community_act = view.findViewById(R.id.rl_community_act);
        rl_community_act.setOnClickListener(this);
        rl_subsidy = view.findViewById(R.id.rl_subsidy);
        rl_subsidy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_fast_cure: //极速问诊
                Intent intent = new Intent(getContext(), ChooseClassActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_location:  //腕宝定位
                Intent intent3 = new Intent(getContext(), LocationActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_elect_fence:   //电子围栏
                Intent intent1 = new Intent(getContext(), ElectFenceActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_house_doctor:      //家庭医生
                checkDoctor();
                break;
            case R.id.btn_cancel:
                popuPhoneW.dismiss();
                break;
            case R.id.btn_confirm:
                popuPhoneW.dismiss();
                Intent intent2 = new Intent(getContext(), HomeDoctorListActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_remind:        //腕表提醒
                Intent intent4 = new Intent(getContext(), PromptActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_organ:     //附近机构
                Intent intent5 = new Intent(getContext(), OrganActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_community_act: //社区活动
                Intent intent6 = new Intent(getContext(), CommunityActionActivity.class);
                startActivity(intent6);
                break;
            case R.id.rl_subsidy:   //高龄补贴
                Intent intent12 = new Intent(getContext(), VolunteerActivity.class);
                startActivity(intent12);
                break;
        }
    }

    public void checkDoctor(){

        OkGo.<String>get(Urls.getInstance().APPLYDOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取签约情况", json.toString());
                            if (json.getInt("status") == 200) {
                                    Intent intent2 = new Intent(getContext(), HouseDoctorActivity.class);
                                    startActivity(intent2);
                            }else if(json.getInt("status") == 201) {
                                popuPhoneW.showAtLocation(rl_house_doctor, Gravity.CENTER, 0, 0);
                                backgroundAlpha(0.5f);
                            }else if(json.getInt("status") == 202) {
                                Toast.makeText(context, "医生确认中,请耐心等待", Toast.LENGTH_SHORT).show();
                            }else if(json.getInt("status") == 203) {
                                Intent intent2 = new Intent(getContext(), HouseDoctorActivity.class);
                                startActivity(intent2);
                            }
                            else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(getContext()).inflate(R.layout.popu_home_house_doctor, null);
        btn_cancel = (TextView) popview.findViewById(R.id.btn_cancel);
        btn_confirm = (TextView) popview.findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new poponDismissListener());
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }
}
