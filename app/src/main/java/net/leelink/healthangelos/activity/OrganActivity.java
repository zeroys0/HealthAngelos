package net.leelink.healthangelos.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.NearOrganBean;
import net.leelink.healthangelos.util.Logger;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.functions.Consumer;

public class OrganActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener, AMap.OnMyLocationChangeListener {
    RelativeLayout rl_back;
    private MapView map_view;
    private AMap aMap;
    Context context;
    List<NearOrganBean> list = new ArrayList<>();
    TextView tv_city,tv_detail;
    GeocodeSearch geocoderSearch;
    EditText ed_search;
    PopupWindow pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ);
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        context = this;
        requestPermissions();

    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_city = findViewById(R.id.tv_city);
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChooseCityActivity.class);
                startActivityForResult(intent,100);
            }
        });
        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(this);
        try {
            geocoderSearch = new GeocodeSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        geocoderSearch.setOnGeocodeSearchListener(this);
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                /**
                 * 点击弹出框 因为暂时无信息
                 */
//                showPopup();
                return false;
            }
        };
        //绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
        //搜索机构
        ed_search = findViewById(R.id.ed_search);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchList();
                }
                return false;
            }
        });

    }

    public void initData() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("mername", "天津市");
        OkGo.<String>get(Urls.getInstance().NEARORGAN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取附近机构", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                ArrayList<MarkerOptions> marker_list = new ArrayList<>();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<NearOrganBean>>() {
                                }.getType());
                                if (list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        String address;
                                        try {
                                            JSONObject json_address = new JSONObject(list.get(i).getOrganAddress());
                                            address = json_address.getString("fullAddress");
                                        } catch (Exception e){
                                            address = list.get(i).getOrganAddress();
                                        }
                                        MarkerOptions markerOption = new MarkerOptions();
                                        markerOption.title(list.get(i).getOrganName()).snippet(address);
                                        markerOption.draggable(true);//设置Marker可拖动
                                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                        markerOption.setFlat(true);//设置marker平贴地图效果
                                        double[] doubles = Utils.bd_decrypt(list.get(i).getLat(),list.get(i).getLng());
                                        LatLng latLng = new LatLng(doubles[0],doubles[1]);
                                        markerOption.position(latLng);
                                        aMap.addMarker(markerOption);
                                        marker_list.add(markerOption);
                                    }
                                }
                                aMap.addMarkers(marker_list,false);

                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    public void searchList() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("organName", ed_search.getText().toString().trim());
        OkGo.<String>get(Urls.getInstance().NEARORGAN)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取附近机构", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                ArrayList<MarkerOptions> marker_list = new ArrayList<>();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<NearOrganBean>>() {
                                }.getType());
                                if (list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        MarkerOptions markerOption = new MarkerOptions();
                                        markerOption.title(list.get(i).getOrganName()).snippet(list.get(i).getOrganAddress());
                                        markerOption.draggable(true);//设置Marker可拖动
                                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                .decodeResource(getResources(), R.drawable.img_organ_marker)));
                                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                        markerOption.setFlat(true);//设置marker平贴地图效果
                                        LatLng latLng = new LatLng(list.get(i).getLat(),list.get(i).getLng());
                                        markerOption.position(latLng);
                                        aMap.addMarker(markerOption);
                                        marker_list.add(markerOption);
                                    }
                                    double[] doubles = Utils.bd_decrypt(list.get(0).getLat(),list.get(0).getLng());
                                    LatLng la=new LatLng(doubles[0],doubles[1]);
                                    aMap.moveCamera(CameraUpdateFactory.newLatLng(la));
                                }
                                aMap.addMarkers(marker_list,false);
                                // 定义 Marker 点击事件监听


                            }  else if (json.getInt("status") == 505) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&& resultCode ==1 ) {
            String name = data.getStringExtra("data");
            tv_city.setText(name);
            GeocodeQuery query = new GeocodeQuery(name, name);
            geocoderSearch.getFromLocationNameAsyn(query);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        Log.e("onGeocodeSearched: ", regeocodeResult.getRegeocodeAddress().getCity());
        tv_city.setText(regeocodeResult.getRegeocodeAddress().getCity());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        double latitude=geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint().getLatitude();
        double longitude=geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint().getLongitude();
        Log.e("onGeocodeSearched: ", latitude+"/n"+longitude);
        LatLng la=new LatLng(latitude,longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(la));
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.e( "onMyLocationChange: ",location.getLatitude()+"   "+location.getLongitude() );
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.getLatitude(),location.getLongitude()), 50, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @SuppressLint("CheckResult")
    public boolean requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(OrganActivity.this);
        rxPermission.requestEach(
                Manifest.permission.ACCESS_FINE_LOCATION)   //获取位置
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(com.tbruyelle.rxpermissions2.Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.i("用户已经同意该权限", permission.name + " is granted.");
                            init();
                            initData();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Logger.i("用户拒绝了该权限,没有选中『不再询问』", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.i("用户拒绝了该权限,并且选中『不再询问』", permission.name + " is denied.");
                            Toast.makeText(context, "您已经拒绝该权限,并选择不再询问,请在权限管理中开启权限使用本功能", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        return  true;
    }

    @SuppressLint("WrongConstant")
    public void showPopup() {

        View popView = getLayoutInflater().inflate(R.layout.popup_organ, null);
        SharedPreferences sp = getSharedPreferences("sp", 0);
        String userList = sp.getString("user_name", "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(userList);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        tv_detail = popView.findViewById(R.id.tv_detail);
        tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OrganDetailActivity.class);
                startActivity(intent);
            }
        });
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new OrganActivity.poponDismissListener());

        pop.showAtLocation(ed_search, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
        }
    }
}
