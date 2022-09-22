package net.leelink.healthangelos.activity.a666g;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.G7gBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class G7gBldSugarAdapter extends RecyclerView.Adapter<G7gBldSugarAdapter.ViewHolder> {
    Context context;
    List<G7gBean> list;
    OnOrderListener onItemClickListener;
    G7gBldSgAdapter g7gBldSgAdapter;

    public G7gBldSugarAdapter(Context context, List<G7gBean> list, OnOrderListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a6gbldpressure_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rl_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onButtonClick(v,position);
            }
        });
        if(list.get(position).getIs_show()){
            g7gBldSgAdapter = new G7gBldSgAdapter(context,list.get(position).getList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
            holder.bld_list.setAdapter(g7gBldSgAdapter);
            holder.bld_list.setLayoutManager(layoutManager);
            holder.bld_list.setVisibility(View.VISIBLE);
            holder.img_arrow.setImageResource(R.drawable.arrow_down_grey);
        } else {
            holder.bld_list.setVisibility(View.GONE);
            holder.img_arrow.setImageResource(R.drawable.arrow_right_grey);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_date;
        RecyclerView bld_list;
        ImageView img_arrow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_date = itemView.findViewById(R.id.rl_date);
            bld_list = itemView.findViewById(R.id.bld_list);
            img_arrow = itemView.findViewById(R.id.img_arrow);
        }
    }
}
