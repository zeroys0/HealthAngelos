package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BonusBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BonusAdapter extends RecyclerView.Adapter<BonusAdapter.ViewHolder> {
    Context context;
    List<BonusBean> list;

    public BonusAdapter(Context context, List<BonusBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BonusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bonus_record_item,parent,false);
        BonusAdapter.ViewHolder v= new BonusAdapter.ViewHolder(view);

        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull BonusAdapter.ViewHolder holder, int position) {
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
