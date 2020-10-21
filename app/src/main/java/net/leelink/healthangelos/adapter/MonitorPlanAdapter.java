package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.FencePlanBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MonitorPlanAdapter extends  RecyclerView.Adapter<MonitorPlanAdapter.ViewHolder> {
    List<FencePlanBean> list;
    Context context;
    OnOrderListener onOrderListener;
    public MonitorPlanAdapter (List<FencePlanBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public MonitorPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_plan_item, parent, false); // 实例化viewholder
        MonitorPlanAdapter.ViewHolder viewHolder = new MonitorPlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MonitorPlanAdapter.ViewHolder holder, final int position) {
        holder.tv_time.setText(list.get(position).getStartTime()+"-"+list.get(position).getStopTime());
        if(list.get(position).getCycleType() ==1) {
            holder.tv_circle_type.setText(list.get(position).getMonitorDate());
        } else if(list.get(position).getCycleType() ==2) {
            holder.tv_circle_type.setText(list.get(position).getWeeks());
        }
        holder.interval.setText(list.get(position).getTimeInterval()+"秒");
        holder.scope.setText(list.get(position).getScopeName());
        switch (list.get(position).getAlarmWay()) {
            case 1:
                holder.tv_msg.setText("短信");
                break;
            case 2:
                holder.tv_msg.setText("邮件");
                break;
            case 3:
                holder.tv_msg.setText("短信、邮件");
                break;

        }

        holder.tv_tel.setText(list.get(position).getCellphoneNumber());
        holder.tv_email.setText(list.get(position).getMailAddress());


        
        holder.im_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView im_edit;
        TextView tv_time,tv_circle_type,interval,scope,tv_msg,tv_tel,tv_email;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_edit = itemView.findViewById(R.id.im_edit);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_circle_type = itemView.findViewById(R.id.tv_circle_type);
            interval = itemView.findViewById(R.id.interval);
            scope = itemView.findViewById(R.id.scope);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_tel = itemView.findViewById(R.id.tv_tel);
            tv_email = itemView.findViewById(R.id.tv_email);


        }
    }
}
