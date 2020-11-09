package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.MealBean;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SetListAdapter extends RecyclerView.Adapter<SetListAdapter.ViewHolder> {
    Context context;
    OnOrderListener onOrderListener;
    List<MealBean> list;


    public SetListAdapter(List<MealBean> list,Context context,OnOrderListener onOrderListener) {
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.list = list;
    }

    @NonNull
    @Override
    public SetListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_list,parent,false);
        SetListAdapter.ViewHolder viewHolder = new SetListAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SetListAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getProductName());
        if(list.get(position).getExistState()==0) {
            holder.tv_time.setText(list.get(position).getTerm()+"天");
        } else {
            holder.tv_time.setText(list.get(position).getEndTime());
        }
        holder.tv_price.setText(list.get(position).getPrice());
        holder.tv_detail.setText(list.get(position).getRemark());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_time,tv_price,tv_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_detail = itemView.findViewById(R.id.tv_detail);


        }
    }
}
