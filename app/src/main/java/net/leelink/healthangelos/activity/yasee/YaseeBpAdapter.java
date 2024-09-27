package net.leelink.healthangelos.activity.yasee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class YaseeBpAdapter extends RecyclerView.Adapter<YaseeBpAdapter.ViewHolder>{
    private Context context;
    private List<YaseeBpBean> list;

    public YaseeBpAdapter(Context context, List<YaseeBpBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public YaseeBpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a6g_bld_item,parent,false);
        YaseeBpAdapter.ViewHolder viewHolder = new YaseeBpAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull YaseeBpAdapter.ViewHolder holder, int position) {
        list.get(position).getSystolic();

        holder.tv_bld_pressure.setText(list.get(position).getSystolic()+"/"+list.get(position).getDiastolic());
        if(list.get(position).getSystolic()>140 || list.get(position).getDiastolic()>90 ||list.get(position).getDiastolic()<60 || list.get(position).getSystolic()<90){
            holder.img_bld_pressure.setImageResource(R.drawable.a6g_unusual_bp);
        } else {
            holder.img_bld_pressure.setImageResource(R.drawable.a6g_usual_bp);
        }
        holder.tv_heart_rate.setText(list.get(position).getHeartRate()+"");
        if(list.get(position).getHeartRate()>90 ||list.get(position).getHeartRate()<60){
            holder.img_heart_rate.setImageResource(R.drawable.a6g_unusual_hrt);
        } else {
            holder.img_heart_rate.setImageResource(R.drawable.a6g_usual_hrt);
        }
        holder.tv_time.setText(new SimpleDateFormat("HH:mm").format(new Date(list.get(position).getTestTime())));
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_bld_pressure,tv_heart_rate,tv_time;
        ImageView img_bld_pressure,img_heart_rate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bld_pressure = itemView.findViewById(R.id.tv_bld_pressure);
            tv_heart_rate = itemView.findViewById(R.id.tv_heart_rate);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_bld_pressure = itemView.findViewById(R.id.img_bld_pressure);
            img_heart_rate = itemView.findViewById(R.id.img_heart_rate);
        }
    }
}
