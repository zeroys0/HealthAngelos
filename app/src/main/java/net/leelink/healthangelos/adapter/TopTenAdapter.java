package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.tv_name.setText(list.get(position).getElderlyName());

        if(list.get(position).getTelephone()!=null) {
            holder.tv_phone.setText(list.get(position).getTelephone());
        }
        int b = list.size() -rank;
        holder.tv_number.setText(position+1+"");
        rank++;
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_phone,tv_count,tv_number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_count = itemView.findViewById(R.id.tv_count);
            tv_number = itemView.findViewById(R.id.tv_number);


        }
    }
}
