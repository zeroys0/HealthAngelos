package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BloodSugarBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SinoUaAdapter  extends RecyclerView.Adapter<SinoUaAdapter.ViewHolder>  {
    List<BloodSugarBean> list;
    Context context;

    public SinoUaAdapter  (List<BloodSugarBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SinoUaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sino_blood_sugar,parent,false);
        SinoUaAdapter.ViewHolder viewHolder = new SinoUaAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SinoUaAdapter.ViewHolder holder, int position) {

        holder.tv_value.setText(list.get(position).getBlood_uric()+"μmol/L");
        holder.tv_time.setText(list.get(position).getDetection_time());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_value,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
