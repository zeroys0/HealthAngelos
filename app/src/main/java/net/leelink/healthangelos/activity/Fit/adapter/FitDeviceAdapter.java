package net.leelink.healthangelos.activity.Fit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polidea.rxandroidble2.scan.ScanResult;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FitDeviceAdapter extends RecyclerView.Adapter<FitDeviceAdapter.ViewHolder> {
    private Context context;
    private OnOrderListener onOrderListener;
    private List<ScanResult> list;

    public FitDeviceAdapter(Context context, OnOrderListener onOrderListener) {
        this.context = context;
        this.onOrderListener = onOrderListener;
        this.list = new ArrayList<>(10);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_device_item,parent,false);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_mac.setText(list.get(position).getBleDevice().getMacAddress());
        holder.tv_name.setText(list.get(position).getBleDevice().getName());
        holder.tv_rssi.setText(list.get(position).getRssi()+"");
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_mac,tv_rssi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            tv_rssi = itemView.findViewById(R.id.tv_rssi);
        }
    }

    public void add(ScanResult scanResult){
        int existIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            ScanResult r = list.get(i);
            if (r.getBleDevice().getMacAddress().equals(scanResult.getBleDevice().getMacAddress())) {
                existIndex = i;
                break;
            }
        }
        if (existIndex == -1) {
            list.add(scanResult);
        } else {
            list.set(existIndex, scanResult);
        }
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    public Object getItem(int postion){
        return list.get(postion);
    }
}
