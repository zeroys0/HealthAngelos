package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeSearch;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Utils;

public class MapActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private MapView map_view;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LatLng latLng = new LatLng(0,0);

            switch (getIntent().getIntExtra("type",0))
            {
                case 0:
                    double[] ll = Utils.bd_decrypt(getIntent().getDoubleExtra("lat",0),getIntent().getDoubleExtra("lon",0));
                    latLng = new LatLng(ll[0],ll[1]);
                    break;
                case 2:
                    LatLng g_latLng = new LatLng(getIntent().getDoubleExtra("lat",0),getIntent().getDoubleExtra("lon",0));
                    latLng = Utils.transformFromWGSToGCJ(g_latLng);
                    break;
        }

        aMap = map_view.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
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
    }
}
