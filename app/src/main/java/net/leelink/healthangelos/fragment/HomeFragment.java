package net.leelink.healthangelos.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import net.leelink.healthangelos.activity.HealthDataActivity;
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.activity.WriteDataActivity;
import net.leelink.healthangelos.adapter.HomePagerAdapter;
import net.leelink.healthangelos.adapter.NewsAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.TopTenAdapter;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BannerBean;
import net.leelink.healthangelos.bean.NewsBean;
import net.leelink.healthangelos.bean.RankBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends BaseFragment implements ViewPager.OnPageChangeListener, OnBannerListener, OnOrderListener {
    private ViewPager view_pager;
    private List<Fragment> fragments;
    private RecyclerView top_ten;
    private Banner banner;
    private TopTenAdapter topTenAdapter;
    private RecyclerView news_list;

    List<String> banner_list = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private PopupWindow popuPhoneW;
    private View popview;
    private TextView btn_cancel, btn_confirm, tv_blood_pressure, tv_heartbeat, tv_temperature;
    private LinearLayout ll_health_data;
    private RelativeLayout rl_write_data;
    Context context;
    List<NewsBean> list = new ArrayList<>();

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initBanner(view);
        initViewPager();

        initRank();
        initNews();
//        category();
//        popu_head();
//        getLocation();
//
//        intentThatCalled = getActivity().getIntent();
//        voice2text = intentThatCalled.getStringExtra("v2txt");
//        initRefreshLayout(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initHealthData();
    }

    public void init(View view) {
        view_pager = view.findViewById(R.id.view_pager);
        top_ten = view.findViewById(R.id.top_ten);
        view_pager.setOffscreenPageLimit(1);
        news_list = view.findViewById(R.id.news_list);
        ll_health_data = view.findViewById(R.id.ll_health_data);

        rl_write_data = view.findViewById(R.id.rl_write_data);
        tv_blood_pressure = view.findViewById(R.id.tv_blood_pressure);
        tv_heartbeat = view.findViewById(R.id.tv_heartbeat);
        tv_temperature = view.findViewById(R.id.tv_temperature);

        OnClick();

    }

    public void initBanner(View view) {
        banner = view.findViewById(R.id.banner);
        OkGo.<String>get(Urls.HOMEBANNER)
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
                                    banner_list.add(Urls.IMG_URL + bannerBean.getImgPath());
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
                                banner.setOnBannerListener(HomeFragment.this);
                                banner.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
//                                        Intent intent = new Intent(getContext(), NewsActivity.class);
//                                        intent.putExtra("url", list.get(position).getImgPath());
//                                        startActivity(intent);
                                    }
                                });

                                banner.setImages(banner_list);
                                banner.isAutoPlay(true);
                                banner.setDelayTime(5000);
                                banner.start();
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new FirstLeadFragment());
        fragments.add(new SecondLeadFragment());
        view_pager.setAdapter(new HomePagerAdapter(getChildFragmentManager(), fragments));
        view_pager.setCurrentItem(0);
        view_pager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void OnBannerClick(int position) {

    }

    public void OnClick() {
        ll_health_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, HealthDataActivity.class);
                startActivity(intent1);
            }
        });
        rl_write_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteDataActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initHealthData() {
        OkGo.<String>get(Urls.HEALTHDATA)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("健康数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getString("systolic").equals("null") || json.getString("diastolic").equals("null")) {
                                    tv_blood_pressure.setText("-/-");
                                } else {
                                    tv_blood_pressure.setText(json.getString("systolic") + "/" + json.getString("diastolic"));
                                }
                                if (json.getString("temperature").equals("null")) {
                                    tv_temperature.setText("-");
                                } else {
                                    tv_temperature.setText(json.getString("temperature"));
                                }
                                if (json.getString("heartRate").equals("null")) {
                                    tv_heartbeat.setText("-");
                                } else {
                                    tv_heartbeat.setText(json.getString("heartRate"));
                                }

                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initRank() {
        OkGo.<String>get(Urls.RANK)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("老人排名", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                final List<RankBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<RankBean>>() {
                                }.getType());
                                topTenAdapter = new TopTenAdapter(list, getContext());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                top_ten.setLayoutManager(layoutManager);
                                top_ten.setAdapter(topTenAdapter);


                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initNews() {
        OkGo.<String>get(Urls.NEWS)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", 1)
                .params("pageSize", 3)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("养老咨询", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                final List<NewsBean> newsBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<NewsBean>>() {
                                }.getType());
                                list.addAll(newsBeans);
                                newsAdapter = new NewsAdapter(list, getContext(), HomeFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                news_list.setLayoutManager(layoutManager);
                                news_list.setAdapter(newsAdapter);

                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view) {
        int position = news_list.getChildLayoutPosition(view);
        Intent intent = new Intent(getContext(), WebActivity.class);
        intent.putExtra("url",list.get(position).getAddress());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }


}
