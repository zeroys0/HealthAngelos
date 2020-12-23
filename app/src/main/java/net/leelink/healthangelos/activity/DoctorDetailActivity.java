package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import static net.leelink.healthangelos.app.MyApplication.getContext;

public class DoctorDetailActivity extends BaseActivity implements View.OnClickListener
{
    RelativeLayout rl_back;
    Button btn_confirm;
    Context context;
    TextView tv_follow,tv_name,tv_duties,tv_department,tv_hospital,tv_score,tv_count,tv_collect_count;
    CircleImageView img_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        context = this;
        init();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_follow = findViewById(R.id.tv_follow);
        tv_follow.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("img_head")).into(img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_duties = findViewById(R.id.tv_duties);
        tv_duties.setText(getIntent().getStringExtra("dutis"));
        tv_department = findViewById(R.id.tv_department);
        tv_department.setText(getIntent().getStringExtra("department"));
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_hospital.setText(getIntent().getStringExtra("hospital"));
        tv_score = findViewById(R.id.tv_score);
        tv_score.setText(getIntent().getStringExtra("score"));
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(getIntent().getStringExtra("count")+"");
        tv_collect_count = findViewById(R.id.tv_collect_count);
        tv_collect_count.setText(getIntent().getIntExtra("visit",0)+"");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                backgroundAlpha(0.5f);
                showPopup();
                break;
            case R.id.tv_follow:
                if(tv_follow.getText().equals("关注")) {
                    follow();
                }else {
                    notFollow();
                }
                break;
        }
    }

    public void follow(){
        OkGo.<String>post(Urls.getInstance().FOLLOW+"/"+getIntent().getStringExtra("doctorId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生列表", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已关注", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("已关注");
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

    public void notFollow(){
        OkGo.<String>delete(Urls.getInstance().FOLLOW+"/"+getIntent().getStringExtra("doctorId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生关注", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "已取关", Toast.LENGTH_SHORT).show();
                                tv_follow.setText("关注");
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

    public void showPopup(){
        View popView = getLayoutInflater().inflate(R.layout.popu_choose_cure, null);
        RelativeLayout rl_picture = popView.findViewById(R.id.rl_picture);
        RelativeLayout rl_phone = popView.findViewById(R.id.rl_phone);
        TextView tv_message_price = popView.findViewById(R.id.tv_message_price);
//        tv_message_price.setText("￥"+list.get(position).getCareDoctorRegedit().getImgPrice());
        TextView tv_phone_price = popView.findViewById(R.id.tv_phone_price);
//        tv_phone_price.setText("￥"+list.get(position).getCareDoctorRegedit().getPhonePrice());
        ImageView img_close = popView.findViewById(R.id.img_close);
        tv_message_price.setText("￥"+getIntent().getStringExtra("price"));
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new DoctorDetailActivity.poponDismissListener());
        rl_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PictureCureActivity.class);
                intent.putExtra("price",getIntent().getStringExtra("price"));
                intent.putExtra("doctorId",getIntent().getStringExtra("doctorId"));
                intent.putExtra("img_head",getIntent().getStringExtra("img_head"));
                intent.putExtra("name",getIntent().getStringExtra("name"));
                intent.putExtra("dutis",getIntent().getStringExtra("dutis"));
                intent.putExtra("department",getIntent().getStringExtra("department"));
                intent.putExtra("hospital",getIntent().getStringExtra("hospital"));
                startActivity(intent);
                pop.dismiss();
            }
        });
        rl_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 0);
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
