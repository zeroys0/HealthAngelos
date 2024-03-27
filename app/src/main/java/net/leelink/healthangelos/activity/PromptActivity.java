package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.MapUtil;
import com.pattonsoft.pattonutil1_0.util.StringUtil;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.RemindTimesManager;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.RemindBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;

public class PromptActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back, img_add;
    private TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_time, tv_repetition, tv_content;
    private View view1, view2, view3, view4, view5, view6, view7;
    int changeType = 4;
    LinearLayout ll_content, ll_have, ll_null;
    Button btn_add;
    ListView swipeTarget;
    List<Map<String, Object>> showList;
    List<Map<String, Object>> list;
    List<RemindBean> remindList = new ArrayList<>();
    int day_step = 0;
    Map<String, Object> lastRemindInfo;
    Context mContext;
    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        mContext = this;
        init();
        createProgressBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取最新提醒
        initData();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_repetition = findViewById(R.id.tv_repetition);
        tv_content = findViewById(R.id.tv_content);
        tv_1 = findViewById(R.id.tv_1);
        tv_1.setOnClickListener(this);
        tv_2 = findViewById(R.id.tv_2);
        tv_2.setOnClickListener(this);
        tv_3 = findViewById(R.id.tv_3);
        tv_3.setOnClickListener(this);
        tv_4 = findViewById(R.id.tv_4);
        tv_4.setOnClickListener(this);
        tv_5 = findViewById(R.id.tv_5);
        tv_5.setOnClickListener(this);
        tv_6 = findViewById(R.id.tv_6);
        tv_6.setOnClickListener(this);
        tv_7 = findViewById(R.id.tv_7);
        tv_7.setOnClickListener(this);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        view7 = findViewById(R.id.view7);
        ll_content = findViewById(R.id.ll_content);
        swipeTarget = findViewById(R.id.swipe_target);
        ll_have = findViewById(R.id.ll_have);
        ll_have.setVisibility(View.GONE);
        ll_null = findViewById(R.id.ll_null);
        setDaysOfWeek();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_1:
                changeType = 1;
                topChange(changeType);
                break;
            case R.id.tv_2:
                changeType = 2;
                topChange(changeType);
                break;
            case R.id.tv_3:
                changeType = 3;
                topChange(changeType);
                break;
            case R.id.tv_4:
                changeType = 4;
                topChange(changeType);
                break;
            case R.id.tv_5:
                changeType = 5;
                topChange(changeType);
                break;
            case R.id.tv_6:
                changeType = 6;
                topChange(changeType);
                break;
            case R.id.tv_7:
                changeType = 7;
                topChange(changeType);
                break;
            case R.id.img_add:
            case R.id.btn_add:
                Intent intent = new Intent(this, EditRemindActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().REMINDLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", MyApplication.userInfo.getJwotchImei())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取提醒列表", json.toString());
                            if (json.getInt("status") == 200) {
                                if (json.has("data")) {
                                    Map<String, Object> data = new Gson().fromJson(json.toString(), new TypeToken<Map<String, Object>>() {
                                    }.getType());
                                    list = (List<Map<String, Object>>) data.get("data");

                                    //获取即将到来的提醒
                                    String h = StringUtil.getTime("HH");
                                    String m = StringUtil.getTime("mm");
                                    int c = 25;
                                    int k = 61;
                                    for (int j = 0; j < list.size(); j++) {
                                        Map<String, Object> map = list.get(j);
                                        if (RemindTimesManager.ifTodayRemind(MapUtil.getInt(map, "Type"), 0, true, MapUtil.getInt(map, "Year"), MapUtil.getInt(map, "Month"), MapUtil.getInt(map, "Day"))) {
                                            int hour = MapUtil.getInt(map, "Hour");
                                            int minute = MapUtil.getInt(map, "Minute");
                                            int d = hour - Integer.valueOf(h);
                                            int n = minute - Integer.valueOf(m);
                                            if (c > d) {
                                                c = d;
                                                lastRemindInfo = map;
                                            } else if (c == d) {
                                                if (k > n) {
                                                    k = n;
                                                    lastRemindInfo = map;
                                                }
                                            }
                                        }
                                    }

                                    setInfo();
                                    topChange(changeType);
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(mContext);
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setDaysOfWeek() {
        long day = System.currentTimeMillis();
        long oneday = 24 * 60 * 60 * 1000;
        long day1 = day - 3 * oneday;
        long day2 = day - 2 * oneday;
        long day3 = day - oneday;
        long day4 = day;
        long day5 = day + oneday;
        long day6 = day + 2 * oneday;
        long day7 = day + 3 * oneday;
        SimpleDateFormat format = new SimpleDateFormat("E");
        tv_1.setText(format.format(new Date(day1)));
        tv_2.setText(format.format(new Date(day2)));
        tv_3.setText(format.format(new Date(day3)));
        tv_4.setText("今天");
        tv_5.setText(format.format(new Date(day5)));
        tv_6.setText(format.format(new Date(day6)));
        tv_7.setText(format.format(new Date(day7)));
    }

    //标签切换
    void topChange(int i) {
        tv_1.setTextColor(getResources().getColor(R.color.black));
        tv_2.setTextColor(getResources().getColor(R.color.black));
        tv_3.setTextColor(getResources().getColor(R.color.black));
        tv_4.setTextColor(getResources().getColor(R.color.black));
        tv_5.setTextColor(getResources().getColor(R.color.black));
        tv_6.setTextColor(getResources().getColor(R.color.black));
        tv_7.setTextColor(getResources().getColor(R.color.black));

        view1.setBackgroundColor(getResources().getColor(R.color.white));
        view2.setBackgroundColor(getResources().getColor(R.color.white));
        view3.setBackgroundColor(getResources().getColor(R.color.white));
        view4.setBackgroundColor(getResources().getColor(R.color.white));
        view5.setBackgroundColor(getResources().getColor(R.color.white));
        view6.setBackgroundColor(getResources().getColor(R.color.white));
        view7.setBackgroundColor(getResources().getColor(R.color.white));

        ll_content.setVisibility(View.GONE);
        swipeTarget.setVisibility(View.GONE);
        day_step = i - 4;
        showList = new ArrayList<>();
        if (list != null) {
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> map = list.get(j);
                if (RemindTimesManager.ifTodayRemind(MapUtil.getInt(map, "Type"), day_step, true, MapUtil.getInt(map, "Year"), MapUtil.getInt(map, "Month"), MapUtil.getInt(map, "Day"))) {
                    showList.add(map);
                }
            }
        }


        Collections.sort(showList, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {


                int i = MapUtil.getInt(o1, "Hour") - MapUtil.getInt(o2, "Hour");
                if (i == 0) {
                    return MapUtil.getInt(o1, "Minute") - MapUtil.getInt(o1, "Minute");
                }
                return i;
            }
        });
        //排序

        if (showList.size() > 0) {
            swipeTarget.setAdapter(new ListAdapter());
            swipeTarget.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.VISIBLE);
        }
        switch (i) {
            case 1:
                tv_1.setTextColor(getResources().getColor(R.color.blue));
                view1.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 2:
                tv_2.setTextColor(getResources().getColor(R.color.blue));
                view2.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 3:
                tv_3.setTextColor(getResources().getColor(R.color.blue));
                view3.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 4:
                tv_4.setTextColor(getResources().getColor(R.color.blue));
                view4.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 5:
                tv_5.setTextColor(getResources().getColor(R.color.blue));
                view5.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 6:
                tv_6.setTextColor(getResources().getColor(R.color.blue));
                view6.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case 7:
                tv_7.setTextColor(getResources().getColor(R.color.blue));
                view7.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
        }

    }


    /**
     * 提醒列表适配器
     */
    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View v = view;
            Holder holder;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_watch_remaind_list, viewGroup, false);
                holder = new Holder(v);
                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            Map<String, Object> map = showList.get(i);

            final int hour = MapUtil.getInt(map, "Hour");
            final int minute = MapUtil.getInt(map, "Minute");
            final int Id = MapUtil.getInt(map, "Id");
            boolean State;
