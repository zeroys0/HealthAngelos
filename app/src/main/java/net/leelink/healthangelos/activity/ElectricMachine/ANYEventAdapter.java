package net.leelink.healthangelos.activity.ElectricMachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.leelink.healthangelos.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ANYEventAdapter extends RecyclerView.Adapter<ANYEventAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.any_event_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
