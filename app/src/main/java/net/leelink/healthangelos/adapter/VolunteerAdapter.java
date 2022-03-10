package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.VolunteerEventBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.ViewHolder> {
    List<VolunteerEventBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public VolunteerAdapter(List<VolunteerEventBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volunteer_event,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text_title.setText(list.get(position).getServTitle());
        holder.tv_content.setText(list.get(position).getServContent());
        holder.tv_time.setText(list.get(position).getServTime());
        if(list.get(position).getType()==1){
            //个人任务
            holder.tv_label.setText("个人任务");
        } else if(list.get(position).getType()==2){
            //团队任务
            holder.tv_label.setBackground(context.getResources().getDrawable(R.drawable.team_mission));
            holder.tv_label.setText("团队"+list.get(position).getNum()+"人");
            holder.tv_label.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.flag_team),null,null,null);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_title,tv_content,tv_time,tv_label;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_label = itemView.findViewById(R.id.tv_label);
        }
    }
}
