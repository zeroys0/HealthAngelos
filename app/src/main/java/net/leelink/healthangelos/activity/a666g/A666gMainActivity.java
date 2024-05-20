package net.leelink.healthangelos.activity.a666g;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.a666g.bean.A6gBean;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class A666gMainActivity extends BaseActivity implements View.OnClickListener, OnChartValueSelectedListener {
    private Context context;
    private RelativeLayout rl_back;
    private BarChart bar_chart;
    private TextView tv_unbind,tv_more,tv_change,tv_dp,tv_sp,tv_heart_rate,tv_time,tv_imei,tv_bp_state,tv_heart_rate_state;
    private String imei;
    private LineChart line_chart;
    List<BarEntry>list;
    List<BarEntry>list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a666g_main);
        context = this;
        init();
        getLast();
        initData();
    }

    public void init(){
        try {
            imei = getIntent().getStringExtra("imei");
        } catch (NullPointerException e){
            imei = "";
        }
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_more = findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);
        line_chart = findViewById(R.id.line_chart);
        tv_change = findViewById(R.id.tv_change);
        tv_change.setOnClickListener(this);
        bar_chart = findViewById(R.id.bar_chart);
        tv_dp = findViewById(R.id.tv_dp);
        tv_sp = findViewById(R.id.tv_sp);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_time = findViewById(R.id.tv_time);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(imei);
        tv_bp_state = findViewById(R.id.tv_bp_state);
        tv_heart_rate_state = findViewById(R.id.tv_heart_rate_state);

    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().AALP7DAY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取7天血压信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<A6gBean> list = gson.fromJson(jsonArray.toString(),new TypeToken<List<A6gBean>>(){}.getType());
                                setData2(list);
                                setData(list);
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

    //获取最后一次测量数据
    public void getLast(){
        OkGo.<String>get(Urls.getInstance().AALPLAST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询最新血压测量数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");

                                tv_dp.setText(json.getString("dia"));
                                tv_sp.setText(json.getString("sys"));
                                if(json.getInt("sys")<90 || json.getInt("sys")>140) {
                                    tv_bp_state.setBackground(getResources().getDrawable(R.drawable.bg_red_radius_14));
                                    tv_bp_state.setText("异常");
                                }
                                if(json.getInt("dia")<70 || json.getInt("dia")>90) {
                                    tv_bp_state.setBackground(getResources().getDrawable(R.drawable.bg_red_radius_14));
                                    tv_bp_state.setText("异常");
                                }
                                tv_heart_rate.setText(json.getString("pul"));
                                if(json.getInt("pul")<60 || json.getInt("pul")>100) {
                                    tv_heart_rate_state.setBackground(getResources().getDrawable(R.drawable.bg_red_radius_14));
                                    tv_heart_rate_state.setText("异常");
                                }
                                tv_time.setText("最后一次测量时间:"+json.getString("test_time"));

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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_unbind:
                unbind();
                break;
            case R.id.tv_more:
                Intent intent = new Intent(context,A666gListActivity.class);
                intent.putExtra("imei",imei);
                startActivity(intent);
                break;
            case R.id.tv_change:
                if(bar_chart.getVisibility()==View.VISIBLE){
                    bar_chart.setVisibility(View.GONE);
                    line_chart.setVisibility(View.VISIBLE);
                }else {
                    bar_chart.setVisibility(View.VISIBLE);
                    line_chart.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void unbind(){
        LoadDialog.start(context);
        OkGo.<String>delete(Urls.getInstance().UNBINDA+"/"+imei)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LoadDialog.stop();
                    }
                });
    }


    public void setData(List<A6gBean> dataList){
        line_chart.setDescription(null);
        line_chart.setDragEnabled(false);   //能否拖拽
        XAxis xAxis = line_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
//        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setLabelCount(7);  //设置X轴的显示个数
//        xAxis.setAvoidFirstLastClipping(false);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setDrawGridLines(false);
//        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
//        xAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
//        List<String> day_list = getDays();
//
//
//
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis);
            }

            @Override
            public String getFormattedValue(float value) {

                int i = (int) (value/0.899999);
                /**
                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
                 */
                Log.d( "getFormattedValue: ",value+"");
//                Log.d( "getFormattedValue: ",i+"");
//                return (int)value+"";
                return (int)i+"";

            }
        });


        YAxis leftAxis = line_chart.getAxisLeft();
        line_chart.getAxisRight().setEnabled(false);
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

        mValues.add(new Entry(0f, dataList.get(0).getPul()));
        mValues.add(new Entry(1f, dataList.get(1).getPul()));
        mValues.add(new Entry(2f, dataList.get(2).getPul()));
        mValues.add(new Entry(3f, dataList.get(3).getPul()));
        mValues.add(new Entry(4f, dataList.get(4).getPul()));
        mValues.add(new Entry(5f, dataList.get(5).getPul()));
        mValues.add(new Entry(6f, dataList.get(6).getPul()));

        LineDataSet set1 = new LineDataSet(mValues,"心率");
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return line_chart.getAxisLeft().getAxisMinimum();
            }
        });

        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_blue);
            set1.setFillDrawable(drawable);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData data = new LineData(dataSets);
        // 添加到图表中
        line_chart.setData(data);
        //绘制图表
        line_chart.invalidate();
        //判断图表中原来是否有数据
    }

