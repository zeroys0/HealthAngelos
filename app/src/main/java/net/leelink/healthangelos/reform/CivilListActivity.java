package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.CivilBean;
import net.leelink.healthangelos.reform.adapter.CivilAdapter;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CivilListActivity extends BaseActivity implements OnOrderListener {
    private RecyclerView civil_list;
    private RelativeLayout rl_back;
    private CivilAdapter civilAdapter;
    private Context context;
    private int page = 1;
    private boolean hasNextPage;
    private TwinklingRefreshLayout refreshLayout;
    List<CivilBean> list = new ArrayList<>();
    private EditText ed_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civil_list);
        init();
        context = this;
        initList();
        initRefreshLayout();
    }

    public void init(){
        civil_list = findViewById(R.id.civil_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ed_key = findViewById(R.id.ed_key);
        ed_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    list.clear();
                    page = 1;
                    initList();
                }
                return false;
            }
        });
    }

    public  void initList(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("content",ed_key.getText().toString());
        httpParams.put("pageNum",page);
        httpParams.put("pageSize",10);

        OkGo.<String>get(Urls.getInstance().COMMITTEE_LIST)
                .tag(this)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询民政单位", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson= new Gson();
                                json  = json.getJSONObject("data");
                                hasNextPage = json.getBoolean("hasNextPage");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<CivilBean> beans = gson.fromJson(jsonArray.toString(),new TypeToken<List<CivilBean>>(){}.getType());
                                list.addAll(beans);
                                civilAdapter = new CivilAdapter(list,context,CivilListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                civil_list.setAdapter(civilAdapter);
                                civil_list.setLayoutManager(layoutManager);
                                if(page>1){
                                    civil_list.scrollToPosition(civilAdapter.getItemCount()-1);
                                }
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    public void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
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
        int position  = civil_list.getChildLayoutPosition(view);
        Intent intent = new Intent();
        intent.putExtra("name",list.get(position).getName());
        intent.putExtra("id",list.get(position).getCivillId());
        setResult(7,intent);
        finish();
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
