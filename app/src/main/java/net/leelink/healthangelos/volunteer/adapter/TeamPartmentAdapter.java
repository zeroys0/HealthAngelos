package net.leelink.healthangelos.volunteer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.TeamMemberBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.volunteer.MyTeamActivity.REMOVE_TEAM_MEMBER;
import static net.leelink.healthangelos.volunteer.MyTeamActivity.TEAM_MISSION;
import static net.leelink.healthangelos.volunteer.MyTeamActivity.TEAM_MISSION_REFUSE;
import static net.leelink.healthangelos.volunteer.VolunteerActivity.VOL_ID;

public class TeamPartmentAdapter extends RecyclerView.Adapter<TeamPartmentAdapter.ViewHolder> {
    List<TeamMemberBean> list;
    Context context;
    OnOrderListener onOrderListener;
    int  type;
    int volId = -1;

    public TeamPartmentAdapter(List<TeamMemberBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    public TeamPartmentAdapter(List<TeamMemberBean> list, Context context, OnOrderListener onOrderListener,int type) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.type = type;
    }
    public TeamPartmentAdapter(List<TeamMemberBean> list, Context context, OnOrderListener onOrderListener,int type,int volId) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.type = type;
        this.volId = volId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_partment,parent,false);
        TeamPartmentAdapter.ViewHolder viewHolder = new TeamPartmentAdapter.ViewHolder(v);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getName());
        if(list.get(position).getSex()==0) {
            holder.tv_sex.setText("男");
        } else {
            holder.tv_sex.setText("女");
        }
        holder.tv_phone.setText(list.get(position).getTelephone());

        if(type == REMOVE_TEAM_MEMBER){
            holder.btn_confirm.setBackground(context.getResources().getDrawable(R.drawable.red_stroke));
            holder.btn_confirm.setText("移除成员");
            holder.btn_confirm.setTextColor(context.getResources().getColor(R.color.vol_red));
            String id = list.get(position).getVolId()+"";
            if(id.equals(String.valueOf(volId))){
                holder.btn_confirm.setVisibility(View.GONE);
            }
        }
        if(type == TEAM_MISSION_REFUSE ){
            holder.btn_confirm.setBackground(context.getResources().getDrawable(R.drawable.red_stroke));
            holder.btn_confirm.setText("拒绝此人参加");
            holder.btn_confirm.setTextColor(context.getResources().getColor(R.color.vol_red));
            holder.tv_name.setText(list.get(position).getVolName());
            if(list.get(position).getVolSex()==0) {
                holder.tv_sex.setText("男");
            } else {
                holder.tv_sex.setText("女");
            }
            holder.tv_phone.setText(list.get(position).getVolTelephone());
            if(list.get(position).getId().equals(String.valueOf(volId))){
                holder.btn_confirm.setBackground(context.getResources().getDrawable(R.drawable.bg_grey_stroke));
                holder.btn_confirm.setText("取消报名");
                holder.btn_confirm.setTextColor(context.getResources().getColor(R.color.text_gray));
            }
        }
        if(type == TEAM_MISSION ){
            holder.btn_confirm.setVisibility(View.INVISIBLE);
            holder.tv_name.setText(list.get(position).getVolName());
            if(list.get(position).getVolSex()==0) {
                holder.tv_sex.setText("男");
            } else {
                holder.tv_sex.setText("女");
            }
            holder.tv_phone.setText(list.get(position).getVolTelephone());
            if(list.get(position).getId().equals(VOL_ID+"")){
                holder.btn_confirm.setVisibility(View.VISIBLE);
                holder.btn_confirm.setBackground(context.getResources().getDrawable(R.drawable.bg_grey_stroke));
                holder.btn_confirm.setText("取消报名");
                holder.btn_confirm.setTextColor(context.getResources().getColor(R.color.text_gray));
            }
        }

        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_sex,tv_phone;
        Button btn_confirm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
        }
    }
}
