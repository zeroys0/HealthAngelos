package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.TeamMemberBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamApplyAdapter extends RecyclerView.Adapter<TeamApplyAdapter.ViewHolder> {
    List<TeamMemberBean> list;
    Context context;
    OnApplyListener onApplyListener;

    public TeamApplyAdapter(List<TeamMemberBean> list, Context context, OnApplyListener onApplyListener) {
        this.list = list;
        this.context = context;
        this.onApplyListener = onApplyListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_apply,parent,false);
        TeamApplyAdapter.ViewHolder viewHolder = new TeamApplyAdapter.ViewHolder(v);

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
        holder.btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onApplyListener.onRefuse(v,position);
            }
        });
        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onApplyListener.onConfirm(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_sex,tv_phone;
        Button btn_refuse,btn_confirm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btn_refuse = itemView.findViewById(R.id.btn_refuse);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
        }
    }
}
