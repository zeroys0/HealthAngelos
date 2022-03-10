package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.SkrHistoryBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SkrHistoryAdapter extends RecyclerView.Adapter<SkrHistoryAdapter.ViewHolder> {
    Context context;
    List<SkrHistoryBean> list;

    public SkrHistoryAdapter(Context context, List<SkrHistoryBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skr_history_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getCreateTime());
        holder.tv_detail.setText(list.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_detail = itemView.findViewById(R.id.tv_detail);
        }
    }
}
