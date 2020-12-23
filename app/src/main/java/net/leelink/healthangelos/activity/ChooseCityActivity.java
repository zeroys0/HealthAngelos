package net.leelink.healthangelos.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.CommonRecycleAdapter;
import net.leelink.healthangelos.adapter.CommonViewHolder;
import net.leelink.healthangelos.adapter.DefaultItemDecoration;
import net.leelink.healthangelos.adapter.ItemClickListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.CityBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.LetterSideBarView;
import net.leelink.healthangelos.view.SideBarTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChooseCityActivity extends BaseActivity   {
    private RecyclerView mRv;
    private LetterSideBarView mLetterSideBarView;
    private TextView mIndexTv;
    List<CityBean> mList = new ArrayList<>();
    private boolean isScale = false;
    Context context;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        context = this;
        init();

        initCity();
    }

    public void init(){
        mRv = findViewById(R.id.rv);
        mLetterSideBarView = findViewById(R.id.letterSideBarView);
        mIndexTv = findViewById(R.id.indexTv);
        mLetterSideBarView.setOnSideBarTouchListener(new SideBarTouchListener() {
            @Override
            public void onTouch(String letter, boolean isTouch) {
                for (int i = 0; i < mList.size(); i++) {
                    if (letter.equals(mList.get(i).getTabNum().charAt(0) + "")) {
                        mRv.scrollToPosition(i);
                        break;
                    }
                }
                showCurrentIndex(letter);
            }
        });
    }

    public void initCity(){
        OkGo.<String>get(Urls.getInstance().GETALLCITY)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取城市列表", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                mList = gson.fromJson(jsonArray.toString(),new TypeToken<List<CityBean>>(){}.getType());
                                Collections.sort(mList);
                                CityAdapter adapter = new CityAdapter(context, mList, R.layout.person_recycler_item);
                                adapter.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Intent intent = new Intent();
                                        intent.putExtra("data",mList.get(position).getName());
                                        setResult(1,intent);
                                        finish();

                                    }
                                });
                                mRv.setAdapter(adapter);
                                mRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                mRv.addItemDecoration(new DefaultItemDecoration(context, R.drawable.default_item));
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            }  else {
                                Toast.makeText(ChooseCityActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private class CityAdapter extends CommonRecycleAdapter<CityBean> {

        public CityAdapter(Context context, List<CityBean> mData, int layoutId) {
            super(context, mData, layoutId);

        }

        @Override
        protected void convert(CommonViewHolder holder, CityBean city, int position) {
            String currentWord =city.getTabNum();
            if (position > 0) {
                String lastWord = mList.get(position - 1).getTabNum() + "";
                //拿当前的首字母和上一个首字母比较,与首字母相同，需要隐藏当前item的索引
                holder.setVisibility(R.id.indexTv, currentWord.equals(lastWord) ? View.GONE : View.VISIBLE);
            } else {
                holder.setVisibility(R.id.indexTv, View.VISIBLE);
            }
            holder.setText(R.id.indexTv, currentWord);
            holder.setText(R.id.userNameTv, city.getName());

        }
    }

    private void showCurrentIndex(String letter) {
        mIndexTv.setText(letter);
        if (!isScale) {
            isScale = true;
            ViewCompat.animate(mIndexTv)
                    .scaleX(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(380)
                    .start();
            ViewCompat.animate(mIndexTv)
                    .scaleY(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(380)
                    .start();
        }

        mHandler.removeCallbacksAndMessages(null);
        // 延时隐藏
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCompat.animate(mIndexTv)
                        .scaleX(0f)
                        .setDuration(380)
                        .start();
                ViewCompat.animate(mIndexTv)
                        .scaleY(0f)
                        .setDuration(380)
                        .start();
                isScale = false;
            }
        }, 380);
    }


}
