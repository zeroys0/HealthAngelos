package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.CommentListAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.BannerBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrganDetailActivity extends BaseActivity implements View.OnClickListener, OnBannerListener {
    private RelativeLayout rl_back;
    private Context context;
    private CommentListAdapter commentListAdapter;
    private RecyclerView comment_list;
    private Banner banner;
    List<String> banner_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_detail);
        context = this;
        init();
        initList();
        initBanner();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    public void initList(){
        comment_list = findViewById(R.id.comment_list);
        commentListAdapter = new CommentListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        comment_list.setAdapter(commentListAdapter);
        comment_list.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;

        }
    }


    public void initBanner( ) {
        banner = findViewById(R.id.banner);
        OkGo.<String>get(Urls.getInstance().HOMEBANNER)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("首页banner", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                final List<BannerBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<BannerBean>>() {
                                }.getType());
                                for (BannerBean bannerBean : list) {
                                    banner_list.add(Urls.getInstance().IMG_URL + bannerBean.getImgPath());
                                }

                                //banner.setBannerTitles(bannertitles);
                                banner.setBannerStyle(BannerConfig.NOT_INDICATOR);

                                banner.setIndicatorGravity(BannerConfig.RIGHT);
                                banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide 加载图片简单用法
                                        Glide.with(context).load(path).into(imageView);

                                    }
                                });
                                banner.setOnBannerListener(OrganDetailActivity.this);
                                banner.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        Intent intent = new Intent(context, WebActivity.class);
                                        try {
                                            intent.putExtra("url", jsonArray.getJSONObject(position).getString("imgRemark"));
                                            intent.putExtra("title", jsonArray.getJSONObject(position).getString("title"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                    }
                                });

                                banner.setImages(banner_list);
                                banner.isAutoPlay(true);
                                banner.setDelayTime(5000);
                                banner.start();
                            } else if (json.getInt("status") == 505) {
                                SharedPreferences sp = Objects.requireNonNull(context).getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("secretKey");
                                editor.remove("telephone");
                                editor.apply();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
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
    public void OnBannerClick(int position) {

    }
}
