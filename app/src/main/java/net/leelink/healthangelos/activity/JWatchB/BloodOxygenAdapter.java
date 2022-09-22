package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.OxyBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BloodOxygenAdapter extends RecyclerView.Adapter<BloodOxygenAdapter.ViewHolder> {
    private Context context;
    private List<OxyBean> list;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public BloodOxygenAdapter(Context context, List<OxyBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloodoxygen_item,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_blood_oxygen.setText(String.valueOf(list.get(position).getO()));
        if(list.get(position).getO()<94){
            Glide.with(context).load(R.drawable.unusual_blood_oxygen).into(holder.img_oxygen);
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
        TextView tv_blood_oxygen,tv_date,tv_time;
        ImageView img_oxygen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_blood_oxygen = itemView.findViewById(R.id.tv_blood_oxygen);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_oxygen = itemView.findViewById(R.id.img_oxygen);
        }
    }
}
