package net.leelink.healthangelos.activity.BioRadar;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BioRadarMainActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private LineChart line_chart;
    private TextView tv_unbind,tv_imei,tv_sex,tv_name,tv_age,tv_phone,tv_home_address;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_radar_main);
        context = this;
        imei = getIntent().getStringExtra("imei");
        init();
        initData();
    }

    public  void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        line_chart = findViewById(R.id.line_chart);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
        tv_imei =findViewById(R.id.tv_imei);
        tv_sex = findViewById(R.id.tv_sex);
        tv_name = findViewById(R.id.tv_name);
        tv_age = findViewById(R.id.tv_age);
        tv_phone = findViewById(R.id.tv_phone);
        tv_home_address = findViewById(R.id.tv_home_address);
        setData();
    }

    public void initData(){
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().FINOLDDETAIL)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询基本信息s", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_imei.setText(json.getString("deviceId"));
                                tv_name.setText(json.getString("elderlyName"));
                                int sex = json.getInt("sex");
                                if(sex ==0){
                                    tv_sex.setText("男");
                                } else {
                                    tv_sex.setText("女");
                                }
                                tv_age.setText(json.getString("age"));
                                tv_phone.setText(json.getString("telephone"));
                                String address = json.getString("address");
                                JSONObject jsonObject = new JSONObject(address);
                                tv_home_address.setText(jsonObject.getString("address"));

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
        switch(v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_unbind:
                unbind();
                break;
        }
    }

    public void unbind(){
        LoadDialog.start(context);
        OkGo.<String>delete(Urls.getInstance().RADAR_UNBIND+"/"+imei)
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


    public void setData(){
        line_chart.setDescription(null);
        line_chart.setDragEnabled(false);   //能否拖拽
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
        mValues.add(new Entry(0f, 20));
        mValues.add(new Entry(1f, 25));
        mValues.add(new Entry(2f, 17));
        mValues.add(new Entry(3f, 19));
        mValues.add(new Entry(4f, 1));
        mValues.add(new Entry(5f, 18));
        mValues.add(new Entry(6f, 3));
        mValues.add(new Entry(7f, 3));
        mValues.add(new Entry(8f, 3));
        mValues.add(new Entry(9f, 3));
        mValues.add(new Entry(10f, 3));
        mValues.add(new Entry(11f, 7));
        mValues.add(new Entry(12f, 3));
        mValues.add(new Entry(13f, 16));
        mValues.add(new Entry(14f, 3));
        mValues.add(new Entry(15f, 22));
        mValues.add(new Entry(16f, 1));
        mValues.add(new Entry(17f, 9));
        mValues.add(new Entry(18f, 9));
        mValues.add(new Entry(19f, 0));
        mValues.add(new Entry(20f, 9));
        mValues.add(new Entry(21f, 25));
        mValues.add(new Entry(22f, 11));
        mValues.add(new Entry(23f, 9));
        LineDataSet set1 = new LineDataSet(mValues,"活跃度");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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

}