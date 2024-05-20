package com.pattonsoft.pattonutil1_0.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import pattonutil.R;

/**
 * 自定义刷新组建  头部
 * Created by loading182 on 2017/12/28.
 */

public class FooterView extends LinearLayout implements SwipeLoadMoreTrigger, SwipeTrigger {
    private TextView tvStatus;
    private ProgressBar progress;

    public FooterView(Context context) {
        this(context, null, 0);
    }


    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //这个view随意定义

        //这里的原理就是简单的动态布局加入
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(getContext(), R.layout.header_view, null);
        addView(view, lp);

        progress = (ProgressBar) view.findViewById(R.id.progress);
        tvStatus = (TextView) view.findViewById(R.id.tv_state);
    }


    @Override
    public void onPrepare() {
        progress.setVisibility(GONE);

    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {
        tvStatus.setText("释放加载更多...");
        progress.setVisibility(GONE);

    }

    @Override
    public void onRelease() {
        progress.setVisibility(VISIBLE);
    }


    @Override
    public void onComplete() {
        tvStatus.setText("加载成功!!");
        progress.setVisibility(VISIBLE);
    }

    @Override
    public void onReset() {
        tvStatus.setText("释放加载更多...");
        progress.setVisibility(GONE);

    }


    @Override
    public void onLoadMore() {
        tvStatus.setText("正在加载...");
        progress.setVisibility(VISIBLE);
    }
}
