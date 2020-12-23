package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.HomeDoctorBean;
import net.leelink.healthangelos.util.Urls;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDoctorListAdapter extends RecyclerView.Adapter<HomeDoctorListAdapter.ViewHolder> {
    List<HomeDoctorBean> list;
    public Context context;
    OnOrderListener onOrderListener;

    public HomeDoctorListAdapter(List<HomeDoctorBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_doctor_item, parent, false); // 实例化viewholder
        HomeDoctorListAdapter.ViewHolder viewHolder = new HomeDoctorListAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(Urls.getInstance().IMG_URL+list.get(position).getCareDoctorRegedit().getImgPath()).into(holder.img_head);
        holder.tv_name.setText(list.get(position).getCareDoctorRegedit().getName());
        holder.tv_position.setText(list.get(position).getCareDoctorRegedit().getDuties());
        holder.tv_class.setText(list.get(position).getCareDoctorRegedit().getDepartment());
        holder.tv_hospital.setText(list.get(position).getCareDoctorRegedit().getHospital());
        holder.tv_good_at.setText(list.get(position).getCareDoctorRegedit().getSkill());
        holder.tv_score.setText(list.get(position).getCareDoctorRegedit().getTotalScore());
        holder.month_count.setText("月回答:"+list.get(position).getCareDoctorRegedit().getTotalCount());
        holder.tv_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list ==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name,tv_position,tv_class,tv_hospital,tv_good_at,tv_score,month_count,tv_ask;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_position= itemView.findViewById(R.id.tv_position);
            tv_class = itemView.findViewById(R.id.tv_class);
            tv_hospital = itemView.findViewById(R.id.tv_hospital);
            tv_good_at = itemView.findViewById(R.id.tv_good_at);
            tv_score = itemView.findViewById(R.id.tv_score);
            month_count = itemView.findViewById(R.id.month_count);
            tv_ask = itemView.findViewById(R.id.tv_ask);
        }
    }
}