//            Double b = (Double) map.get("State");
//            assert b != null;
            //处理一下返回的提示状态
            try {
                State = (boolean) map.get("State");
            } catch (ClassCastException exception) {
                Double b = (Double) map.get("State");
                if (b.intValue() == 1) {
                    State = true;
                } else {
                    State = false;
                }
            }

            if (!State) {
                holder.tvCancel.setVisibility(View.VISIBLE);
            } else {
                holder.tvCancel.setVisibility(View.GONE);
            }
            holder.tvTime.setText(hour + " : " + minute);
            final String Title = MapUtil.getString(map, "Title");
            holder.tvContent.setText(Title);
            final ImageView im = holder.imEdt;

            holder.tvTop.setVisibility(View.GONE);

            //状态
            //前3天
            if (changeType < 4) {
                holder.timeView.setImageResource(R.drawable.reminder_completed);
                holder.tvTime.setTextColor(getResources().getColor(R.color.black));
                holder.tvContent.setTextColor(getResources().getColor(R.color.black));
                if (!State) {
                    holder.timeView.setImageResource(R.drawable.reminder_closed);
                }
            }
            //后3天
            if (changeType > 4) {
                holder.tvTop.setVisibility(View.VISIBLE);
                holder.timeView.setImageResource(R.drawable.reminder_future);
                holder.tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                holder.tvContent.setTextColor(getResources().getColor(R.color.colorPrimary));
                if (!State) {
                    holder.timeView.setImageResource(R.drawable.reminder_closed);
                }
            }
            if (changeType == 4) {  //当天
                //之前
                String h = StringUtil.getTime("HH");
                String m = StringUtil.getTime("mm");
                if (hour < Integer.parseInt(h)) {
                    holder.timeView.setImageResource(R.drawable.reminder_completed);
                    holder.tvTime.setTextColor(getResources().getColor(R.color.black));
                    holder.tvContent.setTextColor(getResources().getColor(R.color.black));
                }
                if (hour > Integer.parseInt(h)) {
                    holder.tvTop.setVisibility(View.VISIBLE);
                    holder.timeView.setImageResource(R.drawable.reminder_future);
                    holder.tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                    holder.tvContent.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                if (hour == Integer.parseInt(h)) {
                    if (minute <= Integer.parseInt(m)) {
                        holder.timeView.setImageResource(R.drawable.reminder_completed);
                        holder.tvTime.setTextColor(getResources().getColor(R.color.black));
                        holder.tvContent.setTextColor(getResources().getColor(R.color.black));
                    }
                    if (minute > Integer.parseInt(m)) {
                        holder.tvTop.setVisibility(View.VISIBLE);
                        holder.timeView.setImageResource(R.drawable.reminder_future);
                        holder.tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                        holder.tvContent.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }

                //是否即将开始
                int Hour = MapUtil.getInt(lastRemindInfo, "Hour");
                int Minute = MapUtil.getInt(lastRemindInfo, "Minute");
                if (hour == Hour & minute == Minute) {
                    holder.tvTop.setVisibility(View.GONE);
                    holder.timeView.setImageResource(R.drawable.reminder_next);
                    holder.tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
                    holder.tvContent.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (!State) {
                    holder.timeView.setImageResource(R.drawable.reminder_closed);
                }
            }


            final Map<String, Object> finalMap = map;
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] str = {"编辑", "删除"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setItems(str, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(mContext, EditRemindActivity.class);
                                    intent.putExtra("editType", 1);
                                    intent.putExtra("Title", Title);
                                    intent.putExtra("Hour", MapUtil.getInt(finalMap, "Hour"));
                                    intent.putExtra("Minute", MapUtil.getInt(finalMap, "Minute"));
                                    intent.putExtra("Year", MapUtil.getInt(finalMap, "Year"));
                                    intent.putExtra("Month", MapUtil.getInt(finalMap, "Month"));
                                    intent.putExtra("Day", MapUtil.getInt(finalMap, "Day"));
                                    intent.putExtra("Type", MapUtil.getInt(finalMap, "Type"));
                                    intent.putExtra("Id", MapUtil.getInt(finalMap, "Id"));

                                    int  b =(int) Math.round((double)finalMap.get("State"));
                                    boolean bo;
                                    if(b==1){
                                        bo = true;
                                    } else {
                                        bo = false;
                                    }
                                    intent.putExtra("State", bo);

                                    startActivity(intent);
                                    break;
                                case 1:
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                    builder1.setMessage("确定删除此提醒么");
                                    builder1.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DeleteRemind(Id);
                                        }
                                    });
                                    builder1.setNegativeButton("否", null);
                                    builder1.show();

                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });

            return v;
        }


        class Holder {

            ImageView timeView;

            TextView tvTime;

            TextView tvCancel;

            ImageView imEdt;

            TextView tvContent;

            View tvTop;

            Holder(View view) {

                timeView = view.findViewById(R.id.timeView);
                tvTime = view.findViewById(R.id.tv_time);
                tvCancel = view.findViewById(R.id.tv_cancel);
                imEdt = view.findViewById(R.id.im_edt);
                tvContent = view.findViewById(R.id.tv_content);
                tvTop = view.findViewById(R.id.tv_top);
            }
        }
    }


    //设置最新提醒
    private void setInfo() {
        if (lastRemindInfo != null && lastRemindInfo.size() > 0) {
            String Title = MapUtil.getString(lastRemindInfo, "Title");
            int Type = MapUtil.getInt(lastRemindInfo, "Type");
            int Year = MapUtil.getInt(lastRemindInfo, "Year");
            int Month = MapUtil.getInt(lastRemindInfo, "Month");
            int Day = MapUtil.getInt(lastRemindInfo, "Day");
            int Hour = MapUtil.getInt(lastRemindInfo, "Hour");
            int Minute = MapUtil.getInt(lastRemindInfo, "Minute");

            tv_time.setText(Hour + " : " + Minute);

            if (Type == 0) {
                tv_repetition.setText(Year + "-" + Month + "-" + Day + "重复一次");
            } else if (Type == 127) {
                tv_repetition.setText("每天重复");
            } else {
                String text = "重复 ";
                if (RemindTimesManager.ifSundayHave(Type)) text += "周日 ";
                if (RemindTimesManager.ifMondayHave(Type)) text += "周一 ";
                if (RemindTimesManager.ifTuesdayHave(Type)) text += "周二 ";
                if (RemindTimesManager.ifWednesdayHave(Type)) text += "周三 ";
                if (RemindTimesManager.ifThursdayHave(Type)) text += "周四 ";
                if (RemindTimesManager.ifFridayHave(Type)) text += "周五 ";
                if (RemindTimesManager.ifSaturdayHave(Type)) text += "周六 ";
                tv_repetition.setText(text);
            }
            tv_content.setText(Title);
            ll_have.setVisibility(View.VISIBLE);
            ll_null.setVisibility(View.GONE);
        } else {
            ll_have.setVisibility(View.GONE);
            ll_null.setVisibility(View.VISIBLE);
        }

    }

    public void DeleteRemind(int id) {
        OkGo.<String>get(Urls.getInstance().DELETEREMIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", MyApplication.userInfo.getJwotchImei())
                .params("id", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除提醒", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                                initData();
                            } else if (json.getInt("status") == 505) {
                                reLogin(mContext);
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
