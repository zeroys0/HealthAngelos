package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.DoctorBean;
import net.leelink.healthangelos.bean.NewsBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    List<DoctorBean> list = new ArrayList<>();
    public Context context;
    OnOrderListener onOrderListener;


    public DoctorListAdapter(List<DoctorBean> list, Context context,OnOrderListener onOrderListener ) {
        this.list = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public DoctorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false); // 实例化viewholder
        DoctorListAdapter.ViewHolder viewHolder = new DoctorListAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListAdapter.ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return list ==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name,tv_position,tv_class,tv_hospital,tv_return,tv_good_at,tv_score,month_count,tv_message_price,tv_phone_price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_position= itemView.findViewById(R.id.tv_position);
            tv_class = itemView.findViewById(R.id.tv_class);
            tv_hospital = itemView.findViewById(R.id.tv_hospital);
            tv_return = itemView.findViewById(R.id.tv_return);
            tv_good_at = itemView.findViewById(R.id.tv_good_at);
            tv_score = itemView.findViewById(R.id.tv_score);
            month_count = itemView.findViewById(R.id.month_count);
            tv_message_price = itemView.findViewById(R.id.tv_message_price);
            tv_phone_price = itemView.findViewById(R.id.tv_phone_price);
        }
    }
}
