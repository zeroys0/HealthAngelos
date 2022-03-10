package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnItemClickListener;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.PictureAdapter;
import net.leelink.healthangelos.adapter.WorkAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.PhotoBean;
import net.leelink.healthangelos.bean.WorkBean;
import net.leelink.healthangelos.util.HtmlUtil;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ActionDetailActivity extends BaseActivity implements View.OnClickListener, OnOrderListener, OnItemClickListener {
    private RelativeLayout rl_back;
    private Context context;
    private RecyclerView picture_list, work_list;
    private PictureAdapter pictureAdapter;
    private WorkAdapter workAdapter;
    private FrameLayout fm_new;
    private TextView tv_time, tv_address, tv_main, tv_phone, tv_data, text_title,tv_content;
    private String id;
    private LinearLayout ll_1, ll_2;
    private List<PhotoBean> photo_list;
    private List<WorkBean> workBeanList =  new ArrayList<>();
    private TwinklingRefreshLayout refreshLayout;
    private int page = 1;
    private boolean hasNextPage;
    private String orgName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFontSize();
        setContentView(R.layout.activity_action_detail);
        context = this;
        createProgressBar(context);
        EventBus.getDefault().register(this);
        init();
        initData();
        initList();
        initWorkList();
        //initRefreshLayout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WorkBean workBean) {
        initWorkList();
    }


    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        picture_list = findViewById(R.id.picture_list);
        work_list = findViewById(R.id.work_list);
        work_list.setNestedScrollingEnabled(true);
        fm_new = findViewById(R.id.fm_new);
        fm_new.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_address = findViewById(R.id.tv_address);
        tv_main = findViewById(R.id.tv_main);
        tv_phone = findViewById(R.id.tv_phone);
        id = getIntent().getStringExtra("activity_id");
        text_title = findViewById(R.id.text_title);
        ll_2 = findViewById(R.id.ll_2);
        ll_1 = findViewById(R.id.ll_1);
        tv_content = findViewById(R.id.tv_content);

        tv_data = findViewById(R.id.tv_data);
        tv_data.setOnClickListener(this);
        int sign =  getIntent().getIntExtra("sign",0);

        if(sign == 1){
            ll_2.setVisibility(View.VISIBLE);
            work_list.setVisibility(View.VISIBLE);
            fm_new.setVisibility(View.VISIBLE);
        }

    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ACTION + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_time.setText(json.getString("startTime") + "至" + json.getString("endTime"));
                                JSONObject json_address = new JSONObject(json.getString("actAddress"));
                                tv_address.setText(json_address.getString("fullAddress"));
                                tv_main.setText(json.getString("orgName"));
                                tv_phone.setText(json.getString("orgTelephone"));
                                text_title.setText(json.getString("actName"));
                                String detail = json.getString("actContent");
                                tv_content.setText(HtmlUtil.delHTMLTag(detail));
                                int state = json.getInt("sign");
                                if (state == 1) {
                                    ll_2.setVisibility(View.VISIBLE);
                                    work_list.setVisibility(View.VISIBLE);
                                }
                                orgName = json.getString("orgName");

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

    public void initList() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().ACTION_PHOTO + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取活动图片", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("data");
                                photo_list = gson.fromJson(jsonArray.toString(), new TypeToken<List<PhotoBean>>() {
                                }.getType());
                                pictureAdapter = new PictureAdapter(ActionDetailActivity.this, photo_list, context);
                                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false);
                                picture_list.setAdapter(pictureAdapter);
                                picture_list.setLayoutManager(layoutManager);
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

    public void initWorkList() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().PRODUCTION)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("activityId", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取活动作品列表", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                json = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<WorkBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<WorkBean>>() {
                                }.getType());
                                workBeanList.addAll(list);
                                workAdapter = new WorkAdapter(workBeanList, context, ActionDetailActivity.this);
                                RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                work_list.setAdapter(workAdapter);
                                work_list.setLayoutManager(layoutManager);
                                SpacesItemDecoration decoration = new SpacesItemDecoration(16);
                                work_list.addItemDecoration(decoration);
                                if(page>1){
                                    work_list.smoothScrollToPosition(workBeanList.size()-1);
                                }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.fm_new:
                Intent intent = new Intent(context, NewWorkActivity.class);
                intent.putExtra("activity_id", id);
                startActivity(intent);
                break;
            case R.id.tv_data:
                Intent intent1 = new Intent(context, ActionCommentActivity.class);
                intent1.putExtra("activity_id",id);
                intent1.putExtra("name",text_title.getText().toString());
                intent1.putExtra("address",tv_address.getText().toString());
                intent1.putExtra("orgName",orgName);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        int position = picture_list.getChildLayoutPosition(view);

        LayoutInflater inflater = LayoutInflater.from(context);
        View imgEntryView = inflater.inflate(R.layout.dialog_photo, null);
// 加载自定义的布局文件
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
        Glide.with(this).load(Urls.getInstance().IMG_URL + "/img/" + photo_list.get(position).getPhotoName()).into(img);
        dialog.setView(imgEntryView); // 自定义dialog
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        imgEntryView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    @Override
    public void OnItemClick(View view) {
        int position = work_list.getChildLayoutPosition(view);
        Intent intent = new Intent(context, WorkDetailActivity.class);
        intent.putExtra("work_id", workBeanList.get(position).getId());
        startActivity(intent);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;


        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }

    public void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(this);
        headerView.setTextColor(0xff745D5C);
//        refreshLayout.setHeaderView((new ProgressLayout(getActivity())));
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setBottomView(new LoadingView(this));
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        workBeanList.clear();
                        page = 1;
                        initWorkList();

                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                        if (hasNextPage) {
                            page++;
                            initWorkList();
                        }
                    }
                }, 1000);
            }

        });
        // 是否允许开启越界回弹模式
        refreshLayout.setEnableOverScroll(false);
        //禁用掉加载更多效果，即上拉加载更多
        refreshLayout.setEnableLoadmore(true);
        // 是否允许越界时显示刷新控件
        refreshLayout.setOverScrollRefreshShow(true);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

