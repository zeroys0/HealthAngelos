package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.WorkCommentBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<WorkCommentBean> list;
    private OnContactListener onContactListener;

    public CommentAdapter(Context context,List<WorkCommentBean> list, OnContactListener onContactListener) {
        this.context = context;
        this.list = list;
        this.onContactListener = onContactListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_comment,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(list.get(position).getReplyElderlyName()!=null) {
            holder.tv_name.setText(list.get(position).getElderlyName() + "回复" + list.get(position).getReplyElderlyName() + ":");
        } else {
            holder.tv_name.setText(list.get(position).getElderlyName()+ ":");
        }
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactListener.OnDeleteClick(v,position);
            }
        });
        holder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactListener.OnEditClick(v,position);
            }
        });
        holder.tv_time.setText(list.get(position).getCommentTime());
        holder.tv_message.setText(list.get(position).getCommentContent());
        String id = list.get(position).getElderlyId()+"";
        if(!id.equals( MyApplication.userInfo.getOlderlyId())){
            holder.tv_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_message,tv_time,tv_reply,tv_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_reply = itemView.findViewById(R.id.tv_reply);
            tv_delete = itemView.findViewById(R.id.tv_delete);

        }
    }
}
