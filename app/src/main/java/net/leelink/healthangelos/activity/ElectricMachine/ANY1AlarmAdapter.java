package net.leelink.healthangelos.activity.ElectricMachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ANY1AlarmAdapter extends RecyclerView.Adapter<ANY1AlarmAdapter.ViewHolder> {
    OnOrderListener onOrderListener;
    List<ElectAlarmBean> list;

    public ANY1AlarmAdapter(OnOrderListener onOrderListener) {
        this.onOrderListener = onOrderListener;
    }

    public ANY1AlarmAdapter(OnOrderListener onOrderListener, List<ElectAlarmBean> list) {
        this.onOrderListener = onOrderListener;
        this.list = list;
    }

    @NonNull
    @Override
    public ANY1AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_any_alarm,parent,false);
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
    public void onBindViewHolder(@NonNull ANY1AlarmAdapter.ViewHolder holder, int position) {
        holder.tv_alarm.setText(list.get(position).getAlarmName());
        holder.tv_content.setText(list.get(position).getAlarmTime() + list.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_alarm,tv_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_alarm = itemView.findViewById(R.id.tv_alarm);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
