package net.leelink.healthangelos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.AlarmBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class NeoAlarmAdapter extends RecyclerView.Adapter<NeoAlarmAdapter.ViewHolder> {
    Context context;
    OnAlarmChangeListener onAlarmChangeListener;
    List<AlarmBean> list;

    public NeoAlarmAdapter(Context context, OnAlarmChangeListener onAlarmChangeListener, List<AlarmBean> list) {
        this.context = context;
        this.onAlarmChangeListener = onAlarmChangeListener;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.neo_item_alarm, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(list.get(position).getProductName());
        if(list.get(position).getExistState()==0) {
            holder.cb_check.setChecked(false);
        } else {
            holder.cb_check.setChecked(true);
        }
        holder.cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onAlarmChangeListener.OnChangeListener(buttonView,position,isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        SwitchCompat cb_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_check = itemView.findViewById(R.id.cb_check);
        }
    }
}
