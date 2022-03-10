package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.WorkBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {
    List<WorkBean> list;
    Context context;
    OnItemClickListener onItemClickListener;

    public WorkAdapter(List<WorkBean> list, Context context, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String  img_arr = list.get(position).getPicture();
        if(img_arr.length()>0){
            String[] arr = img_arr.split(",");
            Glide.with(context).load(Urls.getInstance().IMG_URL+"/img/"+arr[0]).into(holder.img_pic);
        }
        holder.tv_content.setText(list.get(position).getIntroduce());
        holder.tv_name.setText(list.get(position).getElderlyName());
//        Glide.with(context).load(Urls.getInstance().IMG_URL+list.get(position).get)
        holder.tv_count.setText(list.get(position).getThumbUpNumber()+"");
        if(list.get(position).getThumbUp() == 1) {
            holder.img_good.setImageResource(R.drawable.img_dianzan_small);
        }else {
            holder.img_good.setImageResource(R.drawable.img_weidianzan_small);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_pic,img_head,img_good;
        TextView tv_content,tv_name,tv_count;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_pic = itemView.findViewById(R.id.img_pic);
            tv_content = itemView.findViewById(R.id.tv_content);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_count = itemView.findViewById(R.id.tv_count);
            img_good = itemView.findViewById(R.id.img_good);
        }
    }
}
