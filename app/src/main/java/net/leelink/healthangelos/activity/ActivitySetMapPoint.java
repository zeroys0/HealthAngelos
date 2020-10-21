package net.leelink.healthangelos.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.FencePlanBean;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.fragment.MonitorLimitsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class ActivitySetMapPoint extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener {
    private MapView map_view;
    private AMap aMap;
    public int mark = 0;
    LatLng start_latlng, end_latlng;
    TextView tv_clear, tv_next, tv_start_time,tv_end_time,tv_circle_time,tv_time_scape,tv_limit_name,tv_alarm_type,tv_alarm_phone,tv_alarm_email;
    GeocodeSearch geocoderSearch;
    List<String> address_list = new ArrayList<>();
    Context context;
    LinearLayout ll_head;
    public int action = 0;
    String id;
    private RelativeLayout rl_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_map_point);
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        context = this;
        init();

    }

    public void init() {
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_circle_time = findViewById(R.id.tv_circle_time);
        tv_time_scape = findViewById(R.id.tv_time_scape);
        tv_limit_name = findViewById(R.id.tv_limit_name);
        tv_alarm_type = findViewById(R.id.tv_alarm_type);
        tv_alarm_phone = findViewById(R.id.tv_alarm_phone);
        tv_alarm_email = findViewById(R.id.tv_alarm_email);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(20000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mark == 0) {
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.title("A点").snippet("起始点");
                    markerOption.draggable(true);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.marker_a)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    start_latlng = latLng;
                    markerOption.position(latLng);
                    aMap.addMarker(markerOption);


                    RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(start_latlng.latitude, start_latlng.longitude), 50, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(query);


                    mark++;
                } else if (mark == 1) {
                    end_latlng = latLng;
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.title("B点").snippet("起始点");
                    markerOption.draggable(true);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.marker_b)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    markerOption.position(latLng);
                    aMap.addMarker(markerOption);
                    List<LatLng> list = new ArrayList<>();
                    list.add(new LatLng(start_latlng.latitude, start_latlng.longitude));
                    list.add(new LatLng(start_latlng.latitude, end_latlng.longitude));
                    list.add(new LatLng(end_latlng.latitude, end_latlng.longitude));
                    list.add(new LatLng(end_latlng.latitude, start_latlng.longitude));
                    aMap.addPolygon(new PolygonOptions()
                            .addAll(list)
                            .fillColor(Color.argb(50, 248, 170, 160)).strokeColor(Color.RED).strokeWidth(3));
                    mark++;
                    RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(end_latlng.latitude, end_latlng.longitude), 50, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(query);
                    tv_next.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_clear = findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                mark = 0;
                tv_next.setVisibility(View.INVISIBLE);
            }
        });
        tv_next = findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, LimitTitleActivity.class);
                intent.putExtra("start_lat", start_latlng.latitude);
                intent.putExtra("start_lon", start_latlng.longitude);
                intent.putExtra("end_lat", end_latlng.latitude);
                intent.putExtra("end_lon", end_latlng.longitude);
                intent.putExtra("a_address", address_list.get(0));
                intent.putExtra("b_address", address_list.get(1));
                if (action == 1) {
                    intent.putExtra("id", id);
                    intent.putExtra("action", 1);
                }
                startActivity(intent);
            }
        });
        initHead();
    }

    public void initHead() {
        ll_head = findViewById(R.id.ll_head);
        //编辑电子围栏范围
        if (getIntent().getIntExtra("type", 0) == MonitorLimitsFragment.EDIT_LIMIT) {
            action = 1;
            mark = 2;
            LimitBean limitBean = getIntent().getParcelableExtra("bean");
            id = limitBean.getId();
            drawMap(new LatLng(limitBean.getLa1(), limitBean.getLo1()), new LatLng(limitBean.getLa2(), limitBean.getLo2()));
        } else if (getIntent().getIntExtra("type", 0) == MonitorLimitsFragment.ADD_LIMIT) {     //添加电子围栏范围
            ll_head.setVisibility(View.INVISIBLE);
        } else if (getIntent().getIntExtra("type", 0) == MonitorLimitsFragment.CHECK_LIMIT) {       //查看电子围栏计划
            mark = 2;
            ll_head.setVisibility(View.VISIBLE);
            FencePlanBean fencePlanBean = getIntent().getParcelableExtra("data");
            tv_start_time.setText("开始时间:"+fencePlanBean.getStartTime());
            tv_end_time.setText("截止时间:"+fencePlanBean.getStopTime());
            if(fencePlanBean.getCycleType() ==1) {
                tv_circle_time.setText("循环次数:"+fencePlanBean.getMonitorDate());
            } else if(fencePlanBean.getCycleType() ==2) {
                tv_circle_time.setText("循环次数:" + fencePlanBean.getWeeks());
            }
            tv_time_scape.setText("时间间隔:"+fencePlanBean.getTimeInterval()+"秒");
            tv_limit_name.setText("监控范围:"+fencePlanBean.getScopeName());
            switch (fencePlanBean.getAlarmWay()) {
                case 1:
                   tv_alarm_type.setText("报警方式:短信");
                    break;
                case 2:
                    tv_alarm_type.setText("报警方式:邮件");
                    break;
                case 3:
                    tv_alarm_type.setText("报警方式:短信、邮件");
                    break;

            }
            tv_alarm_phone.setText("报警电话:"+fencePlanBean.getCellphoneNumber());
            tv_alarm_email.setText("报警邮箱:"+fencePlanBean.getMailAddress());
            tv_clear.setVisibility(View.INVISIBLE);
            drawMap(new LatLng(fencePlanBean.getLa1(),fencePlanBean.getLo1()),new LatLng(fencePlanBean.getLa2(),fencePlanBean.getLo2()));
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        Log.e("onGeocodeSearched: ", regeocodeResult.getRegeocodeAddress().getFormatAddress());
        address_list.add(regeocodeResult.getRegeocodeAddress().getFormatAddress());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    void drawMap(LatLng start_latlng, LatLng end_latlng) {

        ArrayList<MarkerOptions> option_list = new ArrayList<>();
        /**
         *添加a点
         */

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.title("A点").snippet("起始点");
        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.marker_a)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        this.start_latlng = start_latlng;
        markerOption.position(start_latlng);
        aMap.addMarker(markerOption);
        option_list.add(markerOption);

        /**
         *添加b点
         */
        MarkerOptions markerOption1 = new MarkerOptions();
        markerOption1.title("A点").snippet("起始点");
        markerOption1.draggable(true);//设置Marker可拖动
        markerOption1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.marker_b)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption1.setFlat(true);//设置marker平贴地图效果
        this.end_latlng = end_latlng;
        markerOption1.position(end_latlng);
        aMap.addMarker(markerOption1);
        option_list.add(markerOption1);
        aMap.addMarkers(option_list, true);

        /**
         * 画范围
         */
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(start_latlng.latitude, start_latlng.longitude));
        list.add(new LatLng(start_latlng.latitude, end_latlng.longitude));
        list.add(new LatLng(end_latlng.latitude, end_latlng.longitude));
        list.add(new LatLng(end_latlng.latitude, start_latlng.longitude));
        aMap.addPolygon(new PolygonOptions()
                .addAll(list)
                .fillColor(Color.argb(50, 248, 170, 160)).strokeColor(Color.RED).strokeWidth(3));
    }
}
