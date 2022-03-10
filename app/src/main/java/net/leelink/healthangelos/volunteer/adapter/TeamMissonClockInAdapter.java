package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.TeamMissionBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamMissonClockInAdapter extends RecyclerView.Adapter<TeamMissonClockInAdapter.ViewHolder> {
    List<TeamMissionBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public TeamMissonClockInAdapter(List<TeamMissionBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volunteer_clockin, parent, false);
        TeamMissonClockInAdapter.ViewHolder viewHolder = new TeamMissonClockInAdapter.ViewHolder(view);
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
        holder.btn_clockin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v, position);
            }
        });
        holder.tv_title.setText(list.get(position).getServTitle());


        switch (list.get(position).getState()) {
            case 0:
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                holder.tv_state.setText("活动尚未开始");
                break;
            case 3:
                holder.tv_state.setText("进行中");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 4:
                holder.tv_state.setText("审核中");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 5:
            case 8:
                holder.tv_state.setText("已完成");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 6:
            case 9:
                holder.tv_state.setText("审核未通过");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 7:
                holder.tv_state.setText("申诉待审核");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 10:
                holder.tv_state.setText("已撤销");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 11:
                holder.tv_state.setText("已结束");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            default:
                break;

        }

        switch (list.get(position).getMyState()) {
            case 1:
                holder.tv_state.setText("待打卡");
                holder.btn_clockin.setVisibility(View.VISIBLE);
                holder.btn_clockin.setText("开始打卡");
                break;
            case 2:
                holder.tv_state.setText("已打卡");
                holder.btn_clockin.setVisibility(View.VISIBLE);
                holder.btn_clockin.setText("结束打卡");
                break;
        }
        holder.tv_time.setText(list.get(position).getServTime());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_state, tv_time;
        Button btn_clockin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_time = itemView.findViewById(R.id.tv_time);
            btn_clockin = itemView.findViewById(R.id.btn_clockin);
        }
    }
}
