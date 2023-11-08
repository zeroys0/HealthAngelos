package net.leelink.healthangelos.reform.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.reform.bean.ProcessBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NeoProgressAdapter extends RecyclerView.Adapter<NeoProgressAdapter.ViewHolder> {
    Context context;
    List<ProcessBean> list;
    OnOrderListener onOrderListener;

    public NeoProgressAdapter(Context context, List<ProcessBean> list, OnOrderListener onOrderListener) {
        this.context = context;
        this.list = list;
        this.onOrderListener = onOrderListener;
    }


    @NonNull
    @Override
    public NeoProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_neo, parent, false);
        NeoProgressAdapter.ViewHolder viewHolder = new NeoProgressAdapter.ViewHolder(view);
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
//        try {
//            JSONObject json = jsonArray.getJSONObject(position);
//            String date = json.getString("applyTime");
//            holder.tv_day.setText(date.substring(5,10));
//            holder.tv_time.setText(date.substring(11,16));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if (position == 0) {
            GradientDrawable gradientDrawable = (GradientDrawable) holder.img_circle.getBackground();
            gradientDrawable.setColor(Color.parseColor("#27c443"));
        } else {
            GradientDrawable gradientDrawable = (GradientDrawable) holder.img_circle.getBackground();
            gradientDrawable.setColor(Color.parseColor("#dedede"));
        }
        holder.tv_name.setText(list.get(position).getCreateBy());
        holder.tv_content.setText(list.get(position).getContent());
        String time = list.get(position).getCreateTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            holder.tv_year.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
            holder.tv_time.setText(new SimpleDateFormat("HH:mm").format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_year, tv_content, tv_time;
        TextView img_circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_circle = itemView.findViewById(R.id.img_circle);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_year = itemView.findViewById(R.id.tv_year);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

}
