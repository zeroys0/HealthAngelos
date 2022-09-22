package net.leelink.healthangelos.activity.JWatchB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.LocateBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Saas_LocateAdapter extends RecyclerView.Adapter<Saas_LocateAdapter.ViewHolder> {
    private Context context;
    List<LocateBean> list;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public Saas_LocateAdapter(Context context, List<LocateBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saas_locate_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(position == list.size()-1){
            holder.img_label.setImageResource(R.drawable.saas_locate);
            holder.line.setVisibility(View.INVISIBLE);
        }else {
            holder.img_label.setImageResource(R.drawable.circle_empty);
            holder.line.setVisibility(View.VISIBLE);
        }
        String start_time = list.get(position).getCt();
        String end_time = list.get(position).getUt();
        try {
            Date sdate = simpleDateFormat.parse(start_time);
            Date edate = simpleDateFormat.parse(end_time);
            holder.tv_time.setText(timeFormat.format(sdate)+"-"+timeFormat.format(edate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_address.setText(list.get(position).getStr());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View line;
        ImageView img_label;
        TextView tv_time,tv_address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
            img_label = itemView.findViewById(R.id.img_label);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }
}
