package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.icu.text.Edits;
import android.text.NoCopySpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends  RecyclerView.Adapter<DataAdapter.ViewHolder> {
    List<String[]> list;
    Context context;

    public DataAdapter(  List<String[]> list,Context context){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false); // 实例化viewholder
        DataAdapter.ViewHolder viewHolder = new DataAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_data.setText(list.get(position)[0]+":");
        holder.tv_value.setText(list.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_data,tv_value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_data = itemView.findViewById(R.id.tv_data);
            tv_value = itemView.findViewById(R.id.tv_value);
        }
    }
}
