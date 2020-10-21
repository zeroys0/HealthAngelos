package com.pattonsoft.pattonutil1_0.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 待滑动监听的ScrollView
 */
public class MyScrollView extends ScrollView {

    public interface ScrollViewListener {

        void onScrollChanged(MyScrollView scrollView, int x, int y,
                             int old_x, int old_y);

    }

    private ScrollViewListener scrollViewListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int old_x, int old_y) {
        super.onScrollChanged(x, y, old_x, old_y);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, old_x, old_y);
        }
    }

}