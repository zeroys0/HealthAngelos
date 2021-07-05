package net.leelink.healthangelos.reform.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NeoProgressAdapter extends RecyclerView.Adapter<NeoProgressAdapter.ViewHolder> {
    Context context;
    OnOrderListener onOrderListener;

    public NeoProgressAdapter(Context context, OnOrderListener onOrderListener) {
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public NeoProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_neo,parent,false);
        NeoProgressAdapter.ViewHolder viewHolder = new NeoProgressAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NeoProgressAdapter.ViewHolder holder, int position) {
//        try {
//            JSONObject json = jsonArray.getJSONObject(position);
//            String date = json.getString("applyTime");
//            holder.tv_day.setText(date.substring(5,10));
//            holder.tv_time.setText(date.substring(11,16));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        GradientDrawable gradientDrawable = (GradientDrawable) holder.img_circle.getBackground();
        gradientDrawable.setColor(Color.parseColor("#c42727"));
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_day,tv_time,tv_content;
        TextView img_circle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_circle = itemView.findViewById(R.id.img_circle);
        }
    }
}
