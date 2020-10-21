package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EpAdapter extends RecyclerView.Adapter<EpAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;
    OnItemClickListener onItemClickListener;

    public EpAdapter(JSONArray jsonArray,Context context,OnItemClickListener onItemClickListener){
        this.jsonArray = jsonArray;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public void update(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ep,parent,false);
        EpAdapter.ViewHolder viewHolder = new EpAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.tv_name.setText(jsonArray.getJSONObject(position).getString("deviceName"));
        } catch (JSONException e) {

        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name  = itemView.findViewById(R.id.tv_name);

        }
    }
}
