package net.leelink.healthangelos.activity.owonDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OwonHisAdapter extends RecyclerView.Adapter<OwonHisAdapter.ViewHolder> {
    List<HisBean> list;
    Context context;
    public OwonHisAdapter(List<HisBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OwonHisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_owon_his, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OwonHisAdapter.ViewHolder holder, int position) {
        holder.tv_alarm_name.setText(list.get(position).getEvtName());
        holder.tv_time.setText(list.get(position).getCreateTime());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_alarm_name, tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_alarm_name = itemView.findViewById(R.id.tv_alarm_name);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
