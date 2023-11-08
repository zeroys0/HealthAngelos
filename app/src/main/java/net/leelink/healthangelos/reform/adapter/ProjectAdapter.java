package net.leelink.healthangelos.reform.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.reform.bean.ProductItemBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    Context context;
    OnOrderListener onOrderListener;
    List<ProductItemBean> list;

    public ProjectAdapter(Context context, OnOrderListener onOrderListener, List<ProductItemBean> list) {
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pro, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.tv_name.setText(list.get(position).getProductName());
        holder.tv_content.setText(list.get(position).getProductContent());
        holder.tv_number.setText("x" + list.get(position).getNum());
        holder.tv_price.setText("￥" + list.get(position).getAmount());
        holder.tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_detail, tv_name, tv_content, tv_number, tv_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_detail = itemView.findViewById(R.id.tv_detail);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_price = itemView.findViewById(R.id.tv_price);

        }
    }
}
