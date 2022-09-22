package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.SaaSHeartBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeartRateAdapter extends RecyclerView.Adapter<HeartRateAdapter.ViewHolder> {
    private List<SaaSHeartBean> list;
    Context context;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public HeartRateAdapter(List<SaaSHeartBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.heart_rate_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_heart_rate.setText(String.valueOf(list.get(position).getH()));
        if(list.get(position).getH() <60 || list.get(position).getH()>90){
            holder.img_heart.setImageResource(R.drawable.heart_unusual);
        }
        String time = list.get(position).getTime();
        try {
            Date date = simpleDateFormat.parse(time);
            holder.tv_date.setText(dateFormat.format(date));
            holder.tv_time.setText(timeFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_heart_rate,tv_date,tv_time;
        ImageView img_heart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_heart_rate = itemView.findViewById(R.id.tv_heart_rate);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_heart = itemView.findViewById(R.id.img_heart);
        }
    }
}
