package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataLeadAdapter extends RecyclerView.Adapter<DataLeadAdapter.ViewHolder> {
    Context context;
    List<String> list;
    OnOrderListener onOrderListener;

    public DataLeadAdapter(Context context, List<String> list, OnOrderListener onOrderListener) {
        this.context = context;
        this.list = list;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort, parent, false);
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
        if (list.get(position + 1).equals("步 数")) {
            holder.rl_item.setBackground(context.getResources().getDrawable(R.drawable.img_bushu));
            holder.tv_name.setText("步数");
            holder.tv_content.setText("2933");
            holder.tv_extra.setVisibility(View.VISIBLE);
            holder.tv_extra.setText("消耗74kcal");
        } else if (list.get(position + 1).equals("心 率")) {
            holder.rl_item.setBackground(context.getResources().getDrawable(R.drawable.heart_beat));
            holder.tv_name.setText("心率");
            holder.tv_content.setText("97");
        } else if (list.get(position + 1).equals("血 压")) {
            holder.rl_item.setBackground(context.getResources().getDrawable(R.drawable.blood_pressure_back));
            holder.tv_name.setText("血压");
            holder.tv_content.setText("120");
            holder.tv_extra.setVisibility(View.VISIBLE);
            holder.tv_extra.setText("低压40");
        } else if (list.get(position + 1).equals("血 氧")) {
            holder.rl_item.setBackground(context.getResources().getDrawable(R.drawable.blood_oxygen));
            holder.tv_name.setText("血氧");
            holder.tv_content.setText("64%");
        } else if (list.get(position + 1).equals("血 糖")) {
            holder.rl_item.setBackground(context.getResources().getDrawable(R.drawable.blood_sugar));
            holder.tv_name.setText("血糖");
            holder.tv_content.setText("5.6");
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_item;
        TextView tv_name,tv_content,tv_extra;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_extra = itemView.findViewById(R.id.tv_extra);
        }
    }
}
