package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BpIntervalBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BpIntervalAdapter extends RecyclerView.Adapter<BpIntervalAdapter.ViewHolder> {
    private Context context;
    private List<BpIntervalBean> list;

    private OnOrderListener onOrderListener;
    public BpIntervalAdapter(List<BpIntervalBean> list, Context context,OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public BpIntervalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bp_interval,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BpIntervalAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getStartTime()+"-"+list.get(position).getEndTime());
        holder.tv_interval.setText(list.get(position).getInterval()+"分钟");
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_interval;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_interval = itemView.findViewById(R.id.tv_interval);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
