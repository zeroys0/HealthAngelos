package net.leelink.healthangelos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.leelink.healthangelos.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SafeDeviceAdapter extends RecyclerView.Adapter<SafeDeviceAdapter.ViewHolder> {
    @NonNull
    @Override
    public SafeDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SafeDeviceAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
