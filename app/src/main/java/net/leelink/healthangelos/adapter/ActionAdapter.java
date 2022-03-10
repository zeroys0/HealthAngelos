package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.ActionBean;
import net.leelink.healthangelos.util.HtmlUtil;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {
    List<ActionBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public ActionAdapter(List<ActionBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myaction,parent,false);
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
        holder.tv_title.setText(list.get(position).getActName());
        holder.tv_content.setText(HtmlUtil.delHTMLTag(list.get(position).getRemark()));
        holder.tv_time.setText(list.get(position).getTime());
        holder.tv_state.setVisibility(View.INVISIBLE);
        if (list.get(position).getTitleImg() != null && list.get(position).getTitleImg().length() > 0) {
            Log.e("onBindViewHolder: ", list.get(position).getTitleImg().length() + "");
            Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getTitleImg()).into(holder.img_head);
        }

        if(list.get(position).getSign()==1){
            holder.tv_type.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_time,tv_name,tv_content,tv_type,tv_state;
        ImageView img_head;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_head = itemView.findViewById(R.id.img_head);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }
}
