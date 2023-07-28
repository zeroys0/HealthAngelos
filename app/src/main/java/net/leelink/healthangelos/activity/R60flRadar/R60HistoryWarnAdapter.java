package net.leelink.healthangelos.activity.R60flRadar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class R60HistoryWarnAdapter extends RecyclerView.Adapter<R60HistoryWarnAdapter.ViewHolder> {
    private Context context;
    private List<R60Bean> list;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar calendar= Calendar.getInstance();

    public R60HistoryWarnAdapter(Context context, List<R60Bean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public R60HistoryWarnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_r60history_msg, parent, false);
        R60HistoryWarnAdapter.ViewHolder viewHolder = new R60HistoryWarnAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull R60HistoryWarnAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.line_up.setVisibility(View.INVISIBLE);
        }
        if (position == list.size() - 1) {
            holder.line_down.setVisibility(View.INVISIBLE);
        }
        if (list.get(position).getPropertyValue().equals("00")) {
            holder.tv_content.setText("正常");
            holder.img_point.setImageResource(R.drawable.r60_circle_green);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.r60_green));
            holder.tv_content.setTextColor(context.getResources().getColor(R.color.r60_green));
        } else if (list.get(position).getPropertyValue().equals("01")){
            holder.tv_content.setText("驻留");
            holder.img_point.setImageResource(R.drawable.r60_circle_yellow);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.r60_yellow));
            holder.tv_content.setTextColor(context.getResources().getColor(R.color.r60_yellow));
        } else if (list.get(position).getPropertyValue().equals("10")){
            holder.tv_content.setText("跌倒");
            holder.img_point.setImageResource(R.drawable.r60_circle_red);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.red));
            holder.tv_content.setTextColor(context.getResources().getColor(R.color.red));
        } else if (list.get(position).getPropertyValue().equals("11")){
            holder.tv_content.setText("跌倒并驻留");
            holder.img_point.setImageResource(R.drawable.r60_circle_red);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.red));
            holder.tv_content.setTextColor(context.getResources().getColor(R.color.red));
        }
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
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View line_up, line_down;
        TextView tv_time, tv_content;
        ImageView img_point;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            line_up = itemView.findViewById(R.id.line_up);
            line_down = itemView.findViewById(R.id.line_down);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            img_point = itemView.findViewById(R.id.img_point);
        }
    }
}
