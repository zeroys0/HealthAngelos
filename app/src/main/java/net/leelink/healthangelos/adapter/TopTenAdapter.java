package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.RankBean;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.ViewHolder> {
    List<RankBean> list = new ArrayList<>();
    public Context context;
    int rank  = 0;

    public TopTenAdapter(List<RankBean> list, Context context ) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public TopTenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_item, parent, false); // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopTenAdapter.ViewHolder holder, int position) {
        holder.tv_count.setText(list.get(position).getTotalCount()+"次");
        String name = list.get(position).getElderlyName();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if(sb.length()>1) {
            sb.setCharAt(1, '*');
        }
        if(sb.length()>3) {
            for(int i =0;i<sb.length();i++) {
                if(i<sb.length()-1 && i>0) {
                    sb.setCharAt(i, '*');
                }
            }
        }
        holder.tv_name.setText(sb.toString());

        if(list.get(position).getRowValue()!=null) {
            holder.tv_phone.setText(list.get(position).getRowValue());
        } else {
              holder.tv_phone.setText("心率:"+list.get(position).getRowNo()+"次/分钟");
        }
        if(position ==0) {
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(R.drawable.img_first);
        }
        if(position ==1){
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(R.drawable.img_second);
        }
        if(position ==2){
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(R.drawable.img_third);
        }
        if(position >2
        ) {
            holder.tv_number.setVisibility(View.VISIBLE);
            holder.tv_number.setText(position + 1 + "");
        }
        rank++;
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_phone,tv_count,tv_number;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_count = itemView.findViewById(R.id.tv_count);
            tv_number = itemView.findViewById(R.id.tv_number);
            img = itemView.findViewById(R.id.img);

        }
    }
}
