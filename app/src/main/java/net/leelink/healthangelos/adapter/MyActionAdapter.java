package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.ActionBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyActionAdapter extends RecyclerView.Adapter<MyActionAdapter.ViewHolder>{
    List<ActionBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public MyActionAdapter(List<ActionBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public MyActionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myaction,parent,false);
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
    public void onBindViewHolder(@NonNull MyActionAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getActName());
        holder.tv_detail.setText(list.get(position).getRemark());
        holder.tv_time.setText(list.get(position).getTime());
        Glide.with(context).load(Urls.IMG_URL+list.get(position).getTitleImg()).into(holder.img_head);
        switch (list.get(position).getState()){
            case 1:
                holder.tv_state.setText("未打卡");
                break;
            case 2:
                holder.tv_state.setText("已打卡");
                break;
            case 3:
                holder.tv_state.setText("已完成");
                break;
            case 4:
                holder.tv_state.setText("已过期");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name,tv_detail,tv_time,tv_state;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_title);
            tv_detail = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_state = itemView.findViewById(R.id.tv_state);

        }
    }
}
