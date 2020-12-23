package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DoctorListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.HomeDoctorBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.app.MyApplication.getContext;

public class DoctorListActivity extends BaseActivity implements OnOrderListener , View.OnClickListener {
private RelativeLayout rl_back;
private RecyclerView doctor_list;
private DoctorListAdapter doctorListAdapter;
private TextView tv_total,tv_type,tv_price,tv_alias;
private int standRow = 0;
private int questType = 1;
private int priceRow = 1;
private String doctorAlias = "主任医师";

    List<HomeDoctorBean> list = new ArrayList<>();

    Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        init();
        context = this;
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_total = findViewById(R.id.tv_total);
        tv_total.setOnClickListener(this);
        tv_type = findViewById(R.id.tv_type);
        tv_type.setOnClickListener(this);
        tv_price = findViewById(R.id.tv_price);
        tv_price.setOnClickListener(this);
        tv_alias = findViewById(R.id.tv_alias);
        tv_alias.setOnClickListener(this);
    }

    public void initData(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("department",getIntent().getStringExtra("type"));
        httpParams.put("doctorAlias",doctorAlias);
        httpParams.put("pageNum",1);
        httpParams.put("pageSize",10);
        httpParams.put("priceRow",priceRow);
        httpParams.put("questType",questType);
        if(standRow != 0) {
            httpParams.put("standRow",standRow);
        }

        doctor_list = findViewById(R.id.doctor_list);
        OkGo.<String>get(Urls.getInstance().DOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<HomeDoctorBean>>() {}.getType());
                                doctorListAdapter = new DoctorListAdapter(list,getContext(),DoctorListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                                doctor_list.setLayoutManager(layoutManager);
                                doctor_list.setAdapter(doctorListAdapter);


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
    public void onItemClick(View view) {
        int position  = doctor_list.getChildLayoutPosition(view);
        Intent intent = new Intent(this,DoctorDetailActivity.class);

        Log.e("onSuccess:doctorId ", list.get(position).getCareDoctorRegedit().getImgPath());

        intent.putExtra("doctorId",list.get(position).getCareDoctorRegedit().getId());
        intent.putExtra("img_head",list.get(position).getCareDoctorRegedit().getImgPath());
        intent.putExtra("name",list.get(position).getCareDoctorRegedit().getName());
        intent.putExtra("dutis",list.get(position).getCareDoctorRegedit().getDuties());
        intent.putExtra("department",list.get(position).getCareDoctorRegedit().getDepartment());
        intent.putExtra("hospital",list.get(position).getCareDoctorRegedit().getHospital());
        intent.putExtra("score",list.get(position).getCareDoctorRegedit().getTotalScore());
        intent.putExtra("count",list.get(position).getCareDoctorRegedit().getTotalCount());
        intent.putExtra("visit",list.get(position).getCareDoctorRegedit().getVisit());
        intent.putExtra("price",list.get(position).getCareDoctorRegedit().getImgPrice());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {
        backgroundAlpha(0.5f);
        showPopup(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_total:
                showtype();
                break;
            case R.id.tv_type:
                choosetype();
                break;
            case R.id.tv_price:
                showPrice();
                break;
            case R.id.tv_alias:
                showAlias();
                break;
        }
    }

    public void showPopup(int position){
        View popView = getLayoutInflater().inflate(R.layout.popu_choose_cure, null);
        RelativeLayout rl_picture = popView.findViewById(R.id.rl_picture);
        RelativeLayout rl_phone = popView.findViewById(R.id.rl_phone);
        TextView tv_message_price = popView.findViewById(R.id.tv_message_price);
        tv_message_price.setText("￥"+list.get(position).getCareDoctorRegedit().getImgPrice());
        TextView tv_phone_price = popView.findViewById(R.id.tv_phone_price);
        tv_phone_price.setText("￥"+list.get(position).getCareDoctorRegedit().getPhonePrice());
        ImageView img_close = popView.findViewById(R.id.img_close);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new DoctorListActivity.poponDismissListener());
        rl_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PictureCureActivity.class);
                intent.putExtra("price",list.get(position).getCareDoctorRegedit().getImgPrice());
                intent.putExtra("doctorId",list.get(position).getCareDoctorRegedit().getId());
                intent.putExtra("img_head",list.get(position).getCareDoctorRegedit().getImgPath());
                intent.putExtra("name",list.get(position).getCareDoctorRegedit().getName());
                intent.putExtra("dutis",list.get(position).getCareDoctorRegedit().getDuties());
                intent.putExtra("department",list.get(position).getCareDoctorRegedit().getDepartment());
                intent.putExtra("hospital",list.get(position).getCareDoctorRegedit().getHospital());
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

    public void showtype(){
        View popView = getLayoutInflater().inflate(R.layout.doctor_total_search, null);
        LinearLayout ll_score = (LinearLayout) popView.findViewById(R.id.ll_score);
        LinearLayout ll_low = (LinearLayout) popView.findViewById(R.id.ll_low);
        LinearLayout ll_high = (LinearLayout) popView.findViewById(R.id.ll_high);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_total.setText("按评分");
                standRow = 1;
                initData();
                pop.dismiss();
            }
        });
        ll_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_total.setText("从低到高");
                standRow = 2;
              initData();
                pop.dismiss();
            }
        });
        ll_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_total.setText("从高到低");
                standRow = 3;
                initData();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_total);
    }

    public void choosetype(){
        View popView = getLayoutInflater().inflate(R.layout.doctor_type_search, null);
        LinearLayout ll_all = (LinearLayout) popView.findViewById(R.id.ll_all);
        LinearLayout ll_picture = (LinearLayout) popView.findViewById(R.id.ll_picture);
        LinearLayout ll_phone = (LinearLayout) popView.findViewById(R.id.ll_phone);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_type.setText("全部");
                questType = 1;
                initData();
                pop.dismiss();
            }
        });
        ll_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_type.setText("图文咨询");
                questType = 2;
                initData();
                pop.dismiss();
            }
        });
        ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_type.setText("电话咨询");
                questType = 3;
                initData();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_type);
    }

    public void showPrice(){
        View popView = getLayoutInflater().inflate(R.layout.doctor_price_search, null);
        LinearLayout ll_price_1 = (LinearLayout) popView.findViewById(R.id.ll_price_1);
        LinearLayout ll_price_2 = (LinearLayout) popView.findViewById(R.id.ll_price_2);
        LinearLayout ll_price_3 = (LinearLayout) popView.findViewById(R.id.ll_price_3);
        LinearLayout ll_price_4 = (LinearLayout) popView.findViewById(R.id.ll_price_4);
        LinearLayout ll_price_5 = (LinearLayout) popView.findViewById(R.id.ll_price_5);
        LinearLayout ll_price_6 = (LinearLayout) popView.findViewById(R.id.ll_price_6);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_price_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_price.setText("全部");
                priceRow = 1;
                initData();
                pop.dismiss();
            }
        });
        ll_price_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_price.setText("20以下");
                priceRow = 2;
                initData();
                pop.dismiss();
            }
        });
        ll_price_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("20-29");
                priceRow = 3;
                initData();
                pop.dismiss();
            }
        });
        ll_price_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("30-39");
                priceRow = 4;
                initData();
                pop.dismiss();
            }
        });
        ll_price_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("40-49");
                priceRow = 5;
                initData();
                pop.dismiss();
            }
        });
        ll_price_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_price.setText("50以上");
                priceRow = 6;
                initData();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_price);
    }

    public void showAlias(){
        View popView = getLayoutInflater().inflate(R.layout.doctor_alias_search, null);
        LinearLayout ll_alias_1 = (LinearLayout) popView.findViewById(R.id.ll_alias_1);
        LinearLayout ll_alias_2 = (LinearLayout) popView.findViewById(R.id.ll_alias_2);
        LinearLayout ll_alias_3 = (LinearLayout) popView.findViewById(R.id.ll_alias_3);
        LinearLayout ll_alias_4 = (LinearLayout) popView.findViewById(R.id.ll_alias_4);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        ll_alias_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  团队
                tv_alias.setText("医师");
                doctorAlias = "医师";
                initData();
                pop.dismiss();
            }
        });
        ll_alias_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //个人
                tv_alias.setText("主治医师");
                doctorAlias = "主治医师";
                initData();
                pop.dismiss();
            }
        });
        ll_alias_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_alias.setText("副主任医师");
                doctorAlias = "副主任医师";
                initData();
                pop.dismiss();
            }
        });
        ll_alias_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_alias.setText("主任医师");
                doctorAlias = "主任医师";
                initData();
                pop.dismiss();
            }
        });

        pop.showAsDropDown(tv_alias);
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
