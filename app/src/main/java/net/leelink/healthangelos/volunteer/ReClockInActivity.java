package net.leelink.healthangelos.volunteer;

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

public class ReClockInActivity extends BaseActivity implements OnItemJoinClickListener {

    public static final int IMAGE_ITEM_ADD = -1;
    private List<ImageItem> list = new ArrayList<>();
    RecyclerView recyclerview;
    Context context;
    JoinAdapter joinAdapter;
    RelativeLayout rl_back;
    Button btn_complete;
    public static final int maxImgCount = 3;
    private List<ImageItem> images = new ArrayList<>();
    String img1Path ="";
    String img2Path ="";
    String img3Path ="";
    private EditText ed_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_clock_in);
        context = this;
        createProgressBar(context);
        init();
        initData();
    }

    public void init(){
        recyclerview = findViewById(R.id.recyclerview);
        ed_content =  findViewById(R.id.ed_content);

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_complete = findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()==0) {
                    Toast.makeText(context, "请上传至少一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                getPath(0);
            }
        });
    }

    public void initData() {
        joinAdapter = new JoinAdapter(list, this, this, 3);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(this, 3);
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
                    ActivityCompat.requestPermissions(ReClockInActivity.this,
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




    public void complete(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstAuditPh1",img1Path);
            jsonObject.put("firstAuditPh2",img2Path);
            jsonObject.put("firstAuditPh3",img3Path);
            jsonObject.put("firstAuditRemark",ed_content.getText().toString().trim());
            jsonObject.put("sendId",getIntent().getStringExtra("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "";
        if(getIntent().getIntExtra("type",1)==1) {
            url = Urls.getInstance().VOL_TASKRECHECK;
        } else {
            url = Urls.getInstance().TEAM_TASKRECHECK;
        }
        Log.e( "complete: ",jsonObject.toString() );
        showProgressBar();
        OkGo.<String>post(url)
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
                            Log.d("提交第二次审核", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "重新提交成功,请等待二次审核", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
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

    public void getPath(int position){
        if(position>=list.size()) {
            complete();
            return;
        }
        File file = new File(list.get(position).path);
        final String[] s = {""};
        OkGo.<String>post(Urls.getInstance().PHOTO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("multipartFile",file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取图片地址", json.toString());
                            if (json.getInt("status") == 200) {
                                switch (position){
                                    case 0:
                                        img1Path = json.getString("data");
                                        break;
                                    case 1:
                                        img2Path = json.getString("data");
                                        break;
                                    case 2:
                                        img3Path = json.getString("data");
                                        break;
                                }
                                if(position<list.size()){       //如果小于图片总数 则递归获取地址
                                    int p = position +1;
                                    getPath(p);
                                }
                            } else if(json.getInt("status") == 505){
                                reLogin(context);
                            }else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return;
    }
}
