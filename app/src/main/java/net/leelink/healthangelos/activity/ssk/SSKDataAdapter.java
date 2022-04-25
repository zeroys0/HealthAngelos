package net.leelink.healthangelos.activity.ssk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SSKDataAdapter extends RecyclerView.Adapter<SSKDataAdapter.ViewHolder> {
    List<SSKBean> list;
    Context context;


    public SSKDataAdapter(List<SSKBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SSKDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ssk_data,parent,false);
        SSKDataAdapter.ViewHolder viewHolder = new SSKDataAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SSKDataAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getTime());
        holder.tv_content.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
