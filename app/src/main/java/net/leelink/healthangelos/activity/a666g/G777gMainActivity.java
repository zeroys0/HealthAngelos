package net.leelink.healthangelos.activity.a666g;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.a666g.bean.G7gBean;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class G777gMainActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_unbind,tv_more,tv_blood_sugar,tv_time,tv_state,tv_imei,tv_number_state;
    private String imei;
    private LineChart line_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g777g_main);
        context = this;
        init();
        getLast();
        initData();
    }

    public void init(){
        imei = getIntent().getStringExtra("imei");
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(imei);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_more = findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);
        line_chart = findViewById(R.id.line_chart);
        tv_blood_sugar = findViewById(R.id.tv_blood_sugar);
        tv_time = findViewById(R.id.tv_time);
        tv_state = findViewById(R.id.tv_state);
        tv_number_state = findViewById(R.id.tv_number_state);

    }

    public void getLast(){
        OkGo.<String>get(Urls.getInstance().AALGLAST)
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
                            Log.d("查询最新血糖测量数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_blood_sugar.setText(json.getString("value"));
                                tv_time.setText("最后一次测量时间:"+json.getString("test_time"));
                                if(json.getInt("meal_time")==0){
                                    tv_state.setText("餐前");
                                    if(json.getDouble("value") <3.6 || json.getDouble("value")>6.1){
                                        tv_number_state.setBackground(getResources().getDrawable(R.drawable.bg_red_radius_14));
                                        tv_number_state.setText("异常");
                                    }
                                }else {
                                    tv_state.setText("餐后");
                                    if(json.getDouble("value")>7.8){
                                        tv_number_state.setBackground(getResources().getDrawable(R.drawable.bg_red_radius_14));
                                        tv_number_state.setText("异常");
                                    }
                                }
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

    public void initData(){
        OkGo.<String>get(Urls.getInstance().AALG7DAY)
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
                            Log.d("获取7天血糖信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<G7gBean> list = gson.fromJson(jsonArray.toString(),new TypeToken<List<G7gBean>>(){}.getType());
//                                setData2(list);
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
                Intent intent = new Intent(context,G777gListActivity.class);
                intent.putExtra("imei",imei);
                startActivity(intent);
                break;
        }
    }

    public void unbind(){
        LoadDialog.start(context);
        OkGo.<String>delete(Urls.getInstance().UNBINDB+"/"+imei)
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

    public void setData(List<G7gBean> dataList){
        line_chart.setDescription(null);
        line_chart.setDragEnabled(false);   //能否拖拽
        XAxis xAxis = line_chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的文字在底部
//        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setTextSize(6f);//设置文字大小
        xAxis.setLabelCount(7,true);  //设置X轴的显示个数
//        xAxis.setAvoidFirstLastClipping(false);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setDrawGridLines(false);
//        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
//        xAxis.setGridColor(Color.parseColor("#707070"));  //网格线条的颜色
//        List<String> day_list = getDays();
//
//
//
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> times = new ArrayList<>();
        for(G7gBean bpBean:dataList){
            times.add(bpBean.getDay());
        }
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(times);
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
//                int i = (int) (value/0.899999);
//                /**
//                 * 不知道为什么x轴间隔不到0.9f一个 所以进行处理
//                 */
//                Log.d( "getFormattedValue: ",value+"");
////                Log.d( "getFormattedValue: ",i+"");
////                return (int)value+"";
//                return (int)i+"";
//            }
//        });


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
        List<Entry> mValues2 = new ArrayList<>();

        mValues.add(new Entry(0f, dataList.get(0).getValue1()));
        mValues.add(new Entry(1f, dataList.get(1).getValue1()));
        mValues.add(new Entry(2f, dataList.get(2).getValue1()));
        mValues.add(new Entry(3f, dataList.get(3).getValue1()));
        mValues.add(new Entry(4f, dataList.get(4).getValue1()));
        mValues.add(new Entry(5f, dataList.get(5).getValue1()));
        mValues.add(new Entry(6f, dataList.get(6).getValue1()));

        mValues2.add(new Entry(0f,dataList.get(0).getValue2()));
        mValues2.add(new Entry(1f,dataList.get(1).getValue2()));
        mValues2.add(new Entry(2f,dataList.get(2).getValue2()));
        mValues2.add(new Entry(3f,dataList.get(3).getValue2()));
        mValues2.add(new Entry(4f,dataList.get(4).getValue2()));
        mValues2.add(new Entry(5f,dataList.get(5).getValue2()));
        mValues2.add(new Entry(6f,dataList.get(6).getValue2()));


        LineDataSet set1 = new LineDataSet(mValues,"餐后");
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);
        set1.setColor(rgb("#ff0000"));
        set1.setCircleColor(rgb("#ff0000"));
        // set the filled area
        set1.setDrawFilled(false);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return line_chart.getAxisLeft().getAxisMinimum();
            }
        });

        LineDataSet set2 = new LineDataSet(mValues2,"餐前");
        set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        // customize legend entry
        set2.setFormLineWidth(1f);
        set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set2.setFormSize(15.f);
        set2.setColor(rgb("#0092f5"));
        set2.setCircleColor(rgb("#0092f5"));
        // set the filled area
        set2.setDrawFilled(false);
        set2.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return line_chart.getAxisLeft().getAxisMinimum();
            }
        });



        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData data = new LineData(dataSets);
        // 添加到图表中
        line_chart.setData(data);
        //绘制图表
        line_chart.invalidate();
        //判断图表中原来是否有数据
    }

    public int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }


}