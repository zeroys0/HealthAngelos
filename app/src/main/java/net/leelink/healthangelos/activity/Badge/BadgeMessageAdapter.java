package net.leelink.healthangelos.activity.Badge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BadgeMessageAdapter extends RecyclerView.Adapter<BadgeMessageAdapter.ViewHolder> {
    List<AlertBean> list;

    public BadgeMessageAdapter(List<AlertBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BadgeMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge_message,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeMessageAdapter.ViewHolder holder, int position) {
        if(list.get(position).getType()==18){
            holder.tv_msg.setText("低电量报警");
        }
        if(list.get(position).getType()==19){
            holder.tv_msg.setText("SOS报警");
        }
        if(list.get(position).getType()==20){
            holder.tv_msg.setText("关机报警");
        }
        if(list.get(position).getType()==56){
            holder.tv_msg.setText("SOS取消报警");
        }
        holder.tv_time.setText(list.get(position).getCreateTime());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_msg,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
