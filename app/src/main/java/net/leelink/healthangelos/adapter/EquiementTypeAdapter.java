package net.leelink.healthangelos.adapter;

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

public class EquiementTypeAdapter extends RecyclerView.Adapter<EquiementTypeAdapter.ViewHolder> {

    JSONArray jsonArray;
    Context context;
    OnOrderListener onOrderListener;
    int checkposition = 0;

    public EquiementTypeAdapter(JSONArray jsonArray,Context context,OnOrderListener onOrderListener) {
        this.jsonArray  = jsonArray;
        this.context  =context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public EquiementTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type,parent,false);
        EquiementTypeAdapter.ViewHolder viewHolder = new EquiementTypeAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull EquiementTypeAdapter.ViewHolder holder, int position) {
        if(position == checkposition) {
            holder.label.setVisibility(View.VISIBLE);
            holder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.bg));
            holder.tv_type_name.setTextColor(context.getResources().getColor(R.color.blue));
        } else {
            holder.label.setVisibility(View.INVISIBLE);
            holder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_type_name.setTextColor(context.getResources().getColor(R.color.text_black));
        }
        try {
            holder.tv_type_name.setText(jsonArray.getJSONObject(position).getString("deviceTypeName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public void setChecked(int position) {
        checkposition = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_bg;
        View label;
        TextView tv_type_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_bg = itemView.findViewById(R.id.ll_bg);
            label = itemView.findViewById(R.id.label);
            tv_type_name = itemView.findViewById(R.id.tv_type_name);
        }
    }
}
