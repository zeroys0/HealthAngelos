package net.leelink.healthangelos.volunteer.adapter;

import android.view.View;

public interface OnApplyListener {
    void onRefuse(View view,int position);
    void onConfirm(View view, int position);
    void onItemClick(View view);
}
