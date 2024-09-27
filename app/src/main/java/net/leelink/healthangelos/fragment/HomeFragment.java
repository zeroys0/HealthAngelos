package net.leelink.healthangelos.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.BindEquipmentActivity;
import net.leelink.healthangelos.activity.HealthDataActivity;
import net.leelink.healthangelos.activity.HealthReportActivity;
import net.leelink.healthangelos.activity.LoginActivity;
import net.leelink.healthangelos.activity.NewsActivity;
import net.leelink.healthangelos.activity.WebActivity;
import net.leelink.healthangelos.activity.WriteDataActivity;
import net.leelink.healthangelos.activity.yasee.BindYaseeActivity;
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

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    private RelativeLayout rl_write_data, img_add, rl_news, rl_report;
    Context context;
    List<NewsBean> list = new ArrayList<>();

    private TextView tv_text;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkFontSize();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initBanner(view);
        initViewPager();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initHealthData();
        initRank();
        initNews();
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
        img_add = view.findViewById(R.id.img_add);
        rl_news = view.findViewById(R.id.rl_news);
        rl_report = view.findViewById(R.id.rl_report);
        OnClick();

    }

    public void initBanner(View view) {
        banner = view.findViewById(R.id.banner);
        Log.e( "initBanner: ", Urls.getInstance().HOMEBANNER);
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
                                banner.setOnBannerListener(HomeFragment.this);
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
                                SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("secretKey");
                                editor.remove("telephone");
                                editor.apply();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
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
//        if(position==0){
//            Log.d( "onPageSelected: ","第一页");
//            ViewGroup.LayoutParams params = view_pager.getLayoutParams();
//            params.height = R.dimen.y416;
//            view_pager.setLayoutParams(params);
//        }else if(position ==1){
//            Log.d( "onPageSelected: ","第二页");
//            ViewGroup.LayoutParams params = view_pager.getLayoutParams();
//            params.height = R.dimen.y624;
//            view_pager.setLayoutParams(params);
//        }
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
              //  Intent intent1 = new Intent(context, NeoHealthDataActivity.class);
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
        img_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doGetPermission();
            }
        });
        rl_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                startActivity(intent);
            }
        });
        rl_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HealthReportActivity.class);
                startActivity(intent);
            }
        });

    }

    public void initHealthData() {
        OkGo.<String>get(Urls.getInstance().HEALTHDATA)
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
                                if (!json.has("systolic") || !json.has("diastolic") || json.isNull("systolic") || json.isNull("diastolic")) {
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

                            } else if (json.getInt("status") == 505) {

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
        OkGo.<String>get(Urls.getInstance().RANK)
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


                            } else if (json.getInt("status") == 505) {

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
        OkGo.<String>get(Urls.getInstance().NEWS)
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
//                                final List<NewsBean> newsBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<NewsBean>>() {
//                                }.getType());
//                                list.addAll(newsBeans);
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<NewsBean>>() {
                                }.getType());
                                newsAdapter = new NewsAdapter(list,  getContext(), HomeFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                news_list.setLayoutManager(layoutManager);
                                news_list.setAdapter(newsAdapter);

                            } else if (json.getInt("status") == 505) {
                               reLogin(context);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Log.e("onActivityResult: ", result);
                    if (result.startsWith("https://wechat.yasee.com.cn/wxLogin/")) {
                        Intent intent = new Intent(getContext(), BindYaseeActivity.class);
                        String[] arr = result.split("sn=");
                        intent.putExtra("sn", arr[1]);
                        startActivity(intent);
                    }
                   else if (result.startsWith("http")) {
                        if(result.contains("activePage")) { //活动签到
                            String[] s = result.split("activePage/");

                            Log.e("onActivityResult: ", s[1]);
                            clockIn(s[1]);
                        } else {    //绑定设备
                            Intent intent = new Intent(getContext(), BindEquipmentActivity.class);
                            result = result.substring(23);
                            intent.putExtra("imei", result);
                            startActivity(intent);
                        }
                    }   else {
                        if (result.startsWith("{")) {
                            String s = "";
                            try {

                                JSONObject jsonObject = new JSONObject(result);
                                s = jsonObject.getString("activityId");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            clockIn(s);
                        } else {
                            Intent intent = new Intent(getContext(), BindEquipmentActivity.class);
                            intent.putExtra("imei", result);
                            startActivity(intent);
                        }
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onItemClick(View view) {
        int position = news_list.getChildLayoutPosition(view);
        Intent intent = new Intent(getContext(), WebActivity.class);
        intent.putExtra("url", list.get(position).getAddress());
        startActivity(intent);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    //活动打卡
    public void clockIn(String id) {
        OkGo.<String>post(Urls.getInstance().ACTION_QR + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("社区活动签到", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取权限 并扫描
    void doGetPermission() {
        String s = "";//读取外部存储器;
        if (Build.VERSION.SDK_INT >= 34) {
            s = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            s = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        // 申请权限。
        AndPermission.with(context)
                .permission(
                        Permission.CAMERA, s
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, Object data, RequestExecutor executor) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("扫描需要用户开启相机,是否同意开启相机权限");
                        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户同意去设置：
                                executor.execute();
                            }
                        });
                        //设置点击对话框外部区域不关闭对话框
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户不同意去设置：
                                executor.cancel();
                                Mytoast.show(context, "无法打开相机");

                            }
                        });
                        builder.show();
                    }

                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(Object data) {
                        try {
                            Intent intent = new Intent(context, CaptureActivity.class);
                            startActivityForResult(intent, 1);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(Object data) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, (List<String>) data)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。

                            final SettingService settingService = AndPermission.permissionSetting(context);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("相机权限已被禁止,用户将无法打开摄像头,无法进入扫描,请到\"设置\"开启");
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户同意去设置：
                                    settingService.execute();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户不同意去设置：
                                    settingService.cancel();
                                }
                            });
                            //设置点击对话框外部区域不关闭对话框
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }
                })
                .start();
    }

    public static boolean isGoodJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }

        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        } catch (JsonParseException e) {
            return false;
        }
    }


}
