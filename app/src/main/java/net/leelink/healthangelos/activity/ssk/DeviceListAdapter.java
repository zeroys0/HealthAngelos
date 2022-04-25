package net.leelink.healthangelos.activity.ssk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListAdapter  extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    Context context;
    private ArrayList<SSKDeviceBean> SensorList = null;// 传感器列表
    OnOrderListener onOrderListener;

    public DeviceListAdapter(Context context, ArrayList<SSKDeviceBean> sensorList, OnOrderListener onOrderListener) {
        this.context = context;
        SensorList = sensorList;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ssk_device,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(SensorList.get(position).sensorBean.Device.DeviceNick);
        if(SensorList.get(position).is_bind){
            holder.tv_state.setText("已绑定");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.text_grey));
            holder.tv_state.setBackground(null);
        } else {
            holder.tv_state.setText("绑定");
        }
        holder.tv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SensorList.get(position).is_bind) {
                    onOrderListener.onButtonClick(v,position);
                }
            }
        });
        if(SensorList.get(position).online){
            holder.tv_ol_state.setText("在线");
        } else {
            holder.tv_ol_state.setText("离线");
        }

    }

    @Override
    public int getItemCount() {
        return SensorList==null?0:SensorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_state,tv_ol_state;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_ol_state = itemView.findViewById(R.id.tv_ol_state);
        }
    }
}
