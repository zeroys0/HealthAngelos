package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    public Context context;
    public int[] images;
    public List<String> list;
    public int type;
    public OnClassListener onClassListener;
    public ClassAdapter(Context context,int[] images,List<String> list,OnClassListener onClassListener,int type) {
        this.context = context;
        this.images = images;
        this.list = list;
        this.onClassListener = onClassListener;
        this.type = type;
    }

    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false); // 实例化viewholder
        ClassAdapter.ViewHolder viewHolder = new ClassAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, final int position) {
        Glide.with(context).load(images[position]).into(holder.img);
        holder.tv_name.setText(list.get(position));
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClassListener.onItemClick(position,type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tv_name;
        LinearLayout ll_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_item = itemView.findViewById(R.id.ll_item);
        }
    }
}
