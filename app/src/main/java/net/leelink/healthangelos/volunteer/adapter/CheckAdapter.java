package net.leelink.healthangelos.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.bean.VolCheckBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<VolCheckBean> list;
    Context context;
    OnOrderListener onOrderListener;

    public CheckAdapter(List<VolCheckBean> list, Context context, OnOrderListener onOrderListener) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1 || viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_uncheck, parent, false);
            return new ViewHolder0(view);
        } else if (viewType == 3) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_check, parent, false);
            return new ViewHolder1(view);
        }  else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_uncheck, parent, false);
            return new ViewHolder0(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder0) {      //未完成打卡
            ((ViewHolder0) holder).tv_name.setText(list.get(position).getVolName());
            if (list.get(position).getElderlyImgPath() != null) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getElderlyImgPath()).into(((ViewHolder0) holder).img_head);
            }
            if (list.get(position).getVolSex() == 0) {
                ((ViewHolder0) holder).tv_sex.setText("男");
            } else {
                ((ViewHolder0) holder).tv_sex.setText("女");
            }
            ((ViewHolder0) holder).tv_phone.setText(list.get(position).getVolTelephone());
            ((ViewHolder0) holder).btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderListener.onButtonClick(v, position);
                }
            });

        } else if (holder instanceof ViewHolder1) {       //已完成打卡上传
            ((ViewHolder1) holder).tv_name.setText(list.get(position).getVolName());
            if (list.get(position).getVolSex() == 0) {
                ((ViewHolder1) holder).tv_sex.setText("男");
            } else {
                ((ViewHolder1) holder).tv_sex.setText("女");
            }
            ((ViewHolder1) holder).tv_phone.setText(list.get(position).getVolTelephone());
            ((ViewHolder1) holder).btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderListener.onButtonClick(v, position);
                }
            });
            ((ViewHolder1) holder).tv_content.setText(list.get(position).getRemark());
            if (list.get(position).getElderlyImgPath() != null) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getElderlyImgPath()).into(((ViewHolder1) holder).img_head);
            }
            if (list.get(position).getImg1Path() != null) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getImg1Path()).into(((ViewHolder1) holder).img_1);
            }
            if (list.get(position).getImg2Path() != null) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getImg2Path()).into(((ViewHolder1) holder).img_2);
            }
            if (list.get(position).getImg3Path() != null) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + list.get(position).getImg3Path()).into(((ViewHolder1) holder).img_3);
            }

        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_sex, tv_phone, tv_name;
        Button btn_confirm;

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        ImageView img_head, img_1, img_2, img_3;
        TextView tv_name, tv_sex, tv_phone, tv_content;
        Button btn_confirm;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
            img_1 = itemView.findViewById(R.id.img_1);
            img_2 = itemView.findViewById(R.id.img_2);
            img_3 = itemView.findViewById(R.id.img_3);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    /**
     * 判断签到打卡状态 1 未开始打卡 2 未结束打卡 3 打卡结束
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return list.get(position).getState();
    }
}
