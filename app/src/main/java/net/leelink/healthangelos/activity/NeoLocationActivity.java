package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class NeoLocationActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private MapView map_view;
    private AMap aMap;
    private Context context;
    private TextView tv_address,tv_time,tv_locate,tv_track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_location);
        context = this;
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();
        initData();
    }


    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_address = findViewById(R.id.tv_address);
        tv_time = findViewById(R.id.tv_time);
        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        tv_locate = findViewById(R.id.tv_locate);
        tv_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locate();
            }
        });
        tv_track = findViewById(R.id.tv_track);
        tv_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,TrackActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initData(){
        OkGo.<String>get(Urls.getInstance().LASTESTGPS)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最后一次定位记录", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                LatLng latLng = new LatLng(json.getDouble("latitude"),json.getDouble("longitude"));
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.title("用户所在位置").snippet(getIntent().getStringExtra("address"));
                                markerOption.draggable(true);//设置Marker可拖动
                                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                        .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                markerOption.setFlat(true);//设置marker平贴地图效果
                                  markerOption.position(latLng);
                                aMap.addMarker(markerOption);
                                  CameraPosition cameraPosition = new CameraPosition(latLng, 15, 0, 30);
                                  CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                  aMap.moveCamera(cameraUpdate);
                                tv_address.setText(json.getString("address"));
                                tv_time.setText("最后更新: "+json.getString("createTime"));
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

        String imei = MyApplication.userInfo.getJwotchImei();;
        OkGo.<String>get(Urls.getInstance().SUPPORTGPS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询设备是否支持gps", json.toString());
                            if (json.getInt("status") == 200) {
                                if(json.getBoolean("data")){
                                    tv_locate.setVisibility(View.VISIBLE);
                                } else {
                                    tv_locate.setVisibility(View.GONE);
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
                });

    }

    public void locate(){
        String imei;
        if(getIntent().getStringExtra("imei")!=null){
            imei = getIntent().getStringExtra("imei");

        }else {
            imei = MyApplication.userInfo.getJwotchImei();
        }
        showProgressBar();
        tv_locate.setClickable(false);
        OkGo.<String>get(Urls.getInstance().OPENGPS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",imei )
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                           tv_locate.setClickable(true);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("发送gps定位", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                initData();
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
                        Toast.makeText(context, "系统繁忙", Toast.LENGTH_SHORT).show();
                        tv_locate.setClickable(true);
                    }
                });
    }
}
//4 13 22
//4 10 11 12 13 14 15 16 22
//1 2 3 4 5 6 7 15 19 22 25
//4 11 12 13 14 15 19 22 25
//4 11 15 19 22 25
//4 11 12 13 14 15 19 20 21 22 23 24 25