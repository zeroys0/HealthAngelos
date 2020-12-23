package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BenefitBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BenefitAdapter extends RecyclerView.Adapter<BenefitAdapter.ViewHolder> {

    List<BenefitBean> list;
    Context context;
    OnItemClickListener onItemClickListener;
    int type;
    public static  final int BENEFIT = 1;
    public static  final int ESTIMATE = 2;


    public BenefitAdapter(List<BenefitBean> list, Context context, OnItemClickListener onItemClickListener,int type) {
        this.list = list;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.type = type;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_benefit,parent,false);
        BenefitAdapter.ViewHolder v= new BenefitAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v);
            }
        });
        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(list.get(position).getTitle());
        String time = list.get(position).getUpdateTime();
        time = time.substring(0,11);
        holder.tv_time.setText(time);
        Glide.with(context).load(Urls.getInstance().IMG_URL+list.get(position).getImgPath()).into(holder.img_head);
        if(type == BENEFIT) {
            holder.tv_resource.setText("来源:" + list.get(position).getResource());
        } else if(type == ESTIMATE) {
            holder.tv_resource.setVisibility(View.INVISIBLE);
        }
        holder.tv_content.setText(list.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_time,tv_resource,tv_content;
        ImageView img_head;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_head = itemView.findViewById(R.id.img_head);
            tv_resource = itemView.findViewById(R.id.tv_resource);
            tv_content = itemView.findViewById(R.id.tv_content);

        }
    }
}
