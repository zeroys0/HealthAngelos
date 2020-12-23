package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.VolBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MissionListAdapter extends RecyclerView.Adapter<MissionListAdapter.ViewHolder> {
    List<VolBean> list;
    Context context;

    public MissionListAdapter(List<VolBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_month.setText(list.get(position).getCreateTime().substring(5,7)+"月");
        holder.tv_days.setText(list.get(position).getCreateTime().substring(8,10)+"日");
        holder.tv_get_time.setText(list.get(position).getServiceTime()+"分钟");
        holder.tv_name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_days,tv_month,tv_get_time,tv_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_days = itemView.findViewById(R.id.tv_days);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_get_time = itemView.findViewById(R.id.tv_get_time);
            tv_name = itemView.findViewById(R.id.tv_name);

        }
    }
}
