package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.FoodBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseFoodAdapter extends RecyclerView.Adapter<ChooseFoodAdapter.ViewHolder> {
    List<FoodBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public ChooseFoodAdapter(List<FoodBean> list,Context context,OnOrderListener onOrderListener){
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_food,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_energy.setText(list.get(position).getKcal()+"千卡/100克");
        holder.rl_add.setOnClickListener(new View.OnClickListener() {
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
        TextView tv_name,tv_energy;
        RelativeLayout rl_add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_energy = itemView.findViewById(R.id.tv_energy);
            rl_add = itemView.findViewById(R.id.rl_add);
        }
    }
}
