package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.OrderBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    List<OrderBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public OrderAdapter(List<OrderBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(view);
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
        Glide.with(context).load(Urls.getInstance().IMG_URL +list.get(position).getImgPath()).into(holder.img_head);
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_duties.setText(list.get(position).getDuties());
        holder.tv_department.setText(list.get(position).getDepartment());
        holder.tv_price.setText("￥"+list.get(position).getActPayPrice());
        holder.tv_content.setText(list.get(position).getRemark());
        holder.tv_time.setText(list.get(position).getCreateTime());
        if(list.get(position).getState() ==3) {
            holder.btn_confirm.setVisibility(View.VISIBLE);
        }
        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
        switch (list.get(position).getState()){
            case 1:
                holder.tv_state.setText("代付款");
                break;
            case 2:
                holder.tv_state.setText("待接单");
                break;
            case 3:
                holder.tv_state.setText("进行中");
                break;
            case 4:
                holder.tv_state.setText("待评价");
                break;
            case 5:
                holder.tv_state.setText("已评价");
                break;
            case 11:
                holder.tv_state.setText("已被拒");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name,tv_duties,tv_price,tv_content,tv_time,tv_department,tv_state;
        Button btn_confirm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_duties = itemView.findViewById(R.id.tv_duties);
            tv_department = itemView.findViewById(R.id.tv_department);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }
}
