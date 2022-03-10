package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.PhotoBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    OnOrderListener onOrderListener;
    List<PhotoBean> list;
    Context context;
    List<String> img_list;
    int type;


    public PictureAdapter(OnOrderListener onOrderListener, List<PhotoBean> list, Context context) {
        this.onOrderListener = onOrderListener;
        this.list = list;
        this.context = context;
        type = 1;
    }

    public PictureAdapter(Context context, List<String> img_list) {
        this.context = context;
        this.img_list = img_list;
        type = 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if(type==1) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderListener.onItemClick(v);
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (type == 1) {
            Glide.with(context).load(Urls.getInstance().IMG_URL + "/img/" + list.get(position).getPhotoName()).into(holder.img_pic);
        } else if (type == 2) {
            Glide.with(context).load(Urls.getInstance().IMG_URL + "/img/" + img_list.get(position)).into(holder.img_pic);
            //Glide.with(context).load("https://test.llky.net.cn:8899/files/img/38a65f1f4bca4cc9872028fcc5a20e93.png").into(holder.img_pic);
        }
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case 1:
                return list == null ? 0 : list.size();
            case 2:
                return img_list == null ? 0 : img_list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_pic = itemView.findViewById(R.id.img_pic);
        }
    }
}
