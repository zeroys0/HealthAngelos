package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    private RelativeLayout rl_back;
    private TabLayout tabLayout;
    private TextView tv_state,tv_name,tv_content,tv_number,tv_price,tv_before_remark,tv_after_remark;
    private Context context;
    private ImageView img_sign1;
    private Banner before_banner,after_banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        init();
        context = this;
        createProgressBar(context);
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
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
//        tv_name = findViewById(R.id.tv_name);
//        tv_name.setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
        tv_number = findViewById(R.id.tv_number);
        tv_price = findViewById(R.id.tv_price);
        tv_before_remark = findViewById(R.id.tv_before_remark);
        tv_after_remark = findViewById(R.id.tv_after_remark);
        img_sign1 = findViewById(R.id.img_sign1);
        before_banner = findViewById(R.id.before_banner);
        after_banner = findViewById(R.id.after_banner);

    }

    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().CIVILL_ORDER_DETAIL +"/"+getIntent().getStringExtra("productId"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询改造项目详情", json.toString());
                            if (json.getInt("status") == 200) {
                                if (!json.has("data")) {
                                    return;
                                }
                                json = json.getJSONObject("data");
                                tv_content.setText(json.getString("productName"));
                                tv_number.setText(json.getString("num"));
                                tv_price.setText("￥"+json.getString("amount"));
                                tv_before_remark.setText(json.getString("beforeRemark"));
                                tv_after_remark.setText(json.getString("afterRemark"));
                                Glide.with(context).load(Urls.getInstance().IMG_URL+json.getString("signImg")).into(img_sign1);
                                //改造前图片
                                String before_images = json.getString("beforeImages");
                                String[] before = before_images.split(",");
                                List<String> b_images = new ArrayList<>();
                                for(int i =0;i<before.length;i++){
                                    b_images.add(Urls.getInstance().IMG_URL+before[i]);
                                }
                                before_banner.setImages(b_images);
                                before_banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide 加载图片简单用法
                                        Glide.with(context).load(path).into(imageView);
                                        
                                    }
                                });
                                before_banner.isAutoPlay(false);
                                before_banner.start();
                                //改造后图片
                                String after_images = json.getString("afterImages");
                                String[] after = after_images.split(",");
                                List<String> a_images = new ArrayList<>();
                                for(int i =0;i<before.length;i++){
                                    a_images.add(Urls.getInstance().IMG_URL+after[i]);
                                }
                                after_banner.setImages(a_images);
                                after_banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide 加载图片简单用法
                                        Glide.with(context).load(path).into(imageView);

                                    }
                                });
                                after_banner.isAutoPlay(false);
                                after_banner.start();
                            } else if (json.getInt("status") == 505) {

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
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_state:
                Intent intent = new Intent(context, NeoReformProgressActivity.class);
                startActivity(intent);
                break;
        }
    }
}
