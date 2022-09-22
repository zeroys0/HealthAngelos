package net.leelink.healthangelos.activity.BioRadar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;
import net.leelink.healthangelos.view.MapContainer;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.widget.NestedScrollView;

public class BindBioRadarActivity extends BaseActivity implements View.OnClickListener , AMap.OnCameraChangeListener {
    private Context context;
    private RelativeLayout rl_back;
    private EditText ed_imei,ed_age,ed_contact_name,ed_phone,ed_home_address;
    private RadioButton rb_a,rb_b;
    private MapView map_view;
    private AMap aMap;
    private NestedScrollView scroll_view;
    private MapContainer mapContainer;
    private Button btn_bind;
    private JSONObject json_address = new JSONObject();
    private ImageView img_head;
    private double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bio_radar);
        context = this;
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        scroll_view = findViewById(R.id.scroll_view);
        img_head = findViewById(R.id.img_head);
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("path")).into(img_head);
        mapContainer = findViewById(R.id.container);
        mapContainer.setScrollView(scroll_view);
        ed_imei = findViewById(R.id.ed_imei);
        rb_a = findViewById(R.id.rb_a);
        rb_b = findViewById(R.id.rb_b);
        ed_age = findViewById(R.id.ed_age);
        ed_contact_name = findViewById(R.id.ed_contact_name);
        ed_phone = findViewById(R.id.ed_phone);
        ed_home_address = findViewById(R.id.ed_home_address);
        ed_home_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    getAddress();
                }
                return false;
            }
        });
        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setOnCameraChangeListener(this);
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);

        btn_bind = findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_bind:
                bind();
                break;
        }
    }

    //绑定雷达设备
    public void bind(){
        int sex;
        if(rb_a.isChecked()){
            sex = 0;
        }else {
            sex = 1;
        }
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().RADAR_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                //2833346
                .params("imei",ed_imei.getText().toString().trim())
                .params("address",json_address.toString())
                .params("age",ed_age.getText().toString())
                .params("contactName",ed_contact_name.getText().toString())
                .params("contactPhone",ed_phone.getText().toString())
                .params("latitude",lat)
                .params("longitude",lon)
                .params("sex",sex)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定雷达设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "绑定成功", Toast.LENGTH_SHORT).show();
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

    public void getAddress(){
        LoadDialog.start(context);
        OkGo.<String>get(Urls.getInstance().PARSEADDRESS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("address", ed_home_address.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据字典查找地址", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(!json.isNull("lat") &&!json.isNull("lng")  ) {
                                    double[] latlng = Utils.bd_decrypt(json.getDouble("lat"), json.getDouble("lng"));
                                    LatLng latLng = new LatLng(latlng[0],latlng[1]);
                                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));

                                    json_address.put("province", json.getString("province"));
                                    json_address.put("city", json.getString("city"));
                                    json_address.put("countyId",  json.getString("countyId"));
                                    json_address.put("county",  json.getString("county"));
                                    json_address.put("townId",  json.getString("townId"));
                                    json_address.put("cityId",  json.getString("cityId"));
                                    json_address.put("provinceId",  json.getString("provinceId"));
                                    json_address.put("town", json.getString("town"));
                                    json_address.put("address",  json.getString("address"));
                                    json_address.put("fullAddress", json.getString("fullAddress"));

                                    lat = json.getDouble("lat");
                                    lon = json.getDouble("lon");

                                } else {
                                    Toast.makeText(context, "请输入详细地址", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng target = cameraPosition.target;
        lat = target.latitude;
        lon = target.longitude;
//        System.out.println(target.latitude + "jinjin------" + target.longitude);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }
}