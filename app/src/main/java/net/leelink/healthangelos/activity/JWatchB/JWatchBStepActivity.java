package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.StepBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class JWatchBStepActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private LineChart line_chart;
    private TextView tv_step_number,tv_calorie;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatFormat = new SimpleDateFormat("MM-dd");
    List<StepBean> list;
    private ImageView img_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwatch_bstep);
        context = this;
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        line_chart = findViewById(R.id.line_chart);
        tv_step_number = findViewById(R.id.tv_step_number);
        img_refresh = findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(this);
    }

    public void initData(){
        String imei = getSharedPreferences("sp", 0).getString("imei", "");
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().STEP)
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
                            Log.d("查看计步数据", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new  TypeToken<List<StepBean>>(){}.getType());
                                setData();
                                tv_step_number.setText(list.get(6).getStep()+"");
                             //   tv_calorie.setText("卡路里: "+list.get(6).getRoll());
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
            case R.id.img_refresh:
                initData();
                break;

        }
    }
    private DecimalFormat mFormat;

//    public MyValueFormatter() {
//        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
//    }


    public void setData(){
        line_chart.setDescription(null);
        line_chart.setDragEnabled(false);   //能否拖拽
        XAxis xAxis = line_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setTextSize(10f);//设置文字大小
        xAxis.setLabelCount(7);  //设置X轴的显示个数
        xAxis.setAvoidFirstLastClipping(false);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
        List<String> day_list = getDays();



        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis);
            }

            @Override
            public String getFormattedValue(float value) {

                int i = (int) (value/0.899);
                /**
                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
                 */
                Log.d( "getFormattedValue: ",value+"");
                Log.d( "getFormattedValue: ",i+"");
                return day_list.get(i);
            }
        });


        YAxis leftAxis = line_chart.getAxisLeft();
        YAxis axisRight = line_chart.getAxisRight();
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        leftAxis.setGranularity(20f); // 网格线条间距
        leftAxis.setEnabled(false);     //设置是否使用 Y轴左边的
        axisRight.setEnabled(false);   //设置是否使用 Y轴右边的
        leftAxis.setDrawGridLines(true);      //是否使用 Y轴网格线条
        leftAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
        leftAxis.setDrawLabels(true);        //是否显示Y轴刻度
        leftAxis.setStartAtZero(true);        //设置Y轴数值 从零开始
        leftAxis.setTextSize(12f);            //设置Y轴刻度字体
        leftAxis.setTextColor(Color.parseColor("#656565"));   //设置字体颜色

        List<Entry> mValues = new ArrayList<>();
        mValues.add(new Entry(0f, list.get(0).getStep()));
        mValues.add(new Entry(1f, list.get(1).getStep()));
        mValues.add(new Entry(2f, list.get(2).getStep()));
        mValues.add(new Entry(3f, list.get(3).getStep()));
        mValues.add(new Entry(4f, list.get(4).getStep()));
        mValues.add(new Entry(5f, list.get(5).getStep()));
        mValues.add(new Entry(6f, list.get(6).getStep()));
        LineDataSet set1 = new LineDataSet(mValues,"计步数据");
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

    public List<String> getDays(){
        List<String> list = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        list.add(dateFormatFormat.format(date));
        list.add(dateFormatFormat.format(date));
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(date);
        for(int i =0;i<6;i++) {
            calendar.add(calendar.DATE, -1);
            String date1 = dateFormatFormat.format(calendar.getTime());
            list.add(date1);
        }
        Collections.reverse(list);
        return list;
    }

}