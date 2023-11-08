package net.leelink.healthangelos.activity.Badge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HouseLocateAdapter extends RecyclerView.Adapter<HouseLocateAdapter.ViewHolder> {
    Context context;
    List<HouseLocateBean> list;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HouseLocateAdapter(Context context, List<HouseLocateBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HouseLocateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge_house,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HouseLocateAdapter.ViewHolder holder, int position) {
        try {
            holder.tv_time.setText(sdf.format(simpleDateFormat.parse(list.get(position).getTestTime())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_room.setText(list.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_room;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_room = itemView.findViewById(R.id.tv_room);
        }
    }
}
