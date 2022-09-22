package net.leelink.healthangelos.activity.a666g;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.G7gBloodSugarBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class G7gBldSgAdapter extends RecyclerView.Adapter<G7gBldSgAdapter.ViewHolder> {
    Context context;
    List<G7gBloodSugarBean> list;

    public G7gBldSgAdapter(Context context, List<G7gBloodSugarBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bld_sugar_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_blood_sugar.setText(list.get(position).getValue()+"");
        if(list.get(position).getMeal_time()==1){
            holder.tv_dinner.setText("餐前");
            if(list.get(position).getValue()<3.9 ||list.get(position).getValue()>6.1){
                holder.img_bld_sugar.setImageResource(R.drawable.unusual_bld_sugar);
            }
        }else {
            holder.tv_dinner.setText("餐后");
            if(list.get(position).getValue()>7.8){
                holder.img_bld_sugar.setImageResource(R.drawable.unusual_bld_sugar);
            }
        }
        holder.tv_time.setText(list.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_blood_sugar,tv_dinner,tv_time;
        ImageView img_bld_sugar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_blood_sugar = itemView.findViewById(R.id.tv_blood_sugar);
            tv_dinner = itemView.findViewById(R.id.tv_dinner);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_bld_sugar = itemView.findViewById(R.id.img_bld_sugar);
        }
    }
}
