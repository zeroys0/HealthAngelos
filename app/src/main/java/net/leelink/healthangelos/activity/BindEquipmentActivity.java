package net.leelink.healthangelos.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.Nullable;

public class BindEquipmentActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    EditText ed_code;
    ImageView img_code;
    Context context;
    Button btn_bind;
    private int deviceId,modelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_equipment);
        context = this;
        init();
        initData();
    }

    public void init(){
        rl_back  = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_code = findViewById(R.id.ed_code);
        ed_code.setText(getIntent().getStringExtra("imei"));
        img_code = findViewById(R.id.img_code);
        img_code.setOnClickListener(this);
        btn_bind = findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);
    }

    public void initData(){
        deviceId = getIntent().getIntExtra("deviceId",0);
        modelId = getIntent().getIntExtra("modelId",0);
        if(getIntent().getStringExtra("imei")!=null) {
            getId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_code:
                doGetPermission();
                break;
            case R.id.btn_bind:
                bind();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Log.e( "onActivityResult: ", result);
                    if(result.startsWith("http")) {
                        result = result.substring(23);
                        ed_code.setText(result);
                    }
//                    else {
//                        String s = "";
//                        try {
//                            JSONObject jsonObject = new JSONObject(result);
//                            s = jsonObject.getString("activityId");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void bind(){
        JSONObject json = new JSONObject();
        try {
            json.put("deviceId",deviceId);
            json.put("modelId",modelId);
            json.put("imei",ed_code.getText().toString().trim());
            json.put("isBluetooth",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定设备", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
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

    public void getId(){
        OkGo.<String>get(Urls.getInstance().IMEI_BIND+"/"+ed_code.getText().toString().trim())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取设备信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                deviceId = json.getInt("deviceId");
                                modelId = json.getInt("modelId");
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

    //获取权限 并扫描
    void doGetPermission() {
        AndPermission.with(context)
                .permission(
                        Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(final Context context, List<String> permissions, final RequestExecutor executor) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("扫描需要用户开启相机,是否同意开启相机权限");
                        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户同意去设置：
                                executor.execute();
                            }
                        });
                        //设置点击对话框外部区域不关闭对话框
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户不同意去设置：
                                executor.cancel();
                                Mytoast.show(context, "无法打开相机");

                            }
                        });
                        builder.show();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        try {
                            Intent intent = new Intent(context, CaptureActivity.class);
                            startActivityForResult(intent, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。

                            final SettingService settingService = AndPermission.permissionSetting(context);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("相机权限已被禁止,用户将无法打开摄像头,无法进入扫描,请到\"设置\"开启");
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户同意去设置：
                                    settingService.execute();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户不同意去设置：
                                    settingService.cancel();
                                }
                            });
                            //设置点击对话框外部区域不关闭对话框
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }

                })
                .start();
    }


}
