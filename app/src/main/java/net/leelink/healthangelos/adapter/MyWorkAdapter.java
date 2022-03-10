package net.leelink.healthangelos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.WorkBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyWorkAdapter extends RecyclerView.Adapter<MyWorkAdapter.ViewHolder> {
    RecyclerView.RecycledViewPool recycledViewPool;
    PictureAdapter pictureAdapter;
    Context context;
    OnOrderListener onOrderListener;
    List<WorkBean> workBeanList;

    public MyWorkAdapter(Context context, OnOrderListener onOrderListener,List<WorkBean> workBeanList) {
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.workBeanList = workBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_work,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(recycledViewPool !=null){
            holder.image_list.setRecycledViewPool(recycledViewPool);
        }
        recycledViewPool = holder.image_list.getRecycledViewPool();
        String img_list = workBeanList.get(position).getPicture();
        String[] img_arr = img_list.split(",");
        List<String> imgs = Arrays.asList(img_arr);
        pictureAdapter = new PictureAdapter(context,imgs);

        holder.image_list.setAdapter(pictureAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,3,RecyclerView.VERTICAL,false);
        holder.image_list.setLayoutManager(layoutManager);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(workBeanList.get(position).getUploadTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_time.setText(date.getMonth()+"月");

        holder.tv_name.setText(workBeanList.get(position).getActName());
        holder.tv_content.setText(workBeanList.get(position).getIntroduce());

    }

    @Override
    public int getItemCount() {
        return workBeanList==null?0:workBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView image_list;
        TextView tv_time,tv_name,tv_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_list = itemView.findViewById(R.id.image_list);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
