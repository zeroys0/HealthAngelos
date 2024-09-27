package net.leelink.healthangelos.activity.yasee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class YaseeDataAdapter extends RecyclerView.Adapter<YaseeDataAdapter.ViewHolder>{
    Context context;
    List<YaseeBean> list;
    OnOrderListener onItemClickListener;
    YaseeBpAdapter yaseeBpAdapter;
    YaseeBsAdapter yaseeBsAdapter;

    int type ;

    public YaseeDataAdapter(Context context, List<YaseeBean> list, OnOrderListener onItemClickListener,int type) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.type = type;
    }

    @NonNull
    @Override
    public YaseeDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a6gbldpressure_item,parent,false);
        YaseeDataAdapter.ViewHolder viewHolder = new YaseeDataAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull YaseeDataAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_time.setText(list.get(position).getName());
        holder.rl_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onButtonClick(v,position);
            }
        });
        if(list.get(position).getIs_show()){
            if (type==2) {
                yaseeBpAdapter = new YaseeBpAdapter(context, list.get(position).getList());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                holder.bld_list.setAdapter(yaseeBpAdapter);
                holder.bld_list.setLayoutManager(layoutManager);
            } else {
                Log.d( "onBindViewHolder: ","显示血糖 尿酸");
                yaseeBsAdapter = new YaseeBsAdapter(context, list.get(position).getList2(),type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                holder.bld_list.setAdapter(yaseeBsAdapter);
                holder.bld_list.setLayoutManager(layoutManager);
            }
            holder.bld_list.setVisibility(View.VISIBLE);
            holder.img_arrow.setImageResource(R.drawable.arrow_down_grey);
        } else {
            holder.bld_list.setVisibility(View.GONE);
            holder.img_arrow.setImageResource(R.drawable.arrow_right_grey);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time;
        RecyclerView bld_list;
        RelativeLayout rl_date;
        ImageView img_arrow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            bld_list = itemView.findViewById(R.id.bld_list);
            rl_date = itemView.findViewById(R.id.rl_date);
            img_arrow = itemView.findViewById(R.id.img_arrow);
        }
    }
}
