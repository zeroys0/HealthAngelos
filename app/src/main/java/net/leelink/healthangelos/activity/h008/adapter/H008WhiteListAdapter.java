package net.leelink.healthangelos.activity.h008.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class H008WhiteListAdapter extends RecyclerView.Adapter<H008WhiteListAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;
    OnAlarmClockListener onAlarmClockListener;

    public H008WhiteListAdapter(JSONArray jsonArray, Context context, OnAlarmClockListener onAlarmClockListener) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.onAlarmClockListener = onAlarmClockListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_h008_whitelist,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.tv_name.setText(jsonArray.getJSONObject(position).getString("name"));
            holder.tv_phone.setText(jsonArray.getJSONObject(position).getString("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlarmClockListener.onDelete(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray==null?0:jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_phone;
        LinearLayout ll_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            ll_delete = itemView.findViewById(R.id.ll_delete);
        }
    }
}
