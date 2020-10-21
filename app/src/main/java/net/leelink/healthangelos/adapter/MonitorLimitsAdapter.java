package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.LimitBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MonitorLimitsAdapter extends  RecyclerView.Adapter<MonitorLimitsAdapter.ViewHolder> {

    List<LimitBean> list;
    Context context;
    OnOrderListener onOrderListener;
    public MonitorLimitsAdapter (List<LimitBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public MonitorLimitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_limits_item, parent, false); // 实例化viewholder
        MonitorLimitsAdapter.ViewHolder viewHolder = new MonitorLimitsAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MonitorLimitsAdapter.ViewHolder holder, final int position) {
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
        holder.tv_name.setText(list.get(position).getAlias());
        holder.tv_addr1.setText(list.get(position).getAaddress());
        holder.tv_addr2.setText(list.get(position).getBaddress());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tv_edit;
        TextView tv_name,tv_addr1,tv_addr2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_edit = itemView.findViewById(R.id.tv_edit);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_addr1 = itemView.findViewById(R.id.tv_addr1);
            tv_addr2 = itemView.findViewById(R.id.tv_addr2);
        }
    }
}
