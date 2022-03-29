package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.VolunteerEventBean;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import static net.leelink.healthangelos.volunteer.VolunteerActivity.VOL_ID;


public class MissionDetailActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back, rl_clock_in, rl_clock_in_2;
    private Context context;
    private TextView tv_call;
    private Button btn_confirm;
    private VolunteerEventBean volunteerEventBean;
    private TextView tv_cancel, tv_type, tv_start_time, tv_end_time, tv_address, tv_people, tv_phone, tv_content, tv_title, tv_auditing, tv_reason, tv_reason_2, tv_name, tv_vol_phone, tv_sex, tv_vol_content, tv_name_2, tv_vol_phone_2, tv_sex_2, tv_vol_content_2;
    private ImageView img_head, img_1, img_2, img_3, img_head_2, sec_img_1, sec_img_2, sec_img_3;
    private LinearLayout ll_auiting_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        context = this;
        init();
        initData();
        getInfo();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_call = findViewById(R.id.tv_call);
        tv_call.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setText("个人任务");
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_address = findViewById(R.id.tv_address);
        tv_people = findViewById(R.id.tv_people);
        tv_phone = findViewById(R.id.tv_phone);
        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_auditing = findViewById(R.id.tv_auditing);
        tv_reason = findViewById(R.id.tv_reason);
        tv_reason_2 = findViewById(R.id.tv_reason_2);
        rl_clock_in = findViewById(R.id.rl_clock_in);
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_vol_phone = findViewById(R.id.tv_vol_phone);
        tv_sex = findViewById(R.id.tv_sex);
        img_1 = findViewById(R.id.img_1);
        img_2 = findViewById(R.id.img_2);
        img_3 = findViewById(R.id.img_3);
        tv_vol_content = findViewById(R.id.tv_vol_content);
        ll_auiting_2 = findViewById(R.id.ll_auiting_2);
        tv_name_2 = findViewById(R.id.tv_name_2);
        tv_vol_phone_2 = findViewById(R.id.tv_vol_phone_2);
        tv_sex_2 = findViewById(R.id.tv_sex_2);
        sec_img_1 = findViewById(R.id.sec_img_1);
        sec_img_2 = findViewById(R.id.sec_img_2);
        sec_img_3 = findViewById(R.id.sec_img_3);
        tv_vol_content_2 = findViewById(R.id.tv_vol_content_2);
        rl_clock_in_2 = findViewById(R.id.rl_clock_in_2);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);


    }

    public void initData() {
        volunteerEventBean = (VolunteerEventBean) getIntent().getSerializableExtra("mission");
        tv_title.setText(volunteerEventBean.getServTitle());
        tv_start_time.setText(volunteerEventBean.getStartTime());
        tv_end_time.setText(volunteerEventBean.getServEndTime());
        String s = volunteerEventBean.getServAddress();
        try {
            JSONObject jsonObject = new JSONObject(s);
            tv_address.setText(jsonObject.getString("fullAddress"));
        } catch (JSONException e) {
            e.printStackTrace();
            tv_address.setText(volunteerEventBean.getServAddress());
        }
        tv_people.setText(volunteerEventBean.getServName());
        tv_phone.setText(volunteerEventBean.getServTelephone());
        tv_content.setText(volunteerEventBean.getServContent());
        switch (volunteerEventBean.getState()) {
            case 1:     //可领取
                btn_confirm.setText("领取任务");
                break;
            case 2:     //待开始打卡
                btn_confirm.setText("开始打卡");
                break;
            case 3:     //待结束打卡
                btn_confirm.setText("结束打卡");
                break;
            case 4:     //等待审核
                btn_confirm.setVisibility(View.GONE);
                tv_auditing.setVisibility(View.VISIBLE);
                rl_clock_in.setVisibility(View.VISIBLE);
                break;
            case 5:     //审核已通过
                btn_confirm.setVisibility(View.GONE);
                rl_clock_in.setVisibility(View.VISIBLE);
                break;
            case 6:     //审核未通过
                tv_reason.setVisibility(View.VISIBLE);
                rl_clock_in.setVisibility(View.VISIBLE);
                tv_reason.setText(volunteerEventBean.getCause());
                btn_confirm.setText("重新提交");
                tv_cancel.setVisibility(View.VISIBLE);
                break;
            case 7:     //二次审核中
                tv_reason.setVisibility(View.VISIBLE);
                tv_reason.setText(volunteerEventBean.getCause());
                tv_auditing.setVisibility(View.VISIBLE);
                rl_clock_in.setVisibility(View.VISIBLE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                btn_confirm.setVisibility(View.GONE);
                break;
            case 8:     //结束
                tv_reason.setVisibility(View.VISIBLE);
                btn_confirm.setVisibility(View.GONE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                break;
            case 9:     //二次审核未通过
                tv_reason.setVisibility(View.VISIBLE);
                tv_reason_2.setVisibility(View.VISIBLE);
                rl_clock_in.setVisibility(View.VISIBLE);
                ll_auiting_2.setVisibility(View.VISIBLE);
                rl_clock_in_2.setVisibility(View.VISIBLE);
                btn_confirm.setVisibility(View.GONE);
                break;
            default:

                break;
        }


        try {
            JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
            if (jsonObject==null) {

                btn_confirm.setText("成为志愿者");
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, VolunteerApplyActivity.class);
                        startActivity(intent);
                    }
                });
                return;
            } else if (jsonObject.getInt("state")!=1) {
                btn_confirm.setVisibility(View.GONE);
                return;
            }
            tv_name.setText(jsonObject.getString("volName"));
            tv_name_2.setText(jsonObject.getString("volName"));
            if (jsonObject.getInt("volSex") == 0) {
                tv_sex.setText("男");
                tv_sex_2.setText("男");
            } else {
                tv_sex.setText("女");
                tv_sex_2.setText("女");
            }
            tv_vol_phone.setText(jsonObject.getString("volTelephone"));
            tv_vol_phone_2.setText(jsonObject.getString("volTelephone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getInfo() {
        OkGo.<String>get(Urls.getInstance().VOL_TASK + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询任务详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_end_time.setText(json.getString("servEndTime"));
                                if (!json.isNull("img1Path")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("img1Path")).into(img_1);
                                }
                                if (!json.isNull("img2Path")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("img2Path")).into(img_2);
                                }
                                if (!json.isNull("img3Path")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("img3Path")).into(img_3);
                                }
                                if (!json.isNull("firstAuditPh1")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh1")).into(sec_img_1);
                                }
                                if (!json.isNull("firstAuditPh2")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh2")).into(sec_img_2);
                                }
                                if (!json.isNull("firstAuditPh3")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("firstAuditPh3")).into(sec_img_3);
                                }
                                if (!json.isNull("remark")) {
                                    tv_vol_content.setText(json.getString("remark"));
                                }
                                if (!json.isNull("firstAuditRemark")) {
                                    tv_vol_content_2.setText(json.getString("firstAuditRemark"));
                                }
                                if (!json.isNull("cause")) {
                                    tv_reason.setText(json.getString("cause"));
                                }
                                if (!json.isNull("causeIssue")) {
                                    tv_reason_2.setText(json.getString("causeIssue"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_call:      //拨打电话
                call();
                break;
            case R.id.btn_confirm:
                if (volunteerEventBean.getState() == 1) {      //领取任务
                    if (VOL_ID == volunteerEventBean.getSenderId()) {
                        Toast.makeText(context, "无法领取自己发布的任务", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = Acache.get(context).getAsJSONObject("volunteer");
                    if(jsonObject!=null){
                        try {
                            if(jsonObject.getInt("state")!=1) {
                                Toast.makeText(context, "未成为志愿者,无法接取任务", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    getMission();
                } else if (volunteerEventBean.getState() == 2) {  //开始打卡
                    startMission();
                } else if (volunteerEventBean.getState() == 3) {     //结束打卡
                    Intent intent = new Intent(context, ClockInActivity.class);
                    intent.putExtra("id", volunteerEventBean.getId());
                    startActivity(intent);
                } else if (volunteerEventBean.getState() == 6) {    //申诉任务
                    Intent intent = new Intent(context, ReClockInActivity.class);
                    intent.putExtra("id", volunteerEventBean.getId());
                    startActivity(intent);
                }
                break;
            case R.id.tv_cancel:        //取消申诉
                backgroundAlpha(0.5f);
                showpopu();
                break;
        }
    }

    void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + volunteerEventBean.getServTelephone());
        intent.setData(data);
        startActivity(intent);
    }

    public void getMission() {
        OkGo.<String>post(Urls.getInstance().VOL_ACCEPT + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("接受任务", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "任务接取成功,记得完成哦", Toast.LENGTH_SHORT).show();
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

    //开始打卡
    public void startMission() {
        OkGo.<String>post(Urls.getInstance().VOL_BEGIN + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("开始打卡", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "打卡成功", Toast.LENGTH_SHORT).show();
                                volunteerEventBean.setState(3);
                                btn_confirm.setText("结束打卡");
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

    //放弃申诉
    public void confirmExit() {
        OkGo.<String>post(Urls.getInstance().VOL_TASK_TASKRECHECK_REFUSE + "/" + volunteerEventBean.getId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("放弃申诉", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "放弃了申诉", Toast.LENGTH_SHORT).show();
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

    public void showpopu() {
        View popView = getLayoutInflater().inflate(R.layout.popu_exit_team, null);
        TextView btn_cancel = popView.findViewById(R.id.btn_cancel);
        TextView btn_confirm = popView.findViewById(R.id.btn_confirm);
        TextView tv_title = popView.findViewById(R.id.tv_title);
        tv_title.setText("确定要取消申诉吗?");
        TextView tv_content = popView.findViewById(R.id.tv_content);
        tv_content.setText("放弃申诉后,任务将会被取消,无法再次申诉");
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new MissionDetailActivity.poponDismissListener());
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        btn_confirm.setText("放弃申诉");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExit();
            }
        });
        pop.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
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

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

}
