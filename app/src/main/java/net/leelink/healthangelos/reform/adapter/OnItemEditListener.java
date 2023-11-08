package net.leelink.healthangelos.reform.adapter;

import android.view.View;

public interface OnItemEditListener {
    void onItemClick(View view);
    void onProductEdit(View view, int position);
    void onProductDel(View view, int position);
}
