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

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.FoodAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.EatBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleProgressView;
import net.leelink.healthangelos.view.HorzProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodRecordActivity extends BaseActivity implements View.OnClickListener {
    CircleProgressView circle_progress;
    HorzProgressView progress_carbon, progress_protein, progress_fat;
    RelativeLayout rl_back, bottom_add;
    TextView tv_time, tv_carbon, tv_protein, tv_fat,tv_empty,tv_empty1,tv_empty2,tv_empty3,tv_breakfast_total,tv_lunch_total,tv_dinner_total,tv_extra_total;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    ImageView img_before, img_after;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    RecyclerView breakfast_list, lunch_list, dinner_list, extra_list;
    FoodAdapter foodAdapter1, foodAdapter2, foodAdapter3, foodAdapter4;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);
        createProgressBar(this);
        context = this;
        init();
        initTime();
        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        initData(now);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void init() {
        circle_progress = findViewById(R.id.circle_progress);
        circle_progress.setCurrentNum(0);
        progress_carbon = findViewById(R.id.progress_carbon);
        progress_carbon.setCurrentNum(0);
        tv_carbon = findViewById(R.id.tv_carbon);
        progress_protein = findViewById(R.id.progress_protein);
        progress_protein.setCurrentNum(0);
        progress_fat = findViewById(R.id.progress_fat);
        progress_fat.setCurrentNum(0);
        tv_fat = findViewById(R.id.tv_fat);
        tv_protein = findViewById(R.id.tv_protein);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        img_before = findViewById(R.id.img_before);
        img_before.setOnClickListener(this);
        img_after = findViewById(R.id.img_after);
        img_after.setOnClickListener(this);
        breakfast_list = findViewById(R.id.breakfast_list);
        lunch_list = findViewById(R.id.lunch_list);
        dinner_list = findViewById(R.id.dinner_list);
        extra_list = findViewById(R.id.extra_list);
        bottom_add = findViewById(R.id.bottom_add);
        bottom_add.setOnClickListener(this);
        tv_empty = findViewById(R.id.tv_empty);
        tv_empty1 = findViewById(R.id.tv_empty1);
        tv_empty2 = findViewById(R.id.tv_empty2);
        tv_empty3 = findViewById(R.id.tv_empty3);
        tv_breakfast_total = findViewById(R.id.tv_breakfast_total);
        tv_lunch_total = findViewById(R.id.tv_lunch_total);
        tv_dinner_total = findViewById(R.id.tv_dinner_total);
        tv_extra_total = findViewById(R.id.tv_extra_total);

    }

    public void initData(String date) {
        showProgressBar();
        OkGo.<String>get(Urls.RECORD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date", date)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取饮食记录", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONObject m_json = json.getJSONObject("measureData");
                                if (json.isNull("totalKcal")) {
                                    circle_progress.setCurrentNum(0);
                                } else {
                                    circle_progress.setMaxNum(m_json.getInt("bigKcal"));
                                    circle_progress.setCurrentNum(json.getInt("totalKcal"));
                                }
                                if (json.isNull("totalCarbo")) {
                                    progress_carbon.setCurrentNum(0);
                                    tv_carbon.setText(0 + "/" + m_json.getInt("bigCarbo"));
                                } else {
                                    progress_carbon.setMax(m_json.getInt("bigCarbo"));
                                    progress_carbon.setCurrentNum(json.getInt("totalCarbo"));
                                    tv_carbon.setText(json.getInt("totalCarbo") + "/" + m_json.getInt("bigCarbo"));
                                }

                                if (json.isNull("totalProtein")) {
                                    progress_protein.setCurrentNum(0);
                                    tv_protein.setText(0 + "/" + m_json.getInt("bigProtein"));
                                } else {
                                    progress_protein.setMax(m_json.getInt("bigProtein"));
                                    progress_protein.setCurrentNum(json.getInt("totalProtein"));
                                    tv_protein.setText(json.getInt("totalProtein") + "/" + m_json.getInt("bigProtein"));
                                }

                                if (json.isNull("totalFat")) {
                                    progress_fat.setCurrentNum(0);
                                    tv_fat.setText(0 + "/" + m_json.getInt("bigFat"));
                                } else {
                                    progress_fat.setMax(m_json.getInt("bigFat"));
                                    progress_fat.setCurrentNum(json.getInt("totalFat"));
                                    tv_fat.setText(json.getInt("totalFat") + "/" + m_json.getInt("bigFat"));
                                }

                                Gson gson = new Gson();
                                //早餐
                                JSONArray jsonArray_breakfast = json.getJSONArray("breakFastVoList");
                                if(jsonArray_breakfast.length()>0) {
                                    List<EatBean> brea_list = gson.fromJson(jsonArray_breakfast.toString(), new TypeToken<List<EatBean>>() {
                                    }.getType());
                                    foodAdapter1 = new FoodAdapter(brea_list, context);
                                    RecyclerView.LayoutManager break_layoutmanager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                    breakfast_list.setLayoutManager(break_layoutmanager);
                                    breakfast_list.setAdapter(foodAdapter1);
                                    tv_empty.setVisibility(View.GONE);
                                    breakfast_list.setVisibility(View.VISIBLE);
                                    int total =0;
                                    for(int i=0;i<brea_list.size();i++) {
                                        total += brea_list.get(i).getTotalKcal();
                                    }
                                    tv_breakfast_total.setText(total+"千卡");
                                } else {
                                    tv_empty.setVisibility(View.VISIBLE);
                                    breakfast_list.setVisibility(View.GONE);
                                    tv_breakfast_total.setText("0千卡");
                                }
                                //午餐
                                JSONArray jsonArray_lunch = json.getJSONArray("lunchVos");
                                if(jsonArray_lunch.length()>0) {
                                    List<EatBean> lun_list = gson.fromJson(jsonArray_lunch.toString(), new TypeToken<List<EatBean>>() {
                                    }.getType());
                                    foodAdapter2 = new FoodAdapter(lun_list, context);
                                    RecyclerView.LayoutManager lunch_layoutmanager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                    lunch_list.setLayoutManager(lunch_layoutmanager);
                                    lunch_list.setAdapter(foodAdapter2);
                                    tv_empty1.setVisibility(View.GONE);
                                    lunch_list.setVisibility(View.VISIBLE);

                                    int total =0;
                                    for(int i=0;i<lun_list.size();i++) {
                                        total += lun_list.get(i).getTotalKcal();
                                    }
                                    tv_lunch_total.setText(total+"千卡");
                                } else {
                                    tv_empty1.setVisibility(View.VISIBLE);
                                    lunch_list.setVisibility(View.GONE);
                                    tv_lunch_total.setText("0千卡");
                                }
                                //晚餐
                                JSONArray jsonArray_dinner = json.getJSONArray("dinnerVos");
                                if(jsonArray_dinner.length()>0) {
                                    List<EatBean> din_list = gson.fromJson(jsonArray_dinner.toString(), new TypeToken<List<EatBean>>() {
                                    }.getType());
                                    foodAdapter3 = new FoodAdapter(din_list, context);
                                    RecyclerView.LayoutManager dinner_layoutmanager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                    dinner_list.setLayoutManager(dinner_layoutmanager);
                                    dinner_list.setAdapter(foodAdapter3);
                                    tv_empty2.setVisibility(View.GONE);
                                    dinner_list.setVisibility(View.VISIBLE);

                                    int total =0;
                                    for(int i=0;i<din_list.size();i++) {
                                        total += din_list.get(i).getTotalKcal();
                                    }
                                    tv_dinner_total.setText(total+"千卡");
                                } else {
                                    tv_empty2.setVisibility(View.VISIBLE);
                                    dinner_list.setVisibility(View.GONE);
                                    tv_dinner_total.setText("0千卡");
                                }
                                //加餐
                                JSONArray jsonArray_extra = json.getJSONArray("extraVos");
                                if(jsonArray_extra.length()>0) {
                                    List<EatBean> ex_list = gson.fromJson(jsonArray_extra.toString(), new TypeToken<List<EatBean>>() {
                                    }.getType());
                                    foodAdapter4 = new FoodAdapter(ex_list, context);
                                    RecyclerView.LayoutManager extra_layoutmanager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                    extra_list.setLayoutManager(extra_layoutmanager);
                                    extra_list.setAdapter(foodAdapter4);
                                    tv_empty3.setVisibility(View.GONE);
                                    extra_list.setVisibility(View.VISIBLE);

                                    int total =0;
                                    for(int i=0;i<ex_list.size();i++) {
                                        total += ex_list.get(i).getTotalKcal();
                                    }
                                    tv_extra_total.setText(total+"千卡");
                                } else {
                                    tv_empty3.setVisibility(View.VISIBLE);
                                    extra_list.setVisibility(View.GONE);
                                    tv_extra_total.setText("0千卡");
                                }
                            }  else if (json.getInt("status") == 505) {
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
            case R.id.tv_time:
                pvTime.show();
                break;
            case R.id.img_before:

                if (tv_time.getText().toString().equals("今日")) {
                    Date date = new Date(System.currentTimeMillis());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, -1);
                    String date2 = simpleDateFormat.format(calendar.getTime());
                    tv_time.setText(date2);

                } else {
                    Date date;
                    try {
                        date = simpleDateFormat.parse(tv_time.getText().toString());
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.DATE, -1);
                        String date2 = simpleDateFormat.format(calendar.getTime());
                        tv_time.setText(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                initData(tv_time.getText().toString());
                break;
            case R.id.img_after:
                if (tv_time.getText().toString().equals("今日")) {


                } else {
                    Date date;
                    try {
                        date = simpleDateFormat.parse(tv_time.getText().toString());
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        calendar.add(calendar.DATE, +1);
                        String date2 = simpleDateFormat.format(calendar.getTime());
                        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                        if (date2.equals(now)) {
                            tv_time.setText("今日");
                        } else {
                            tv_time.setText(date2);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                initData(tv_time.getText().toString());
                break;
            case R.id.bottom_add:
                showPopup();
                backgroundAlpha(0.5f);
                break;
        }
    }


    public void showPopup() {
        View popView = getLayoutInflater().inflate(R.layout.popu_bottom_add_food, null);
        LinearLayout ll_add_breakfast = popView.findViewById(R.id.ll_add_breakfast);
        LinearLayout ll_add_lunch = popView.findViewById(R.id.ll_add_lunch);
        LinearLayout ll_add_dinner = popView.findViewById(R.id.ll_add_dinner);
        LinearLayout ll_add_extra = popView.findViewById(R.id.ll_add_extra);

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new poponDismissListener());
        ll_add_breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseFoodActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });
        ll_add_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseFoodActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });
        ll_add_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseFoodActivity.class);
                intent.putExtra("type", 3);
                startActivity(intent);
            }
        });
        ll_add_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseFoodActivity.class);
                intent.putExtra("type", 4);
                startActivity(intent);
            }
        });

        pop.showAtLocation(FoodRecordActivity.this.findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
    }

    private void initTime() {
        boolean[] type = {true, true, true, false, false, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
                initData(tv_time.getText().toString());
            }
        }).setType(type).build();
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
