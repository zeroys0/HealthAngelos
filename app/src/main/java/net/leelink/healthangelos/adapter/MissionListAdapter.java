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
        holder.tv_time.setText(list.get(position).getCreateTime().substring(5,7)+"月"+list.get(position).getCreateTime().substring(8,10)+"日");

        holder.tv_name.setText(list.get(position).getName());
        if(list.get(position).getType() ==1 ){
            holder.tv_get_time.setTextColor(context.getResources().getColor(R.color.red));
            holder.tv_get_time.setText(list.get(position).getServiceTime()+"分钟");
        } else if(list.get(position).getType() ==2){
            holder.tv_get_time.setTextColor(context.getResources().getColor(R.color.text_green));
            holder.tv_get_time.setText(list.get(position).getServiceTime()+"分钟");
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_get_time,tv_name,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_get_time = itemView.findViewById(R.id.tv_get_time);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
