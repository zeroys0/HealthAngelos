package net.leelink.healthangelos.reform.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReformProgressAdapter extends RecyclerView.Adapter<ReformProgressAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reform_progress,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position ==0) {
            holder.img_down.setVisibility(View.INVISIBLE);
        }

        if(position ==3) {
            holder.tv_new.setVisibility(View.VISIBLE);
            holder.img_icon.setVisibility(View.INVISIBLE);
            holder.img_up.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View img_down,img_up;
        ImageView img_icon;
        TextView tv_new;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_down = itemView.findViewById(R.id.img_down);
            tv_new = itemView.findViewById(R.id.tv_new);
            img_icon = itemView.findViewById(R.id.img_icon);
            img_up = itemView.findViewById(R.id.img_up);
        }
    }
}
