package net.leelink.healthangelos.activity.Badge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import androidx.core.content.ContextCompat;

public class BadgeHrActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private Context context;
    private Button btn_before, btn_after;
    private String imei;
    private TextView tv_date;
    Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private LineChart line_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_hr);
        context = this;
        createProgressBar(context);
        imei = getIntent().getStringExtra("imei");
        init();
        getHrData();
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
                getHrData();
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
                getHrData();
                break;
        }
    }

    public void getHrData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().BADGE_HR)
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
                            Log.d("查询心率列表", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<HrBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<HrBean>>() {
                                }.getType());
                                setData(list);

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

    public void setData(List<HrBean> dataList) {
        line_chart.setDescription(null);
        line_chart.setDragEnabled(true);   //能否拖拽
        XAxis xAxis = line_chart.getXAxis();
        line_chart.setVisibleXRangeMaximum(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(10, true); //设置X轴的显示个数
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(dataList.size() - 1f);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis);
            }

            @Override
            public String getFormattedValue(float value) {

                //     int i = (int) (value/0.899999);
                /**
                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
                 */
                if(dataList.size()==0){
                    return "";
                }
                int i = (int) Math.max(0, Math.min(value, dataList.size() - 1));
                Log.d("getFormattedValue: ", value + "");
//                Log.d( "getFormattedValue: ",i+"");
//                return (int)value+"";
                if (i - 1 <= dataList.size()) {
                    String time = "";
                    try {
                        Date date = sdf.parse(dataList.get(i).getTestTime());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                        time = outputFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return time;
                } else {
                    return (int) i + "";
                }
            }
        });


        YAxis leftAxis = line_chart.getAxisLeft();
        line_chart.getAxisRight().setEnabled(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期

        List<Entry> mValues = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            mValues.add(new Entry(i, dataList.get(i).getHr()));
        }

        LineDataSet set1 = new LineDataSet(mValues, "心率");
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setDrawValues(true);
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
        if (dataList.size() <= 10) {    //数据不到10
            line_chart.setVisibleXRange(0, dataList.size() - 1);
        } else {
            line_chart.setVisibleXRange(0, 9);
        }
        //绘制图表
        line_chart.invalidate();
        //判断图表中原来是否有数据
    }

}