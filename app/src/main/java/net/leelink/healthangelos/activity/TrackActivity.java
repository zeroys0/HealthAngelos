package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.amap.api.maps.model.MarkerOptions;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrackActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private TextView tv_start_time,tv_end_time;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    int time_type;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private MapView map_view;
    private AMap aMap;
    private String start,end;

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

    public void init(){
        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        tv_start_time.setText(now);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
        tv_end_time.setText(now);
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

        if(day>30){
            Toast.makeText(context, "最多显示30天的轨迹数据", Toast.LENGTH_SHORT).show();
            long e = end.getTime() - (1000L * 60 * 60 * 24 * 30);
            tv_start_time.setText(simpleDateFormat.format(e));
        }

        OkGo.<String>get(Urls.getInstance().GPSRECORD)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("beginDate", tv_start_time.getText().toString())
                .params("endDate",tv_end_time.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取定位记录", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                ArrayList<MarkerOptions> list = new ArrayList<>();
                                JSONArray ja = json.getJSONArray("list");
                                for(int i =0;i<ja.length();i++){
                                    LatLng latLng = new LatLng(ja.getJSONObject(i).getDouble("latitude"),ja.getJSONObject(i).getDouble("longitude"));
                                    MarkerOptions markerOption = new MarkerOptions();
                                    markerOption.title(ja.getJSONObject(i).getString("address")).snippet(ja.getJSONObject(i).getString("createTime"));
                                    markerOption.draggable(true);//设置Marker可拖动
                                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                            .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                    markerOption.setFlat(true);//设置marker平贴地图效果
                                    markerOption.position(latLng);
                                    list.add(markerOption);
                                }
                                if(list.size()>0) {
                                    aMap.addMarkers(list, true);
                                }
                                LatLng lg = new LatLng(ja.getJSONObject(0).getDouble("latitude"),ja.getJSONObject(0).getDouble("longitude"));
                                CameraPosition cameraPosition = new CameraPosition(lg, 15, 0, 30);
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                aMap.moveCamera(cameraUpdate);
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
        switch (v.getId()){
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
}