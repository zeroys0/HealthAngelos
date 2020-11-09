package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.TeamMemberBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamPartmentAdapter extends RecyclerView.Adapter<TeamPartmentAdapter.ViewHolder> {
    List<TeamMemberBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public TeamPartmentAdapter(List<TeamMemberBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_partment,parent,false);
        TeamPartmentAdapter.ViewHolder viewHolder = new TeamPartmentAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getName());
        if(list.get(position).getSex()==0) {
            holder.tv_sex.setText("男");
        } else {
            holder.tv_sex.setText("女");
        }
        holder.tv_phone.setText(list.get(position).getTelephone());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_sex,tv_phone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_phone = itemView.findViewById(R.id.tv_phone);
        }
    }
}
