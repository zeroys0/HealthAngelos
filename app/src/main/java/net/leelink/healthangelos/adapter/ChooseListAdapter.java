package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseListAdapter extends RecyclerView.Adapter<ChooseListAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;
    OnchooseLisenter onchooseLisenter;
    List<String> name_list;

    public ChooseListAdapter(JSONArray jsonArray, Context context, OnchooseLisenter onchooseLisenter, List<String> name_list){
        this.jsonArray = jsonArray;
        this.context = context;
        this.onchooseLisenter = onchooseLisenter;
        this.name_list = name_list;
    }
    @NonNull
    @Override
    public ChooseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseListAdapter.ViewHolder holder, final int position) {
        holder.tv_name.setText(name_list.get(position));
        try {
            holder.tv_energy.setText(jsonArray.getJSONObject(position).getString("totalKcal")+"千卡");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.rl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onchooseLisenter.onChooseClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_energy;
        RelativeLayout rl_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_energy = itemView.findViewById(R.id.tv_energy);
            rl_delete = itemView.findViewById(R.id.rl_delete);

        }
    }
}
