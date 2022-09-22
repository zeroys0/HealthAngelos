package net.leelink.healthangelos.activity.a666g;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.A6gBloodPressureBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class A6gBldAdapter extends RecyclerView.Adapter<A6gBldAdapter.ViewHolder> {
    private Context context;
    private List<A6gBloodPressureBean> list;

    public A6gBldAdapter(Context context, List<A6gBloodPressureBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a6g_bld_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        list.get(position).getSys();

        holder.tv_bld_pressure.setText(list.get(position).getSys()+"/"+list.get(position).getDia());
        if(list.get(position).getSys()>140 || list.get(position).getDia()>90 ||list.get(position).getDia()<60 || list.get(position).getSys()<90){
            holder.img_bld_pressure.setImageResource(R.drawable.a6g_unusual_bp);
        } else {
            holder.img_bld_pressure.setImageResource(R.drawable.a6g_usual_bp);
        }
        holder.tv_heart_rate.setText(list.get(position).getPul()+"");
        if(list.get(position).getPul()>90 ||list.get(position).getPul()<60){
            holder.img_heart_rate.setImageResource(R.drawable.a6g_unusual_hrt);
        } else {
            holder.img_heart_rate.setImageResource(R.drawable.a6g_usual_hrt);
        }
        holder.tv_time.setText(list.get(position).getTime());
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
