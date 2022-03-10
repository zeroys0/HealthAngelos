package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;

    public ItemListAdapter(JSONArray jsonArray, Context context) {
        this.jsonArray = jsonArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.tv_name.setText(jsonArray.getJSONObject(position).getString("productName"));

            holder.tv_times.setText(jsonArray.getJSONObject(position).getInt("quantity")+"/"+jsonArray.getJSONObject(position).getInt("quantity")+"次");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_times;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_times = itemView.findViewById(R.id.tv_times);

        }
    }
}
