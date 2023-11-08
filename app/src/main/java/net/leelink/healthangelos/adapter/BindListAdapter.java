package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BindBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BindListAdapter extends RecyclerView.Adapter<BindListAdapter.ViewHolder> {
    List<BindBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public BindListAdapter(List<BindBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bind,parent,false);
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
        holder.tv_name.setText(list.get(position).getCommitteeName());
        holder.tv_time.setText(list.get(position).getBindTime());
        int state = list.get(position).getAuthState();
        switch (state){
            case 1:
                holder.tv_state.setText("审核中");
                holder.tv_state.setTextColor(Color.parseColor("#37c851"));
                break;
            case 2:
                holder.tv_state.setText("已绑定");
                holder.tv_state.setTextColor(Color.parseColor("#2387ff"));
                break;
            case 3:
                holder.tv_state.setText("审核未通过");
                holder.tv_state.setTextColor(Color.parseColor("#ff463c"));
                break;
            case 4:
                holder.tv_state.setText("已解绑");
                holder.tv_state.setTextColor(Color.parseColor("#999999"));
                break;
            case 5:
                holder.tv_state.setText("已撤销");
                holder.tv_state.setTextColor(Color.parseColor("#999999"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_time,tv_state;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }
}
