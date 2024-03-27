package net.leelink.healthangelos.activity.NBdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TiTimeAdapter extends RecyclerView.Adapter<TiTimeAdapter.ViewHolder> {
    private Context context;
    private List<TiTimeBean> list;

    public TiTimeAdapter(Context context, List<TiTimeBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TiTimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nb_list, parent, false);
        TiTimeAdapter.ViewHolder viewHolder = new TiTimeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TiTimeAdapter.ViewHolder holder, int position) {
        String time = list.get(position).getYear() + "-" + list.get(position).getMonth() + "-" + list.get(position).getDay();
        holder.tv_day.setText(time);

        TiAdapter nbMsgTypeAAdapter = new TiAdapter(context, list.get(position).getList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.msg_list.setAdapter(nbMsgTypeAAdapter);
        holder.msg_list.setLayoutManager(layoutManager);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_day;
        RecyclerView msg_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.tv_day);
            msg_list = itemView.findViewById(R.id.msg_list);
        }
    }
}
