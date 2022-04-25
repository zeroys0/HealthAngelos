package net.leelink.healthangelos.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.BitmapCompress;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.core.content.ContextCompat;

public class MyInfoActivty extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back;
    ImageView img_edit, img_head;
    Context context;
    private TextView tv_name, tv_sex, tv_age, tv_organ, tv_organize, tv_info_name, tv_info_sex, tv_nation, tv_phone, tv_card, tv_educate, tv_province, tv_city, tv_local, tv_address, tv_tall, tv_weight, tv_contact;
    private PopupWindow popuPhoneW;
    private View popview;
    private Button btn_album, btn_photograph;
    private File file;
    private int organId;
    private Bitmap bitmap = null;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_activty);
        context = this;
        createProgressBar(this);
        init();

        popu_head();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_edit = findViewById(R.id.img_edit);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditInfoActivity.class);
                intent.putExtra("organId", organId);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
        img_head = findViewById(R.id.img_head);
        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestPermissions()) {
                    popuPhoneW.showAtLocation(img_head, Gravity.CENTER, 0, 0);
                    backgroundAlpha(0.5f);
                }
            }
        });
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_age = findViewById(R.id.tv_age);
        tv_organ = findViewById(R.id.tv_organ);
        tv_organize = findViewById(R.id.tv_organize);
        tv_info_name = findViewById(R.id.tv_info_name);
        tv_info_sex = findViewById(R.id.tv_info_sex);
        tv_nation = findViewById(R.id.tv_nation);
        tv_phone = findViewById(R.id.tv_phone);
        tv_card = findViewById(R.id.tv_card);
        tv_educate = findViewById(R.id.tv_educate);
        tv_province = findViewById(R.id.tv_province);
        tv_city = findViewById(R.id.tv_city);
        tv_local = findViewById(R.id.tv_local);
        tv_address = findViewById(R.id.tv_address);
        tv_tall = findViewById(R.id.tv_tall);
        tv_weight = findViewById(R.id.tv_weight);
        tv_contact = findViewById(R.id.tv_contact);

    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (!json.isNull("headImgPath")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("headImgPath")).into(img_head);
                                }
                                tv_name.setText(json.getString("name"));
                                tv_info_name.setText(json.getString("name"));
                                if (json.has("sex")) {
                                    switch (json.getInt("sex")) {
                                        case 0:
                                            tv_sex.setText("男");
                                            tv_info_sex.setText("男");
                                            break;
                                        case 1:
                                            tv_sex.setText("女");
                                            tv_info_sex.setText("女");
                                            break;
                                    }
                                }
                                tv_age.setText(json.getInt("age") + "岁");
                                tv_organ.setText(json.getString("organName"));
                                tv_organize.setText(json.getString("organName"));
                                JSONObject jsonObject = json.getJSONObject("elderlyUserInfo");
                                tv_nation.setText(jsonObject.getString("nation"));
                                tv_card.setText(jsonObject.getString("idCard"));
                                tv_phone.setText(jsonObject.getString("telephone"));
                                String[] ed = new String[]{"小学", "初中", "高中", "技工学校", "中专/中技", "大专", "本科", "硕士", "博士", "其他"};
                                if (!jsonObject.getString("education").equals("null") && jsonObject.getInt("education") > 0 && jsonObject.getInt("education") < 10) {
                                    tv_educate.setText(ed[jsonObject.getInt("education")]);
                                }
                                organId = jsonObject.getInt("organId");
                                address = jsonObject.getString("address");
                                JSONObject j = new JSONObject(address);
                                tv_address.setText(j.getString("fullAddress"));
                                tv_province.setText(j.getString("province"));
                                tv_city.setText(j.getString("city"));
                                tv_local.setText(j.getString("county"));
                                tv_tall.setText(jsonObject.getString("height") + "cm");
                                tv_weight.setText(jsonObject.getString("weight") + "kg");
                                tv_contact.setText(jsonObject.getString("urgentPhone"));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    bitmap = BitmapCompress.decodeUriBitmap(MyInfoActivty.this, uri);
                    img_head.setImageBitmap(bitmap);
                    file = BitmapCompress.compressImage(bitmap,context);
                    break;
                case 2:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = (Bitmap) bundle.get("data");
                        img_head.setImageBitmap(bitmap);
                        file = BitmapCompress.compressImage(bitmap,context);
                    }
                    break;

                default:
                    break;
            }
            upload();
        }
    }

    //上传头像
    public void upload() {
        OkGo.<String>post(Urls.getInstance().UPLOADHEADIMAGE)
                .tag(this)
                .params("multipartFile", file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传头像", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "用户头像修改成功", Toast.LENGTH_SHORT).show();
                            } else {

                            }
                            Toast.makeText(MyInfoActivty.this, json.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取图片
    @SuppressLint("WrongConstant")
    private void popu_head() {
        // TODO Auto-generated method stub
        popview = LayoutInflater.from(MyInfoActivty.this).inflate(R.layout.popu_head, null);
        btn_album = (Button) popview.findViewById(R.id.btn_album);
        btn_photograph = (Button) popview.findViewById(R.id.btn_photograph);
        btn_album.setOnClickListener(this);
        btn_photograph.setOnClickListener(this);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new poponDismissListener());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photograph://拍照
                popuPhoneW.dismiss();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //当拒绝了授权后，为提升用户体验，可以以弹窗的方式引导用户到设置中去进行设置
                    new AlertDialog.Builder(this)
                            .setMessage("需要开启权限才能使用此功能")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //引导用户到设置中去进行设置
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(intent);

                                }
                            }).setNegativeButton("取消", null).create().show();
                } else {
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent2, 2);
                }
                break;
            case R.id.btn_album://相册
                popuPhoneW.dismiss();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, 1);
                break;
        }
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
        }
    }
}
