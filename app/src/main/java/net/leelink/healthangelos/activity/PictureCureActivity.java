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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;
import net.leelink.healthangelos.view.CustomLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PictureCureActivity extends BaseActivity implements OnItemJoinClickListener, View.OnClickListener {
private RecyclerView recyclerview;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int maxImgCount = 4;
    private List<ImageItem> images = new ArrayList<>();
    private List<ImageItem> list = new ArrayList<>();
    private RelativeLayout rl_back;
    Context context;
    JoinAdapter joinAdapter;
    Button btn_confirm,btn_1,btn_2,btn_3,btn_4;
    TextView tv_name,tv_duties,tv_department,tv_hospital;
    CircleImageView img_head;
    private LinearLayout ll_1,ll_2,ll_3,ll_4;
    private EditText ed_content,ed_1,ed_2,ed_3,ed_4;
    private String imgFirstPath,imgSecondPath,imgThirdPath,imgForthPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_cure);
        context = this;
        init();
        createProgressBar(this);
        initData();
    }

    public void init(){
        recyclerview = findViewById(R.id.recyclerview);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        img_head = findViewById(R.id.img_head);
        Glide.with(context).load(Urls.getInstance().IMG_URL+getIntent().getStringExtra("img_head")).into(img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_duties = findViewById(R.id.tv_duties);
        tv_duties.setText(getIntent().getStringExtra("dutis"));
        tv_department = findViewById(R.id.tv_department);
        tv_department.setText(getIntent().getStringExtra("department"));
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_hospital.setText(getIntent().getStringExtra("hospital"));
        btn_1 = findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2 = findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3 = findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_4 = findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        ll_1 = findViewById(R.id.ll_1);
        ll_2 = findViewById(R.id.ll_2);
        ll_3 = findViewById(R.id.ll_3);
        ll_4 = findViewById(R.id.ll_4);
        ed_content = findViewById(R.id.ed_content);
        ed_1 = findViewById(R.id.ed_1);
        ed_2 = findViewById(R.id.ed_2);
        ed_3 = findViewById(R.id.ed_3);
        ed_4 = findViewById(R.id.ed_4);
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
                    ActivityCompat.requestPermissions(PictureCureActivity.this,
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                confirmOrder();

                break;
            case R.id.btn_1:
                ll_1.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_2:
                ll_2.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_3:
                ll_3.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_4:
                ll_4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public  void confirmOrder(){
        showProgressBar();
        if(list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                int finalI = i;
                OkGo.<String>post(Urls.getInstance().PHOTO)
                        .tag(this)
                        .params("multipartFile", new File(list.get(i).path))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {

                                try {
                                    String body = response.body();
                                    JSONObject json = new JSONObject(body);
                                    Log.d("获取地址 ", json.toString());
                                    if (json.getInt("status") == 200) {
                                        if (finalI == 0) {
                                            imgFirstPath = json.getString("data");
                                        }
                                        if (finalI == 1) {
                                            imgSecondPath = json.getString("data");
                                        }
                                        if (finalI == 2) {
                                            imgThirdPath = json.getString("data");
                                        }
                                        if (finalI == 3) {
                                            imgForthPath = json.getString("data");
                                        }
                                        if (finalI == list.size() - 1) {
                                            stopProgressBar();
                                            Intent intent = new Intent(context, DoctorPrepayActivity.class);
                                            intent.putExtra("price", getIntent().getStringExtra("price"));
                                            if (list.size() > 0) {
                                                intent.putExtra("imgFirstPath", imgFirstPath);
                                            }
                                            if (list.size() > 1) {
                                                intent.putExtra("imgSecondPath", imgSecondPath);
                                            }
                                            if (list.size() > 2) {
                                                intent.putExtra("imgThirdPath", imgThirdPath);
                                            }
                                            if (list.size() > 3) {
                                                intent.putExtra("imgForthPath", imgForthPath);
                                            }
                                            intent.putExtra("size", list.size());
                                            intent.putExtra("doctorId", getIntent().getStringExtra("doctorId"));
                                            String content = getContent();
                                            intent.putExtra("remark", content);
                                            intent.putExtra("img_head",getIntent().getStringExtra("img_head"));
                                            intent.putExtra("name",getIntent().getStringExtra("name"));
                                            intent.putExtra("department",getIntent().getStringExtra("department"));
                                            intent.putExtra("dutis",getIntent().getStringExtra("dutis"));
                                            intent.putExtra("hospital",getIntent().getStringExtra("hospital"));
                                            startActivity(intent);
                                            finish();
                                        }
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
                                Toast.makeText(context, "网络不给力呀", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }else {
            Intent intent = new Intent(context, DoctorPrepayActivity.class);
            intent.putExtra("size", list.size());
            intent.putExtra("doctorId", getIntent().getStringExtra("doctorId"));
            String content = getContent();
            intent.putExtra("remark", content);
            intent.putExtra("img_head",getIntent().getStringExtra("img_head"));
            intent.putExtra("name",getIntent().getStringExtra("name"));
            intent.putExtra("department",getIntent().getStringExtra("department"));
            intent.putExtra("dutis",getIntent().getStringExtra("dutis"));
            intent.putExtra("price", getIntent().getStringExtra("price"));
            intent.putExtra("hospital",getIntent().getStringExtra("hospital"));
            startActivity(intent);
        }


    }

    public String getContent(){
        StringBuilder sb  = new StringBuilder();
        sb.append(ed_content.getText().toString());
        if(ll_1.getVisibility()==View.VISIBLE){
            sb.append("症状描述:"+ed_1.getText().toString().trim());
        }
        if(ll_2.getVisibility()==View.VISIBLE){
            sb.append("患病时长:"+ed_2.getText().toString().trim());
        }
        if(ll_3.getVisibility()==View.VISIBLE){
            sb.append("医院检查:"+ed_3.getText().toString().trim());
        }
        if(ll_4.getVisibility()==View.VISIBLE){
            sb.append("用药情况:"+ed_4.getText().toString().trim());
        }
        return  sb.toString();
    }

}
