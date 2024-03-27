package net.leelink.healthangelos.activity.h008.adapter;

import android.view.View;

public interface OnAlarmClockListener {
    void onItemClick(View view);
    void onDelete(View view,int position);
    void onCheckChange(View view, int position,boolean checked);
}
