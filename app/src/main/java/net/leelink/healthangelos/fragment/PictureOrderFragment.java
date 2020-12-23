package net.leelink.healthangelos.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

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
import net.leelink.healthangelos.activity.CommentActivity;
import net.leelink.healthangelos.activity.PayFunctionActivity;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.OrderAdapter;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.OrderBean;
import net.leelink.healthangelos.im.ChatActivity;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PictureOrderFragment extends BaseFragment implements OnOrderListener {
    Context context;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    List<OrderBean> list = new ArrayList<>();
    OrderAdapter orderAdapter;
    private RecyclerView order_list;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_order, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initRefreshLayout(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initList();
    }

    public void init(View view) {
        order_list = view.findViewById(R.id.order_list);
    }

    public void initList() {
        OkGo.<String>get(Urls.getInstance().HEALTH_ORDER)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", page)
                .params("pageSize", 10)
                .params("type", 2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("订单列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                hasNextPage = json.getBoolean("hasNextPage");
                                Gson gson = new Gson();
                                List<OrderBean> orderBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<OrderBean>>() {
                                }.getType());
                                list.addAll(orderBeans);
                                orderAdapter = new OrderAdapter(list, context, PictureOrderFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                order_list.setLayoutManager(layoutManager);
                                order_list.setAdapter(orderAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initRefreshLayout(View view) {
        refreshLayout = view.findViewById(R.id.refreshLayout);
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
                        list.clear();
                        page = 1;
                        initList();

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
                            initList();
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
    public void onItemClick(View view) {
        int position = order_list.getChildLayoutPosition(view);
        switch (list.get(position).getState()) {
            case 1:
                Intent intent = new Intent(context, PayFunctionActivity.class);
                intent.putExtra("orderId", list.get(position).getOrderId());
                intent.putExtra("actPayPrice", list.get(position).getActPayPrice() + "");
                startActivity(intent);
                break;
            case 2:     //待医生接诊
                break;
            case 3:     //进入聊天
                Intent intent1 = new Intent(context, ChatActivity.class);
                intent1.putExtra("clientId", list.get(position).getClientId());
                intent1.putExtra("receive_head",list.get(position).getImgPath());
                intent1.putExtra("name",list.get(position).getName());
                startActivity(intent1);
                break;
            case 4:     //去评价
                Intent intent2 = new Intent(context, CommentActivity.class);
                intent2.putExtra("orderBean",list.get(position));
                startActivity(intent2);
                break;
            case 5:
                break;

        }
    }

    @Override
    public void onButtonClick(View view, int position) {
        OkGo.<String>post(Urls.getInstance().SUCCESSORDER + "/" + list.get(position).getOrderId())
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("订单列表", json.toString());
                            if (json.getInt("status") == 200) {
                                list.clear();
                                initList();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
