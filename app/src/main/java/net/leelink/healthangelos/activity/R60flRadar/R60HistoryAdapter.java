package net.leelink.healthangelos.activity.R60flRadar;

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

public class R60HistoryAdapter extends RecyclerView.Adapter<R60HistoryAdapter.ViewHolder> {
    private Context context;
    private List<R60TimeBean> list;
    private int type;

    public R60HistoryAdapter(Context context, List<R60TimeBean> list,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }


    @NonNull
    @Override
    public R60HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_r60history,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull R60HistoryAdapter.ViewHolder holder, int position) {
        holder.tv_day.setText(String.valueOf(list.get(position).getDay()));
        holder.tv_month.setText(String.valueOf(list.get(position).getMonth())+"月");
        if (type ==1) {
            R60HistoryItemAdapter r60HistoryItemAdapter = new R60HistoryItemAdapter(context, list.get(position).getList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.msg_list.setAdapter(r60HistoryItemAdapter);
            holder.msg_list.setLayoutManager(layoutManager);
        }
        if (type ==2) {
            R60HistoryWarnAdapter r60HistoryItemAdapter = new R60HistoryWarnAdapter(context, list.get(position).getList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.msg_list.setAdapter(r60HistoryItemAdapter);
            holder.msg_list.setLayoutManager(layoutManager);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_day,tv_month;
        private RecyclerView msg_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_month = itemView.findViewById(R.id.tv_month);
            msg_list = itemView.findViewById(R.id.msg_list);
        }
    }
}
