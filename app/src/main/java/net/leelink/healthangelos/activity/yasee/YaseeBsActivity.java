package net.leelink.healthangelos.activity.yasee;

import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
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

import androidx.core.content.ContextCompat;

public class YaseeBsActivity extends BaseActivity {
    ImageView img_before, img_after;
    private Context context;
    private RelativeLayout rl_back;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private TextView tv_time,tv_more;

    private LineChart line_chart;

    private TimePickerView pvTime;

    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yasee_bs);
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
        line_chart = findViewById(R.id.line_chart);
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
                intent.putExtra("type",1);
                startActivity(intent);
                break;
        }
    }

    public void initData(String date) {
        OkGo.<String>get(Urls.getInstance().YASEE_BS)
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
                            Log.d("获取血糖信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<YaseeBsBean> list = gson.fromJson(jsonArray.toString(),new TypeToken<List<YaseeBsBean>>(){}.getType());
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

    public void setData(List<YaseeBsBean> dataList){
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
        List<Date> times = new ArrayList<>();
        for(YaseeBsBean bean:dataList){
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
        for(int i =0;i<dataList.size();i++) {
            mValues.add(new Entry((float) i, dataList.get(i).getResult()));
        }


        LineDataSet set1 = new LineDataSet(mValues,"血糖");
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