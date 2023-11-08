package net.leelink.healthangelos.activity.Badge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class BadgeBpActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private Context context;
    private Button btn_before, btn_after;
    private String imei;
    private TextView tv_date;
    Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private LineChart line_chart;
    List<BarEntry> list;
    List<BarEntry> list2;
    private BarChart bar_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_bp);
        context = this;
        createProgressBar(context);
        imei = getIntent().getStringExtra("imei");
        init();
        getBpData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_before = findViewById(R.id.btn_before);
        btn_before.setOnClickListener(this);
        btn_after = findViewById(R.id.btn_after);
        btn_after.setOnClickListener(this);
        tv_date = findViewById(R.id.tv_date);
        long time = System.currentTimeMillis();
        date = new Date(time);
        tv_date.setText(simpleDateFormat.format(date));
        line_chart = findViewById(R.id.line_chart);
        bar_chart = findViewById(R.id.bar_chart);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            //前一天
            case R.id.btn_before:
                try {
                    date = simpleDateFormat.parse(tv_date.getText().toString());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, -1);
                    String date2 = simpleDateFormat.format(calendar.getTime());
                    tv_date.setText(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getBpData();
                break;
            //后一天
            case R.id.btn_after:
                try {
                    String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    if (tv_date.getText().toString().equals(now)) {
                        return;
                    }
                    date = simpleDateFormat.parse(tv_date.getText().toString());
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, +1);
                    String date2 = simpleDateFormat.format(calendar.getTime());
                    tv_date.setText(date2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getBpData();
                break;
        }
    }

    public void getBpData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BADGE_BP)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei", imei)
                .params("testTime", tv_date.getText().toString().trim())
                .execute(new StringCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询血压列表", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<BpBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<BpBean>>() {
                                }.getType());
                                setData2(list);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopProgressBar();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }
    public int count = 0;

    public void setData2(List<BpBean> dataList) {

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        int x = 0;
        XAxis xAxis = bar_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7, false); //设置X轴的显示个数,false为是否固定x轴坐标
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(dataList.size() - 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //居中对齐
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelRotationAngle(0f);
        List<String> times = new ArrayList<>();
        for(BpBean bpBean:dataList){
            Date date = null;
            try {
                date = sdf.parse(bpBean.getTestTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                  String  time = outputFormat.format(date);
            times.add(time);
        }
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(times);
        // 设置X轴的值格式化器为自定义的formatter
        xAxis.setValueFormatter(formatter);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                return super.getAxisLabel(value, axis);
//            }
//
//            @Override
//            public String getFormattedValue(float value) {
//
//               // int i = (int) ((value-0.5)/1.5);
//                /**
//                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
//                 */
//                if(dataList.size()==0){
//                    return "";
//                }
//                Log.d( "getFormattedValue: ",value+"");
//                int i = (int) Math.max(0, Math.min(value, dataList.size() - 1));
//                Log.d( "getFormattedValueI: ",i+"");
//
//                    String time= "";
//                    try {
//                        Date date = sdf.parse(dataList.get(count).getTestTime());
//                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
//                        time = outputFormat.format(date);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    count++;
//                    return time;
//
//
//            }
//        });
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            list.add(new BarEntry(x, dataList.get(i).getPs()));
            list2.add(new BarEntry(x, dataList.get(i).getPd()));
            x++;
            if (dataList.get(i).getPs() > 130) {
                colors.add(Color.RED);
            } else {
                colors.add(rgb("#0092f5"));
            }
        }

        BarDataSet barDataSet = new BarDataSet(list, "高压");
        barDataSet.setColor(rgb("#e3f3fe"));    //为第一组柱子设置颜色
        //设置一组文字颜色
        barDataSet.setValueTextColors(colors);
        BarDataSet barDataSet2 = new BarDataSet(list2, "低压");



        barDataSet2.setColor(Color.WHITE);   //为第二组柱子设置颜色
        //设置文字颜色
        //  barDataSet.setValueTextColor(rgb("#0092f5"));
        barDataSet2.setValueTextColors(colors);
        BarData barData = new BarData(barDataSet);   //加上第一组
        //重点！！！ 加上第二组（多组也可以用同样的方法）  一定是以数据大小的降序添加
        barData.addDataSet(barDataSet2);
        bar_chart.setData(barData);
        barData.setBarWidth(0.6f);//柱子的宽度
        YAxis leftAxis = bar_chart.getAxisLeft();
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        bar_chart.getXAxis().setDrawGridLines(false);
        bar_chart.getAxisLeft().setAxisMaximum(200);   //Y轴最大数值
        bar_chart.getDescription().setEnabled(false);    //右下角一串英文字母不显示
        bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);   //X轴的位置设置为下  默认为上
        bar_chart.getAxisRight().setEnabled(false);     //右侧Y轴不显示   默认为显示
        if (dataList.size() <= 10) {    //数据不到10
            bar_chart.setVisibleXRange(0, dataList.size() - 1);
        } else {
            bar_chart.setVisibleXRange(0, 7);
        }
        bar_chart.invalidate();
        bar_chart.refreshDrawableState();
    }

}