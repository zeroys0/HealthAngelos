package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ui.FaceDetectActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.views.LoadDialog;

import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.DefaultDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.RequiresApi;

public class FaceDetectExpActivity extends FaceDetectActivity {
    String image;
    private DefaultDialog mDefaultDialog;
    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDetectCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        super.onDetectCompletion(status, message, base64ImageMap);
        if (status == FaceStatusEnum.OK && mIsCompletion) {
            showMessageDialog("人脸图像采集", "采集成功");
            for(Map.Entry<String,String> entry:base64ImageMap.entrySet()) {
                image = entry.getValue();

            }

        } else if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {
            showMessageDialog("人脸图像采集", "采集超时");
        }
    }

    private void showMessageDialog(String title, String message) {
        if (mDefaultDialog == null) {
            DefaultDialog.Builder builder = new DefaultDialog.Builder(this);
            builder.setTitle(title).
                    setMessage(message).
                    setNegativeButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDefaultDialog.dismiss();

                                    if(Objects.equals(getIntent().getStringExtra("type"), "normal")) {

                                        vertify();

                                    }else if(Objects.equals(getIntent().getStringExtra("type"), "housekeep")){



                                    }
                                    //   Toast.makeText(FaceDetectExpActivity.this, "完成识别", Toast.LENGTH_SHORT).show();

                                }
                            });
            mDefaultDialog = builder.create();
            mDefaultDialog.setCancelable(true);
        }
        mDefaultDialog.dismiss();
        mDefaultDialog.show();
    }

    public void vertify(){

        Log.e("name: ", getIntent().getStringExtra("name"));
        Log.e("idCard: ", getIntent().getStringExtra("idNumber"));
        File file = base64ToFile(image,context);
        File front = new File(Objects.requireNonNull(getIntent().getStringExtra("front_path")));
        File back = new File(Objects.requireNonNull(getIntent().getStringExtra("back_path")));
        LoadDialog.start(context);
        OkGo.<String>post(Urls.getInstance().VERTIFY)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("file", file)
                .params("idcardzm",front)
                .params("idcardfm",back)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadDialog.stop();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("实名认证", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(FaceDetectExpActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK,intent);
                                finish();
                            } else {
                                Toast.makeText(FaceDetectExpActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                finish();
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

    public static File base64ToFile(String base64,Context context) {
        File file = null;
        String fileName = "/testFile.amr";
        FileOutputStream out = null;
        try {
            // 解码，然后将字节转换为文件
            file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            if (Build.VERSION.SDK_INT >= 29) {
                //Android10之后
                file = new File(context.getExternalFilesDir(null), fileName); //获取应用所在根目录/Android/data/your.app.name/file/ 也可以根据沙盒机制传入自己想传的参数，存放在指定目录
            } else {
                file = new File(Environment.getExternalStorageDirectory(), fileName);// 获取SD卡根目录
            }
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }


    @Override
    public void finish() {
        super.finish();
    }
}
