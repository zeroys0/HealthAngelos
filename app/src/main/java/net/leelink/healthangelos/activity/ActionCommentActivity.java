package net.leelink.healthangelos.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.JoinAdapter;
import net.leelink.healthangelos.adapter.OnItemJoinClickListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Logger;
import net.leelink.healthangelos.util.RatingBar;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CustomLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Consumer;

import static net.leelink.healthangelos.activity.WriteDataActivity.IMAGE_ITEM_ADD;

public class ActionCommentActivity extends BaseActivity implements View.OnClickListener, OnItemJoinClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private TextView tv_data, tv_name, tv_address;
    private EditText ed_content;
    private RatingBar rb_degree;
    JoinAdapter joinAdapter;
    private List<ImageItem> list = new ArrayList<>();
    private RecyclerView recyclerview;
    private List<ImageItem> images = new ArrayList<>();
    static int index_rx = 0;
    public static final int maxImgCount = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_comment);
        context = this;
        createProgressBar(context);
        init();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_data = findViewById(R.id.tv_data);
        tv_data.setOnClickListener(this);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("orgName"));
        tv_address = findViewById(R.id.tv_address);
        tv_address.setText(getIntent().getStringExtra("address"));
        rb_degree = findViewById(R.id.rb_degree);
        recyclerview = findViewById(R.id.recyclerview);
        ed_content = findViewById(R.id.ed_content);
        ed_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ed_content.getText().toString().length() > 0) {
                    tv_data.setTextColor(getResources().getColor(R.color.blue));
                }
            }
        });
        joinAdapter = new JoinAdapter(list, this, this, 3,2);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(this, 3);
        recyclerview.setAdapter(joinAdapter);
        recyclerview.setLayoutManager(customLinearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_data:
                commit();
                break;
        }
    }

    public void commit() {
        if(ed_content.getText().toString().equals("")){
            Toast.makeText(context, "请上传评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        if(list.size()==0){
            Toast.makeText(context, "请上传评论图片" , Toast.LENGTH_SHORT).show();
            return;
        }
        HttpParams httpParams = new HttpParams();
        httpParams.put("activityId", getIntent().getStringExtra("activity_id"));
        httpParams.put("degree", rb_degree.getSelectedNumber());
        httpParams.put("reviewText", ed_content.getText().toString());
        for(ImageItem imageItem:list) {
            httpParams.put("pictureFiles",new File(imageItem.path));
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().ACTION_REVIEW)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("评价活动", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "评价完成", Toast.LENGTH_SHORT).show();
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
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == 100) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    list.addAll(images);
                    joinAdapter.setImages(list);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == 101) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    list.clear();
                    list.addAll(images);
                    joinAdapter.setImages(list);
                }
            }
        }
    }

    @Override
    public void onItemAdd(View view, int position) {
        requestPermissions(position);
    }

    @SuppressLint("CheckResult")
    private void requestPermissions(int position) {
        String[] permission;
        if (Build.VERSION.SDK_INT >= 34) {
            permission = new String[]{   Manifest.permission.READ_MEDIA_IMAGES,//读取外部存储器
                    Manifest.permission.CAMERA};//照相机}
        } else {
            permission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
                    Manifest.permission.READ_EXTERNAL_STORAGE,//读取外部存储器
                    Manifest.permission.CAMERA};//照相机}
        }
        RxPermissions rxPermission = new RxPermissions(ActionCommentActivity.this);
        rxPermission.requestEach(permission)//照相机
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.i("用户已经同意该权限", permission.name + " is granted.");

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Logger.i("用户拒绝了该权限,没有选中『不再询问』", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.i("用户拒绝了该权限,并且选中『不再询问』", permission.name + " is denied.");
                            Toast.makeText(context, "您已经拒绝该权限,请在权限管理中开启权限使用本功能", Toast.LENGTH_SHORT).show();
                        }
                        index_rx++;
                        if (index_rx == 3) {
                            switch (position) {
                                case IMAGE_ITEM_ADD:
                                    if (ContextCompat.checkSelfPermission(context,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ActionCommentActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                1);

                                    } else {
                                        ImagePicker.getInstance().setSelectLimit(maxImgCount - list.size());
                                        Intent intent = new Intent(context, ImageGridActivity.class);
                                        startActivityForResult(intent, 100);
                                    }
                                    break;
                                default:
                                    //打开预览
                                    Intent intentPreview = new Intent(context, ImagePreviewDelActivity.class);
                                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) joinAdapter.getImages());
                                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                                    startActivityForResult(intentPreview, 101);
                                    break;
                            }
                            index_rx = 0;
                        }
                    }
                });
    }
}