//    public void setData2(){
//        bar_chart.setOnChartValueSelectedListener(this);
//        bar_chart.setDrawValueAboveBar(true);
//        bar_chart.getDescription().setEnabled(false);
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        bar_chart.setMaxVisibleValueCount(40);
//
//        // scaling can now only be done on x- and y-axis separately
//        bar_chart.setPinchZoom(false);
//
//        bar_chart.setDrawGridBackground(false);
//        bar_chart.setDrawBarShadow(false);
//        bar_chart.setHighlightFullBarEnabled(false);
//
//        // change the position of the y-labels
//        YAxis leftAxis = bar_chart.getAxisLeft();
//        leftAxis.setValueFormatter(new MyAxisValueFormatter());
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        bar_chart.getAxisRight().setEnabled(false);
//
//        XAxis xLabels = bar_chart.getXAxis();
//        xLabels.setPosition(XAxis.XAxisPosition.TOP);
//
//        // chart.setDrawXLabels(false);
//        // chart.setDrawYLabels(false);
//
//        // setting data
////        seekBarX.setProgress(12);
////        seekBarY.setProgress(100);
//
//        Legend l = bar_chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setFormSize(8f);
//        l.setFormToTextSpace(4f);
//        l.setXEntrySpace(6f);
//
//
//        BarDataSet set1;
//
//
//        ArrayList<BarEntry> values = new ArrayList<>();
//
//        for (int i = 0; i < 6; i++) {
//            float mul = (7 + 1);
//            float val1 = (float) (Math.random() * mul) + mul / 3;
//            float val2 = (float) (Math.random() * mul) + mul / 3;
////            float val3 = (float) (Math.random() * mul) + mul / 3;
//
//            values.add(new BarEntry(
//                    i,
//                    new float[]{val1, val2},
//                    getResources().getDrawable(R.drawable.star)));
//        }
//        values.add(new BarEntry(
//                7,
//                new float[]{74, 104},
//                getResources().getDrawable(R.drawable.star)));
//        if (bar_chart.getData() != null &&
//                bar_chart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) bar_chart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//
//            bar_chart.getData().notifyDataChanged();
//            bar_chart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(values, " ");
//            set1.setDrawIcons(false);
//            set1.setColors(getColors());
//            set1.setStackLabels(new String[]{"低压", "高压"});
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            data.setValueFormatter(new MyValueFormatter());
//            data.setValueTextColor(rgb("#0092f5"));
//
//            bar_chart.setData(data);
//        }
//
//        bar_chart.setFitBars(true);
//        bar_chart.invalidate();
//
//    }
    public void setData2(List<A6gBean> dataList){

        list=new ArrayList<>();
        list2=new ArrayList<>();
        int x= 1;
        List<Integer> colors = new ArrayList<>();
        for(int i =0;i<dataList.size();i++){
            list.add(new BarEntry(x,dataList.get(i).getSys()));
            list2.add(new BarEntry(x,dataList.get(i).getDia()));
            x++;
            Log.d( "setData2: ",dataList.get(i).getSys()+"   " + dataList.size());
            if(dataList.get(i).getSys()>130){
                colors.add(Color.RED);
            }else {
                colors.add(rgb("#0092f5"));
            }
        }

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
        bar_chart.getXAxis().setLabelCount(7);
        bar_chart.getDescription().setEnabled(false);    //右下角一串英文字母不显示
        bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);   //X轴的位置设置为下  默认为上
        bar_chart.getAxisRight().setEnabled(false);     //右侧Y轴不显示   默认为显示

        bar_chart.invalidate();
        bar_chart.refreshDrawableState();
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[2];
        int[] MATERIAL_COLORS = {rgb("#ffffff"),rgb("#e3f3fe")};
        System.arraycopy(MATERIAL_COLORS, 0, colors, 0, 2);

        return colors;
    }

    public int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
}