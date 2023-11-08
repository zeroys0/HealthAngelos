package net.leelink.healthangelos.activity.NBdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.NBdevice.NbMsgBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NbMsgTypeBAdapter extends RecyclerView.Adapter<NbMsgTypeBAdapter.ViewHolder> {
    private Context context;
    private List<NbMsgBean> list;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar calendar= Calendar.getInstance();

    public NbMsgTypeBAdapter(Context context, List<NbMsgBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NbMsgTypeBAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nb_type_b_msg, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NbMsgTypeBAdapter.ViewHolder holder, int position) {
        try {
            Date date = sdf.parse(list.get(position).getCreateTime());
            calendar.setTime(date);
            String hour =String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) ;
            String minute ,second;
            if(calendar.get(Calendar.MINUTE)<10){
                minute = "0"+calendar.get(Calendar.MINUTE);
            } else {
                minute =String.valueOf(calendar.get(Calendar.MINUTE));
            }
            if(calendar.get(Calendar.SECOND)<10){
                second = "0"+calendar.get(Calendar.SECOND);
            } else {
                second =String.valueOf(calendar.get(Calendar.SECOND));
            }

            holder.tv_time.setText(hour+":"+minute+":"+second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_value.setText(list.get(position).getEventContext());
        holder.tv_content.setText(list.get(position).getEventValue());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_value,tv_content,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
