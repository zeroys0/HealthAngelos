package net.leelink.healthangelos.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.JoinAdapter;
import net.leelink.healthangelos.adapter.OnItemJoinClickListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CustomLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestActivity extends BaseActivity implements OnItemJoinClickListener {
    RelativeLayout rl_back;
    private RecyclerView recyclerview;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int maxImgCount = 4;
    private List<ImageItem> images = new ArrayList<>();
    private List<ImageItem> list = new ArrayList<>();
    JoinAdapter joinAdapter;
    Context context;
    private Button btn_next;
    private EditText ed_suggest,ed_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        init();
        createProgressBar(this);
        context = this;
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        ed_suggest = findViewById(R.id.ed_suggest);
        ed_phone = findViewById(R.id.ed_phone);
    }

    public void initData() {
        joinAdapter = new JoinAdapter(list, this, this, 4);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(this, 4);
        recyclerview.setAdapter(joinAdapter);
        recyclerview.setLayoutManager(customLinearLayoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        switch (position) {
            case IMAGE_ITEM_ADD:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SuggestActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                } else {
//                    Intent intent = new Intent(this, ImageGridActivity.class);
//                    intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
//                    startActivityForResult(intent, 1);
                    //打开选择,本次允许选择的数量
                    ImagePicker.getInstance().setSelectLimit(maxImgCount - list.size());
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) joinAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, 101);
                break;
        }
    }

    public void submit(){
        if(ed_phone.getText().toString().equals("")) {
            Toast.makeText(context, "请您填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content",ed_suggest.getText().toString());
            if(list.size()>0){
              jsonObject.put("file1",new File(list.get(0).path));
            }
            if(list.size()>1){
                jsonObject.put("file2",new File(list.get(1).path));
            }
            if(list.size()>2){
                jsonObject.put("file3",new File(list.get(2).path));
            }
            if(list.size()>3){
                jsonObject.put("file4",new File(list.get(3).path));
            }
            jsonObject.put("telephone",ed_phone.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().ADVICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("意见反馈", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "反馈成功!", Toast.LENGTH_LONG).show();
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }
}
