package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.TeamBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {
    List<TeamBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public TeamListAdapter(List<TeamBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list,parent,false);
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
        holder.tv_title.setText(list.get(position).getTeamName());
        holder.tv_address.setText(list.get(position).getAreaAddress());
        holder.tv_type.setText(list.get(position).getServiceRequair());

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
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
        TextView tv_title,tv_address,tv_type;
        Button btn_add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_address =  itemView.findViewById(R.id.tv_address);
            tv_type = itemView.findViewById(R.id.tv_type);
            btn_add = itemView.findViewById(R.id.btn_add);
        }
    }
}
