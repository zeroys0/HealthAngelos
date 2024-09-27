package net.leelink.healthangelos.activity.yasee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class YaseeBsAdapter  extends RecyclerView.Adapter<YaseeBsAdapter.ViewHolder>{
    private Context context;
    private List<YaseeBsBean> list;
    private int type;

    public YaseeBsAdapter(Context context, List<YaseeBsBean> list,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public YaseeBsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yasee_bs_item,parent,false);
        YaseeBsAdapter.ViewHolder viewHolder = new YaseeBsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull YaseeBsAdapter.ViewHolder holder, int position) {
        if(type==1){
            holder.tv_unit.setText("mmol/L");
            holder.img_head.setImageDrawable(context.getResources().getDrawable(R.drawable.img_yasee_bs));

        } else if(type ==4){
            holder.tv_unit.setText("umol/L");
            holder.img_head.setImageDrawable(context.getResources().getDrawable(R.drawable.img_yasee_acid_s));
        }
        holder.tv_value.setText(list.get(position).getResult()+"");
        holder.tv_time.setText(new SimpleDateFormat("HH:mm").format(new Date(list.get(position).getTestTime())));
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_value,tv_unit,tv_time;
        ImageView img_head;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_unit = itemView.findViewById(R.id.tv_unit);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_head = itemView.findViewById(R.id.img_head);
        }
    }
}
