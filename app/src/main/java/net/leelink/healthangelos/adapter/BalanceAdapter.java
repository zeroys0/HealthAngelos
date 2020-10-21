package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BalanceBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BalanceAdapter  extends RecyclerView.Adapter<BalanceAdapter.ViewHolder>  {
    Context context;
    List<BalanceBean> list;

    public BalanceAdapter(Context context,List<BalanceBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_record_item,parent,false);
        BalanceAdapter.ViewHolder v= new BalanceAdapter.ViewHolder(view);

        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getCreateTime());
        holder.tv_cost.setText("+"+list.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_time,tv_cost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_cost = itemView.findViewById(R.id.tv_cost);
        }
    }
}
