package net.leelink.healthangelos.activity.Fit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.bean.EcgBean;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FitStepAdapter extends RecyclerView.Adapter<FitStepAdapter.ViewHolder> {
    List<EcgBean> list;
    Context context;
    OnOrderListener onOrderListener;


    public FitStepAdapter(List<EcgBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public FitStepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_step_item,parent,false);
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
    public void onBindViewHolder(@NonNull FitStepAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getTestTime());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_point;
        TextView tv_content,tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_point = itemView.findViewById(R.id.img_point);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
