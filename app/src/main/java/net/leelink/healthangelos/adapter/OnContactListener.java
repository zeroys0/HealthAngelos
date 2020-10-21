package net.leelink.healthangelos.adapter;

import android.view.View;

public interface OnContactListener  {
    void OnItemClick(View v);
    void OnEditClick(View v,int position);
    void OnDeleteClick(View v,int position);
}
