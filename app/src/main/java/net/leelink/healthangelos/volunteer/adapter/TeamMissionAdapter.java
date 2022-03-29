package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.TeamMissionBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamMissionAdapter extends RecyclerView.Adapter<TeamMissionAdapter.ViewHolder> {
    List<TeamMissionBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public TeamMissionAdapter(List<TeamMissionBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_mission,parent,false);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text_title.setText(list.get(position).getServTitle());
        holder.tv_content.setText(list.get(position).getContent());
        String s = list.get(position).getServTime();
        if(list.get(position).getServTime()!=null) {
            s = s.substring(0, s.length() - 3);
            holder.tv_time.setText(s);
        }
        s = list.get(position).getEndTime();
        s = s.substring(0,s.length()-3);
        holder.tv_end_time.setText(s);
        holder.tv_number.setText("所需人数: "+list.get(position).getNum()+"人");
    }

    @Override
    public int getItemCount() {
        return  list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_title,tv_content,tv_time,tv_end_time,tv_number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
            tv_number = itemView.findViewById(R.id.tv_number);
        }
    }
}
