package net.leelink.healthangelos.adapter;

import android.view.View;

public interface OnSetMealListener {
    void onItemClick(View view);
    void onDetailClick(View view, int position);
    void onButtonClick(View view, int position);
}
