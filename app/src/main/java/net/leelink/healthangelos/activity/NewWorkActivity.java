package net.leelink.healthangelos.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
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
import net.leelink.healthangelos.bean.WorkBean;
import net.leelink.healthangelos.util.Logger;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CustomLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
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

public class NewWorkActivity extends BaseActivity implements View.OnClickListener, OnItemJoinClickListener {
    private RelativeLayout rl_back;
    private Context context;
    JoinAdapter joinAdapter;
        private List<ImageItem> list = new ArrayList<>();
    private RecyclerView recyclerview;
    private PopupWindow popuPhoneW;
    private View popview;
    TextView tv_cancel, tv_confirm, tv_agreement,tv_data;
    private EditText ed_content;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init(){
        sp = getSharedPreferences("sp",0);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        recyclerview = findViewById(R.id.recyclerview);
        ed_content = findViewById(R.id.ed_content);
        tv_data = findViewById(R.id.tv_data);
        tv_data.setOnClickListener(this);
    }


    public void initData() {
        ed_content.setText(sp.getString("work_text",""));
        joinAdapter = new JoinAdapter(list, this, this, 9,2);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(this, 3);
        recyclerview.setAdapter(joinAdapter);
        recyclerview.setLayoutManager(customLinearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_data:
                upload();
//                comment();
                break;
        }
    }

    public void upload(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("activityId",getIntent().getStringExtra("activity_id"));
        httpParams.put("introduce",ed_content.getText().toString());
        for(ImageItem imageItem:list) {
            httpParams.put("pictureFiles",new File(imageItem.path));
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().PRODUCTION)
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
                            Log.d("上传活动作品", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "作品发表成功", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new WorkBean());
                                finish();
                            }else if(json.getInt("status") == 505){
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

    public void comment(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("productionId",20);
        httpParams.put("commentContent",ed_content.getText().toString());
    //    httpParams.put("replyId",20);

        showProgressBar();
        OkGo.<String>post(Urls.getInstance().REVIEW)
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
                            Log.d("作品评论", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "发表成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(json.getInt("status") == 505){
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

    private List<ImageItem> images = new ArrayList<>();
    static int index_rx = 0;
    public static final int maxImgCount = 9;

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

        RxPermissions rxPermission = new RxPermissions(NewWorkActivity.this);
        rxPermission.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
                Manifest.permission.READ_EXTERNAL_STORAGE,//读取外部存储器
                Manifest.permission.CAMERA)//照相机
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
                                        ActivityCompat.requestPermissions(NewWorkActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                1);

                                    } else {
//                    Intent intent = new Intent(this, ImageGridActivity.class);
//                    intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
//                    startActivityForResult(intent, 1);
                                        //打开选择,本次允许选择的数量
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            showpup();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("WrongConstant")
    public void showpup(){
        popview = LayoutInflater.from(NewWorkActivity.this).inflate(R.layout.popu_save, null);
        popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("work_text",ed_content.getText().toString());
                editor.commit();
                Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();
                popuPhoneW.dismiss();
                finish();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new NewWorkActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
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
