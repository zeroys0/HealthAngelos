package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.bean.LocationBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationListAdapter extends  RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    List<LocationBean> list;
    Context context;
    OnOrderListener onOrderListener;
    public LocationListAdapter (List<LocationBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false); // 实例化viewholder
        LocationListAdapter.ViewHolder viewHolder = new LocationListAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListAdapter.ViewHolder holder, int position) {
        holder.tv_address.setText(list.get(position).getAddress());
        switch (list.get(position).getLocType()) {
            case 1:
                holder.tv_location_type.setText("红键");
                break;
            case 2:
                holder.tv_location_type.setText("绿键");
                break;
            case 3:
                holder.tv_location_type.setText("一般定位");
                break;
        }

        holder.tv_time.setText(list.get(position).getCreateTime());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address,tv_location_type,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_location_type = itemView.findViewById(R.id.tv_location_type);
            tv_time = itemView.findViewById(R.id.tv_time);


        }
    }
}
