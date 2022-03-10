package net.leelink.healthangelos.activity.ssk;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

import androidx.recyclerview.widget.RecyclerView;

public class BindSSKActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView device_list;
    private Button btn_search;
    private static final String ApiKey = "1b482efa715e448e9b659c69e548e046";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_ssk);
        init();

        try {
//            bool_BLE = ProtocolBluetoothActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
//            //启用调试模式，显示Toast，控制台输出日志，日志未输出到文件
//            ProtocolBluetooth.enableDebugModel();
//            //初始化接口
//            ProtocolBluetooth.init(this, this, ApiKey, bool_BLE);
//            ProtocolBluetooth.enableDebugModel(LogPath_Absolute);//启用调试模式，显示Toast，控制台输出日志，同时输出到指定文件
//            ProtocolBluetooth.disableLog();//禁用日志输出
//            ProtocolBluetooth.disableToast();//禁用Toast显示
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        device_list = findViewById(R.id.device_list);
        btn_search = findViewById(R.id.btn_search);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_search:

                break;
        }
    }
}
