package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.LocateBean;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrailActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_trail_list;
    private MapView map_view;
    private AMap aMap;
    private RecyclerView locate_list;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ImageView img_before,img_after;
    private TextView tv_time,tv_show,tv_array,tv_default;
    private TimePickerView pvTime;
    private SimpleDateFormat sdf;
    private Saas_LocateAdapter saas_locateAdapter;
    private boolean isShow = false;
    private List<LocateBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);
        context = this;
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();
        initTime();
        String now = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        initList(now);
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);

        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        locate_list = findViewById(R.id.locate_list);
        img_before = findViewById(R.id.img_before);
        img_before.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        img_after = findViewById(R.id.img_after);
        img_after.setOnClickListener(this);
        tv_show = findViewById(R.id.tv_show);
        tv_show.setOnClickListener(this);
        rl_trail_list = findViewById(R.id.rl_trail_list);
        tv_array = findViewById(R.id.tv_array);
        tv_array.setOnClickListener(this);
        tv_default = findViewById(R.id.tv_default);

    }

    public void initList(String date){
        String uid = getSharedPreferences("sp", 0).getString("uid", "");
        date =  date.replace("-","");
        Log.d( "initList: ",date);
        LoadDialog.start(context);
        aMap.clear();
        list.clear();
        OkGo.<String>get(Urls.getInstance().APPFOOTPRINTQUERY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("date", date)
                .params("uId", uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询腕表轨迹", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("pos");
                                if(jsonArray.length()==0){
                                    tv_default.setVisibility(View.VISIBLE);
                                    locate_list.setVisibility(View.INVISIBLE);
                                } else {
                                    tv_default.setVisibility(View.INVISIBLE);
                                    locate_list.setVisibility(View.VISIBLE);
                                }
                                jsonArray = jsonArray.getJSONObject(0).getJSONArray("detail");
                                ArrayList<MarkerOptions> marker_list = new ArrayList<>();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<LocateBean>>(){}.getType());

                                if (list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        MarkerOptions markerOption = new MarkerOptions();
                                        markerOption.draggable(true);//设置Marker可拖动
                                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                        markerOption.setFlat(true);//设置marker平贴地图效果
                                        LatLng latLng = new LatLng(list.get(i).getLat(),list.get(i).getLon());
                                        markerOption.position(latLng);
                                        aMap.addMarker(markerOption);
                                        marker_list.add(markerOption);
                                    }
                                }
                                aMap.addMarkers(marker_list,false);
                                saas_locateAdapter = new Saas_LocateAdapter(context,list);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                locate_list.setLayoutManager(layoutManager);
                                locate_list.setAdapter(saas_locateAdapter);
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

    public void initData() {
        String imei = getSharedPreferences("sp", 0).getString("imei", "");
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().SAAS_DEVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("deviceList", imei)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询腕表基本信息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = json.getJSONArray("data");
                                json = jsonArray.getJSONObject(0);
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                        .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                markerOption.title("老人当前位置");
                                LatLng latLng = new LatLng(json.getDouble("lat"), json.getDouble("lon"));
                                markerOption.position(latLng);
                                aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                                aMap.addMarker(markerOption);
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
            case R.id.tv_time:
                pvTime.show();
                break;
            case R.id.tv_array:
                Collections.reverse(list);
                saas_locateAdapter.notifyDataSetChanged();

                break;
            case R.id.tv_show:
               RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rl_trail_list.getLayoutParams();
               if(isShow){
                   layoutParams.height = 729;
                   isShow = false;
                   tv_show.setText("展开");
               }else {
                   layoutParams.height = 1440;
                   isShow = true;
                   tv_show.setText("收起");
               }
               rl_trail_list.setLayoutParams(layoutParams);
                break;
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
                initList(tv_time.getText().toString());
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
                            initList(now);
                        } else {
                            tv_time.setText(date2);
                            initList(tv_time.getText().toString());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }



    private void initTime() {
        boolean[] type = {true, true, true, false, false, false};
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_time.setText(sdf.format(date));
                initList(tv_time.getText().toString());
            }
        }).setType(type).build();
    }
}