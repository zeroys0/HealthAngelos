package net.leelink.healthangelos.activity.yasee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class YaseeMainActivity extends BaseActivity {
    RelativeLayout rl_back, rl_bp, rl_hr, rl_bs, rl_acid;
    private TextView tv_unbind,tv_imei,tv_value,tv_hr_value,tv_bs_value,tv_acid_value,tv_time_bp,tv_time_hr,tv_time_bs,tv_time_acid;

    private Context context;
    private TwinklingRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yasee_main);

        context = this;
        createProgressBar(context);
        init();
        initRefreshLayout();
        initData();

    }
    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_bp = findViewById(R.id.rl_bp);
        rl_bp.setOnClickListener(this);
        rl_hr = findViewById(R.id.rl_hr);
        rl_hr.setOnClickListener(this);
        rl_bs = findViewById(R.id.rl_bs);
        rl_bs.setOnClickListener(this);
        rl_acid = findViewById(R.id.rl_acid);
        rl_acid.setOnClickListener(this);
        tv_value = findViewById(R.id.tv_value);
        tv_hr_value = findViewById(R.id.tv_hr_value);
        tv_bs_value = findViewById(R.id.tv_bs_value);
        tv_acid_value = findViewById(R.id.tv_acid_value);
        tv_time_bp = findViewById(R.id.tv_time_bp);
        tv_time_hr = findViewById(R.id.tv_time_hr);
        tv_time_bs = findViewById(R.id.tv_time_bs);
        tv_time_acid = findViewById(R.id.tv_time_acid);
        tv_imei = findViewById(R.id.tv_imei);
        tv_imei.setText(getIntent().getStringExtra("imei"));
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_bp:
                Intent intent = new Intent(context, YaseeBpActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_hr:
                Intent intent1 = new Intent(context, YaseeHrActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_bs:
                Intent intent2 = new Intent(context, YaseeBsActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_acid:
                Intent intent3 = new Intent(context, YaseeAcidActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_unbind:
                unbind();
                break;
            default:
                break;
        }
    }

    public void initData(){
        showProgressBar();

        OkGo.<String>get(Urls.getInstance().YASEE_LAST+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取最新数据", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_value.setText( json.getJSONObject("bloodPressure").getString("diastolic")+"/"+json.getJSONObject("bloodPressure").getString("systolic"));
                                tv_hr_value.setText(json.getJSONObject("bloodPressure").getString("heartRate"));
                                tv_bs_value.setText(json.getJSONObject("bloodSugar").getString("result"));
                                tv_acid_value.setText(json.getJSONObject("bloodUric").getString("result"));
                                tv_time_bp.setText("最近一次测量时间: "+ json.getJSONObject("bloodPressure").getString("createTime"));
                                tv_time_hr.setText("最近一次测量时间: "+ json.getJSONObject("bloodPressure").getString("createTime"));
                                tv_time_bs.setText("最近一次测量时间: "+ json.getJSONObject("bloodSugar").getString("createTime"));
                                tv_time_acid.setText("最近一次测量时间: "+json.getJSONObject("bloodUric").getString("createTime"));

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

    public void unbind(){
        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().YASEE_UNBIND+"/"+getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("解除绑定", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "解绑成功", Toast.LENGTH_SHORT).show();
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
                    }
                });
    }

    public void initRefreshLayout() {
        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(context);
        headerView.setTextColor(0xff745D5C);
//        refreshLayout.setHeaderView((new ProgressLayout(getActivity())));
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setBottomView(new LoadingView(context));
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        initData();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();

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
}