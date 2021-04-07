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

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ViewHolder> {
    List<String> list;
    OnDeviceChooseListener onDeviceChooseListener;
    int type;
    int checked = -1;
    Context context;

    public ChooseAdapter(List<String> list, Context context,OnDeviceChooseListener onDeviceChooseListener,int type) {
        this.list = list;
        this.onDeviceChooseListener = onDeviceChooseListener;
        this.type = type;
        this.context = context;
    }

    @NonNull
    @Override
    public ChooseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_username,parent,false);
        ChooseAdapter.ViewHolder viewHolder = new ChooseAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceChooseListener.onItemClick(v,type);
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChooseAdapter.ViewHolder holder, int position) {
        holder.tv_user_name.setText(list.get(position));
        if(checked == position){
            holder.rl_bg.setBackgroundColor(context.getResources().getColor(R.color.bg));
        } else {
            holder.rl_bg.setBackgroundColor(context.getResources().getColor(R.color.white));

        }
    }

    @Override
    public int getItemCount() {
       return  list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name;
        RelativeLayout rl_bg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            rl_bg = itemView.findViewById(R.id.rl_bg);
        }
    }

    public void setChecked(int position){
        checked = position;
        notifyDataSetChanged();
    }
}
