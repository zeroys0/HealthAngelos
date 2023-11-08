package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TrackActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_start_time, tv_end_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int time_type;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private MapView map_view;
    private AMap aMap;
    private ImageView img_play;
    private int position = 0;
    private boolean isrunning = false;

    ArrayList<MarkerOptions> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        context = this;
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();
        initPickerView();
        initData();
    }



    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (list.size() > 1) {
                aMap.addMarker(list.get(position));
                Marker marker =aMap.addMarker(list.get(position));
                marker.showInfoWindow();
                Log.d("position2: ", list.get(position).getPosition().latitude + " " + list.get(position).getPosition().longitude);
            } else {
                return;
            }
            // LatLng lg = new LatLng(ja.getJSONObject(0).getDouble("latitude"),ja.getJSONObject(0).getDouble("longitude"));
            LatLng lg = list.get(position).getPosition();
            CameraPosition cameraPosition = new CameraPosition(lg, 15, 0, 30);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            aMap.moveCamera(cameraUpdate);
            List<LatLng> latLngs = new ArrayList<LatLng>();
            if (position > 0) {
                latLngs.add(list.get(position).getPosition());
                latLngs.add(list.get(position - 1).getPosition());
                Log.d("position1: ", list.get(position - 1).getPosition().latitude + " " + list.get(position - 1).getPosition().longitude);

                aMap.addPolyline(new PolylineOptions().
                        addAll(latLngs).width(10).color(Color.argb(150, 255, 161, 00)));
            }
            mHandler.postDelayed(this, 2000); // 2秒后再次执行任务
            position++;
            //停止绘制
            if (position == list.size()) {
                mHandler.removeCallbacks(mRunnable);
                isrunning = false;
                img_play.setImageResource(R.drawable.img_track_start);
            }
        }
    };

    public void init() {
        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        tv_start_time.setText(now);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
        tv_end_time.setText(now);
        img_play = findViewById(R.id.img_play);
        img_play.setOnClickListener(this);

    }

    public void initData() {
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(tv_start_time.getText().toString());
            end = simpleDateFormat.parse(tv_end_time.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = end.getTime() - start.getTime();
        long day = diff / (1000 * 60 * 60 * 24);

        if (day > 30) {
            Toast.makeText(context, "最多显示30天的轨迹数据", Toast.LENGTH_SHORT).show();
            long e = end.getTime() - (1000L * 60 * 60 * 24 * 30);
            tv_start_time.setText(simpleDateFormat.format(e));
        }

        list.clear();
        position = 0;
        OkGo.<String>get(Urls.getInstance().GPSRECORD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("beginDate", tv_start_time.getText().toString())
                .params("endDate", tv_end_time.getText().toString())
                .params("pageSize", 1000)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取行动轨迹", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");

                                JSONArray ja = json.getJSONArray("list");

                                for (int i = 0; i < ja.length(); i++) {
                                    double[] lng = new double[2];
                                    switch (ja.getJSONObject(i).getInt("gpsType")) {
                                        case 0:
                                        case 3:
                                            //百度坐标转高德
                                            lng = Utils.bd_decrypt(ja.getJSONObject(i).getDouble("latitude"), ja.getJSONObject(i).getDouble("longitude"));
                                            break;
                                        case 1:
                                            //高德地图火星坐标
                                            lng = new double[]{ja.getJSONObject(i).getDouble("latitude"), ja.getJSONObject(i).getDouble("longitude")};
                                            break;
                                    }
                                    LatLng latLng = new LatLng(lng[0], lng[1]);
                                    if(ja.getJSONObject(i).getInt("gpsType")==2){//世界转火星
                                        latLng = new LatLng(ja.getJSONObject(i).getDouble("latitude"), ja.getJSONObject(i).getDouble("longitude"));
                                        latLng = Utils.transformFromWGSToGCJ(latLng);
                                    }
                                    //(ja.length()-i)
                                    MarkerOptions markerOption = new MarkerOptions();
                                    markerOption.title(i+","+ja.getJSONObject(i).getString("address")+","+ja.getJSONObject(i).getString("createTime"));
                                    markerOption.draggable(true);//设置Marker可拖动
                                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                            .decodeResource(getResources(), R.drawable.img_track_marker)));
                                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                    markerOption.setFlat(true);//设置marker平贴地图效果
                                    markerOption.position(latLng);
                                    list.add(markerOption);
                                    aMap.clear();
                                    // LatLng lg = new LatLng(ja.getJSONObject(0).getDouble("latitude"),ja.getJSONObject(0).getDouble("longitude"));
                                }
                                Collections.reverse(list);
                                if (list.size() > 0) {
                                    aMap.addMarker(list.get(position));
                                    aMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                                    Marker marker =aMap.addMarker(list.get(position));
                                    marker.showInfoWindow();
                                    LatLng lg = list.get(position).getPosition();
                                    CameraPosition cameraPosition = new CameraPosition(lg, 15, 0, 30);
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                    aMap.moveCamera(cameraUpdate);
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
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_start_time:
                pvTime.show();
                time_type = 1;
                break;
            case R.id.tv_end_time:
                pvTime.show();
                time_type = 2;
                break;
            case R.id.img_play:
                if (isrunning) {
                    mHandler.removeCallbacks(mRunnable);
                    isrunning = false;
                    img_play.setImageResource(R.drawable.img_track_start);
                } else {
                    if(list.size()==position){
                        position = 0;
                        aMap.clear();
                    }
                    mRunnable.run();
                    isrunning = true;
                    img_play.setImageResource(R.drawable.img_track_stop);
                }
                break;
        }
    }

    private void initPickerView() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean[] type = {true, true, true, false, false, false};
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (time_type == 1) {
                    tv_start_time.setText(sdf.format(date));

                }
                if (time_type == 2) {
                    tv_end_time.setText(sdf.format(date));
                }
                initData();
            }
        }).setType(type).build();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable); // 停止任务
    }

    public class MyInfoWindowAdapter implements AMap.InfoWindowAdapter {
        View infoWindow = null;

        @Override
        public View getInfoWindow(Marker marker) {
            if(infoWindow == null) {
                infoWindow = LayoutInflater.from(context).inflate(
                        R.layout.custom_info_window, null);
            }
            render(marker, infoWindow);
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        /**
         * 自定义infowinfow窗口
         */
        public void render(Marker marker, View view) {
//如果想修改自定义Infow中内容，请通过view找到它并修改
            TextView tv_address = view.findViewById(R.id.tv_address);
            TextView tv_number = view.findViewById(R.id.tv_number);
            TextView tv_time = view.findViewById(R.id.tv_time);
            String[] content = marker.getTitle().split(",");
            tv_number.setText(content[0]);
//            if(content[0].length()==2){
//                Log.d( "render: ","两位数");
//                ViewGroup.LayoutParams layoutParams = tv_number.getLayoutParams();
//                tv_number.setLayoutParams(layoutParams);
//                tv_number.setTextSize(25);
//            }
//            if(content[0].length()>2){
//                Log.d( "render: ","三位数");
//                ViewGroup.LayoutParams layoutParams = tv_number.getLayoutParams();
//                tv_number.setLayoutParams(layoutParams);
//                tv_number.setTextSize(17);
//            }
            tv_address.setText(content[1]);
            tv_time.setText(content[2]);
        }
    }

}