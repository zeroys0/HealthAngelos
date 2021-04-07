package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.AlarmMessageBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmMessageAdapter extends RecyclerView.Adapter<AlarmMessageAdapter.ViewHolder> {
    List<AlarmMessageBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public AlarmMessageAdapter(List<AlarmMessageBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm_message,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        switch (list.get(position).getType())
        holder.tv_title.setText(list.get(position).getType()+"异常");
        StringBuilder sb = new StringBuilder();
        sb.append("当前"+list.get(position).getType()+"值");
        sb.append(list.get(position).getCurrentData()+",");
        sb.append("正常"+list.get(position).getType()+"范围");
        sb.append(list.get(position).getRefeRange());
        holder.tv_content.setText(sb.toString());
        holder.tv_time.setText(list.get(position).getUpdateTime());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_title,tv_content,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
