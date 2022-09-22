package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BldBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BloodPressureAdapter extends RecyclerView.Adapter<BloodPressureAdapter.ViewHolder> {
    private Context context;
    List<BldBean> list;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public BloodPressureAdapter(Context context, List<BldBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloodpressure,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_sbp.setText(String.valueOf(list.get(position).getX()));
        if(list.get(position).getX()>140){
            Glide.with(context).load(R.drawable.unusual_sbp).into(holder.img_sbp);
        }
        if(list.get(position).getY()>90){
            Glide.with(context).load(R.drawable.unusual_dbp).into(holder.img_dbp);
        }
        holder.tv_dbp.setText(String.valueOf(list.get(position).getY()));
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
        return  list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sbp,tv_dbp,tv_date,tv_time;
        ImageView img_sbp,img_dbp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sbp = itemView.findViewById(R.id.tv_sbp);
            tv_dbp = itemView.findViewById(R.id.tv_dbp);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_sbp = itemView.findViewById(R.id.img_sbp);
            img_dbp = itemView.findViewById(R.id.img_dbp);
        }
    }
}
