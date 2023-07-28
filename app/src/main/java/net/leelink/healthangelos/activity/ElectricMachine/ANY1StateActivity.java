package net.leelink.healthangelos.activity.ElectricMachine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ANY1StateActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView event_list;
    private ANYEventAdapter anyEventAdapter;
    private LineChart line_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any1_state);
        context = this;
        init();
        initData();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        event_list = findViewById(R.id.event_list);
        line_chart = findViewById(R.id.line_chart);
       // setData(line_chart);
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().ANY1_STATES)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("familyId",getIntent().getStringExtra("familyId"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看实时数据", json.toString());
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

    public void initList(){

        OkGo.<String>get(Urls.getInstance().ANY1_STATES+"/"+getIntent().getStringExtra("familyId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查看最新实时数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                String data = json.getString("currentDailyData");
                                Gson gson = new Gson();
                                List<List<Double>> doubleList = gson.fromJson(data, new TypeToken<List<List<Double>>>(){}.getType());
                                List<List<Integer>> integerList = new ArrayList<>();
                                for (List<Double> sublist : doubleList) {
                                    List<Integer> temp = new ArrayList<>();
                                    for (Double value : sublist) {
                                        temp.add(value.intValue());
                                    }
                                    integerList.add(temp);
                                }
                                setData(line_chart,integerList);
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

        anyEventAdapter = new ANYEventAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        event_list.setAdapter(anyEventAdapter);
        event_list.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    public void setData(LineChart line_chart, List<List<Integer>> integerlist){
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
//
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
        float x = 0;
        for(int i=0;i<integerlist.size();i++){
            for(int n=0;n<integerlist.get(i).size();n++){
                mValues.add(new Entry(x, integerlist.get(i).get(n)));
                x++;
            }
        }
//        mValues.add(new Entry(0f, 0.6f));
//        mValues.add(new Entry(1f, 0.4f));
//        mValues.add(new Entry(2f, 0.2f));
//        mValues.add(new Entry(3f, 0.5f));
//        mValues.add(new Entry(4f, 0.5f));
//        mValues.add(new Entry(5f, 0.5f));
//        mValues.add(new Entry(6f, 0.3f));
//        mValues.add(new Entry(7f, 0.5f));
//        mValues.add(new Entry(8f, 0.6f));
//        mValues.add(new Entry(9f, 0.4f));
//        mValues.add(new Entry(10f, 0.2f));
//        mValues.add(new Entry(11f, 0.1f));
//        mValues.add(new Entry(12f, 0f));
//        mValues.add(new Entry(13f, 0f));
//        mValues.add(new Entry(14f, 0f));
//        mValues.add(new Entry(15f, 0.3f));
//        mValues.add(new Entry(16f, 0.5f));
//        mValues.add(new Entry(17f, 0.2f));
//        mValues.add(new Entry(18f, 0.3f));
//        mValues.add(new Entry(19f, 0.4f));
//        mValues.add(new Entry(20f, 0.5f));
//        mValues.add(new Entry(21f, 0.1f));
//        mValues.add(new Entry(22f, 0.1f));
//        mValues.add(new Entry(23f, 0.2f));
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

}