package net.leelink.healthangelos.activity.Fit;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htsmart.wristband2.WristbandApplication;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.adapter.FitDeviceAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.AndPermissionHelper;
import net.leelink.healthangelos.util.Utils2;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SearchFitWatchActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private Context context;
    private RelativeLayout rl_back;
    private Button btn_search;
    private FitDeviceAdapter fitDeviceAdapter;
    private RxBleClient mRxBleClient;
    private Disposable mScanDisposable;
    public static final String EXTRA_DEVICE = "device";
    private RecyclerView device_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fit_watch);
        mRxBleClient = WristbandApplication.getRxBleClient();
        context = this;
        createProgressBar(context);
        requestBlePermissions(this,1);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        fitDeviceAdapter = new FitDeviceAdapter(context,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_search:
                startScanning();
                showpop();
                showProgressBar();
                break;
        }
    }

    public void showpop(){
        View popView = getLayoutInflater().inflate(R.layout.popu_choose_fit, null);
        device_list = popView.findViewById(R.id.device_list);
        TextView tv_stop= popView.findViewById(R.id.tv_stop);
        tv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanning();
                stopProgressBar();
                Toast.makeText(context, "停止扫描", Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        device_list.setAdapter(fitDeviceAdapter);
        device_list.setLayoutManager(layoutManager);
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new SearchFitWatchActivity.poponDismissListener());


        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onItemClick(View view) {
        int position = device_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context,BindFitWatchActivity.class);
        ScanResult scanResult = (ScanResult) fitDeviceAdapter.getItem(position);
        BluetoothDevice device = scanResult.getBleDevice().getBluetoothDevice();
        intent.putExtra(EXTRA_DEVICE,device);
        startActivity(intent);
        stopScanning();
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }
    /**
     * Start scan
     */
    private void startScanning() {

        fitDeviceAdapter.clear();
        if (Utils2.checkLocationForBle(this)) {
            AndPermissionHelper.blePermissionRequest(this, new AndPermissionHelper.AndPermissionHelperListener1() {
                @Override
                public void onSuccess() {
                    ScanSettings scanSettings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build();

                   // mSwipeRefreshLayout.setRefreshing(true);
                    invalidateOptionsMenu();

                    mScanDisposable = mRxBleClient.scanBleDevices(scanSettings)
                            .subscribe(new Consumer<ScanResult>() {
                                @Override
                                public void accept(ScanResult scanResult) throws Exception {
                                    if(scanResult.getBleDevice().getName()!=null) {
                                        fitDeviceAdapter.add(scanResult);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    stopScanning();
                                }
                            });
                }
            });
        }
    }

    /**
     * Stop scan
     */
    private void stopScanning() {
        if (mScanDisposable != null)
            mScanDisposable.dispose();
    //    mSwipeRefreshLayout.setRefreshing(false);
        invalidateOptionsMenu();
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
            backgroundAlpha(1f);
            stopScanning();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

}