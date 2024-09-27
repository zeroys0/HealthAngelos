package net.leelink.healthangelos.activity.yasee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class YaseeBpActivity extends BaseActivity {
    ImageView img_before, img_after;
    private Context context;
    private RelativeLayout rl_back;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private TextView tv_time,tv_more;

    private BarChart bar_chart;

    private TimePickerView pvTime;

    private SimpleDateFormat sdf;

    List<BarEntry>list;
    List<BarEntry>list2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yasee_bp);
        context = this;
        createProgressBar(context);
        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        init();
        initTime();
        initData(now);
    }

    public void init(){
        img_before = findViewById(R.id.img_before);
        img_before.setOnClickListener(this);
        img_after = findViewById(R.id.img_after);
        img_after.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        bar_chart = findViewById(R.id.bar_chart);
        tv_more = findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
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
                            initData(now);
                        } else {
                            tv_time.setText(date2);
                            initData(tv_time.getText().toString());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.tv_time:
                pvTime.show();
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_more:
                Intent intent = new Intent(context, YaseeBpMoreActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
                break;

        }
    }

    public void initData(String date) {
        OkGo.<String>get(Urls.getInstance().YASEE_BP)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date",date)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取血压信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<YaseeBpBean> list = gson.fromJson(jsonArray.toString(),new TypeToken<List<YaseeBpBean>>(){}.getType());
                                setData2(list);

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LoadDialog.stop();
                    }
                });
    }

    public void setData2(List<YaseeBpBean> dataList){

        list=new ArrayList<>();
        list2=new ArrayList<>();
        XAxis xAxis = bar_chart.getXAxis();
        int x= 0;
        List<Integer> colors = new ArrayList<>();
        for(int i =0;i<dataList.size();i++){
            list.add(new BarEntry(x,dataList.get(i).getSystolic()));
            list2.add(new BarEntry(x,dataList.get(i).getDiastolic()));
            x++;
            Log.d( "setData2: ",dataList.get(i).getSystolic()+"   " + dataList.size());
            if(dataList.get(i).getSystolic()>130){
                colors.add(Color.RED);
            }else {
                colors.add(rgb("#0092f5"));
            }
        }

        List<Date> times = new ArrayList<>();
        for(YaseeBpBean bean:dataList){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(bean.getCreateTime());
                times.add(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        Collections.reverse(times);
        //获取数据时间列表
        Date[] dates = times.toArray(new Date[0]);


        //创建TimeFormatter指定时间格式
        class TimeValueFormatter extends ValueFormatter {
            private SimpleDateFormat mFormat;

            public TimeValueFormatter(String pattern) {
                mFormat = new SimpleDateFormat(pattern);
            }

            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dates.length) {
                    return mFormat.format(dates[index]);
                } else {
                    return "";
                }
            }
        }
        ValueFormatter formatter = new TimeValueFormatter("HH:mm");
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 可选位置
        xAxis.setGranularity(1f); // 控制标签的密度，这里设置为每个点都显示标签
        xAxis.setGranularityEnabled(true);

//        //为第一组添加数据
//        list.add(new BarEntry(1,dataList.));
//        list.add(new BarEntry(2,109));
//
//        //为第二组添加数据
//        list2.add(new BarEntry(1,80));
//        list2.add(new BarEntry(2,74));

        BarDataSet barDataSet=new BarDataSet(list,"高压");
        barDataSet.setColor(rgb("#e3f3fe"));    //为第一组柱子设置颜色
        //设置一组文字颜色
        barDataSet.setValueTextColors(colors);
        BarDataSet barDataSet2=new BarDataSet(list2,"低压");
        barDataSet2.setColor(Color.WHITE);   //为第二组柱子设置颜色
        //设置文字颜色
        //  barDataSet.setValueTextColor(rgb("#0092f5"));
        barDataSet2.setValueTextColors(colors);
        BarData barData=new BarData(barDataSet);   //加上第一组
        //重点！！！ 加上第二组（多组也可以用同样的方法）  一定是以数据大小的降序添加
        barData.addDataSet(barDataSet2);
        bar_chart.setData(barData);

        barData.setBarWidth(0.4f);//柱子的宽度
        YAxis leftAxis = bar_chart.getAxisLeft();
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        bar_chart.getXAxis().setDrawGridLines(false);

        bar_chart.getAxisLeft().setAxisMaximum(200);   //Y轴最大数值
        // bar_chart.getAxisLeft().setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        bar_chart.getAxisLeft().setLabelCount(4,false);
//        bar_chart.getXAxis().setAxisMaximum(7);   //X轴最大数值
//        bar_chart.getXAxis().setAxisMinimum(0);   //X轴最小数值
        //X轴坐标的个数
//        bar_chart.getXAxis().setLabelCount(7);
        bar_chart.getDescription().setEnabled(false);    //右下角一串英文字母不显示
        bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);   //X轴的位置设置为下  默认为上
        bar_chart.getAxisRight().setEnabled(false);     //右侧Y轴不显示   默认为显示

        bar_chart.invalidate();
        bar_chart.refreshDrawableState();
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
}
