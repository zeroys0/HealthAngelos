package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.SubsidyBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubsidyAdapter extends RecyclerView.Adapter<SubsidyAdapter.ViewHolder> {
    List<SubsidyBean> list;
    Context context;

    public SubsidyAdapter(List<SubsidyBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subsidy_item,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (list.get(position).getRelationType()){
            case 1:
                holder.tv_name.setText("低保");
                break;
            case 2:
                holder.tv_name.setText("低收入家庭救助");
                break;
            case 3:
                holder.tv_name.setText("优抚");
                break;
            case 4:
                holder.tv_name.setText("空巢");
                break;
            case 5:
                holder.tv_name.setText("失能");
                break;
            case 6:
                holder.tv_name.setText("独生子女父母");
                break;
            case 7:
                holder.tv_name.setText("市级劳动模范");
                break;

        }
        holder.tv_ammount.setText(list.get(position).getAmount());

        if(list.get(position).getSendTime()!=null) {
            holder.tv_time.setText(list.get(position).getSendTime());
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_ammount,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_ammount = itemView.findViewById(R.id.tv_ammount);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
