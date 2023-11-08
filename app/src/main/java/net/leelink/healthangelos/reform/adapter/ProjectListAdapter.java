package net.leelink.healthangelos.reform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.reform.bean.ProductItemBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    List<ProductItemBean> list;
    Context context;
//    OnItemEditListener onItemEditListener;

    public ProjectListAdapter(List<ProductItemBean> list, Context context) {
        this.list = list;
        this.context = context;
    }
//
//    public ProjectListAdapter(List<ProductItemBean> list, Context context, OnItemEditListener onItemEditListener) {
//        this.list = list;
//        this.context = context;
//        this.onItemEditListener = onItemEditListener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getProductName());
        holder.tv_content.setText(list.get(position).getProductContent());
        holder.tv_number.setText("x"+list.get(position).getNum());
        holder.tv_price.setText("￥"+list.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_content,tv_number,tv_price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }
}
