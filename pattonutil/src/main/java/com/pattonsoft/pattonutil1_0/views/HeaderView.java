package com.pattonsoft.pattonutil1_0.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import pattonutil.R;

/**
 * 自定义刷新组建  头部
 * Created by loading182 on 2017/12/28.
 */

public class HeaderView extends LinearLayout implements SwipeRefreshTrigger, SwipeTrigger {
    private TextView tvStatus;
    private ProgressBar progress;

    public HeaderView(Context context) {
        this(context, null, 0);
    }


    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        tvStatus.setText("松开刷新...");
        progress.setVisibility(GONE);

    }

    @Override
    public void onRelease() {
        progress.setVisibility(VISIBLE);
    }

    @Override
    public void onRefresh() {
        tvStatus.setText("正在刷新...");
        progress.setVisibility(VISIBLE);
    }

    @Override
    public void onComplete() {
        tvStatus.setText("刷新成功!!");
        progress.setVisibility(VISIBLE);
    }

    @Override
    public void onReset() {
        tvStatus.setText("松开刷新...");
        progress.setVisibility(GONE);

    }


}
