package net.leelink.healthangelos.activity.slaap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.SlaapReportBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SleepAdapter extends RecyclerView.Adapter<SleepAdapter.ViewHolder> {
    private Context context;
    private List<SlaapReportBean> list;
    private OnOrderListener onOrderListener;

    public SleepAdapter(Context context, List<SlaapReportBean> list, OnOrderListener onOrderListener) {
        this.context = context;
        this.list = list;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slaap_data,parent,false);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String y =list.get(position).getDate().substring(0,4);
        String m = list.get(position).getDate().substring(4,6);
        String d = list.get(position).getDate().substring(6,8);
        holder.tv_time.setText(y+"-"+m+"-"+d);


    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);

        }
    }
}
