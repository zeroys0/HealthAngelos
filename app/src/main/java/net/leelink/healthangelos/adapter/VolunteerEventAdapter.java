package net.leelink.healthangelos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.VolunteerEventBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VolunteerEventAdapter extends RecyclerView.Adapter<VolunteerEventAdapter.ViewHolder> {
    OnOrderListener onOrderListener;
    int type ;
    List<VolunteerEventBean> list;

    public VolunteerEventAdapter(List<VolunteerEventBean> list,OnOrderListener onOrderListener,int type ) {
        this.onOrderListener = onOrderListener;
        this. type = type;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volunteer_clockin,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_title.setText(list.get(position).getServTitle());

        switch (list.get(position).getState()){
            case 2:
                holder.tv_state.setText("待打卡");
                holder.btn_clockin.setText("开始打卡");
                if(showBtn(list.get(position).getServStartTime())) {
                    holder.btn_clockin.setVisibility(View.VISIBLE);
                } else {
                    holder.btn_clockin.setVisibility(View.INVISIBLE);
                }
                break;
            case 3:
                holder.tv_state.setText("待结束");
                holder.btn_clockin.setText("结束打卡");
//                if(showBtn(list.get(position).getServEndTime())) {
                    holder.btn_clockin.setVisibility(View.VISIBLE);
//                } else {
//                    holder.btn_clockin.setVisibility(View.INVISIBLE);
//                }
                break;
            case 4:
                holder.tv_state.setText("等待审核");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 5:
                holder.tv_state.setText("审核通过");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 6:
            case 11:
                holder.tv_state.setText("审核失败");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 7:
                holder.tv_state.setText("二次审核中");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 8:
                holder.tv_state.setText("已完成");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 9:
                holder.tv_state.setText("申诉未通过");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;
            case 10:
                holder.tv_state.setText("已撤销");
                holder.btn_clockin.setVisibility(View.INVISIBLE);
                break;

        }
        if(type ==2){
            holder.btn_clockin.setVisibility(View.INVISIBLE);
        }
        holder.btn_clockin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
        holder.tv_time.setText(list.get(position).getServTime());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_state,tv_time;
        Button btn_clockin;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_time = itemView.findViewById(R.id.tv_time);
            btn_clockin = itemView.findViewById(R.id.btn_clockin);
        }
    }

//    public long getMinus(String servTime){
//
//        try
//        {
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String now = df.format(new Date(System.currentTimeMillis()));
//            Date startTime = df.parse(servTime);
//            Date endTime = df.parse(now);
//
//            long diff = startTime.getTime() - endTime.getTime();
//            long days = diff / (1000 * 60 * 60 * 24);
//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
//
//            return minutes;
//        }catch (Exception e)
//        {
//        }
//        return 0;
//    }

    public boolean showBtn(String servTime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(servTime);
            long time = date.getTime();
            if(time < System.currentTimeMillis()){
                return true;
            }else {
                return  false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
