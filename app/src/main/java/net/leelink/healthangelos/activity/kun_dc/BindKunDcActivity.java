package net.leelink.healthangelos.activity.kun_dc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;
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

public class BindKunDcActivity extends BaseActivity {
    RelativeLayout rl_back;
    private ImageView img_head,img_code;
    private EditText ed_code;

    private Context context;

    private Button btn_bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_kun_dc);

        context = this;
        init();
        createProgressBar(context);
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(v -> finish());
        img_head = findViewById(R.id.img_head);
        ed_code = findViewById(R.id.ed_code);
        img_code = findViewById(R.id.img_code);
        img_code.setOnClickListener(v -> doGetPermission());
        btn_bind = findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(v -> bind());

        if(getIntent().getStringExtra("sn")!=null){
            ed_code.setText(getIntent().getStringExtra("sn"));
        }

        if(getIntent().getStringExtra("path")!=null){
            Glide.with(context).load(Urls.getInstance().IMG_URL+"/"+getIntent().getStringExtra("path")).into(img_head);
        }
    }
    public void bind(){
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().ZEN_BIND)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("elderlyId",MyApplication.userInfo.getOlderlyId())
                .params("imei",ed_code.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("绑定坤睡眠检测仪", json.toString());
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
                    try {
                        Log.e( "onActivityResult: ", result);
                        ed_code.setText(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void doGetPermission() {
        String s = "";//读取外部存储器;
        if (Build.VERSION.SDK_INT >= 34) {
            s = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            s = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        // 申请权限。
        AndPermission.with(context)
                .permission(
                        Permission.CAMERA, s
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, Object data, RequestExecutor executor) {
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
                    public void onAction(Object data) {
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
                    public void onAction(Object data) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, (List<String>) data)) {
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