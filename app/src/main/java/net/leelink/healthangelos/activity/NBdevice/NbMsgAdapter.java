package net.leelink.healthangelos.activity.NBdevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.NBdevice.adapter.NbMsgTypeAAdapter;
import net.leelink.healthangelos.activity.NBdevice.adapter.NbMsgTypeBAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NbMsgAdapter extends RecyclerView.Adapter<NbMsgAdapter.ViewHolder> {
    private Context context;
    private List<NbMsgTimeBean> list;
    private int type;

    public NbMsgAdapter(Context context, List<NbMsgTimeBean> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public NbMsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nb_list,parent,false);
        NbMsgAdapter.ViewHolder viewHolder = new NbMsgAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NbMsgAdapter.ViewHolder holder, int position) {
        String time = list.get(position).getYear() +"-"+list.get(position).getMonth()+"-"+list.get(position).getDay();
        holder.tv_day.setText(time);
        if (type ==1) {
            NbMsgTypeAAdapter nbMsgTypeAAdapter = new NbMsgTypeAAdapter(context, list.get(position).getList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.msg_list.setAdapter(nbMsgTypeAAdapter);
            holder.msg_list.setLayoutManager(layoutManager);
        }
        if (type ==2) {
            NbMsgTypeBAdapter nbMsgTypeBAdapter = new NbMsgTypeBAdapter(context, list.get(position).getList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.msg_list.setAdapter(nbMsgTypeBAdapter);
            holder.msg_list.setLayoutManager(layoutManager);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
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
