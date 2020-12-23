package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.EstimateBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EstimateAdapter extends RecyclerView.Adapter<EstimateAdapter.ViewHolder> {

    List<EstimateBean> list;
    Context context;
    OnItemClickListener onItemClickListener;



    public EstimateAdapter(List<EstimateBean> list, Context context, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public EstimateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_benefit,parent,false);
        EstimateAdapter.ViewHolder v= new EstimateAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v);
            }
        });
        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull EstimateAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(list.get(position).getQuestionTitle());
        String time = list.get(position).getUpdateTime();
        time = time.substring(0,11);
        holder.tv_time.setText(time);
        holder.tv_resource.setVisibility(View.INVISIBLE);
        holder.tv_content.setText(list.get(position).getQuestionRemark());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_time,tv_resource,tv_content;
        ImageView img_head;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_head = itemView.findViewById(R.id.img_head);
            tv_resource = itemView.findViewById(R.id.tv_resource);
            tv_content = itemView.findViewById(R.id.tv_content);

        }
    }
}
