package net.leelink.healthangelos.activity.ElectricMachine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.MyCircleProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ANY1UserActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private Button btn_before, btn_after;
    private TextView tv_time;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String now;
    private LineChart line_chart1,line_chart2,line_chart3;
    private BarChart bar_chart;
    private MyCircleProgressView circle_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any1_user);
        context = this;
        init();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_before = findViewById(R.id.btn_before);
        btn_before.setOnClickListener(this);
        btn_after = findViewById(R.id.btn_after);
        btn_after.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        tv_time.setText(now);
        line_chart1 = findViewById(R.id.line_chart1);
        line_chart2 = findViewById(R.id.line_chart2);
        line_chart3 = findViewById(R.id.line_chart3);
        bar_chart = findViewById(R.id.bar_chart);

//        initData(now);
        setData(line_chart1);
        setData(line_chart2);
        setData2(line_chart3);
        setData3();
        circle_progress = findViewById(R.id.circle_progress);
        circle_progress.setCurrentNum(0);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().ANY1_PROFILE+"/"+getIntent().getStringExtra("familyId")+"/"+tv_time.getText().toString())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看用户画像", json.toString());
                            if (json.getInt("status") == 200) {

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
            case R.id.btn_before:
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
                break;
            case R.id.btn_after:
                if (tv_time.getText().toString().equals(now)) {

                } else {
                    Date date1;
                    try {
                        date1 = simpleDateFormat.parse(tv_time.getText().toString());
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(date1);
                        calendar.add(calendar.DATE, +1);
                        String date2 = simpleDateFormat.format(calendar.getTime());
                        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                        tv_time.setText(date2);
//                        if (date2.equals(now)) {
//                            tv_time.setText("今日");
//                        } else {
//                            tv_time.setText(date2);
//                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void setData(LineChart line_chart){
        line_chart.getDescription().setEnabled(false);
        line_chart.setDragEnabled(false);   //能否拖拽
        line_chart.setScaleEnabled(false);
        XAxis xAxis = line_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
//        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setLabelCount(24);  //设置X轴的显示个数
//        xAxis.setAvoidFirstLastClipping(false);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setDrawGridLines(false);
//        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
//        xAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
//        List<String> day_list = getDays();
//
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis);
            }

            @Override
            public String getFormattedValue(float value) {

//                int i = (int) (value/0.8999);
                /**
                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
                 */
                Log.d( "getFormattedValue: ",value+"");
//                Log.d( "getFormattedValue: ",i+"");
                value = (int)value+1;
                return (int)value+"";
            }
        });


        YAxis leftAxis = line_chart.getAxisLeft();
        line_chart.getAxisRight().setEnabled(false);
        leftAxis.setTextSize(6f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
//        leftAxis.setGranularity(20f); // 网格线条间距
//        leftAxis.setEnabled(false);     //设置是否使用 Y轴左边的
//        leftAxis.setDrawGridLines(true);      //是否使用 Y轴网格线条
//        leftAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
//        leftAxis.setDrawLabels(true);        //是否显示Y轴刻度
//        leftAxis.setStartAtZero(true);        //设置Y轴数值 从零开始
//        leftAxis.setTextSize(12f);            //设置Y轴刻度字体
//        leftAxis.setTextColor(Color.parseColor("#656565"));   //设置字体颜色


        List<Entry> mValues = new ArrayList<>();
        mValues.add(new Entry(0f, 0.6f));
        mValues.add(new Entry(1f, 0.4f));
        mValues.add(new Entry(2f, 0.2f));
        mValues.add(new Entry(3f, 0.5f));
        mValues.add(new Entry(4f, 0.5f));
        mValues.add(new Entry(5f, 0.5f));
        mValues.add(new Entry(6f, 0.3f));
        mValues.add(new Entry(7f, 0.5f));
        mValues.add(new Entry(8f, 0.6f));
        mValues.add(new Entry(9f, 0.4f));
        mValues.add(new Entry(10f, 0.2f));
        mValues.add(new Entry(11f, 0.1f));
        mValues.add(new Entry(12f, 0f));
        mValues.add(new Entry(13f, 0f));
        mValues.add(new Entry(14f, 0f));
        mValues.add(new Entry(15f, 0.3f));
        mValues.add(new Entry(16f, 0.5f));
        mValues.add(new Entry(17f, 0.2f));
        mValues.add(new Entry(18f, 0.3f));
        mValues.add(new Entry(19f, 0.4f));
        mValues.add(new Entry(20f, 0.5f));
        mValues.add(new Entry(21f, 0.1f));
        mValues.add(new Entry(22f, 0.1f));
        mValues.add(new Entry(23f, 0.2f));
        LineDataSet set1 = new LineDataSet(mValues,"电力");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        set1.setColor(Color.parseColor("#70ad47"));
        set1.setDrawCircles(false);
        set1.setLineWidth(3f);
        set1.setDrawValues(false);


        // set the filled area
        // set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return line_chart.getAxisLeft().getAxisMinimum();
            }
        });
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData data = new LineData(dataSets);
        // 添加到图表中
        line_chart.setData(data);
        //隐藏label
        line_chart.getLegend().setEnabled(false);
        //绘制图表
        line_chart.invalidate();
        //判断图表中原来是否有数据
    }

    public void setData2(LineChart line_chart){
        line_chart.getDescription().setEnabled(false);
        line_chart.setDragEnabled(false);   //能否拖拽
        line_chart.setScaleEnabled(false);

        XAxis xAxis = line_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setLabelCount(7);  //设置X轴的显示个数
        xAxis.setDrawGridLines(false);

//
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis);
            }

            @Override
            public String getFormattedValue(float value) {

                int i = (int) (value/0.8999);
                /**
                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
                 */
                Log.d( "getFormattedValue2: ",value+"");
                i++;
                return i+"";
            }
        });


        YAxis leftAxis = line_chart.getAxisLeft();
        line_chart.getAxisRight().setEnabled(false);
        leftAxis.setTextSize(6f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期


        List<Entry> mValues = new ArrayList<>();
        mValues.add(new Entry(0f, 4f));
        mValues.add(new Entry(1f, 3f));
        mValues.add(new Entry(2f, 1f));
        mValues.add(new Entry(3f, 1f));
        mValues.add(new Entry(4f, 2f));
        mValues.add(new Entry(5f, 6f));
        mValues.add(new Entry(6f, 5f));

        LineDataSet set1 = new LineDataSet(mValues,"电力");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        set1.setColor(Color.parseColor("#055280"));
        set1.setDrawCircles(false);
        set1.setLineWidth(3f);
        set1.setDrawValues(false);


        // set the filled area
        // set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return line_chart.getAxisLeft().getAxisMinimum();
            }
        });
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData data = new LineData(dataSets);
        // 添加到图表中
        line_chart.setData(data);
        //隐藏label
        line_chart.getLegend().setEnabled(false);
        //绘制图表
        line_chart.invalidate();
        //判断图表中原来是否有数据
    }

    public void setData3(){

        List<BarEntry> list=new ArrayList<>();

//        //为第一组添加数据
        list.add(new BarEntry(1,37));
        list.add(new BarEntry(2,109));
        list.add(new BarEntry(3,77));
//
//        //为第二组添加数据
//        list2.add(new BarEntry(1,80));
//        list2.add(new BarEntry(2,74));

        BarDataSet barDataSet=new BarDataSet(list,"");
        barDataSet.setColor(Color.parseColor("#e3f3fe"));    //为第一组柱子设置颜色
        barDataSet.setDrawValues(false);
        bar_chart.getLegend().setEnabled(false);

        //设置文字颜色
        //  barDataSet.setValueTextColor(rgb("#0092f5"));

        BarData barData=new BarData(barDataSet);   //加上第一组
        //重点！！！ 加上第二组（多组也可以用同样的方法）  一定是以数据大小的降序添加
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
        bar_chart.getXAxis().setLabelCount(7);
        bar_chart.getDescription().setEnabled(false);    //右下角一串英文字母不显示
        bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);   //X轴的位置设置为下  默认为上
        bar_chart.getAxisRight().setEnabled(false);     //右侧Y轴不显示   默认为显示

        bar_chart.invalidate();
        bar_chart.refreshDrawableState();
    }

}