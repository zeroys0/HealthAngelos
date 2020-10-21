package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.NewsBean;
import net.leelink.healthangelos.bean.RankBean;
import net.leelink.healthangelos.util.Urls;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;




public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<NewsBean> list = new ArrayList<>();
    public Context context;
    OnOrderListener onOrderListener;


    public NewsAdapter(List<NewsBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false); // 实例化viewholder
        NewsAdapter.ViewHolder viewHolder = new NewsAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_time.setText(list.get(position).getUpdateTime());
        Glide.with(context).load(Urls.IMG_URL+list.get(position).getImgPath()).into(holder.img_head);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_time;
        ImageView img_head;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_head = itemView.findViewById(R.id.img_head);


        }
    }
}
