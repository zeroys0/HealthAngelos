package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    JSONArray jsonArray ;
    OnOrderListener onOrderListener;
    Context context;

    public DeviceListAdapter(JSONArray jsonArray,OnOrderListener onOrderListener,Context context){
        this.jsonArray = jsonArray;
        this.onOrderListener = onOrderListener;
        this.context = context;
    }
    @NonNull
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
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
    public void onBindViewHolder(@NonNull DeviceListAdapter.ViewHolder holder, int position) {
        try {
            if (!jsonArray.getJSONObject(position).isNull("imgPath")) {
                Glide.with(context).load(Urls.getInstance().IMG_URL+jsonArray.getJSONObject(position).getString("imgPath")).into(holder.img_head);
            }
            holder.tv_name.setText(jsonArray.getJSONObject(position).getString("deviceName"));
            holder.tv_detail.setText(jsonArray.getJSONObject(position).getString("deviceModel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name,tv_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_detail = itemView.findViewById(R.id.tv_detail);

        }
    }
}
