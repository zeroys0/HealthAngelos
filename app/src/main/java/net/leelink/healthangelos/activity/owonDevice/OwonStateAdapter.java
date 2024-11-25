package net.leelink.healthangelos.activity.owonDevice;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OwonStateAdapter extends RecyclerView.Adapter<OwonStateAdapter.ViewHolder> {
    List<OwonStateBean> list;
    Context context;
    public OwonStateAdapter(List<OwonStateBean> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public OwonStateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_owon_state,null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OwonStateAdapter.ViewHolder holder, int position) {
        holder.tv_state_name.setText(list.get(position).getKey());
        if(list.get(position).getValue()==0) {
         holder.tv_state.setTextColor(context.getResources().getColor(R.color.text_blue));
        } else {
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.tv_state.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_state_name,tv_state;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_state_name = itemView.findViewById(R.id.tv_state_name);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }
}
